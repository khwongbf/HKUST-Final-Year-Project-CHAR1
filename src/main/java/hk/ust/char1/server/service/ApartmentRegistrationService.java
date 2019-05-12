package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.ApartmentDTO;
import hk.ust.char1.server.model.*;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.ApartmentRepository;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ApartmentRegistrationService {
    private final ApartmentOwnerRepository apartmentOwnerRepository;

    private final ApartmentRepository apartmentRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public ApartmentRegistrationService(ApartmentOwnerRepository apartmentOwnerRepository, ApartmentRepository apartmentRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.apartmentOwnerRepository = apartmentOwnerRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Registers a new {@link Apartment}.
     * <p>
     *     This method adds the new {@link Apartment} to the database.
     * </p>
     * <p>
     *     If the given username is not found, this method returns a flag of -1.
     *     If the given username is not an {@link ApartmentOwner} yet, it should promote the {@link User user} status/role to {@link ApartmentOwner} by the appropriate method.
     * </p>
     * @param username The username of the owner of the soon-to-be-registered {@link Apartment}.
     * @param apartmentDTO The DTO of the apartment that is to be mapped to a {@link Apartment}, which is to-be-stored to the database.
     * @return <code>true</code> if the apartment is successfully added, <code>false</code> otherwise.
     */
    @Transactional
    public boolean addNewApartment(String username, ApartmentDTO apartmentDTO){
        if (!userRepository.existsUserByUsername(username) || apartmentDTO == null|| apartmentRepository.existsByUniqueTag(apartmentDTO.getUniqueTag())){
            return false;
        } else{
            if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username) ){
                boolean flag = registerNewOwner(username);
                if (!flag){
                    return false;
                }
            }
            // Create a new apartment
            Apartment apartment = new Apartment();
            apartment.setFacilities(apartmentDTO.getFacilities()
                    .parallelStream()
                    .map(Facility::new)
                    .collect(Collectors.toList()));

            apartment.setApartmentOwner(apartmentOwnerRepository.findApartmentOwnerByUsername(username));

            apartment.setGeolocation(new Geolocation(apartmentDTO.getLatitude(), apartmentDTO.getLongitude()));
            apartment.setUniqueTag(apartmentDTO.getUniqueTag());
            apartment.setAddress(apartmentDTO.getAddress());
            apartment.setPhoto(apartmentDTO.getPhoto());
            apartment.setSize(apartmentDTO.getSize());
            apartment.setPetsAllowed(apartmentDTO.isPetsAllowed());

            apartmentRepository.saveAndFlush(apartment);

            return true;
        }
    }

    /**
     * Promotes the {@link User user} to the {@link ApartmentOwner} status.
     * @param username The username that the user need to be promoted to {@link ApartmentOwner}.
     * @return <code>true</code> if the registration is successful , <code>false</code> otherwise.
     */
    @Transactional
    public boolean registerNewOwner(String username){
        if (!userRepository.existsUserByUsername(username)){
            return false;
        } else{

                User user =  userRepository.findUserByUsername(username);
                // Clone the user to the apartment owner
                ApartmentOwner apartmentOwner = new ApartmentOwner(user);

                apartmentOwner.setOwnedApartments(new ArrayList<>());
                if (!roleRepository.existsRoleByRole("LANDLORD")){
                    Role role = new Role();
                    role.setRole("LANDLORD");
                    roleRepository.saveAndFlush(role);
                }

                apartmentOwner.getRole().clear();
                apartmentOwner.getRole().add(roleRepository.findRoleByRole("USER"));
                apartmentOwner.getRole().add(roleRepository.findRoleByRole("LANDLORD"));

                userRepository.deleteUserByUsername(username);
                userRepository.flush();
                apartmentOwnerRepository.saveAndFlush(apartmentOwner);


        }
        return true;
    }

    /**
     * Updates the apartment details by the given address.
     * <p>
     *     This method first checks whether there exists an {@link Apartment} by the given address.
     *     If there is no such {@link Apartment} exists, return false.
     * </p>
     * <p>
     *     Then, the method checks whether the {@link Apartment Apartment's} {@link ApartmentOwner Owner} has the same username as given.
     *     If not, return false.
     * </p>
     * <p>
     *     Lastly, update the {@link Apartment} information <b>except</b> the <b>address</b>, and store it into the database.
     * </p>
     * @param username The username of the owner.
     * @param tag The exact tag of the apartment.
     * @param apartmentDTO The new details of the apartment, with the address not parsed.
     * @return <code>true</code> if the apartment details have been successfully changed, <code>false</code> otherwise.
     * @see ApartmentDTO
     * @see ApartmentOwnerRepository#findApartmentOwnerByUsername(String)
     * @see ApartmentRepository#findApartmentByUniqueTag(String)
     * @see ApartmentRepository#saveAndFlush(Object)
     */
    @Transactional
    public boolean updateApartment (String username, String tag, ApartmentDTO apartmentDTO){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username) || !apartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else{
            Apartment apartment = apartmentRepository.findApartmentByUniqueTag(tag);
            if (!apartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }

            apartment.setFacilities(apartmentDTO.getFacilities()
                    .stream()
                    .map(Facility::new)
                    .collect(Collectors.toList()));
            apartment.setGeolocation(new Geolocation(apartmentDTO.getLatitude(), apartmentDTO.getLongitude()));
            apartment.setPetsAllowed(apartmentDTO.isPetsAllowed());
            apartment.setSize(apartmentDTO.getSize());
            apartmentRepository.saveAndFlush(apartment);
            return true;
        }
    }

    /**
     * Updates and stores the image of an {@link Apartment} as a byte array.
     * <p>
     *     If there exists an image already, simply overwrite the image.
     * </p>
     * <p>
     *     Return false if;
     *     <ul>
     *         <li>There is no {@link Apartment} that has the tag</li>
     *         <li>There is no image provided</li>
     *     </ul>
     * </p>
     * @param tag The exact tag of the {@link Apartment} as the external identifier.
     * @param image The image to be stored
     * @return <code>true</code> if the image is successfully written to the database, <code>false</code> otherwise.
     */
    @Transactional
    public boolean storeImage (String tag, byte[] image){
        if (!apartmentRepository.existsByUniqueTag(tag) || image == null){
            return false;
        }
        Apartment apartment = apartmentRepository.findApartmentByUniqueTag(tag);
        apartment.setPhoto(image);
        apartmentRepository.saveAndFlush(apartment);
        return true;
    }

    /**
     * Removes an {@link Apartment} by the given tag.
     * <p>
     *     Preliminary checks are:
     *     <ol>
     *         <li>There is an {@link ApartmentOwner owner} with the given username.</li>
     *         <li>There is an {@link Apartment apartment} with the given tag</li>
     *         <li>The {@link Apartment apartment} is owned by the corresponding {@link ApartmentOwner owner} with the given username.</li>
     *     </ol>
     * </p>
     * <p>Upon finishing the preliminary checks, the apartment should be deleted from the database, along with the corresponding {@link RentalApartment rental apartment} and the {@link SellableApartment sellable apartment}.</p>
     * @param username The username that owns the apartment.
     * @param tag The tag of the apartment that is to be deleted from the database.
     * @return <code>true</code> if the preliminary checks are passed, <code>false</code> otherwise.
     */
    @Transactional
    public boolean removeApartment (String username, String tag){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username) || !apartmentRepository.existsByUniqueTag(tag)){
            return false;
        }else{
            if (!apartmentRepository.findApartmentByUniqueTag(tag).getApartmentOwner().getUsername().equals(username)){
                return false;
            }else {
                apartmentRepository.deleteApartmentByUniqueTag(tag);
                apartmentRepository.flush();
                return true;
            }
        }
    }
}
