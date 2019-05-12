package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
public class IndividualTenantApartmentPreference extends TenantApartmentPreference implements Serializable {



    // ManyToOne
    @ManyToOne (targetEntity = Tenant.class, optional = false)
    @JoinColumn(name = "USERNAME")
    private Tenant tenant;

    public IndividualTenantApartmentPreference() {
    }

    public IndividualTenantApartmentPreference(@Digits(integer = 5, fraction = 2) BigDecimal preferredSize, String title, @Valid Geolocation preferredGeolocation, boolean petsAllowed, @Digits(integer = 6, fraction = 2) BigDecimal preferredMonthlyRent, Tenant tenant) {
        super(preferredSize, title, preferredGeolocation, petsAllowed, preferredMonthlyRent);
        this.tenant = tenant;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IndividualTenantApartmentPreference that = (IndividualTenantApartmentPreference) o;
        return Objects.equals(tenant, that.tenant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant);
    }
}
