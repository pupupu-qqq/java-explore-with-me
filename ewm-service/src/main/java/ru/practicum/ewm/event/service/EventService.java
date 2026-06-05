package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.AdminEventAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.UserEventAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSpecifications;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.stats.StatsService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {
	private final EventRepository eventRepository;
	private final ParticipationRequestRepository requestRepository;
	private final UserService userService;
	private final CategoryService categoryService;
	private final StatsService statsService;

	public EventService(EventRepository eventRepository,
						ParticipationRequestRepository requestRepository,
						UserService userService,
						CategoryService categoryService,
						StatsService statsService) {
		this.eventRepository = eventRepository;
		this.requestRepository = requestRepository;
		this.userService = userService;
		this.categoryService = categoryService;
		this.statsService = statsService;
	}

	@Transactional
	public EventFullDto create(Long userId, NewEventDto dto) {
		validateEventDate(dto.getEventDate(), 2);
		User initiator = userService.getById(userId);
		Category category = categoryService.getById(dto.getCategory());
		Event event = EventMapper.toEntity(dto);
		event.setInitiator(initiator);
		event.setCategory(category);
		Event savedEvent = eventRepository.save(event);
		return toFullDto(savedEvent);
	}

	@Transactional(readOnly = true)
	public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
		userService.getById(userId);
		List<Event> events = eventRepository.findAllByInitiatorId(userId, new OffsetPageRequest(from, size));
		return toShortDtos(events);
	}

	@Transactional(readOnly = true)
	public EventFullDto getUserEvent(Long userId, Long eventId) {
		userService.getById(userId);
		return toFullDto(getUserEventEntity(userId, eventId));
	}

	@Transactional
	public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
		userService.getById(userId);
		Event event = getUserEventEntity(userId, eventId);
		if (event.getState() == EventState.PUBLISHED) {
			throw new ConflictException("Only pending or canceled events can be changed");
		}
		updateCommonFields(event, request.getAnnotation(), request.getCategory(), request.getDescription(),
				request.getEventDate(), request.getLocation(), request.getPaid(), request.getParticipantLimit(),
				request.getRequestModeration(), request.getTitle(), 2);
		if (request.getStateAction() == UserEventAction.SEND_TO_REVIEW) {
			event.setState(EventState.PENDING);
		} else if (request.getStateAction() == UserEventAction.CANCEL_REVIEW) {
			event.setState(EventState.CANCELED);
		}
		return toFullDto(eventRepository.save(event));
	}

	@Transactional(readOnly = true)
	public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
		userService.getById(userId);
		getUserEventEntity(userId, eventId);
		return requestRepository.findAllByEventId(eventId).stream()
				.map(ParticipationRequestMapper::toDto)
				.toList();
	}

	@Transactional
	public EventRequestStatusUpdateResult updateEventRequests(Long userId,
															  Long eventId,
															  EventRequestStatusUpdateRequest request) {
		userService.getById(userId);
		Event event = getUserEventEntity(userId, eventId);
		if (request.getRequestIds() == null || request.getRequestIds().isEmpty()) {
			return new EventRequestStatusUpdateResult();
		}
		if (request.getStatus() != RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.REJECTED) {
			throw new ValidationException("Status must be CONFIRMED or REJECTED");
		}
		List<ParticipationRequest> requests = requestRepository.findAllByIdInAndEventId(request.getRequestIds(),
				eventId);
		if (requests.size() != request.getRequestIds().size()) {
			throw new NotFoundException("Request was not found");
		}
		for (ParticipationRequest participationRequest : requests) {
			if (participationRequest.getStatus() != RequestStatus.PENDING) {
				throw new ConflictException("Request must have status PENDING");
			}
		}
		if (request.getStatus() == RequestStatus.REJECTED) {
			return rejectRequests(requests);
		}
		return confirmRequests(event, requests);
	}

	@Transactional(readOnly = true)
	public List<EventFullDto> searchAdmin(List<Long> users,
										  List<EventState> states,
										  List<Long> categories,
										  LocalDateTime rangeStart,
										  LocalDateTime rangeEnd,
										  int from,
										  int size) {
		validateRange(rangeStart, rangeEnd);
		Specification<Event> specification = Specification
				.where(EventSpecifications.initiatorIn(users))
				.and(EventSpecifications.stateIn(states))
				.and(EventSpecifications.categoryIn(categories))
				.and(EventSpecifications.eventDateAfterOrEqual(rangeStart))
				.and(EventSpecifications.eventDateBeforeOrEqual(rangeEnd));
		List<Event> events = eventRepository.findAll(specification,
				new OffsetPageRequest(from, size, Sort.by("id").ascending())).getContent();
		return toFullDtos(events);
	}

	@Transactional
	public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest request) {
		Event event = getEventEntity(eventId);
		updateCommonFields(event, request.getAnnotation(), request.getCategory(), request.getDescription(),
				request.getEventDate(), request.getLocation(), request.getPaid(), request.getParticipantLimit(),
				request.getRequestModeration(), request.getTitle(), 1);
		if (request.getStateAction() == AdminEventAction.PUBLISH_EVENT) {
			publish(event);
		} else if (request.getStateAction() == AdminEventAction.REJECT_EVENT) {
			reject(event);
		}
		return toFullDto(eventRepository.save(event));
	}

	@Transactional(readOnly = true)
	public List<EventShortDto> searchPublic(String text,
											List<Long> categories,
											Boolean paid,
											LocalDateTime rangeStart,
											LocalDateTime rangeEnd,
											Boolean onlyAvailable,
											EventSort sort,
											int from,
											int size,
											HttpServletRequest request) {
		statsService.saveHit(request);
		validateRange(rangeStart, rangeEnd);
		LocalDateTime start = rangeStart == null ? LocalDateTime.now() : rangeStart;
		Specification<Event> specification = Specification
				.where(EventSpecifications.stateIn(List.of(EventState.PUBLISHED)))
				.and(EventSpecifications.textContains(text))
				.and(EventSpecifications.categoryIn(categories))
				.and(EventSpecifications.paid(paid))
				.and(EventSpecifications.eventDateAfterOrEqual(start))
				.and(EventSpecifications.eventDateBeforeOrEqual(rangeEnd));
		List<Event> events = new ArrayList<>(eventRepository.findAll(specification));
		if (Boolean.TRUE.equals(onlyAvailable)) {
			Map<Long, Long> confirmed = getConfirmedRequests(events);
			events = events.stream()
					.filter(event -> isAvailable(event, confirmed.getOrDefault(event.getId(), 0L)))
					.toList();
		}
		Map<Long, Long> views = statsService.getViews(events.stream().map(Event::getId).toList());
		if (sort == EventSort.VIEWS) {
			events.sort(Comparator.comparing((Event event) -> views.getOrDefault(event.getId(), 0L)).reversed());
		} else {
			events.sort(Comparator.comparing(Event::getEventDate));
		}
		return toShortDtos(slice(events, from, size), views);
	}

	@Transactional(readOnly = true)
	public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
		statsService.saveHit(request);
		Event event = getEventEntity(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		return toFullDto(event);
	}

	@Transactional(readOnly = true)
	public Event getEventEntity(Long eventId) {
		return eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
	}

	@Transactional(readOnly = true)
	public List<EventShortDto> toShortDtos(List<Event> events) {
		return toShortDtos(events, statsService.getViews(events.stream().map(Event::getId).toList()));
	}

	private List<EventShortDto> toShortDtos(List<Event> events, Map<Long, Long> views) {
		Map<Long, Long> confirmed = getConfirmedRequests(events);
		return events.stream()
				.map(event -> EventMapper.toShortDto(event, confirmed.getOrDefault(event.getId(), 0L),
						views.getOrDefault(event.getId(), 0L)))
				.toList();
	}

	private List<EventFullDto> toFullDtos(List<Event> events) {
		Map<Long, Long> confirmed = getConfirmedRequests(events);
		Map<Long, Long> views = statsService.getViews(events.stream().map(Event::getId).toList());
		return events.stream()
				.map(event -> EventMapper.toFullDto(event, confirmed.getOrDefault(event.getId(), 0L),
						views.getOrDefault(event.getId(), 0L)))
				.toList();
	}

	private EventFullDto toFullDto(Event event) {
		long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
		long views = statsService.getViews(List.of(event.getId())).getOrDefault(event.getId(), 0L);
		return EventMapper.toFullDto(event, confirmed, views);
	}

	private Event getUserEventEntity(Long userId, Long eventId) {
		Event event = getEventEntity(eventId);
		if (!event.getInitiator().getId().equals(userId)) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		return event;
	}

	private Map<Long, Long> getConfirmedRequests(List<Event> events) {
		Map<Long, Long> counts = new HashMap<>();
		List<Long> eventIds = events.stream()
				.map(Event::getId)
				.toList();
		if (eventIds.isEmpty()) {
			return counts;
		}
		for (Object[] row : requestRepository.countByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED)) {
			counts.put((Long) row[0], (Long) row[1]);
		}
		return counts;
	}

	private void updateCommonFields(Event event,
									String annotation,
									Long categoryId,
									String description,
									LocalDateTime eventDate,
									ru.practicum.ewm.event.dto.LocationDto location,
									Boolean paid,
									Integer participantLimit,
									Boolean requestModeration,
									String title,
									int minHours) {
		if (annotation != null) {
			event.setAnnotation(annotation);
		}
		if (categoryId != null) {
			event.setCategory(categoryService.getById(categoryId));
		}
		if (description != null) {
			event.setDescription(description);
		}
		if (eventDate != null) {
			validateEventDate(eventDate, minHours);
			event.setEventDate(eventDate);
		}
		if (location != null) {
			event.setLocation(EventMapper.toLocation(location));
		}
		if (paid != null) {
			event.setPaid(paid);
		}
		if (participantLimit != null) {
			event.setParticipantLimit(participantLimit);
		}
		if (requestModeration != null) {
			event.setRequestModeration(requestModeration);
		}
		if (title != null) {
			event.setTitle(title);
		}
	}

	private EventRequestStatusUpdateResult rejectRequests(List<ParticipationRequest> requests) {
		EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
		for (ParticipationRequest request : requests) {
			request.setStatus(RequestStatus.REJECTED);
			result.getRejectedRequests().add(ParticipationRequestMapper.toDto(request));
		}
		requestRepository.saveAll(requests);
		return result;
	}

	private EventRequestStatusUpdateResult confirmRequests(Event event, List<ParticipationRequest> requests) {
		EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
		int limit = event.getParticipantLimit();
		if (limit == 0) {
			for (ParticipationRequest request : requests) {
				request.setStatus(RequestStatus.CONFIRMED);
				result.getConfirmedRequests().add(ParticipationRequestMapper.toDto(request));
			}
			requestRepository.saveAll(requests);
			return result;
		}
		long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
		if (confirmed >= limit) {
			throw new ConflictException("Participant limit has been reached");
		}
		for (ParticipationRequest request : requests) {
			if (confirmed < limit) {
				request.setStatus(RequestStatus.CONFIRMED);
				confirmed++;
				result.getConfirmedRequests().add(ParticipationRequestMapper.toDto(request));
			} else {
				request.setStatus(RequestStatus.REJECTED);
				result.getRejectedRequests().add(ParticipationRequestMapper.toDto(request));
			}
		}
		requestRepository.saveAll(requests);
		return result;
	}

	private void publish(Event event) {
		if (event.getState() != EventState.PENDING) {
			throw new ConflictException("Only pending events can be published");
		}
		if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
			throw new ConflictException("Event date must be at least one hour after publishing");
		}
		event.setState(EventState.PUBLISHED);
		event.setPublishedOn(LocalDateTime.now());
	}

	private void reject(Event event) {
		if (event.getState() == EventState.PUBLISHED) {
			throw new ConflictException("Published event cannot be rejected");
		}
		event.setState(EventState.CANCELED);
	}

	private boolean isAvailable(Event event, long confirmedRequests) {
		return event.getParticipantLimit() == 0 || confirmedRequests < event.getParticipantLimit();
	}

	private void validateEventDate(LocalDateTime eventDate, int minHours) {
		if (eventDate.isBefore(LocalDateTime.now().plusHours(minHours))) {
			throw new ValidationException("Event date must be at least " + minHours + " hours from now");
		}
	}

	private void validateRange(LocalDateTime start, LocalDateTime end) {
		if (start != null && end != null && start.isAfter(end)) {
			throw new ValidationException("Range start must be before range end");
		}
	}

	private List<Event> slice(List<Event> events, int from, int size) {
		if (from >= events.size()) {
			return List.of();
		}
		return events.subList(from, Math.min(from + size, events.size()));
	}
}
