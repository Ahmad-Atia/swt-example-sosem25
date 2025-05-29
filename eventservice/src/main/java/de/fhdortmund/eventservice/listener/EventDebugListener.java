package de.fhdortmund.eventservice.listener;

import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDebugListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        printEventDebugInfo();
    }

    private void printEventDebugInfo() {
        try {
            List<Event> events = eventRepository.findAll();

            System.out.println("\n\n==================================================");
            System.out.println("EVENT DEBUG INFORMATION");
            System.out.println("==================================================");
            System.out.println("Total Events in Database: " + events.size());

            if (events.isEmpty()) {
                System.out.println("WARNUNG: Keine Events in der Datenbank gefunden!");
                System.out.println("Überprüfen Sie, ob die TestDataConfig korrekt ausgeführt wird.");
            } else {
                System.out.println("\nEvent-IDs für API-Tests:");
                System.out.println("--------------------------------------------------");

                for (Event event1 : events) {
                    System.out.println("Event: " + event1.getTitle());
                    System.out.println("ID: " + event1.getId());
                    System.out.println("Test-URL: http://localhost:8081/api/events/" + event1.getId());
                    System.out.println("--------------------------------------------------");
                }

                Event firstEvent = events.get(0);
                System.out.println("\nAPITEST-BEISPIELE:");
                System.out.println("1. Einzelnes Event abrufen:");
                System.out.println("   GET http://localhost:8081/api/events/" + firstEvent.getId());

                System.out.println("\n2. Teilnehmer eines Events abrufen:");
                System.out.println("   GET http://localhost:8081/api/events/" + firstEvent.getId() + "/participants");

                System.out.println("\n3. Alle Events abrufen:");
                System.out.println("   GET http://localhost:8081/api/events");

                System.out.println("\n4. Events nach Kategorie suchen:");
                System.out.println("   GET http://localhost:8081/api/events/search?category=" + firstEvent.getCategory());
            }

            System.out.println("==================================================\n\n");
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Event-Debug-Informationen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
