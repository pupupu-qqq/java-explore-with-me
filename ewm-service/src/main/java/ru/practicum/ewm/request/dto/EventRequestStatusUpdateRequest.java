package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public class EventRequestStatusUpdateRequest {
	private List<Long> requestIds;
	private RequestStatus status;

	public List<Long> getRequestIds() {
		return requestIds;
	}

	public void setRequestIds(List<Long> requestIds) {
		this.requestIds = requestIds;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}
}
