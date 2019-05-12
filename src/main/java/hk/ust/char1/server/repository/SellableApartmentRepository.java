package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.SellableApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SellableApartmentRepository extends JpaRepository<SellableApartment, String> {
    /**
     * Retrieves the {@link SellableApartment Sellable Apartments} that contains a certain address.
     * @param address The partial address to be searched.
     * @return The {@link SellableApartment Sellable Apartments} that contains a certain address, an empty {@link List} if no results are obtained.
     */
    List<SellableApartment> findAllByAddressContaining(String address);

    /**
     * Retrieves  the {@link SellableApartment Sellable Apartments} whose owner has a certain username.
     * @param username The username of the owner of the apartment.
     * @return The {@link SellableApartment Apartments} that are currently on sale and is owned by the specified apartment owner.
     */
    List<SellableApartment> findAllByApartmentOwner_Username(String username);

    /**
     * Retrieves the {@link SellableApartment Sellable Apartments} that have a price less than or equal to a certain threshold.
     * @param maxPrice The upper bound of the price.
     * @return The {@link SellableApartment Apartments} that are currently on sale with the price lower than or equal to the given upper bound.
     */
    List<SellableApartment> findAllByPriceLessThanEqual(BigDecimal maxPrice);

    /**
     * Retrieves the {@link SellableApartment Sellable Apartments} that have its size greater than or equal to a certain threshold.
     * @param minSize The lower bound of the size.
     * @return The {@link SellableApartment Apartments} that are currently on sale with the size greater than or equal to the given lower bound.
     */
    List<SellableApartment> findAllBySizeGreaterThanEqual(BigDecimal minSize);

    boolean existsByUniqueTag(String tag);

    void deleteByUniqueTag(String address);

    SellableApartment findByUniqueTag(String address);
}
