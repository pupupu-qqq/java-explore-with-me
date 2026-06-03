package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public class UpdateCompilationRequest {
	private Set<Long> events;
	private Boolean pinned;
	@Size(min = 1, max = 50)
	private String title;

	public Set<Long> getEvents() {
		return events;
	}

	public void setEvents(Set<Long> events) {
		this.events = events;
	}

	public Boolean getPinned() {
		return pinned;
	}

	public void setPinned(Boolean pinned) {
		this.pinned = pinned;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
