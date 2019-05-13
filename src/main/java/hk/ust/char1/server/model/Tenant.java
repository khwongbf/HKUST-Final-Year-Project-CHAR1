package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Tenant extends User{

    // Max = ??
    @DecimalMax("100.00")
    private BigDecimal tenantRating;

    @PositiveOrZero
    private int numberRented;

    // OneToMany
    @OneToMany(targetEntity = IndividualTenantApartmentPreference.class)
    private List<IndividualTenantApartmentPreference> individualTenantApartmentPreferences;

    // OneToOne, optional
    @OneToOne(targetEntity = TenantFlatmatePreference.class, mappedBy = "tenant")
    private TenantFlatmatePreference tenantFlatmatePreference;

    @ManyToOne(targetEntity = TenantGroup.class, cascade = CascadeType.REMOVE)
    private TenantGroup inTenantGroup;

    @OneToOne(targetEntity = TenantGroup.class, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private TenantGroup ownerGroup;

    public Tenant() {
    }

    public Tenant(String username, String password, @Pattern(regexp = "[\\d]{4}[\\d]{4}") String phoneNumber, @Email String email, @DecimalMax("100.00") BigDecimal tenantRating, int numberRented) {
        super(username, password, phoneNumber, email);
        this.tenantRating = tenantRating;
        this.numberRented = numberRented;
    }

    public Tenant(@NotNull User user){
        super(user.getUsername(), user.getPassword(), user.getPhoneNumber(), user.getEmail());
        this.setRole(user.getRole());
        this.setActivated(user.isActivated());
    }

    public BigDecimal getTenantRating() {
        return tenantRating;
    }

    public void setTenantRating(BigDecimal tenantRating) {
        this.tenantRating = tenantRating;
    }

    public int getNumberRented() {
        return numberRented;
    }

    public void setNumberRented(int numberRented) {
        this.numberRented = numberRented;
    }

    public List<IndividualTenantApartmentPreference> getIndividualTenantApartmentPreferences() {
        return individualTenantApartmentPreferences;
    }

    public void setIndividualTenantApartmentPreferences(List<IndividualTenantApartmentPreference> tenantApartmentPreferences) {
        this.individualTenantApartmentPreferences = tenantApartmentPreferences;
    }

    public TenantFlatmatePreference getTenantFlatmatePreference() {
        return tenantFlatmatePreference;
    }

    public void setTenantFlatmatePreference(TenantFlatmatePreference tenantFlatmatePreference) {
        this.tenantFlatmatePreference = tenantFlatmatePreference;
    }

    public TenantGroup getInTenantGroup() {
        return inTenantGroup;
    }

    public void setInTenantGroup(TenantGroup inTenantGroup) {
        this.inTenantGroup = inTenantGroup;
    }

    public TenantGroup getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(TenantGroup ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(tenantRating, tenant.tenantRating) &&
                Objects.equals(individualTenantApartmentPreferences, tenant.individualTenantApartmentPreferences) &&
                Objects.equals(tenantFlatmatePreference, tenant.tenantFlatmatePreference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenantRating, individualTenantApartmentPreferences, tenantFlatmatePreference);
    }
}
