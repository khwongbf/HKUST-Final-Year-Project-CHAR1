package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.RentalApartmentPreferenceDTO;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.GroupTenantApartmentPreference;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantGroup;
import hk.ust.char1.server.repository.GroupTenantApartmentPreferenceRepository;
import hk.ust.char1.server.repository.TenantGroupRepository;
import hk.ust.char1.server.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupPreferenceIndicationService {
    private final GroupTenantApartmentPreferenceRepository groupTenantApartmentPreferenceRepository;

    private final TenantGroupRepository tenantGroupRepository;

    private final TenantRepository tenantRepository;

    @Autowired
    public GroupPreferenceIndicationService(GroupTenantApartmentPreferenceRepository groupTenantApartmentPreferenceRepository, TenantGroupRepository tenantGroupRepository, TenantRepository tenantRepository) {
        this.groupTenantApartmentPreferenceRepository = groupTenantApartmentPreferenceRepository;
        this.tenantGroupRepository = tenantGroupRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public boolean addNewPreference(String ownerName, String groupName, RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO){
        if (!(tenantGroupRepository.existsTenantGroupByGroupName(groupName) && tenantRepository.existsTenantByUsername(ownerName))|| groupTenantApartmentPreferenceRepository.existsByTitle(rentalApartmentPreferenceDTO.getTitle())){
            return false;
        }else{
            TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
            if (!tenantGroup.getGroupOwner().getUsername().equals(ownerName)){
                return false;
            }

            GroupTenantApartmentPreference groupPreference = new GroupTenantApartmentPreference();
            groupPreference.setTenantGroup(tenantGroup);
            groupPreference.setTitle(rentalApartmentPreferenceDTO.getTitle());
            groupPreference.setPetsAllowed(rentalApartmentPreferenceDTO.isPetsAllowed());
            groupPreference.setPreferredGeolocation(rentalApartmentPreferenceDTO.getPreferredGeolocation());
            groupPreference.setPreferredMonthlyRent(rentalApartmentPreferenceDTO.getPreferredMonthlyRent());
            groupPreference.setPreferredSize(rentalApartmentPreferenceDTO.getPreferredSize());
            groupPreference.setChildrenAllowed(rentalApartmentPreferenceDTO.isChildrenAllowed());

            groupTenantApartmentPreferenceRepository.saveAndFlush(groupPreference);
            return true;
        }
    }

    @Transactional
    public List<RentalApartmentPreferenceDTO> listAllRelatedPreferences(String username, String groupName){
        if (!(tenantRepository.existsTenantByUsername(username) && tenantGroupRepository.existsTenantGroupByGroupName(groupName))){
            return null;
        }else{
            Tenant tenant = tenantRepository.findTenantByUsername(username);
            if (!tenant.getInTenantGroup().getGroupName().equals(groupName)){
                return null;
            }else{
                List<GroupTenantApartmentPreference> rentalApartmentPreferenceList = groupTenantApartmentPreferenceRepository.findAllByTenantGroup_GroupName(groupName);
                return rentalApartmentPreferenceList.parallelStream()
                        .map(groupTenantApartmentPreference -> {
                            RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO = new RentalApartmentPreferenceDTO();
                            rentalApartmentPreferenceDTO.setPreferredGeolocation(new Geolocation(groupTenantApartmentPreference.getPreferredGeolocation().getLatitude(), groupTenantApartmentPreference.getPreferredGeolocation().getLongitude()));
                            rentalApartmentPreferenceDTO.setPetsAllowed(groupTenantApartmentPreference.isPetsAllowed());
                            rentalApartmentPreferenceDTO.setPreferredMonthlyRent(groupTenantApartmentPreference.getPreferredMonthlyRent());
                            rentalApartmentPreferenceDTO.setPreferredSize(groupTenantApartmentPreference.getPreferredSize());
                            rentalApartmentPreferenceDTO.setTitle(groupTenantApartmentPreference.getTitle());
                            rentalApartmentPreferenceDTO.setChildrenAllowed(groupTenantApartmentPreference.isChildrenAllowed());
                            return rentalApartmentPreferenceDTO;
                        }).collect(Collectors.toList());
            }
        }
    }


    @Transactional
    public boolean modifyPreference (String ownerName, String groupName, String title, RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO){
        if (!(tenantRepository.existsTenantByUsername(ownerName) && tenantGroupRepository.existsTenantGroupByGroupName(groupName) && groupTenantApartmentPreferenceRepository.existsByTitle(title))){
            return false;
        }else{
            TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
            GroupTenantApartmentPreference groupPreference = groupTenantApartmentPreferenceRepository.findByTitle(title);
            if (!(tenantGroup.getGroupOwner().getUsername().equals(ownerName) && groupPreference.getTenantGroup().getGroupName().equals(groupName))){
                return false;
            }

            groupPreference.setPreferredGeolocation(rentalApartmentPreferenceDTO.getPreferredGeolocation());
            groupPreference.setPetsAllowed(rentalApartmentPreferenceDTO.isPetsAllowed());
            groupPreference.setPreferredSize(rentalApartmentPreferenceDTO.getPreferredSize());
            groupPreference.setPreferredMonthlyRent(rentalApartmentPreferenceDTO.getPreferredMonthlyRent());
            groupPreference.setChildrenAllowed(rentalApartmentPreferenceDTO.isChildrenAllowed());

            groupTenantApartmentPreferenceRepository.saveAndFlush(groupPreference);
            return true;
        }
    }

    @Transactional
    public boolean deletePreference (String ownerName, String groupName, String title){
        if (!(tenantRepository.existsTenantByUsername(ownerName) && tenantGroupRepository.existsTenantGroupByGroupName(groupName) && groupTenantApartmentPreferenceRepository.existsByTitle(title))){
            return false;
        }else{
            TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
            GroupTenantApartmentPreference groupPreference = groupTenantApartmentPreferenceRepository.findByTitle(title);
            if (!(tenantGroup.getGroupOwner().getUsername().equals(ownerName) && groupPreference.getTenantGroup().getGroupName().equals(groupName))){
                return false;
            }

            groupTenantApartmentPreferenceRepository.delete(groupPreference);
            groupTenantApartmentPreferenceRepository.flush();
            return true;
        }
    }
}
