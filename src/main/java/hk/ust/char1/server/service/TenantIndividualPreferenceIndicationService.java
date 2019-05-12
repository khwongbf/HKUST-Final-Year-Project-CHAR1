package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.RentalApartmentPreferenceDTO;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.IndividualTenantApartmentPreference;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.repository.IndividualTenantApartmentPreferenceRepository;
import hk.ust.char1.server.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A service that allows tenants to create, view, modify and delete their individual apartment preferences.
 * @author Wong Kwan Ho
 * @version 0.1.0
 */
@Service
public class TenantIndividualPreferenceIndicationService {

    private final TenantRepository tenantRepository;

    private final IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository;

    @Autowired
    public TenantIndividualPreferenceIndicationService(TenantRepository tenantRepository, IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository) {
        this.tenantRepository = tenantRepository;
        this.individualTenantApartmentPreferenceRepository = individualTenantApartmentPreferenceRepository;
    }

    /**
     * Adds a new {@link IndividualTenantApartmentPreference} for a {@link Tenant} with the given username.
     * @param username The username as a unique identifier to identify a {@link Tenant}.
     * @param rentalApartmentPreferenceDTO The DTO that is used to carry the information from the HTTP REST request.
     * @return <code>true</code> if the a preference is successfully added, <code>false</code> otherwise.
     */
    @Transactional
    public boolean addNewPreference(String username, RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO){
        if (!tenantRepository.existsTenantByUsername(username)){
            return false;
        }
        IndividualTenantApartmentPreference individualPreference = new IndividualTenantApartmentPreference();

        Tenant tenant = tenantRepository.findTenantByUsername(username);
        individualPreference.setPetsAllowed(rentalApartmentPreferenceDTO.isPetsAllowed());
        individualPreference.setPreferredGeolocation(new Geolocation(rentalApartmentPreferenceDTO.getPreferredGeolocation().getLatitude(), rentalApartmentPreferenceDTO.getPreferredGeolocation().getLongitude()));
        individualPreference.setPreferredSize(rentalApartmentPreferenceDTO.getPreferredSize());
        individualPreference.setPreferredMonthlyRent(rentalApartmentPreferenceDTO.getPreferredMonthlyRent());
        individualPreference.setTenant(tenant);
        individualPreference.setTitle(rentalApartmentPreferenceDTO.getTitle());
        individualPreference.setChildrenAllowed(rentalApartmentPreferenceDTO.isChildrenAllowed());
        individualTenantApartmentPreferenceRepository.saveAndFlush(individualPreference);
        return true;
    }

    /**
     * Finds all rental apartment preferences by the given tenant's username.
     * @param username The username of the tenant.
     * @return the list of DTOs containing the individual rental apartment preferences, <code>null</code> if the user does not exists.
     */
    @Transactional
    public List<RentalApartmentPreferenceDTO> getAllSelfPreferences(String username){
        if (!tenantRepository.existsTenantByUsername(username)){
            return null;
        }
        List<IndividualTenantApartmentPreference> preferences = individualTenantApartmentPreferenceRepository.findAllByTenant_Username(username);

        return preferences == null? null: preferences.parallelStream().map(individualTenantApartmentPreference -> {
            var dto = new RentalApartmentPreferenceDTO();
            dto.setPetsAllowed(individualTenantApartmentPreference.isPetsAllowed());
            dto.setPreferredMonthlyRent(individualTenantApartmentPreference.getPreferredMonthlyRent());
            dto.setPreferredGeolocation(new Geolocation(individualTenantApartmentPreference.getPreferredGeolocation().getLatitude(), individualTenantApartmentPreference.getPreferredGeolocation().getLongitude()));
            dto.setPreferredSize(individualTenantApartmentPreference.getPreferredSize());
            dto.setTitle(individualTenantApartmentPreference.getTitle());
            dto.setChildrenAllowed(individualTenantApartmentPreference.isChildrenAllowed());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Modifies the individual apartment preference of the tenant.
     * @param username The username of the tenant.
     * @param title The title of the preference.
     * @param rentalApartmentPreferenceDTO The DTO that specifies the new values of the preference
     * @return <code>true</code> if the preference is successfully modified, <code>false</code> otherwise.
     */
    @Transactional
    public boolean modifyPreference(String username, String title, RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO){
        if (!tenantRepository.existsTenantByUsername(username) || !individualTenantApartmentPreferenceRepository.existsIndividualTenantApartmentPreferenceByTitle(title)){
            return false;
        }

        Tenant tenant = tenantRepository.findTenantByUsername(username);
        IndividualTenantApartmentPreference preference = individualTenantApartmentPreferenceRepository.findIndividualTenantApartmentPreferenceByTitle(title);

        if (!preference.getTenant().equals(tenant)){
            return false;
        }

        preference.setPreferredMonthlyRent(rentalApartmentPreferenceDTO.getPreferredMonthlyRent());
        preference.setPreferredSize(rentalApartmentPreferenceDTO.getPreferredSize());
        preference.setPreferredGeolocation(new Geolocation(rentalApartmentPreferenceDTO.getPreferredGeolocation().getLatitude(), rentalApartmentPreferenceDTO.getPreferredGeolocation().getLongitude()));
        preference.setPetsAllowed(rentalApartmentPreferenceDTO.isPetsAllowed());
        preference.setChildrenAllowed(rentalApartmentPreferenceDTO.isChildrenAllowed());
        individualTenantApartmentPreferenceRepository.saveAndFlush(preference);
        return true;
    }

    /**
     * Deletes the apartment preference of an individual tenant from the database.
     * @param username The username of the tenant.
     * @param title The title of the apartment preference.
     * @return <code>true</code> if the preference is successfully deleted, <code>false</code> otherwise.
     */
    @Transactional
    public boolean deletePreference(String username, String title){
        if (!tenantRepository.existsTenantByUsername(username) || !individualTenantApartmentPreferenceRepository.existsIndividualTenantApartmentPreferenceByTitle(title)){
            return false;
        } else{
            Tenant tenant = tenantRepository.findTenantByUsername(username);
            IndividualTenantApartmentPreference preference = individualTenantApartmentPreferenceRepository.findIndividualTenantApartmentPreferenceByTitle(title);

            if (!preference.getTenant().equals(tenant)){
                return false;
            }

            individualTenantApartmentPreferenceRepository.deleteIndividualTenantApartmentPreferenceByTitle(title);
            individualTenantApartmentPreferenceRepository.flush();
            return true;
        }
    }
}
