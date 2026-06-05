package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.Collection;
import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
	List<ParticipationRequest> findAllByRequesterId(Long requesterId);

	List<ParticipationRequest> findAllByEventId(Long eventId);

	List<ParticipationRequest> findAllByIdInAndEventId(Collection<Long> ids, Long eventId);

	long countByEventIdAndStatus(Long eventId, RequestStatus status);

	boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

	@Query("""
			select r.event.id, count(r.id)
			from ParticipationRequest r
			where r.event.id in :eventIds and r.status = :status
			group by r.event.id
			""")
	List<Object[]> countByEventIdsAndStatus(@Param("eventIds") Collection<Long> eventIds,
											@Param("status") RequestStatus status);
}
