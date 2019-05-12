package hk.ust.char1.server.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ApartmentOwner extends User implements Serializable {

    // extends other things from user

    // OneToMany

    @OneToMany(targetEntity = Apartment.class, orphanRemoval = true)
    private List<Apartment> ownedApartments;

    public ApartmentOwner() {
        super();
    }

    public ApartmentOwner(@NotNull User user){
        super(user.getUsername(),user.getPassword(),user.getPhoneNumber(),user.getEmail());
        this.setRole(user.getRole());
        this.setActivated(user.isActivated());
    }

    public ApartmentOwner(String username, String password, @Pattern(regexp = "[\\d]{4}-[\\d]{4}") String phoneNumber, @Email String email, List<Apartment> ownedApartments) {
        super(username, password, phoneNumber, email);
        this.ownedApartments = ownedApartments;
    }

    public List<Apartment> getOwnedApartments() {
        return ownedApartments;
    }

    public void setOwnedApartments(List<Apartment> ownedApartments) {
        this.ownedApartments = ownedApartments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApartmentOwner that = (ApartmentOwner) o;
        return Objects.equals(ownedApartments, that.ownedApartments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownedApartments);
    }
}
