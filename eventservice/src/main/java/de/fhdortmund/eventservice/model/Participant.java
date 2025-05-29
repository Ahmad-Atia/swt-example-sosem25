package de.fhdortmund.eventservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID eventId;

    private UUID userId;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ParticipantStatus.REGISTERED;
        }
        if (role == null) {
            role = ParticipantRole.ATTENDEE;
        }
    }

    /**
     * Überprüft, ob der Teilnehmer aktiv ist (nicht abgesagt)
     * @return true wenn der Teilnehmer aktiv ist
     */
    public Boolean isActive() {
        return status == ParticipantStatus.REGISTERED ||
               status == ParticipantStatus.CONFIRMED ||
               status == ParticipantStatus.ATTENDED;
    }

    /**
     * Überprüft, ob der Teilnehmer ein Moderator ist
     * @return true wenn der Teilnehmer Moderator oder Organisator ist
     */
    public Boolean isModerator() {
        return role == ParticipantRole.MODERATOR || role == ParticipantRole.ORGANIZER;
    }
}
