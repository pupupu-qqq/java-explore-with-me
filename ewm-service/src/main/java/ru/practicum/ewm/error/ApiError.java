package ru.practicum.ewm.error;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
	private List<String> errors;
	private String message;
	private String reason;
	private String status;
	private LocalDateTime timestamp;

	public ApiError() {
	}

	public ApiError(List<String> errors, String message, String reason, String status, LocalDateTime timestamp) {
		this.errors = errors;
		this.message = message;
		this.reason = reason;
		this.status = status;
		this.timestamp = timestamp;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
