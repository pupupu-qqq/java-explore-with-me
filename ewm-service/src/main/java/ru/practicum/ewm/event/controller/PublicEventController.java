package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
public class PublicEventController {
	private final EventService eventService;

	public PublicEventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public List<EventShortDto> searchEvents(@RequestParam(required = false) String text,
											@RequestParam(required = false) List<Long> categories,
											@RequestParam(required = false) Boolean paid,
											@RequestParam(required = false) LocalDateTime rangeStart,
											@RequestParam(required = false) LocalDateTime rangeEnd,
											@RequestParam(defaultValue = "false") Boolean onlyAvailable,
											@RequestParam(required = false) EventSort sort,
											@RequestParam(defaultValue = "0") @PositiveOrZero int from,
											@RequestParam(defaultValue = "10") @Min(1) int size,
											HttpServletRequest request) {
		return eventService.searchPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
				request);
	}

	@GetMapping("/{id}")
	public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
		return eventService.getPublicEvent(id, request);
	}
}
