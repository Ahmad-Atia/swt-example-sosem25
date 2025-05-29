package de.fhdortmund.eventservice.service;

import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.Participant;
import de.fhdortmund.eventservice.model.ParticipantRole;
import de.fhdortmund.eventservice.repository.EventRepository;
import de.fhdortmund.eventservice.repository.ParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    // In a real-world application, we would inject a NotificationService here
    // private final NotificationService notificationService;

    /**
     * Fügt einen Teilnehmer zu einem Event hinzu
     * @param eventId Event-ID
     * @param userId Benutzer-ID
     * @return Der erstellte Teilnehmer
     */
    @Transactional
    public Participant addParticipant(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event nicht gefunden"));

        // Prüfe, ob der Benutzer bereits am Event teilnimmt
        if (participantRepository.findByEventIdAndUserId(eventId, userId).isPresent()) {
            throw new IllegalStateException("Benutzer nimmt bereits an diesem Event teil");
        }

        // Prüfe, ob noch Plätze frei sind
        if (event.isFull()) {
            throw new IllegalStateException("Event ist bereits voll");
        }

        // Prüfe, ob das Event aktiv ist
        if (!event.isActive()) {
            throw new IllegalStateException("Event ist nicht aktiv");
        }

        // Erstelle einen neuen Teilnehmer
        Participant participant = new Participant();
        participant.setEventId(eventId);
        participant.setUserId(userId);

        // Bestimme die Rolle (Organisator ist auch ein Teilnehmer)
        if (event.getOrganizerId().equals(userId)) {
            participant.setRole(ParticipantRole.ORGANIZER);
        }

        // Erhöhe die Teilnehmerzahl
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);

        // Speichere und gib den Teilnehmer zurück
        Participant savedParticipant = participantRepository.save(participant);

        // Sende eine Benachrichtigung (in einer realen Anwendung)
        // notificationService.sendEventJoinNotification(event, userId);

        return savedParticipant;
    }

    /**
     * Entfernt einen Teilnehmer von einem Event
     * @param eventId Event-ID
     * @param userId Benutzer-ID
     */
    @Transactional
    public void removeParticipant(UUID eventId, UUID userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event nicht gefunden"));

        Participant participant = participantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Teilnehmer nicht gefunden"));

        // Prüfe, ob der Benutzer der Organisator ist
        if (participant.getRole() == ParticipantRole.ORGANIZER) {
            throw new IllegalStateException("Der Organisator kann nicht vom Event entfernt werden");
        }

        // Verringere die Teilnehmerzahl
        event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        eventRepository.save(event);

        // Lösche den Teilnehmer
        participantRepository.delete(participant);

        // Sende eine Benachrichtigung (in einer realen Anwendung)
        // notificationService.sendEventLeaveNotification(event, userId);
    }

    /**
     * Gibt alle Teilnehmer eines Events zurück
     * @param eventId Event-ID
     * @return Liste der Teilnehmer
     */
    public List<Participant> getParticipants(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event nicht gefunden");
        }

        return participantRepository.findByEventId(eventId);
    }

    /**
     * Aktualisiert die Rolle eines Teilnehmers
     * @param participantId Teilnehmer-ID
     * @param role Neue Rolle
     * @return Der aktualisierte Teilnehmer
     */
    @Transactional
    public Participant updateParticipantRole(UUID participantId, ParticipantRole role) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Teilnehmer nicht gefunden"));

        // Prüfe, ob die Rolle geändert wird
        if (participant.getRole() == role) {
            return participant;
        }

        // Prüfe, ob der Teilnehmer der Organisator ist (Organisator-Rolle kann nicht geändert werden)
        if (participant.getRole() == ParticipantRole.ORGANIZER) {
            throw new IllegalStateException("Die Rolle des Organisators kann nicht geändert werden");
        }

        participant.setRole(role);
        return participantRepository.save(participant);
    }
}
