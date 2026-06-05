package ru.practicum.ewm.comment.dto;

import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class CommentDto {
	private Long id;
	private String text;
	private UserShortDto author;
	private Long event;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserShortDto getAuthor() {
		return author;
	}

	public void setAuthor(UserShortDto author) {
		this.author = author;
	}

	public Long getEvent() {
		return event;
	}

	public void setEvent(Long event) {
		this.event = event;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
}
