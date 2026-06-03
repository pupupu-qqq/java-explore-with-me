package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
	List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

	boolean existsByCategoryId(Long categoryId);

	List<Event> findAllByIdIn(Collection<Long> ids);

	List<Event> findAllByIdIn(List<Long> ids);

	List<Event> findAllByStateAndIdIn(EventState state, List<Long> ids);

	List<Event> findAllByStateAndEventDateAfter(EventState state, LocalDateTime eventDate);
}
