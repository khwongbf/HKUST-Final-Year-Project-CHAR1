package hk.ust.char1.server.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class SellableApartment extends Apartment implements Serializable {
    // integer digits = 15, fractional digits = 2
    @Digits(integer = 15, fraction = 2)
    @Positive
    private BigDecimal price;

    public SellableApartment() {
    }

    public SellableApartment(@Size(max = 32) String uniqueTag, @Digits(integer = 5, fraction = 2) BigDecimal size, String address, byte[] photo, @Valid Geolocation geolocation, List<Facility> facilities, boolean petsAllowed, ApartmentOwner apartmentOwner, @Digits(integer = 15, fraction = 2) BigDecimal price) {
        super(uniqueTag, size, address, photo, geolocation, facilities, petsAllowed, apartmentOwner);
        this.price = price;
    }

    public SellableApartment(@NotNull Apartment apartment) {
        super(apartment.getUniqueTag(), apartment.getSize(), apartment.getAddress(), apartment.getPhoto(), apartment.getGeolocation(), apartment.getFacilities(), apartment.isPetsAllowed(), apartment.getApartmentOwner());
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SellableApartment that = (SellableApartment) o;
        return Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), price);
    }
}
