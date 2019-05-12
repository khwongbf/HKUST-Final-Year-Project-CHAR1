package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.GroupTenantApartmentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GroupTenantApartmentPreferenceRepository extends JpaRepository<GroupTenantApartmentPreference, Long> {
    List<GroupTenantApartmentPreference> findAllByPreferredMonthlyRentGreaterThanEqual(BigDecimal minimumRent);

    List<GroupTenantApartmentPreference> findAllByPreferredSizeLessThanEqual(BigDecimal maximumSize);

    boolean existsByTitle(String title);

    List<GroupTenantApartmentPreference> findAllByTenantGroup_GroupName(String groupName);

    GroupTenantApartmentPreference findByTitle(String title);

}
