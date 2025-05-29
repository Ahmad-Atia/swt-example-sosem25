package de.fhdortmund.eventservice.config;

import de.fhdortmund.eventservice.model.*;
import de.fhdortmund.eventservice.repository.EventRepository;
import de.fhdortmund.eventservice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class TestDataConfig {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    // Feste UUIDs für einfachen Zugriff in Tests
    public static final UUID SPORT_EVENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID SOCIAL_EVENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID EDUCATIONAL_EVENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID CULTURAL_EVENT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final UUID BUSINESS_EVENT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Bean
    public CommandLineRunner loadTestData() {
        return args -> {
            System.out.println("Starte das Laden der Testdaten...");

            // Prüfe, ob bereits Events vorhanden sind
            if (eventRepository.count() > 0) {
                System.out.println("Testdaten bereits vorhanden, überspringe Initialisierung");
                System.out.println("Sie können den Event-Endpunkt testen mit: http://localhost:8081/api/events/" + SPORT_EVENT_ID);
                return;
            }

            // Erstelle einige Benutzer-IDs für Testdaten
            UUID user1Id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
            UUID user2Id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
            UUID user3Id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

            // Testdaten für Events
            List<Event> events = new ArrayList<>();

            // Event 1: Sportveranstaltung (vergangen)
            Event sportEvent = new Event();
            sportEvent.setId(SPORT_EVENT_ID); // Feste ID für Tests
            sportEvent.setTitle("Fußballturnier");
            sportEvent.setDescription("Ein freundliches Fußballturnier für alle Altersgruppen");
            sportEvent.setStartDateTime(LocalDateTime.now().minusDays(5));
            sportEvent.setEndDateTime(LocalDateTime.now().minusDays(5).plusHours(3));

            Location sportLocation = new Location();
            sportLocation.setLatitude(51.5074);
            sportLocation.setLongitude(-0.1278);
            sportLocation.setAddress("Sportzentrum");
            sportLocation.setCity("Dortmund");
            sportLocation.setCountry("Deutschland");
            sportLocation.setPostalCode("44135");
            sportEvent.setLocation(sportLocation);

            sportEvent.setOrganizerId(user1Id);
            sportEvent.setMaxParticipants(20);
            sportEvent.setCurrentParticipants(20);
            sportEvent.setCategory(EventCategory.SPORTS);
            sportEvent.setStatus(EventStatus.COMPLETED);
            sportEvent.setCreatedAt(LocalDateTime.now().minusDays(20));
            events.add(sportEvent);

            // Event 2: Soziales Event (aktiv)
            Event socialEvent = new Event();
            socialEvent.setId(SOCIAL_EVENT_ID); // Feste ID für Tests
            socialEvent.setTitle("Netzwerk-Treffen");
            socialEvent.setDescription("Treffen für IT-Profis zum Netzwerken und Austausch");
            socialEvent.setStartDateTime(LocalDateTime.now().plusDays(7));
            socialEvent.setEndDateTime(LocalDateTime.now().plusDays(7).plusHours(4));

            Location socialLocation = new Location();
            socialLocation.setLatitude(52.5200);
            socialLocation.setLongitude(13.4050);
            socialLocation.setAddress("TechHub");
            socialLocation.setCity("Berlin");
            socialLocation.setCountry("Deutschland");
            socialLocation.setPostalCode("10117");
            socialEvent.setLocation(socialLocation);

            socialEvent.setOrganizerId(user2Id);
            socialEvent.setMaxParticipants(50);
            socialEvent.setCurrentParticipants(30);
            socialEvent.setCategory(EventCategory.SOCIAL);
            socialEvent.setStatus(EventStatus.PUBLISHED);
            socialEvent.setCreatedAt(LocalDateTime.now().minusDays(10));
            events.add(socialEvent);

            // Event 3: Bildungsveranstaltung (aktiv)
            Event educationalEvent = new Event();
            educationalEvent.setId(EDUCATIONAL_EVENT_ID); // Feste ID für Tests
            educationalEvent.setTitle("Spring Boot Workshop");
            educationalEvent.setDescription("Einführung in die Entwicklung mit Spring Boot");
            educationalEvent.setStartDateTime(LocalDateTime.now().plusDays(14));
            educationalEvent.setEndDateTime(LocalDateTime.now().plusDays(14).plusHours(8));

            Location educationalLocation = new Location();
            educationalLocation.setLatitude(48.1351);
            educationalLocation.setLongitude(11.5820);
            educationalLocation.setAddress("Tech Campus");
            educationalLocation.setCity("München");
            educationalLocation.setCountry("Deutschland");
            educationalLocation.setPostalCode("80333");
            educationalEvent.setLocation(educationalLocation);

            educationalEvent.setOrganizerId(user1Id);
            educationalEvent.setMaxParticipants(30);
            educationalEvent.setCurrentParticipants(10);
            educationalEvent.setCategory(EventCategory.EDUCATIONAL);
            educationalEvent.setStatus(EventStatus.PUBLISHED);
            educationalEvent.setCreatedAt(LocalDateTime.now().minusDays(5));
            events.add(educationalEvent);

            // Event 4: Kulturveranstaltung (Entwurf)
            Event culturalEvent = new Event();
            culturalEvent.setId(CULTURAL_EVENT_ID); // Feste ID für Tests
            culturalEvent.setTitle("Kunstausstellung");
            culturalEvent.setDescription("Ausstellung lokaler Künstler mit verschiedenen Stilen");
            culturalEvent.setStartDateTime(LocalDateTime.now().plusDays(21));
            culturalEvent.setEndDateTime(LocalDateTime.now().plusDays(25));

            Location culturalLocation = new Location();
            culturalLocation.setLatitude(50.9375);
            culturalLocation.setLongitude(6.9603);
            culturalLocation.setAddress("Kunstgalerie");
            culturalLocation.setCity("Köln");
            culturalLocation.setCountry("Deutschland");
            culturalLocation.setPostalCode("50667");
            culturalEvent.setLocation(culturalLocation);

            culturalEvent.setOrganizerId(user3Id);
            culturalEvent.setMaxParticipants(100);
            culturalEvent.setCurrentParticipants(0);
            culturalEvent.setCategory(EventCategory.CULTURAL);
            culturalEvent.setStatus(EventStatus.DRAFT);
            culturalEvent.setCreatedAt(LocalDateTime.now().minusDays(2));
            events.add(culturalEvent);

            // Event 5: Geschäftsevent (aktiv)
            Event businessEvent = new Event();
            businessEvent.setId(BUSINESS_EVENT_ID); // Feste ID für Tests
            businessEvent.setTitle("Startup Pitch");
            businessEvent.setDescription("Präsentationen innovativer Startups für potenzielle Investoren");
            businessEvent.setStartDateTime(LocalDateTime.now().plusDays(10));
            businessEvent.setEndDateTime(LocalDateTime.now().plusDays(10).plusHours(6));

            Location businessLocation = new Location();
            businessLocation.setLatitude(53.5511);
            businessLocation.setLongitude(9.9937);
            businessLocation.setAddress("Business Center");
            businessLocation.setCity("Hamburg");
            businessLocation.setCountry("Deutschland");
            businessLocation.setPostalCode("20095");
            businessEvent.setLocation(businessLocation);

            businessEvent.setOrganizerId(user2Id);
            businessEvent.setMaxParticipants(40);
            businessEvent.setCurrentParticipants(20);
            businessEvent.setCategory(EventCategory.BUSINESS);
            businessEvent.setStatus(EventStatus.PUBLISHED);
            businessEvent.setCreatedAt(LocalDateTime.now().minusDays(15));
            events.add(businessEvent);

            // Speichere Events in der Datenbank
            List<Event> savedEvents = eventRepository.saveAll(events);
            System.out.println("Testdaten für Events wurden geladen: " + savedEvents.size() + " Events erstellt.");
            System.out.println("Sie können den Event-Endpunkt testen mit: http://localhost:8081/api/events/" + SPORT_EVENT_ID);

            // Testdaten für Teilnehmer
            List<Participant> participants = new ArrayList<>();

            // Für jedes Event Teilnehmer hinzufügen
            for (Event event : savedEvents) {
                // Organisator als Teilnehmer
                Participant organizer = new Participant();
                organizer.setEventId(event.getId());
                organizer.setUserId(event.getOrganizerId());
                organizer.setJoinedAt(event.getCreatedAt());
                organizer.setStatus(ParticipantStatus.CONFIRMED);
                organizer.setRole(ParticipantRole.ORGANIZER);
                participants.add(organizer);

                // Füge weitere Teilnehmer hinzu (nur für aktive Events)
                if (event.getStatus() == EventStatus.PUBLISHED || event.getStatus() == EventStatus.ONGOING) {
                    // User 1 nimmt an allen Events teil, außer an denen, die er organisiert
                    if (!event.getOrganizerId().equals(user1Id)) {
                        Participant participant1 = new Participant();
                        participant1.setEventId(event.getId());
                        participant1.setUserId(user1Id);
                        participant1.setJoinedAt(event.getCreatedAt().plusDays(1));
                        participant1.setStatus(ParticipantStatus.CONFIRMED);
                        participant1.setRole(ParticipantRole.ATTENDEE);
                        participants.add(participant1);
                    }

                    // User 2 nimmt an allen Events teil, außer an denen, die er organisiert
                    if (!event.getOrganizerId().equals(user2Id)) {
                        Participant participant2 = new Participant();
                        participant2.setEventId(event.getId());
                        participant2.setUserId(user2Id);
                        participant2.setJoinedAt(event.getCreatedAt().plusDays(2));
                        participant2.setStatus(ParticipantStatus.CONFIRMED);
                        participant2.setRole(ParticipantRole.ATTENDEE);
                        participants.add(participant2);
                    }

                    // User 3 nimmt an allen Events teil, außer an denen, die er organisiert
                    if (!event.getOrganizerId().equals(user3Id)) {
                        Participant participant3 = new Participant();
                        participant3.setEventId(event.getId());
                        participant3.setUserId(user3Id);
                        participant3.setJoinedAt(event.getCreatedAt().plusDays(3));
                        participant3.setStatus(ParticipantStatus.CONFIRMED);
                        participant3.setRole(event.getCategory() == EventCategory.EDUCATIONAL ? ParticipantRole.MODERATOR : ParticipantRole.ATTENDEE);
                        participants.add(participant3);
                    }
                }
            }

            // Speichere Teilnehmer in der Datenbank
            List<Participant> savedParticipants = participantRepository.saveAll(participants);
            System.out.println("Testdaten für Teilnehmer wurden geladen: " + savedParticipants.size() + " Teilnehmer erstellt.");
        };
    }
}
