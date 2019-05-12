package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ApartmentOwnerRepository extends JpaRepository<ApartmentOwner,String> {
    ApartmentOwner findApartmentOwnerByUsername(String username);

    boolean existsApartmentOwnerByUsername(String username);
}
