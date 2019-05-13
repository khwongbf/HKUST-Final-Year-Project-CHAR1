package hk.ust.char1.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
public class Buyer extends User implements Serializable {

    @PositiveOrZero
    @DecimalMax("100.00")
    private BigDecimal buyerRating;

    @PositiveOrZero
    @Column(name = "NUMBER_OF_HOUSES_BOUGHT")
    private int numberBought;

    public Buyer() {
        super();
    }

    public Buyer(String username, String password, @Pattern(regexp = "[\\d]{4}[\\d]{4}") String phoneNumber, @Email String email, @PositiveOrZero @DecimalMax("100.00") BigDecimal buyerRating, int numberBought) {
        super(username, password, phoneNumber, email);
        this.buyerRating = buyerRating;
        this.numberBought = numberBought;
    }

    public Buyer(@NotNull User user){
        super(user.getUsername(),user.getPassword(),user.getPhoneNumber(),user.getEmail());
        this.setRole(user.getRole());
        this.setActivated(user.isActivated());
    }

    public BigDecimal getBuyerRating() {
        return buyerRating;
    }

    public void setBuyerRating(BigDecimal buyerRating) {
        this.buyerRating = buyerRating;
    }

    public int getNumberBought() {
        return numberBought;
    }

    public void setNumberBought(int numberBought) {
        this.numberBought = numberBought;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Buyer buyer = (Buyer) o;
        return numberBought == buyer.numberBought &&
                Objects.equals(buyerRating, buyer.buyerRating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), buyerRating, numberBought);
    }
}
