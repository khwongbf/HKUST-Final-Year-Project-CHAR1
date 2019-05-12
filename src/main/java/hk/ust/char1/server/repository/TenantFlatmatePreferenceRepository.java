package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.TenantFlatmatePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface TenantFlatmatePreferenceRepository extends JpaRepository<TenantFlatmatePreference, Long> {
    List<TenantFlatmatePreference> findAllByGender(TenantFlatmatePreference.Gender gender);

    List<TenantFlatmatePreference> findAllByMinimumAgeLessThanEqualAndMaximumAgeGreaterThanEqual(int minimumAge, int maximumAge);

    List<TenantFlatmatePreference> findAllByMarriageStatus(TenantFlatmatePreference.MarriageStatus marriageStatus);

    boolean existsByTenant_Username(String username);

    TenantFlatmatePreference findByTenant_Username(String username);
}
