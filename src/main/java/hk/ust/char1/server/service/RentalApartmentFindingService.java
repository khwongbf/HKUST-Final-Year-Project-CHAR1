package hk.ust.char1.server.service;


import hk.ust.char1.server.auxilary.DistanceCalculator;
import hk.ust.char1.server.auxilary.ListOperator;
import hk.ust.char1.server.dto.RentalApartmentDTO;
import hk.ust.char1.server.dto.RentalApartmentPreferenceDTO;
import hk.ust.char1.server.model.Facility;
import hk.ust.char1.server.model.IndividualTenantApartmentPreference;
import hk.ust.char1.server.model.RentalApartment;
import hk.ust.char1.server.repository.IndividualTenantApartmentPreferenceRepository;
import hk.ust.char1.server.repository.RentalApartmentRepository;
import hk.ust.char1.server.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.RentalApartment.RentalMode.INDIVIDUAL;

@Service
public class RentalApartmentFindingService {
    private final TenantRepository tenantRepository;

    private final RentalApartmentRepository rentalApartmentRepository;

    private final IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository;

    private final DistanceCalculator distanceCalculator;

    private final ListOperator listOperator;

    @Autowired
    public RentalApartmentFindingService(TenantRepository tenantRepository, RentalApartmentRepository rentalApartmentRepository, IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository, DistanceCalculator distanceCalculator, ListOperator listOperator) {
        this.tenantRepository = tenantRepository;
        this.rentalApartmentRepository = rentalApartmentRepository;
        this.individualTenantApartmentPreferenceRepository = individualTenantApartmentPreferenceRepository;
        this.distanceCalculator = distanceCalculator;
        this.listOperator = listOperator;
    }

    public List<RentalApartmentDTO> filterAndOrderByNearestLocation(String username, RentalApartmentPreferenceDTO rentalRequestDTO){
        if (!tenantRepository.existsTenantByUsername(username)){
            return null;
        }else{
            List<RentalApartment> list1 = rentalApartmentRepository.findAllBySizeGreaterThanEqual(rentalRequestDTO.getPreferredSize());
            List<RentalApartment> list2 = rentalApartmentRepository.findAllByMonthlyRentLessThanEqual(rentalRequestDTO.getPreferredMonthlyRent());
            List<RentalApartment> list3 = rentalApartmentRepository.findAllByRentalMode(INDIVIDUAL);

            return listOperator.intersect(list1, list2, list3)
                    .stream()
                    .map(rentalApartment -> {
                        RentalApartmentDTO instance = new RentalApartmentDTO();

                        instance.setUniqueTag(rentalApartment.getUniqueTag());

                        instance.setAddress(rentalApartment.getAddress());
                        instance.setChildrenAllowed(rentalApartment.isChildrenAllowed());
                        instance.setFacilities(rentalApartment.getFacilities()
                                .parallelStream()
                                .map(Facility::getFacilityName)
                                .collect(Collectors.toList())
                        );
                        instance.setGeolocation(rentalApartment.getGeolocation());
                        instance.setMonthlyRent(rentalApartment.getMonthlyRent());
                        instance.setPetsAllowed(rentalApartment.isPetsAllowed());
                        instance.setPhoto(rentalApartment.getPhoto());
                        instance.setRentalMode(rentalApartment.getRentalMode().toString());
                        instance.setSize(rentalApartment.getSize());
                        instance.setDistanceInMeters(distanceCalculator.distance(rentalApartment.getGeolocation(), rentalRequestDTO.getPreferredGeolocation()));

                        return instance;
                    })
                    .sorted(Comparator.comparing(RentalApartmentDTO::getDistanceInMeters))
                    .collect(Collectors.toList());
        }
    }

    public List<RentalApartmentDTO> filterUsingPreferenceAndOrderByNearestLocation(String username, String title){
        if (!tenantRepository.existsTenantByUsername(username) || individualTenantApartmentPreferenceRepository.existsIndividualTenantApartmentPreferenceByTitle(title)){
            return null;
        }else {
            IndividualTenantApartmentPreference individualPreference = individualTenantApartmentPreferenceRepository.findIndividualTenantApartmentPreferenceByTitle(title);

            List<RentalApartment> list1 = rentalApartmentRepository.findAllBySizeGreaterThanEqual(individualPreference.getPreferredSize());
            List<RentalApartment> list2 = rentalApartmentRepository.findAllByMonthlyRentLessThanEqual(individualPreference.getPreferredMonthlyRent());
            List<RentalApartment> list3 = rentalApartmentRepository.findAllByRentalMode(INDIVIDUAL);

            return listOperator.intersect(list1, list2, list3)
                    .stream()
                    .map(rentalApartment -> {
                        RentalApartmentDTO instance = new RentalApartmentDTO();

                        instance.setUniqueTag(rentalApartment.getUniqueTag());

                        instance.setAddress(rentalApartment.getAddress());
                        instance.setChildrenAllowed(rentalApartment.isChildrenAllowed());
                        instance.setFacilities(rentalApartment.getFacilities()
                                .parallelStream()
                                .map(Facility::getFacilityName)
                                .collect(Collectors.toList())
                        );
                        instance.setGeolocation(rentalApartment.getGeolocation());
                        instance.setMonthlyRent(rentalApartment.getMonthlyRent());
                        instance.setPetsAllowed(rentalApartment.isPetsAllowed());
                        instance.setPhoto(rentalApartment.getPhoto());
                        instance.setRentalMode(rentalApartment.getRentalMode().toString());
                        instance.setSize(rentalApartment.getSize());
                        instance.setDistanceInMeters(distanceCalculator.distance(rentalApartment.getGeolocation(), individualPreference.getPreferredGeolocation()));

                        return instance;
                    })
                    .sorted(Comparator.comparing(RentalApartmentDTO::getDistanceInMeters))
                    .collect(Collectors.toList());
        }
    }
}
