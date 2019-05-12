package hk.ust.char1.server.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Location obtained by Google Maps API.
 * <p>
 *     This embeddable object is used in {@link Apartment Apartment} and its subclasses, as well as preferred locations in {@link ApartmentPreference ApartmentPreference} and all of its subclasses.
 * </p>
 */
@Embeddable
public class Geolocation implements Serializable {
    // precision = 8, scale = 6
    @Column(precision = 8, scale = 6)
    private BigDecimal latitude;

    // precision = 9, scale = 6
    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    public Geolocation() {
    }

    public Geolocation(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geolocation that = (Geolocation) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
