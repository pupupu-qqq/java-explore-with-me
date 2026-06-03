package ru.practicum.stats.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final RestTemplate restTemplate;
	private final String statsServerUrl;

	public StatsClient(String statsServerUrl) {
		this(statsServerUrl, new RestTemplate());
	}

	public StatsClient(String statsServerUrl, RestTemplate restTemplate) {
		this.statsServerUrl = clearLastSlash(statsServerUrl);
		this.restTemplate = restTemplate;
	}

	public void hit(EndpointHitDto endpointHit) {
		restTemplate.postForEntity(statsServerUrl + "/hit", endpointHit, Void.class);
	}

	public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		String uri = buildStatsUri(start, end, uris, unique);
		ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {
				}
		);
		if (response.getBody() == null) {
			return List.of();
		}
		return response.getBody();
	}

	private String buildStatsUri(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(statsServerUrl + "/stats")
				.queryParam("start", start.format(FORMATTER))
				.queryParam("end", end.format(FORMATTER));
		if (uris != null && !uris.isEmpty()) {
			builder.queryParam("uris", uris.toArray());
		}
		if (unique != null) {
			builder.queryParam("unique", unique);
		}
		return builder
				.build()
				.encode()
				.toUriString();
	}

	private String clearLastSlash(String url) {
		if (url.endsWith("/")) {
			return url.substring(0, url.length() - 1);
		}
		return url;
	}
}
