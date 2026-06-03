package ru.practicum.ewm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;

@Configuration
public class StatsClientConfig {
	@Bean
	public StatsClient statsClient(@Value("${stats.server.url}") String statsServerUrl, ObjectMapper objectMapper) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(objectMapper));
		return new StatsClient(statsServerUrl, restTemplate);
	}
}
