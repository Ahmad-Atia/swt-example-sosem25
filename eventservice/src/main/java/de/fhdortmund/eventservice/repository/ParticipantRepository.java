package de.fhdortmund.eventservice.repository;

import de.fhdortmund.eventservice.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    List<Participant> findByEventId(UUID eventId);

    List<Participant> findByUserId(UUID userId);

    Optional<Participant> findByEventIdAndUserId(UUID eventId, UUID userId);

    void deleteByEventIdAndUserId(UUID eventId, UUID userId);
}
