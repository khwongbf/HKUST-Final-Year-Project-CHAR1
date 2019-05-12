package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, String> {
    List<Apartment> findAllByAddressContaining(String address);

    List<Apartment> findAllByApartmentOwner_Username(String username);

    List<Apartment> findApartmentsBySizeGreaterThanEqual(BigDecimal size);

    Apartment findApartmentByUniqueTag(String tag);

    boolean existsByUniqueTag(String tag);

    void deleteApartmentByUniqueTag(String tag);
}
