package hk.ust.char1.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class TenantGroup implements Serializable {
    // auto-generate
    /**
     * The internal ID for the database to handle.
     */
    @Id
    @GeneratedValue
    private long groupID;

    /**
     * The unique name of the group.
     * <p>
     *     This field is the unique name for the group that is assigned by the group owner.
     *     This field acts as an external identifier for users to identify the group.
     * </p>
     */
    @Column(name = "GROUP_NAME", nullable = false, unique = true)
    private String groupName;

    /**
     * The {@link Tenant Tenants} that the group contains.
     */
    // OneToMany
    @OneToMany(targetEntity = Tenant.class, cascade = CascadeType.REMOVE, mappedBy = "inTenantGroup")
    private List<Tenant> tenants;

    @OneToOne(targetEntity = Tenant.class, cascade = CascadeType.REMOVE, mappedBy = "ownerGroup", optional = false)
    private Tenant groupOwner;


    public TenantGroup() {
    }

    public TenantGroup(String groupName, List<Tenant> tenants) {
        this.groupName = groupName;
        this.tenants = tenants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantGroup that = (TenantGroup) o;
        return Objects.equals(groupName, that.groupName) &&
                Objects.equals(tenants, that.tenants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, tenants);
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public Tenant getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(Tenant groupOwner) {
        this.groupOwner = groupOwner;
    }
}
