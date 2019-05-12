package hk.ust.char1.server.service;

import hk.ust.char1.server.auxilary.ListOperator;
import hk.ust.char1.server.dto.FlatmatePreferenceDTO;
import hk.ust.char1.server.dto.TenantFlatmatePreferenceDTO;
import hk.ust.char1.server.model.Occupation;
import hk.ust.char1.server.model.TenantFlatmatePreference;
import hk.ust.char1.server.repository.TenantFlatmatePreferenceRepository;
import hk.ust.char1.server.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.TenantFlatmatePreference.Gender.FEMALE;
import static hk.ust.char1.server.model.TenantFlatmatePreference.Gender.MALE;
import static hk.ust.char1.server.model.TenantFlatmatePreference.MarriageStatus.*;

@Service
public class TenantFlatmateSearchingService {

    private final TenantFlatmatePreferenceRepository tenantFlatmatePreferenceRepository;

    private final TenantRepository tenantRepository;

    private final ListOperator listOperator;

    @Autowired
    public TenantFlatmateSearchingService(TenantFlatmatePreferenceRepository tenantFlatmatePreferenceRepository, TenantRepository tenantRepository, ListOperator listOperator) {
        this.tenantFlatmatePreferenceRepository = tenantFlatmatePreferenceRepository;
        this.tenantRepository = tenantRepository;
        this.listOperator = listOperator;
    }

    private static TenantFlatmatePreferenceDTO apply(TenantFlatmatePreference tenantFlatmatePreference) {
        TenantFlatmatePreferenceDTO instance = new TenantFlatmatePreferenceDTO();

        instance.setTenantUsername(tenantFlatmatePreference.getTenant().getUsername());
        instance.setOccupations(tenantFlatmatePreference.getOccupations()
                .parallelStream()
                .map(Occupation::getOccupationTitle)
                .collect(Collectors.toList())
        );
        instance.setMinimumAge(tenantFlatmatePreference.getMinimumAge());
        instance.setMaximumAge(tenantFlatmatePreference.getMaximumAge());
        instance.setGender(tenantFlatmatePreference.getGender().toString());
        instance.setMarriageStatus(tenantFlatmatePreference.getMarriageStatus().toString());
        instance.setLifestyleDescription(tenantFlatmatePreference.getLifestyleDescription());
        instance.setHavePets(tenantFlatmatePreference.isHavePets());
        instance.setHaveChildren(tenantFlatmatePreference.isHaveChildren());

        return instance;
    }

    @Transactional
    public List<TenantFlatmatePreferenceDTO> findFlatmateByPreference(String username, FlatmatePreferenceDTO flatmatePreferenceDTO){
        if (!(tenantRepository.existsTenantByUsername(username) && flatmatePreferenceDTO.getMinimumAge() <= flatmatePreferenceDTO.getMaximumAge()
                && flatmatePreferenceDTO.getMarriageStatus() <= 4 && flatmatePreferenceDTO.getMarriageStatus() >= 0
                && flatmatePreferenceDTO.getGender() >= 0 && flatmatePreferenceDTO.getGender() <= 2)
        ){
            return null;
        }else{
            List<TenantFlatmatePreference> list1 = flatmatePreferenceDTO.getGender()==0? tenantFlatmatePreferenceRepository.findAll() : tenantFlatmatePreferenceRepository.findAllByGender(flatmatePreferenceDTO.getGender() == 1? MALE : FEMALE);

            List<TenantFlatmatePreference> list2;
            switch (flatmatePreferenceDTO.getMarriageStatus()){
                case 0:
                    list2 = tenantFlatmatePreferenceRepository.findAll();
                    break;
                case 1:
                    list2 = tenantFlatmatePreferenceRepository.findAllByMarriageStatus(SINGLE);
                    break;
                case 2:
                    list2 = tenantFlatmatePreferenceRepository.findAllByMarriageStatus(COUPLE);
                    break;
                case 3:
                    list2 = tenantFlatmatePreferenceRepository.findAllByMarriageStatus(DIVORCED);
                    break;
                case 4:
                    list2 = tenantFlatmatePreferenceRepository.findAllByMarriageStatus(WIDOWED);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + flatmatePreferenceDTO.getMarriageStatus());
            }

            List<TenantFlatmatePreference> list3 = tenantFlatmatePreferenceRepository.findAllByMinimumAgeLessThanEqualAndMaximumAgeGreaterThanEqual(flatmatePreferenceDTO.getMinimumAge(), flatmatePreferenceDTO.getMaximumAge());

            return listOperator.intersect(list1, list2, list3)
                    .parallelStream()
                    .map(TenantFlatmateSearchingService::apply)
                    .collect(Collectors.toList());

        }
    }
}
