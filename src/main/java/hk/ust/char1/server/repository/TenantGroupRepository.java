package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantGroupRepository extends JpaRepository<TenantGroup, Long> {
    /**
     * Retrieves the {@link TenantGroup Tenant Groups} that contains a certain {@link Tenant}.
     * @param tenant The {@link Tenant} to be queried into.
     * @return The {@link TenantGroup Tenant Groups} that contains the {@link Tenant}.
     */
    List<TenantGroup> findAllByTenantsContaining(Tenant tenant);

    /**
     * Retrieves the {@link TenantGroup Tenant Group} by its unique name.
     * @param name The unique name that acts as an external identifier of the {@link TenantGroup Tenant Group}.
     * @return The {@link TenantGroup Tenant Group} with the corresponding name.
     */
    TenantGroup findTenantGroupByGroupName(String name);

    /**
     * Deletes the {@link TenantGroup Tenant Group} by its unique name.
     * @param name The unique name that acts as an external identifier of the {@link TenantGroup Tenant Group}.
     */
    void deleteTenantGroupByGroupName(String name);

    /**
     * Checks whether the {@link TenantGroup Tenant Group} exists by its unique name.
     * @param name The group name as the parameter.
     * @return <code>true</code> if there exists a {@link TenantGroup} by the given name, <code>false</code> otherwise.
     */
    boolean existsTenantGroupByGroupName(String name);
}
