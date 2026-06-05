package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.time.LocalDateTime;

public class EventMapper {
	private EventMapper() {
	}

	public static Event toEntity(NewEventDto dto) {
		Event event = new Event();
		event.setAnnotation(dto.getAnnotation());
		event.setDescription(dto.getDescription());
		event.setEventDate(dto.getEventDate());
		event.setLocation(toLocation(dto.getLocation()));
		event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
		event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
		event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
		event.setTitle(dto.getTitle());
		event.setCreatedOn(LocalDateTime.now());
		event.setState(EventState.PENDING);
		return event;
	}

	public static EventFullDto toFullDto(Event event, long confirmedRequests, long views) {
		EventFullDto dto = new EventFullDto();
		dto.setAnnotation(event.getAnnotation());
		dto.setCategory(CategoryMapper.toDto(event.getCategory()));
		dto.setConfirmedRequests(confirmedRequests);
		dto.setCreatedOn(event.getCreatedOn());
		dto.setDescription(event.getDescription());
		dto.setEventDate(event.getEventDate());
		dto.setId(event.getId());
		dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
		dto.setLocation(toLocationDto(event.getLocation()));
		dto.setPaid(event.getPaid());
		dto.setParticipantLimit(event.getParticipantLimit());
		dto.setPublishedOn(event.getPublishedOn());
		dto.setRequestModeration(event.getRequestModeration());
		dto.setState(event.getState());
		dto.setTitle(event.getTitle());
		dto.setViews(views);
		return dto;
	}

	public static EventShortDto toShortDto(Event event, long confirmedRequests, long views) {
		EventShortDto dto = new EventShortDto();
		dto.setAnnotation(event.getAnnotation());
		dto.setCategory(CategoryMapper.toDto(event.getCategory()));
		dto.setConfirmedRequests(confirmedRequests);
		dto.setEventDate(event.getEventDate());
		dto.setId(event.getId());
		dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
		dto.setPaid(event.getPaid());
		dto.setTitle(event.getTitle());
		dto.setViews(views);
		return dto;
	}

	public static Location toLocation(LocationDto dto) {
		return new Location(dto.getLat(), dto.getLon());
	}

	public static LocationDto toLocationDto(Location location) {
		return new LocationDto(location.getLat(), location.getLon());
	}
}
