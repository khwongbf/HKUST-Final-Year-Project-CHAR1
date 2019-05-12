package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
public class BuyerApartmentPreference extends ApartmentPreference implements Serializable {
    //auto-generate, Id
    @Id
    @GeneratedValue
    private long preferenceID;

    // int. digits = 15, frac. digits = 2
    @Digits(integer = 15, fraction = 2)
    @PositiveOrZero
    private BigDecimal price;

    //ManyToOne
    @ManyToOne(targetEntity = Buyer.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    private Buyer buyer;

    public BuyerApartmentPreference() {
    }

    public BuyerApartmentPreference(@Digits(integer = 5, fraction = 2) BigDecimal preferredSize, @Valid Geolocation preferredGeolocation, boolean petsAllowed, @NotNull @NotEmpty String title, @Digits(integer = 15, fraction = 2) @PositiveOrZero BigDecimal price, Buyer buyer) {
        super(preferredSize, title, preferredGeolocation, petsAllowed);
        this.price = price;
        this.buyer = buyer;
    }

    public long getPreferenceID() {
        return preferenceID;
    }

    public void setPreferenceID(long preferenceID) {
        this.preferenceID = preferenceID;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BuyerApartmentPreference that = (BuyerApartmentPreference) o;
        return preferenceID == that.preferenceID &&
                Objects.equals(price, that.price) &&
                Objects.equals(buyer, that.buyer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), preferenceID, price, buyer);
    }
}
