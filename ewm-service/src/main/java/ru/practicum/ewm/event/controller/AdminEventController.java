package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/events")
public class AdminEventController {
	private final EventService eventService;

	public AdminEventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> users,
										   @RequestParam(required = false) List<EventState> states,
										   @RequestParam(required = false) List<Long> categories,
										   @RequestParam(required = false) LocalDateTime rangeStart,
										   @RequestParam(required = false) LocalDateTime rangeEnd,
										   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
										   @RequestParam(defaultValue = "10") @Min(1) int size) {
		return eventService.searchAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEvent(@PathVariable Long eventId,
									@Valid @RequestBody UpdateEventAdminRequest request) {
		return eventService.updateAdminEvent(eventId, request);
	}
}
