package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {
	private static final Logger LOG = LoggerFactory.getLogger(StatsService.class);
	private static final String APP_NAME = "ewm-main-service";
	private static final LocalDateTime STATS_START = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

	private final StatsClient statsClient;

	public StatsService(StatsClient statsClient) {
		this.statsClient = statsClient;
	}

	public void saveHit(HttpServletRequest request) {
		EndpointHitDto hit = new EndpointHitDto(null, APP_NAME, request.getRequestURI(), getClientIp(request),
				LocalDateTime.now());
		try {
			statsClient.hit(hit);
		} catch (RuntimeException ignore) {
		}
	}

	public Map<Long, Long> getViews(Collection<Long> eventIds) {
		Map<Long, Long> views = new HashMap<>();
		if (eventIds == null || eventIds.isEmpty()) {
			return views;
		}
		List<String> uris = eventIds.stream()
				.map(id -> "/events/" + id)
				.toList();
		List<ViewStatsDto> stats;
		try {
			stats = statsClient.getStats(STATS_START, LocalDateTime.now().plusSeconds(1), uris, true);
		} catch (RuntimeException exception) {
			LOG.warn("Failed to get event views from stats service", exception);
			return views;
		}
		for (ViewStatsDto stat : stats) {
			Long eventId = parseEventId(stat.getUri());
			if (eventId != null) {
				views.put(eventId, stat.getHits());
			}
		}
		return views;
	}

	private String getClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank()) {
			return forwardedFor.split(",")[0].trim();
		}
		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isBlank()) {
			return realIp.trim();
		}
		return request.getRemoteAddr();
	}

	private Long parseEventId(String uri) {
		String prefix = "/events/";
		if (uri == null || !uri.startsWith(prefix)) {
			return null;
		}
		return Long.parseLong(uri.substring(prefix.length()));
	}
}
