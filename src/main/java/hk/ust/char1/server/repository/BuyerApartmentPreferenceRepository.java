package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.BuyerApartmentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BuyerApartmentPreferenceRepository extends JpaRepository<BuyerApartmentPreference, Long> {
    List<BuyerApartmentPreference> findAllByPriceBetween(BigDecimal minimumBid, BigDecimal maximumBid);

    List<BuyerApartmentPreference> findAllByBuyer_Username(String username);

    List<BuyerApartmentPreference> findAllByPriceGreaterThanEqual(BigDecimal minimumBig);

    boolean existsByTitle(String title);

    BuyerApartmentPreference findByTitle(String title);

    void deleteByTitle(String title);
}
