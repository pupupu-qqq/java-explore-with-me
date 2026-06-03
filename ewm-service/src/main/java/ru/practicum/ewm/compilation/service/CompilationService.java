package ru.practicum.ewm.compilation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompilationService {
	private final CompilationRepository compilationRepository;
	private final EventRepository eventRepository;
	private final EventService eventService;

	public CompilationService(CompilationRepository compilationRepository,
							  EventRepository eventRepository,
							  EventService eventService) {
		this.compilationRepository = compilationRepository;
		this.eventRepository = eventRepository;
		this.eventService = eventService;
	}

	@Transactional
	public CompilationDto create(NewCompilationDto dto) {
		Compilation compilation = new Compilation();
		compilation.setTitle(dto.getTitle());
		compilation.setPinned(Boolean.TRUE.equals(dto.getPinned()));
		compilation.setEvents(getEvents(dto.getEvents()));
		return toDto(compilationRepository.save(compilation));
	}

	@Transactional
	public CompilationDto update(Long compId, UpdateCompilationRequest request) {
		Compilation compilation = getById(compId);
		if (request.getTitle() != null) {
			compilation.setTitle(request.getTitle());
		}
		if (request.getPinned() != null) {
			compilation.setPinned(request.getPinned());
		}
		if (request.getEvents() != null) {
			compilation.setEvents(getEvents(request.getEvents()));
		}
		return toDto(compilationRepository.save(compilation));
	}

	@Transactional
	public void delete(Long compId) {
		if (!compilationRepository.existsById(compId)) {
			throw new NotFoundException("Compilation with id=" + compId + " was not found");
		}
		compilationRepository.deleteById(compId);
	}

	@Transactional(readOnly = true)
	public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
		OffsetPageRequest pageRequest = new OffsetPageRequest(from, size);
		List<Compilation> compilations;
		if (pinned == null) {
			compilations = compilationRepository.findAll(pageRequest).getContent();
		} else {
			compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
		}
		return compilations.stream()
				.map(this::toDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public CompilationDto getCompilation(Long compId) {
		return toDto(getById(compId));
	}

	private Compilation getById(Long compId) {
		return compilationRepository.findById(compId)
				.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
	}

	private Set<Event> getEvents(Set<Long> eventIds) {
		if (eventIds == null || eventIds.isEmpty()) {
			return new LinkedHashSet<>();
		}
		List<Event> events = eventRepository.findAllByIdIn(eventIds);
		if (events.size() != eventIds.size()) {
			throw new NotFoundException("Event was not found");
		}
		return new LinkedHashSet<>(events);
	}

	private CompilationDto toDto(Compilation compilation) {
		List<Event> events = compilation.getEvents().stream().toList();
		return CompilationMapper.toDto(compilation, eventService.toShortDtos(events));
	}
}
