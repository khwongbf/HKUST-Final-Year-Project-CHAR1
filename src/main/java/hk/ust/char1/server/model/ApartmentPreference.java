package hk.ust.char1.server.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@MappedSuperclass
public abstract class ApartmentPreference implements Serializable {

    // integer digits = 5, fraction digits = 2
    @Digits(integer = 5, fraction = 2)
    private BigDecimal preferredSize;

    @Column(name = "APARTMENT_PREFERENCE_TITLE",unique = true)
    private String title;

    @Valid
    @Embedded
    private Geolocation preferredGeolocation;

    private boolean petsAllowed;

    public ApartmentPreference() {
    }

    public ApartmentPreference(@Digits(integer = 5, fraction = 2) BigDecimal preferredSize, @NotNull String title, @Valid Geolocation preferredGeolocation, boolean petsAllowed) {
        this.preferredSize = preferredSize;
        this.preferredGeolocation = preferredGeolocation;
        this.petsAllowed = petsAllowed;
    }

    public BigDecimal getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(BigDecimal preferredSize) {
        this.preferredSize = preferredSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Geolocation getPreferredGeolocation() {
        return preferredGeolocation;
    }

    public void setPreferredGeolocation(Geolocation preferredGeolocation) {
        this.preferredGeolocation = preferredGeolocation;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApartmentPreference that = (ApartmentPreference) o;
        return petsAllowed == that.petsAllowed &&
                Objects.equals(preferredSize, that.preferredSize) &&
                Objects.equals(preferredGeolocation, that.preferredGeolocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preferredSize, preferredGeolocation, petsAllowed);
    }
}
