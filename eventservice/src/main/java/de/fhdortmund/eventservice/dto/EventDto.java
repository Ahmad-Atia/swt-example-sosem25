package de.fhdortmund.eventservice.dto;

import de.fhdortmund.eventservice.model.EventCategory;
import de.fhdortmund.eventservice.model.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocationDto location;
    private UUID organizerId;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private EventCategory category;
    private EventStatus status;
    private LocalDateTime createdAt;
    private Integer remainingSpots;
    private Boolean canJoin;
}
