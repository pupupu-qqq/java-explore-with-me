package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class EventFullDto {
	private String annotation;
	private CategoryDto category;
	private Long confirmedRequests;
	private LocalDateTime createdOn;
	private String description;
	private LocalDateTime eventDate;
	private Long id;
	private UserShortDto initiator;
	private LocationDto location;
	private Boolean paid;
	private Integer participantLimit;
	private LocalDateTime publishedOn;
	private Boolean requestModeration;
	private EventState state;
	private String title;
	private Long views;

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public CategoryDto getCategory() {
		return category;
	}

	public void setCategory(CategoryDto category) {
		this.category = category;
	}

	public Long getConfirmedRequests() {
		return confirmedRequests;
	}

	public void setConfirmedRequests(Long confirmedRequests) {
		this.confirmedRequests = confirmedRequests;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDateTime eventDate) {
		this.eventDate = eventDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserShortDto getInitiator() {
		return initiator;
	}

	public void setInitiator(UserShortDto initiator) {
		this.initiator = initiator;
	}

	public LocationDto getLocation() {
		return location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Integer getParticipantLimit() {
		return participantLimit;
	}

	public void setParticipantLimit(Integer participantLimit) {
		this.participantLimit = participantLimit;
	}

	public LocalDateTime getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(LocalDateTime publishedOn) {
		this.publishedOn = publishedOn;
	}

	public Boolean getRequestModeration() {
		return requestModeration;
	}

	public void setRequestModeration(Boolean requestModeration) {
		this.requestModeration = requestModeration;
	}

	public EventState getState() {
		return state;
	}

	public void setState(EventState state) {
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}
}
