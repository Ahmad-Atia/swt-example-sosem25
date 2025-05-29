package de.fhdortmund.eventservice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
    private String postalCode;

    /**
     * Berechnet die Distanz zwischen zwei Locations basierend auf der Haversine-Formel
     * @param other Die andere Location
     * @return Die Distanz in Kilometern
     */
    public Double getDistance(Location other) {
        if (other == null || latitude == null || longitude == null
                || other.latitude == null || other.longitude == null) {
            return null;
        }

        // Haversine-Formel zur Berechnung der Entfernung zwischen zwei Koordinaten
        final int R = 6371; // Erdradius in km

        double latDistance = Math.toRadians(other.latitude - latitude);
        double lonDistance = Math.toRadians(other.longitude - longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Überprüft, ob die Location gültige Koordinaten hat
     * @return true wenn Latitude und Longitude nicht null sind
     */
    public boolean isValid() {
        return latitude != null && longitude != null;
    }

    /**
     * Gibt eine formatierte Adresszeile zurück
     * @return Die formatierte Adresse
     */
    public String getDisplayAddress() {
        StringBuilder sb = new StringBuilder();

        if (address != null && !address.isEmpty()) {
            sb.append(address);
        }

        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }

        if (postalCode != null && !postalCode.isEmpty()) {
            if (sb.length() > 0 && !sb.toString().contains(postalCode)) {
                sb.append(" ").append(postalCode);
            }
        }

        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }

        return sb.toString();
    }
}
