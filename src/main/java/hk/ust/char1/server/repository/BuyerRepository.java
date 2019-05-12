package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, String> {
    /**
     * Retrieves the Buyer by the username
     * @param username username to be queried.
     * @return the {@link Buyer} object that has the username.
     */
    Buyer findBuyerByUsername(String username);

    /**
     * Retrieves all the {@link Buyer Buyers} that have at least a certain minimum rating.
     * @param rating the minimum rating as a threshold.
     * @return The list of {@link Buyer Buyers} that have rating greater than or equal to the threshold.
     */
    List<Buyer> findAllByBuyerRatingGreaterThanEqual(BigDecimal rating);

    /**
     * Retrieves all the {@link Buyer Buyers} whose rating is in between two certain thresholds.
     * @param min The lower bound threshold of the rating.
     * @param max The upper bound threshold of the rating.
     * @return The list of {@link Buyer Buyers} whose rating meets <b>both</b> the lower and upper bound of the rating thresholds.
     */
    List<Buyer> findAllByBuyerRatingBetween(BigDecimal min, BigDecimal max);

    /**
     * Checks whether a {@link Buyer} exists in the database with the given username.
     * @param username The username as a parameter.
     * @return <code>true</code> if a {@link Buyer} exists, <code>false</code> otherwise.
     */
    boolean existsBuyerByUsername(String username);
}
