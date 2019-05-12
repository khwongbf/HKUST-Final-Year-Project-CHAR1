package hk.ust.char1.server.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class ApartmentSeller  extends ApartmentOwner implements Serializable {
    // Max = ??
    @PositiveOrZero
    @DecimalMax("100.00")
    private BigDecimal sellerRating;

    private int numberSold;

    @OneToMany(targetEntity = SellableApartment.class, cascade = CascadeType.REMOVE)
    private List<SellableApartment> sellableApartments;

    public ApartmentSeller() {
        super();
    }

    public ApartmentSeller(String username, String password, @Pattern(regexp = "[\\d]{4}-[\\d]{4}") String phoneNumber, @Email String email, List<Apartment> ownedApartments, @PositiveOrZero @DecimalMax("100.00") BigDecimal sellerRating) {
        super(username, password, phoneNumber, email, ownedApartments);
        this.sellerRating = sellerRating;
    }

    public ApartmentSeller(ApartmentOwner owner) {
        super(owner.getUsername(), owner.getPassword(), owner.getPhoneNumber(), owner.getEmail(), owner.getOwnedApartments());
        this.sellerRating = new BigDecimal("0.00");
        this.numberSold = 0;
        this.sellableApartments = new ArrayList<>();
    }

    public int getNumberSold() {
        return numberSold;
    }

    public void setNumberSold(int numberSold) {
        this.numberSold = numberSold;
    }

    public BigDecimal getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(BigDecimal sellerRating) {
        this.sellerRating = sellerRating;
    }

    public List<SellableApartment> getSellableApartments() {
        return sellableApartments;
    }

    public void setSellableApartments(List<SellableApartment> sellableApartments) {
        this.sellableApartments = sellableApartments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApartmentSeller that = (ApartmentSeller) o;
        return Objects.equals(sellerRating, that.sellerRating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sellerRating);
    }
}
