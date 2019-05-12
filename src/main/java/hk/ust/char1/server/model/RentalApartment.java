package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class RentalApartment extends Apartment implements Serializable {

    @Column(name = "RENT_TITLE", nullable = false, unique = true)
    private String title;

    // int. part = 6, fract. part = 2
    // Assert Max = 100,000.00
    @Digits(integer = 6,fraction = 2)
    @DecimalMax("100000.00")
    private BigDecimal monthlyRent;

    private boolean childrenAllowed;

    @Enumerated(EnumType.STRING)
    private RentalMode rentalMode;

    public RentalApartment(Apartment apartment) {
        super(apartment.getUniqueTag(), apartment.getSize(), apartment.getAddress(), apartment.getPhoto(), apartment.getGeolocation(), apartment.getFacilities(), apartment.isPetsAllowed(), apartment.getApartmentOwner());
    }

    public enum RentalMode {
        INDIVIDUAL, HOME_SHARING
    }

    public RentalApartment() {
        super();
    }

    public RentalApartment(String title, @Size(max = 32) String uniqueTag, @Digits(integer = 5, fraction = 2) BigDecimal size, String address, byte[] photo, @Valid Geolocation geolocation, List<Facility> facilities, boolean petsAllowed, ApartmentOwner apartmentOwner, @Digits(integer = 6, fraction = 2) @DecimalMax("100000.00") BigDecimal monthlyRent, boolean childrenAllowed, RentalMode rentalMode) {
        super(uniqueTag, size, address, photo, geolocation, facilities, petsAllowed, apartmentOwner);
        this.title = title;
        this.monthlyRent = monthlyRent;
        this.childrenAllowed = childrenAllowed;
        this.rentalMode = rentalMode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

    public RentalMode getRentalMode() {
        return rentalMode;
    }

    public void setRentalMode(RentalMode rentalMode) {
        this.rentalMode = rentalMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RentalApartment that = (RentalApartment) o;
        return childrenAllowed == that.childrenAllowed &&
                Objects.equals(monthlyRent, that.monthlyRent) &&
                rentalMode == that.rentalMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), monthlyRent, childrenAllowed, rentalMode);
    }
}
