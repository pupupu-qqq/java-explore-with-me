package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

public class ParticipationRequestMapper {
	private ParticipationRequestMapper() {
	}

	public static ParticipationRequestDto toDto(ParticipationRequest request) {
		ParticipationRequestDto dto = new ParticipationRequestDto();
		dto.setCreated(request.getCreated());
		dto.setEvent(request.getEvent().getId());
		dto.setId(request.getId());
		dto.setRequester(request.getRequester().getId());
		dto.setStatus(request.getStatus());
		return dto;
	}
}
