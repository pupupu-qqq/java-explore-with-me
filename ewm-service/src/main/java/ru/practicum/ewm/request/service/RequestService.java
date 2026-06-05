package ru.practicum.ewm.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {
	private final ParticipationRequestRepository requestRepository;
	private final UserService userService;
	private final EventService eventService;

	public RequestService(ParticipationRequestRepository requestRepository,
						  UserService userService,
						  EventService eventService) {
		this.requestRepository = requestRepository;
		this.userService = userService;
		this.eventService = eventService;
	}

	@Transactional(readOnly = true)
	public List<ParticipationRequestDto> getUserRequests(Long userId) {
		userService.getById(userId);
		return requestRepository.findAllByRequesterId(userId).stream()
				.map(ParticipationRequestMapper::toDto)
				.toList();
	}

	@Transactional
	public ParticipationRequestDto create(Long userId, Long eventId) {
		User user = userService.getById(userId);
		Event event = eventService.getEventEntity(eventId);
		if (event.getInitiator().getId().equals(userId)) {
			throw new ConflictException("Initiator cannot request participation in own event");
		}
		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException("Cannot request participation in unpublished event");
		}
		if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
			throw new ConflictException("Participation request already exists");
		}
		long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
		if (event.getParticipantLimit() != 0 && confirmed >= event.getParticipantLimit()) {
			throw new ConflictException("Participant limit has been reached");
		}
		ParticipationRequest request = new ParticipationRequest();
		request.setCreated(LocalDateTime.now());
		request.setEvent(event);
		request.setRequester(user);
		if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
			request.setStatus(RequestStatus.CONFIRMED);
		} else {
			request.setStatus(RequestStatus.PENDING);
		}
		return ParticipationRequestMapper.toDto(requestRepository.save(request));
	}

	@Transactional
	public ParticipationRequestDto cancel(Long userId, Long requestId) {
		userService.getById(userId);
		ParticipationRequest request = requestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
		if (!request.getRequester().getId().equals(userId)) {
			throw new NotFoundException("Request with id=" + requestId + " was not found");
		}
		request.setStatus(RequestStatus.CANCELED);
		return ParticipationRequestMapper.toDto(requestRepository.save(request));
	}
}
