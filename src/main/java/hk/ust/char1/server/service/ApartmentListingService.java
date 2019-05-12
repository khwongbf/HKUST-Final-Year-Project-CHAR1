package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.ListingDetailsDTO;
import hk.ust.char1.server.model.*;
import hk.ust.char1.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that lists apartments to sell.
 * @author Wong Kwan Ho
 */
@Service
public class ApartmentListingService {
    private final ApartmentSellerRepository apartmentSellerRepository;

    private final RentalApartmentRepository rentalApartmentRepository;

    private final ApartmentRepository apartmentRepository;

    private final SellableApartmentRepository sellableApartmentRepository;

    private final ApartmentOwnerRepository apartmentOwnerRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public ApartmentListingService(ApartmentSellerRepository apartmentSellerRepository, ApartmentRepository apartmentRepository, SellableApartmentRepository sellableApartmentRepository, ApartmentOwnerRepository apartmentOwnerRepository, RentalApartmentRepository rentalApartmentRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.apartmentSellerRepository = apartmentSellerRepository;
        this.apartmentRepository = apartmentRepository;
        this.sellableApartmentRepository = sellableApartmentRepository;
        this.apartmentOwnerRepository = apartmentOwnerRepository;
        this.rentalApartmentRepository = rentalApartmentRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Puts an apartment into listing.
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>The apartment must not be currently listed.</li>
     *         <li>The apartment must be owned by the owner</li>
     *         <li>The apartment must not be a {@link RentalApartment}</li>
     *     </ol>
     * </p>
     * <p>
     *     This method returns <code>false</code> if the above conditions are not met.
     * </p>
     * @param username The apartment owner who wishes to list the apartment.
     * @param address The address of the apartment. It must be not be listed as a {@link SellableApartment} before.
     * @param listingDetailsDTO The DTO containing the listing details.
     * @return whether the operation is successful.
     */
    @Transactional
    public boolean listNewApartment(String username, String address, ListingDetailsDTO listingDetailsDTO){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username) || !apartmentRepository.existsByUniqueTag(address) || rentalApartmentRepository.existsByUniqueTag(address)){
            return false;
        }else{
            if (!apartmentSellerRepository.existsApartmentSellerByUsername(username)){
                boolean result = registerAsNewSeller(username);
                if (!result){
                    return false;
                }
            }
            ApartmentSeller seller = apartmentSellerRepository.findApartmentSellerByUsername(username);
            Apartment apartment = apartmentRepository.findApartmentByUniqueTag(address);
            if (!apartment.getApartmentOwner().getUsername().equals(seller.getUsername())){
                return false;
            }

            SellableApartment listingApartment = new SellableApartment(apartment);
            listingApartment.setPrice(listingDetailsDTO.getPrice());

            sellableApartmentRepository.saveAndFlush(listingApartment);
        }
        return false;
    }

    /**
     * Registers the user as a new seller.
     * <p>
     *     Return false if the user is currently not an {@link ApartmentOwner}.
     * </p>
     * @param username The username of the {@link ApartmentOwner}
     * @return <code>true</code> if operation successful, <code>false</code> otherwise.
     */
    @Transactional
    public boolean registerAsNewSeller(String username){
        if (apartmentSellerRepository.existsApartmentSellerByUsername(username) || !apartmentOwnerRepository.existsApartmentOwnerByUsername(username)){
            return false;
        }else{
            ApartmentOwner owner = apartmentOwnerRepository.findApartmentOwnerByUsername(username);
            ApartmentSeller seller = new ApartmentSeller(owner);

            seller.setSellerRating(new BigDecimal("0.00"));

            if (!roleRepository.existsRoleByRole("SELLER")){
                Role role = new Role();
                role.setRole("SELLER");
                roleRepository.saveAndFlush(role);
            }

            Role role = roleRepository.findRoleByRole("SELLER");
            seller.getRole().add(role);
            apartmentOwnerRepository.deleteById(username);
            apartmentOwnerRepository.flush();
            apartmentSellerRepository.saveAndFlush(seller);

            return true;
        }
    }

    /**
     * Removes the {@link SellableApartment} from listing.
     * <p>
     *     The username must match the <b>current</b> owner of the {@link SellableApartment}.
     * </p>
     * <p>Return <code>false</code> if the above conditions are not met.</p>
     * @param username The username of the <b>current</b> owner of the {@link SellableApartment}
     * @param tag The tag of the concerned {@link SellableApartment}.
     * @return <code>true</code> if operation successful, false otherwise.
     */
    @Transactional
    public boolean removeListing(String username, String tag){
        if (!apartmentSellerRepository.existsApartmentSellerByUsername(username) || !sellableApartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else{
            SellableApartment apartment = sellableApartmentRepository.findByUniqueTag(tag);
            if (!apartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }
            sellableApartmentRepository.deleteByUniqueTag(tag);
            sellableApartmentRepository.flush();
            return true;
        }
    }


    /**
     * Updates the listing details with the given {@link ListingDetailsDTO}.
     * <p>
     *     Conditions to follow:
     *     <ol>
     *         <li>The username must match the owner of the {@link SellableApartment}.</li>
     *         <li>The username and address exists in the classes {@link ApartmentSeller} and {@link SellableApartment} respectively.</li>
     *     </ol>
     *     Return <code>false</code> if the above conditions are not met.
     * </p>
     * @param username The username of the owner of the {@link SellableApartment}
     * @param address The address of the concerned {@link SellableApartment}.
     * @return <code>true</code> if operation successful, false otherwise.
     */
    @Transactional
    public boolean updateListing(String username, String address, ListingDetailsDTO listingDetailsDTO){
        if (!apartmentSellerRepository.existsApartmentSellerByUsername(username) || !sellableApartmentRepository.existsByUniqueTag(address)){
            return false;
        }else{
            SellableApartment apartment = sellableApartmentRepository.findByUniqueTag(address);
            if (!apartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }
            apartment.setPrice(listingDetailsDTO.getPrice());

            sellableApartmentRepository.saveAndFlush(apartment);
            return true;
        }
    }

    /**
     * Retrieves the currently listing {@link SellableApartment} that is owned by the owner
     * @param username the username of the {@link ApartmentSeller}.
     * @return The list that contains all the {@link SellableApartment} for the given user, <code>null</code> if the user does not exist, an empty list if the current seller owns no listing apartments.
     */
    @Transactional
    public List<ListingDetailsDTO> getListings(String username){
        if (apartmentSellerRepository.findApartmentSellerByUsername(username) == null){
            return null;
        }else{
            List<SellableApartment> sellableApartments = sellableApartmentRepository.findAllByApartmentOwner_Username(username);
            ArrayList<ListingDetailsDTO> listingDetailsDTOs = new ArrayList<>();
            for (SellableApartment sellableApartment: sellableApartments) {
                ListingDetailsDTO listingDetailsDTO = new ListingDetailsDTO();
                listingDetailsDTO.setFacilities(sellableApartment.getFacilities().stream()
                                                    .map(Facility::getFacilityName)
                                                    .collect(Collectors.toList()));
                listingDetailsDTO.setPrice(sellableApartment.getPrice());
                listingDetailsDTO.setLatitude(sellableApartment.getGeolocation().getLatitude());
                listingDetailsDTO.setLongitude(sellableApartment.getGeolocation().getLongitude());
                listingDetailsDTO.setAddress(sellableApartment.getAddress());
                listingDetailsDTO.setPetsAllowed(sellableApartment.isPetsAllowed());
                listingDetailsDTO.setPhoto(sellableApartment.getPhoto());
                listingDetailsDTO.setSize(sellableApartment.getSize());

                listingDetailsDTOs.add(listingDetailsDTO);
            }
            return listingDetailsDTOs;
        }
    }
}
