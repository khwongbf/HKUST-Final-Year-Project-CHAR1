package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.FlatmatePreferenceDTO;
import hk.ust.char1.server.dto.TenantFlatmatePreferenceDTO;
import hk.ust.char1.server.model.Occupation;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantFlatmatePreference;
import hk.ust.char1.server.repository.TenantFlatmatePreferenceRepository;
import hk.ust.char1.server.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.TenantFlatmatePreference.Gender.FEMALE;
import static hk.ust.char1.server.model.TenantFlatmatePreference.Gender.MALE;
import static hk.ust.char1.server.model.TenantFlatmatePreference.MarriageStatus.*;

@Service
public class TenantFlatmateIndicationService {
    private final TenantRepository tenantRepository;

    private final TenantFlatmatePreferenceRepository tenantFlatmatePreferenceRepository;

    @Autowired
    public TenantFlatmateIndicationService(TenantRepository tenantRepository, TenantFlatmatePreferenceRepository tenantFlatmatePreferenceRepository) {
        this.tenantRepository = tenantRepository;
        this.tenantFlatmatePreferenceRepository = tenantFlatmatePreferenceRepository;
    }

    public boolean addPreference(String username, FlatmatePreferenceDTO flatmatePreferenceDTO){
        if (!(tenantRepository.existsTenantByUsername(username) && flatmatePreferenceDTO.getMinimumAge() <= flatmatePreferenceDTO.getMaximumAge() && !tenantFlatmatePreferenceRepository.existsByTenant_Username(username))){
            return false;
        }else{
            TenantFlatmatePreference flatmatePreference = new TenantFlatmatePreference();
            Tenant tenant = tenantRepository.findTenantByUsername(username);

            transferFromDTOToObject(flatmatePreferenceDTO, flatmatePreference);
            flatmatePreference.setTenant(tenant);
            
            tenantFlatmatePreferenceRepository.saveAndFlush(flatmatePreference);
            return true;
        }
    }

    public boolean modifyPreference(String username, FlatmatePreferenceDTO flatmatePreferenceDTO){
        if (!(tenantRepository.existsTenantByUsername(username) && flatmatePreferenceDTO.getMinimumAge() <= flatmatePreferenceDTO.getMaximumAge() && tenantFlatmatePreferenceRepository.existsByTenant_Username(username))){
            return false;
        }else{
            TenantFlatmatePreference preference = tenantFlatmatePreferenceRepository.findByTenant_Username(username);
            transferFromDTOToObject(flatmatePreferenceDTO, preference);

            tenantFlatmatePreferenceRepository.saveAndFlush(preference);
            return true;
        }
    }

    private void transferFromDTOToObject(FlatmatePreferenceDTO flatmatePreferenceDTO, TenantFlatmatePreference preference) {
        switch (flatmatePreferenceDTO.getGender()){
            case 0:
                preference.setGender(null);
                break;
            case 1:
                preference.setGender(MALE);
                break;
            case 2:
                preference.setGender(FEMALE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + flatmatePreferenceDTO.getGender());
        }

        switch (flatmatePreferenceDTO.getMarriageStatus()){
            case 0:
                preference.setMarriageStatus(null);
                break;
            case 1:
                preference.setMarriageStatus(SINGLE);
                break;
            case 2:
                preference.setMarriageStatus(COUPLE);
                break;
            case 3:
                preference.setMarriageStatus(DIVORCED);
                break;
            case 4:
                preference.setMarriageStatus(WIDOWED);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + flatmatePreferenceDTO.getMarriageStatus());
        }

        preference.setHaveChildren(flatmatePreferenceDTO.isHaveChildren());
        preference.setHavePets(flatmatePreferenceDTO.isHavePets());
        preference.setLifestyleDescription(flatmatePreferenceDTO.getLifestyleDescription());
        preference.setMinimumAge(flatmatePreferenceDTO.getMinimumAge());
        preference.setMaximumAge(flatmatePreferenceDTO.getMaximumAge());
        preference.setOccupations(flatmatePreferenceDTO.getOccupations()
                .parallelStream()
                .map(Occupation::new)
                .collect(Collectors.toList())
        );
    }

    public TenantFlatmatePreferenceDTO findSelfTenantFlatematePreferences(String username){
        if (!(tenantRepository.existsTenantByUsername(username)  && tenantFlatmatePreferenceRepository.existsByTenant_Username(username))){
            return null;
        }else{
            TenantFlatmatePreferenceDTO tenantFlatmatePreferenceDTO = new TenantFlatmatePreferenceDTO();
            TenantFlatmatePreference preference = tenantFlatmatePreferenceRepository.findByTenant_Username(username);
            if (preference == null){
                return null;
            }

            tenantFlatmatePreferenceDTO.setGender(preference.getGender() == null? "" : preference.getGender().toString());
            tenantFlatmatePreferenceDTO.setMarriageStatus(preference.getMarriageStatus() == null? "" : preference.getMarriageStatus().toString());
            tenantFlatmatePreferenceDTO.setHaveChildren(preference.isHaveChildren());
            tenantFlatmatePreferenceDTO.setHavePets(preference.isHavePets());
            tenantFlatmatePreferenceDTO.setLifestyleDescription(preference.getLifestyleDescription());
            tenantFlatmatePreferenceDTO.setMaximumAge(preference.getMaximumAge());
            tenantFlatmatePreferenceDTO.setMinimumAge(preference.getMinimumAge());
            tenantFlatmatePreferenceDTO.setOccupations(preference.getOccupations()
                    .parallelStream()
                    .map(Occupation::getOccupationTitle)
                    .collect(Collectors.toList())
            );

            return tenantFlatmatePreferenceDTO;
        }
    }

    public boolean removePreference(String username){
        if (!(tenantRepository.existsTenantByUsername(username)  && tenantFlatmatePreferenceRepository.existsByTenant_Username(username))){
            return false;
        }else{
            TenantFlatmatePreference preference = tenantFlatmatePreferenceRepository.findByTenant_Username(username);
            if (preference == null){
                return false;
            }
            tenantFlatmatePreferenceRepository.deleteById(preference.getFlatmatePreferenceID());
            tenantFlatmatePreferenceRepository.flush();
            return true;
        }
    }
}
