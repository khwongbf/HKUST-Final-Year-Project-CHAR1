package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.IndividualTenantApartmentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IndividualTenantApartmentPreferenceRepository extends JpaRepository<IndividualTenantApartmentPreference, Long> {
    List<IndividualTenantApartmentPreference> findAllByPreferredMonthlyRentGreaterThanEqual(BigDecimal minimumRent);

    List<IndividualTenantApartmentPreference> findAllByTenant_TenantRatingGreaterThanEqual(BigDecimal minimumRating);

    List<IndividualTenantApartmentPreference> findAllByPreferredSizeLessThanEqual(BigDecimal maximumSize);

    List<IndividualTenantApartmentPreference> findAllByTenant_Username(String username);

    boolean existsIndividualTenantApartmentPreferenceByTitle(String title);

    IndividualTenantApartmentPreference findIndividualTenantApartmentPreferenceByTitle(String title);

    void deleteIndividualTenantApartmentPreferenceByTitle(String title);
}
