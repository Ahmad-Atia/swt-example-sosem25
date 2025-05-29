package de.fhdortmund.eventservice.dto;

import de.fhdortmund.eventservice.model.ParticipantRole;
import de.fhdortmund.eventservice.model.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private LocalDateTime joinedAt;
    private ParticipantStatus status;
    private ParticipantRole role;
    private Boolean isActive;
    private Boolean isModerator;
}
