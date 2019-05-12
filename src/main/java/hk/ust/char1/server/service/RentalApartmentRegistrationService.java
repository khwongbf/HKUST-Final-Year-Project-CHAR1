package hk.ust.char1.server.service;


import hk.ust.char1.server.dto.RentalDetailsDTO;
import hk.ust.char1.server.model.*;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.ApartmentRepository;
import hk.ust.char1.server.repository.RentalApartmentRepository;
import hk.ust.char1.server.repository.SellableApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.RentalApartment.RentalMode.*;

/**
 * Service that controls registration of the {@link RentalApartment}.
 */
@Service
public class RentalApartmentRegistrationService {
    private final ApartmentRepository apartmentRepository;

    private final RentalApartmentRepository rentalApartmentRepository;

    private final ApartmentOwnerRepository apartmentOwnerRepository;

    private final SellableApartmentRepository sellableApartmentRepository;

    @Autowired
    public RentalApartmentRegistrationService(ApartmentRepository apartmentRepository, RentalApartmentRepository rentalApartmentRepository, ApartmentOwnerRepository apartmentOwnerRepository, SellableApartmentRepository sellableApartmentRepository) {
        this.apartmentRepository = apartmentRepository;
        this.rentalApartmentRepository = rentalApartmentRepository;
        this.apartmentOwnerRepository = apartmentOwnerRepository;
        this.sellableApartmentRepository = sellableApartmentRepository;
    }

    /**
     * Registers the registered apartment to be on lease.
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>The apartment must be registered.</li>
     *         <li>The apartment must not be a {@link SellableApartment}</li>
     *         <li>The owner truly owns the apartment.</li>
     *         <li>The apartment must not be currently on lease.</li>
     *     </ol>
     *     If the above conditions are met, return <code>true</code>, return <code>false</code> otherwise.
     * </p>
     * @param username The username of the owner of the apartment.
     * @param tag The tag of the apartment.
     * @param rentalDetailsDTO The details regarding the lease.
     * @return <code>true</code> if operation successful, <code>false</code> otherwise.
     */
    @Transactional
    public boolean registerToLease(String username, String tag, RentalDetailsDTO rentalDetailsDTO){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username) || !apartmentRepository.existsByUniqueTag(tag)|| sellableApartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else{
            Apartment apartment = apartmentRepository.findApartmentByUniqueTag(tag);
            if (!apartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }else{

                RentalApartment rentalApartment = new RentalApartment(apartment);

                rentalApartment.setUniqueTag(rentalDetailsDTO.getUniqueTag());
                rentalApartment.setTitle(rentalDetailsDTO.getTitle());
                rentalApartment.setChildrenAllowed(rentalDetailsDTO.isChildrenAllowed());
                rentalApartment.setMonthlyRent(rentalDetailsDTO.getMonthlyRent());
                rentalApartment.setRentalMode(rentalDetailsDTO.isGroupOrNot()? HOME_SHARING: INDIVIDUAL);

                apartmentRepository.delete(apartment);
                apartmentRepository.flush();

                rentalApartmentRepository.saveAndFlush(rentalApartment);
                return true;
            }
        }
    }

    public List<RentalDetailsDTO> getSelfLeasingDetails(String username){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username)){
            return null;
        }else{
            if (apartmentOwnerRepository.findApartmentOwnerByUsername(username)
                    .getRole()
                    .stream()
                    .noneMatch(role1 -> role1.getRole().equals("LANDLORD"))
            ){
                return null;
            }
            var rentalApartments = rentalApartmentRepository.findAllByApartmentOwner_Username(username);
            return rentalApartments.stream()
                    .map(rentalApartment -> {
                        var instance = new RentalDetailsDTO();
                        instance.setUniqueTag(rentalApartment.getUniqueTag());
                        instance.setAddress(rentalApartment.getAddress());
                        instance.setChildrenAllowed(rentalApartment.isChildrenAllowed());
                        instance.setGroupOrNot(rentalApartment.getRentalMode()== HOME_SHARING);
                        instance.setMonthlyRent(rentalApartment.getMonthlyRent());
                        instance.setTitle(rentalApartment.getTitle());
                        instance.setPhoto(rentalApartment.getPhoto());
                        instance.setFacilities(rentalApartment.getFacilities()
                                .stream()
                                .map(Facility::getFacilityName)
                                .collect(Collectors.toList()));
                        instance.setLatitude(rentalApartment.getGeolocation().getLatitude());
                        instance.setLongitude(rentalApartment.getGeolocation().getLongitude());
                        instance.setPetsAllowed(rentalApartment.isPetsAllowed());
                        instance.setSize(rentalApartment.getSize());
                        return instance;
                    }).collect(Collectors.toList());

        }
    }

    /**
     * Updates the registered apartment to be on lease.
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>The apartment must be registered.</li>
     *         <li>The apartment must not be a {@link SellableApartment}</li>
     *         <li>The owner truly owns the apartment.</li>
     *         <li>The apartment must be currently on lease.</li>
     *     </ol>
     *     If the above conditions are met, return <code>true</code>, return <code>false</code> otherwise.
     * </p>
     * @param username The username of the owner of the apartment.
     * @param tag The tag of the apartment.
     * @param rentalDetailsDTO The details regarding the lease.
     * @return <code>true</code> if operation successful, <code>false</code> otherwise.
     */
    @Transactional
    public boolean updateLeasing(String username, String tag, RentalDetailsDTO rentalDetailsDTO){
        if (!rentalApartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else {
            RentalApartment rentalApartment = rentalApartmentRepository.findByUniqueTag(tag);
            if (!rentalApartment.getApartmentOwner().getUsername().equals(username)) {
                return false;
            }
            rentalApartment.setChildrenAllowed(rentalDetailsDTO.isChildrenAllowed());
            rentalApartment.setMonthlyRent(rentalDetailsDTO.getMonthlyRent());
            rentalApartment.setRentalMode(rentalDetailsDTO.isGroupOrNot()? HOME_SHARING: INDIVIDUAL);

            rentalApartmentRepository.saveAndFlush(rentalApartment);
            return true;
        }
    }

    /**
     * Removes the apartment from leasing.
     * <p>
     *     Check the following condition before deletion:
     *     <ol>
     *         <li>
     *             The {@link RentalApartment} of the given tag is currently on lease.
     *         </li>
     *         <li>
     *             The {@link RentalApartment} belongs to the {@link ApartmentOwner} with the given username.
     *         </li>
     *         <li> The username and tag exists in the {@link RentalApartment} and {@link ApartmentOwner} tables.</li>
     *     </ol>
     * </p>
     * @param username The username of the {@link ApartmentOwner}.
     * @param tag The tag of the {@link RentalApartment}.
     * @return <code>true</code> if the {@link RentalApartment} is successfully removed, <code>false</code> otherwise.
     */
    @Transactional
    public boolean removeFromLeasing(String username, String tag){
        if (!rentalApartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else{
            var rentalApartment = rentalApartmentRepository.findByUniqueTag(tag);
            if (!rentalApartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }

            rentalApartmentRepository.delete(rentalApartment);
            rentalApartmentRepository.flush();
            return true;
        }
    }
}
