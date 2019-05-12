package hk.ust.char1.server.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@MappedSuperclass
public abstract class TenantApartmentPreference extends ApartmentPreference implements Serializable {
    // ID, auto-gen
    @Id
    @GeneratedValue
    @Column(name = "T_APARTMENT_PREF_ID")
    private long tenantApartmentPreferenceID;

    // int. part = 6, frac. part = 2
    // Assert Max = 100,000.00

    @Digits(integer = 6, fraction = 2)
    private BigDecimal preferredMonthlyRent;

    private boolean childrenAllowed;

    public TenantApartmentPreference() {
        super();
    }

    public TenantApartmentPreference(@Digits(integer = 5, fraction = 2) BigDecimal preferredSize, String title, @Valid Geolocation preferredGeolocation, boolean petsAllowed, @Digits(integer = 6, fraction = 2) BigDecimal preferredMonthlyRent) {
        super(preferredSize, title, preferredGeolocation, petsAllowed);
        this.preferredMonthlyRent = preferredMonthlyRent;
    }

    public long getTenantApartmentPreferenceID() {
        return tenantApartmentPreferenceID;
    }

    public void setTenantApartmentPreferenceID(long tenantApartmentPreferenceID) {
        this.tenantApartmentPreferenceID = tenantApartmentPreferenceID;
    }

    public BigDecimal getPreferredMonthlyRent() {
        return preferredMonthlyRent;
    }

    public void setPreferredMonthlyRent(BigDecimal preferredMonthlyRent) {
        this.preferredMonthlyRent = preferredMonthlyRent;
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TenantApartmentPreference that = (TenantApartmentPreference) o;
        return tenantApartmentPreferenceID == that.tenantApartmentPreferenceID &&
                childrenAllowed == that.childrenAllowed &&
                Objects.equals(preferredMonthlyRent, that.preferredMonthlyRent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenantApartmentPreferenceID, preferredMonthlyRent, childrenAllowed);
    }
}
