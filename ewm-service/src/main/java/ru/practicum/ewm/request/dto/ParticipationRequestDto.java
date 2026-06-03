package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
	private LocalDateTime created;
	private Long event;
	private Long id;
	private Long requester;
	private RequestStatus status;

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public Long getEvent() {
		return event;
	}

	public void setEvent(Long event) {
		this.event = event;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRequester() {
		return requester;
	}

	public void setRequester(Long requester) {
		this.requester = requester;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}
}
