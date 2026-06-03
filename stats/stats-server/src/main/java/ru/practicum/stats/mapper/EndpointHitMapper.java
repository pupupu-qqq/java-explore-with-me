package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

public class EndpointHitMapper {
	private EndpointHitMapper() {
	}

	public static EndpointHit toEntity(EndpointHitDto endpointHit) {
		return new EndpointHit(
				null,
				endpointHit.getApp(),
				endpointHit.getUri(),
				endpointHit.getIp(),
				endpointHit.getTimestamp()
		);
	}
}
