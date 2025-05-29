package de.fhdortmund.eventservice.repository;

import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.EventCategory;
import de.fhdortmund.eventservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByOrganizerId(UUID organizerId);

    List<Event> findByCategory(EventCategory category);

    @Query("SELECT e FROM Event e WHERE e.startDateTime >= :start AND e.endDateTime <= :end")
    List<Event> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE " +
           "function('earth_distance', " +
           "function('ll_to_earth', e.location.latitude, e.location.longitude), " +
           "function('ll_to_earth', :lat, :lon)) <= :radius")
    List<Event> searchByLocation(@Param("lat") Double latitude,
                                @Param("lon") Double longitude,
                                @Param("radius") Double radiusInMeters);

    // Alternative Implementierung für H2-Datenbank ohne PostGIS-Funktionen
    default List<Event> searchByLocation(Location location, Double radiusInKm) {
        List<Event> allEvents = findAll();
        return allEvents.stream()
                .filter(event -> {
                    if (event.getLocation() == null || !event.getLocation().isValid()) {
                        return false;
                    }
                    Double distance = event.getLocation().getDistance(location);
                    return distance != null && distance <= radiusInKm;
                })
                .toList();
    }
}
