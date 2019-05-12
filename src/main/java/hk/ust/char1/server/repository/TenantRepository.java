package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQuery;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant,String> {
    List<Tenant> findAllByTenantRatingGreaterThanEqual(BigDecimal minimumThreshold);

    List<Tenant> findAllByTenantRatingBetween(BigDecimal minimum, BigDecimal maximum);

    Tenant findTenantByUsername(String username);

    long countAllByTenantRatingGreaterThanEqual(BigDecimal minimum);

    boolean existsTenantByUsername(String username);

}
