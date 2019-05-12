package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.RentalApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RentalApartmentRepository extends JpaRepository<RentalApartment,String> {
    List<RentalApartment> findAllByAddressContaining(String address);

    List<RentalApartment> findAllBySizeGreaterThanEqual(BigDecimal maxSize);

    List<RentalApartment> findAllByMonthlyRentLessThanEqual(BigDecimal maximumRent);

    List<RentalApartment> findAllByApartmentOwner_Username(String username);

    List<RentalApartment> findAllByRentalMode(RentalApartment.RentalMode rentalMode);

    RentalApartment findByUniqueTag(String tag);

    boolean existsByUniqueTag(String tag);
}
