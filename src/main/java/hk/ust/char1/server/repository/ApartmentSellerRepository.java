package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.ApartmentSeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApartmentSellerRepository extends JpaRepository<ApartmentSeller,String> {
    /**
     * Retrieves the {@link ApartmentSeller Seller} with the given unique username
     * @param username The unique username of the {@link ApartmentSeller Seller}
     * @return The {@link ApartmentSeller Seller} that has the given username, or null if there is no such seller that has the given username.
     */
    ApartmentSeller findApartmentSellerByUsername(String username);

    /**
     * Retrieves the {@link ApartmentSeller Sellers} with a minimum rating threshold.
     * @param rating The lower bound of the rating of the search.
     * @return The {@link ApartmentSeller Sellers} that have at least the rating given.
     */
    List<ApartmentSeller> findAllBySellerRatingGreaterThanEqual(BigDecimal rating);

    boolean existsApartmentSellerByUsername(String username);
}
