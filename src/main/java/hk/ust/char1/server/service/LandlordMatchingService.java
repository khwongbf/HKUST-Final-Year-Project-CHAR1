package hk.ust.char1.server.service;

import hk.ust.char1.server.auxilary.ListOperator;
import hk.ust.char1.server.dto.RentalApartmentPreferenceDTO;
import hk.ust.char1.server.dto.RentalMatchingCriteriaDTO;
import hk.ust.char1.server.model.GroupTenantApartmentPreference;
import hk.ust.char1.server.model.IndividualTenantApartmentPreference;
import hk.ust.char1.server.model.RentalApartment.RentalMode;
import hk.ust.char1.server.model.TenantApartmentPreference;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.GroupTenantApartmentPreferenceRepository;
import hk.ust.char1.server.repository.IndividualTenantApartmentPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.RentalApartment.RentalMode.HOME_SHARING;
import static hk.ust.char1.server.model.RentalApartment.RentalMode.INDIVIDUAL;

@Service
public class LandlordMatchingService {
    private final ApartmentOwnerRepository apartmentOwnerRepository;

    private final IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository;

    private final GroupTenantApartmentPreferenceRepository groupTenantApartmentPreferenceRepository;

    private final ListOperator listOperator;

    @Autowired
    public LandlordMatchingService(ApartmentOwnerRepository apartmentOwnerRepository, IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository, GroupTenantApartmentPreferenceRepository groupTenantApartmentPreferenceRepository, ListOperator listOperator) {
        this.apartmentOwnerRepository = apartmentOwnerRepository;
        this.individualTenantApartmentPreferenceRepository = individualTenantApartmentPreferenceRepository;
        this.groupTenantApartmentPreferenceRepository = groupTenantApartmentPreferenceRepository;
        this.listOperator = listOperator;
    }

    @Transactional
    public Map<RentalApartmentPreferenceDTO, RentalMode> matchTenantPreference(String username, RentalMatchingCriteriaDTO rentalMatchingCriteriaDTO){
        if (!apartmentOwnerRepository.existsApartmentOwnerByUsername(username)){
            return null;
        }else{
            List<IndividualTenantApartmentPreference> listInd1 = individualTenantApartmentPreferenceRepository.findAllByPreferredMonthlyRentGreaterThanEqual(rentalMatchingCriteriaDTO.getMinPreferredRent());
            List<IndividualTenantApartmentPreference> listInd2 = individualTenantApartmentPreferenceRepository.findAllByPreferredSizeLessThanEqual(rentalMatchingCriteriaDTO.getMaxPreferredSize());
            List<GroupTenantApartmentPreference> listGrp1 = groupTenantApartmentPreferenceRepository.findAllByPreferredMonthlyRentGreaterThanEqual(rentalMatchingCriteriaDTO.getMinPreferredRent());
            List<GroupTenantApartmentPreference> listGrp2 = groupTenantApartmentPreferenceRepository.findAllByPreferredSizeLessThanEqual(rentalMatchingCriteriaDTO.getMaxPreferredSize());

            List<IndividualTenantApartmentPreference> listInd = listOperator.intersect(listInd1, listInd2);
            List<GroupTenantApartmentPreference> listGrp = listOperator.intersect(listGrp1, listGrp2);

            HashMap<RentalApartmentPreferenceDTO, RentalMode> map = listInd.stream().map(this::convertObjectToDTO).collect(Collectors.toMap(storedItem -> storedItem, storedItem -> INDIVIDUAL, (a, b) -> b, HashMap::new));

            listGrp.stream()
                    .map(this::convertObjectToDTO)
                    .forEach(storedItem -> map.put(storedItem, HOME_SHARING));

            return map;
        }
    }

    private <T extends TenantApartmentPreference> RentalApartmentPreferenceDTO convertObjectToDTO( T tenantApartmentPreference){
        RentalApartmentPreferenceDTO storedItem = new RentalApartmentPreferenceDTO();
        storedItem.setTitle(tenantApartmentPreference.getTitle());
        storedItem.setPreferredSize(tenantApartmentPreference.getPreferredSize());
        storedItem.setPreferredGeolocation(tenantApartmentPreference.getPreferredGeolocation());
        storedItem.setPetsAllowed(tenantApartmentPreference.isPetsAllowed());
        storedItem.setChildrenAllowed(tenantApartmentPreference.isChildrenAllowed());
        return storedItem;
    }
}
