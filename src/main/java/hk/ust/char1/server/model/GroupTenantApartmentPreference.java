package hk.ust.char1.server.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table
public class GroupTenantApartmentPreference extends TenantApartmentPreference implements Serializable {

    // ManyToOne
    @ManyToOne(targetEntity = TenantGroup.class)
    @JoinColumn(name = "groupID")
    private TenantGroup tenantGroup;

    public GroupTenantApartmentPreference() {
    }

    public GroupTenantApartmentPreference(@Digits(integer = 5, fraction = 2) BigDecimal preferredSize, @NotNull String title, @Valid Geolocation preferredGeolocation, boolean petsAllowed, @Digits(integer = 6, fraction = 2) BigDecimal preferredMonthlyRent, TenantGroup tenantGroup) {
        super(preferredSize, title, preferredGeolocation, petsAllowed, preferredMonthlyRent);
        this.tenantGroup = tenantGroup;
    }

    public TenantGroup getTenantGroup() {
        return tenantGroup;
    }

    public void setTenantGroup(TenantGroup tenantGroup) {
        this.tenantGroup = tenantGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroupTenantApartmentPreference that = (GroupTenantApartmentPreference) o;
        return Objects.equals(tenantGroup, that.tenantGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenantGroup);
    }
}
