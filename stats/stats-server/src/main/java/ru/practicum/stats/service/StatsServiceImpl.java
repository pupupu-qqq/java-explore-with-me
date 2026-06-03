package ru.practicum.stats.service;

import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.exception.IncorrectDateException;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final StatsRepository statsRepository;

	public StatsServiceImpl(StatsRepository statsRepository) {
		this.statsRepository = statsRepository;
	}

	@Override
	public void save(EndpointHitDto endpointHit) {
		statsRepository.save(EndpointHitMapper.toEntity(endpointHit));
	}

	@Override
	public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
		LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
		LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);
		if (startTime.isAfter(endTime)) {
			throw new IncorrectDateException("Start date must be before end date");
		}
		if (uris == null || uris.isEmpty()) {
			return getStatsWithoutUris(startTime, endTime, unique);
		}
		return getStatsByUris(startTime, endTime, uris, unique);
	}

	private List<ViewStatsDto> getStatsWithoutUris(LocalDateTime start, LocalDateTime end, boolean unique) {
		if (unique) {
			return statsRepository.findUniqueStats(start, end);
		}
		return statsRepository.findStats(start, end);
	}

	private List<ViewStatsDto> getStatsByUris(LocalDateTime start,
											  LocalDateTime end,
											  List<String> uris,
											  boolean unique) {
		if (unique) {
			return statsRepository.findUniqueStatsByUris(start, end, uris);
		}
		return statsRepository.findStatsByUris(start, end, uris);
	}
}
