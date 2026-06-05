package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {
	private EventSpecifications() {
	}

	public static Specification<Event> initiatorIn(List<Long> users) {
		return (root, query, builder) -> users == null || users.isEmpty()
				? builder.conjunction()
				: root.get("initiator").get("id").in(users);
	}

	public static Specification<Event> stateIn(List<EventState> states) {
		return (root, query, builder) -> states == null || states.isEmpty()
				? builder.conjunction()
				: root.get("state").in(states);
	}

	public static Specification<Event> categoryIn(List<Long> categories) {
		return (root, query, builder) -> categories == null || categories.isEmpty()
				? builder.conjunction()
				: root.get("category").get("id").in(categories);
	}

	public static Specification<Event> eventDateAfterOrEqual(LocalDateTime start) {
		return (root, query, builder) -> start == null
				? builder.conjunction()
				: builder.greaterThanOrEqualTo(root.get("eventDate"), start);
	}

	public static Specification<Event> eventDateBeforeOrEqual(LocalDateTime end) {
		return (root, query, builder) -> end == null
				? builder.conjunction()
				: builder.lessThanOrEqualTo(root.get("eventDate"), end);
	}

	public static Specification<Event> textContains(String text) {
		return (root, query, builder) -> {
			if (text == null || text.isBlank()) {
				return builder.conjunction();
			}
			String pattern = "%" + text.toLowerCase() + "%";
			return builder.or(
					builder.like(builder.lower(root.get("annotation")), pattern),
					builder.like(builder.lower(root.get("description")), pattern)
			);
		};
	}

	public static Specification<Event> paid(Boolean paid) {
		return (root, query, builder) -> paid == null
				? builder.conjunction()
				: builder.equal(root.get("paid"), paid);
	}
}
