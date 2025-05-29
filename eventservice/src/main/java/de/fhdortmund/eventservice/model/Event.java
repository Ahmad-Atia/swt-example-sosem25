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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Embedded
    private Location location;

    private UUID organizerId;

    private Integer maxParticipants;

    private Integer currentParticipants;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (currentParticipants == null) {
            currentParticipants = 0;
        }
        if (status == null) {
            status = EventStatus.DRAFT;
        }
    }

    /**
     * Überprüft, ob das Event aktiv ist (nicht abgesagt oder beendet)
     * @return true wenn das Event aktiv ist
     */
    public Boolean isActive() {
        return status == EventStatus.PUBLISHED || status == EventStatus.ONGOING;
    }

    /**
     * Überprüft, ob Teilnehmer dem Event beitreten können
     * @return true wenn Beitritt möglich ist
     */
    public Boolean canJoin() {
        return isActive() && !isFull() && LocalDateTime.now().isBefore(startDateTime);
    }

    /**
     * Überprüft, ob das Event voll ist
     * @return true wenn alle Plätze belegt sind
     */
    public Boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    /**
     * Gibt die Anzahl der verbleibenden Plätze zurück
     * @return Anzahl der freien Plätze oder null wenn keine Begrenzung
     */
    public Integer getRemainingSpots() {
        if (maxParticipants == null) {
            return null; // Unbegrenzte Teilnehmer
        }
        return maxParticipants - currentParticipants;
    }
}
