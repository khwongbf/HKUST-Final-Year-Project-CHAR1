package hk.ust.char1.server.service;

import hk.ust.char1.server.auxilary.DistanceCalculator;
import hk.ust.char1.server.auxilary.ListOperator;
import hk.ust.char1.server.dto.BuyerPreferenceDTO;
import hk.ust.char1.server.dto.SellableApartmentDTO;
import hk.ust.char1.server.model.Buyer;
import hk.ust.char1.server.model.BuyerApartmentPreference;
import hk.ust.char1.server.model.Facility;
import hk.ust.char1.server.model.SellableApartment;
import hk.ust.char1.server.repository.BuyerApartmentPreferenceRepository;
import hk.ust.char1.server.repository.BuyerRepository;
import hk.ust.char1.server.repository.SellableApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentBuyerService {

    private final BuyerApartmentPreferenceRepository buyerApartmentPreferenceRepository;

    private final SellableApartmentRepository sellableApartmentRepository;

    private final BuyerRepository buyerRepository;

    private final ListOperator listOperator;

    private final DistanceCalculator distanceCalculator;

    @Autowired
    public ApartmentBuyerService(BuyerApartmentPreferenceRepository buyerApartmentPreferenceRepository, BuyerRepository buyerRepository, SellableApartmentRepository sellableApartmentRepository, ListOperator listOperator, DistanceCalculator distanceCalculator) {
        this.buyerApartmentPreferenceRepository = buyerApartmentPreferenceRepository;
        this.buyerRepository = buyerRepository;
        this.sellableApartmentRepository = sellableApartmentRepository;
        this.listOperator = listOperator;
        this.distanceCalculator = distanceCalculator;
    }


    @Transactional
    public List<SellableApartmentDTO> findApartmentByDTOAndOrderByNearestLocation(String username, BuyerPreferenceDTO buyerPreferenceDTO){
        if (!buyerRepository.existsBuyerByUsername(username)){
            return null;
        }else{
            List<SellableApartment> sellableApartments1 = sellableApartmentRepository.findAllByPriceLessThanEqual(buyerPreferenceDTO.getPrice());
            List<SellableApartment> sellableApartments2 = sellableApartmentRepository.findAllBySizeGreaterThanEqual(buyerPreferenceDTO.getPreferredSize());

            List<SellableApartment> mergedList = listOperator.intersect(sellableApartments1, sellableApartments2);

            return mergedList
                    .parallelStream()
                    .filter(sellableApartment -> sellableApartment.isPetsAllowed() && buyerPreferenceDTO.isPetsAllowed())
                    .map(sellableApartment -> {
                        SellableApartmentDTO instance = new SellableApartmentDTO();
                        instance.setAddress(sellableApartment.getAddress());
                        instance.setApartmentOwnerName(sellableApartment.getApartmentOwner().getUsername());
                        instance.setGeolocation(sellableApartment.getGeolocation());
                        instance.setSize(sellableApartment.getSize());
                        instance.setPrice(sellableApartment.getPrice());
                        instance.setFacilities(sellableApartment.getFacilities()
                                .parallelStream()
                                .map(Facility::getFacilityName)
                                .collect(Collectors.toList()));
                        instance.setPhoto(sellableApartment.getPhoto());
                        instance.setPetsAllowed(sellableApartment.isPetsAllowed());
                        instance.setDistanceInMeters(distanceCalculator.distance(buyerPreferenceDTO.getPreferredGeolocation(), sellableApartment.getGeolocation()));
                        return instance;
                    })
                    .sorted(Comparator.comparing(SellableApartmentDTO::getDistanceInMeters))
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public boolean addNewPreference(String username, BuyerPreferenceDTO buyerPreferenceDTO){
        if (!buyerRepository.existsBuyerByUsername(username) || buyerApartmentPreferenceRepository.existsByTitle(buyerPreferenceDTO.getTitle())){
            return false;
        }else{
            Buyer buyer = buyerRepository.findBuyerByUsername(username);
            BuyerApartmentPreference buyerPreference = new BuyerApartmentPreference();
            buyerPreference.setBuyer(buyer);

            buyerPreference.setPrice(buyerPreferenceDTO.getPrice());
            buyerPreference.setPetsAllowed(buyerPreferenceDTO.isPetsAllowed());
            buyerPreference.setTitle(buyerPreferenceDTO.getTitle());
            buyerPreference.setPreferredGeolocation(buyerPreferenceDTO.getPreferredGeolocation());

            buyerApartmentPreferenceRepository.saveAndFlush(buyerPreference);
            return true;
        }
    }

    @Transactional
    public boolean modifyCurrentPreference (String username, String title, BuyerPreferenceDTO buyerPreferenceDTO){
        if (!buyerRepository.existsBuyerByUsername(username) || !buyerApartmentPreferenceRepository.existsByTitle(title)){
            return false;
        }else {
            BuyerApartmentPreference buyerPreference = buyerApartmentPreferenceRepository.findByTitle(title);
            if (!buyerPreference.getBuyer().getUsername().equals(username)){
                return false;
            }

            buyerPreference.setPreferredGeolocation(buyerPreferenceDTO.getPreferredGeolocation());
            buyerPreference.setPetsAllowed(buyerPreferenceDTO.isPetsAllowed());
            buyerPreference.setPreferredSize(buyerPreferenceDTO.getPreferredSize());
            buyerPreference.setPrice(buyerPreferenceDTO.getPrice());

            buyerApartmentPreferenceRepository.saveAndFlush(buyerPreference);
            return true;
        }
    }

    @Transactional
    public List<BuyerPreferenceDTO> getSelfPreferences (String username){
        if (!buyerRepository.existsBuyerByUsername(username)){
            return null;
        }else{
            return buyerApartmentPreferenceRepository.findAllByBuyer_Username(username)
                    .parallelStream()
                    .map(buyerApartmentPreference -> {
                        BuyerPreferenceDTO instance = new BuyerPreferenceDTO();
                        instance.setPetsAllowed(buyerApartmentPreference.isPetsAllowed());
                        instance.setTitle(buyerApartmentPreference.getTitle());
                        instance.setPreferredSize(buyerApartmentPreference.getPreferredSize());
                        instance.setPreferredGeolocation(buyerApartmentPreference.getPreferredGeolocation());
                        instance.setPrice(buyerApartmentPreference.getPrice());
                        return instance;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public boolean deletePreference (String username, String title){
        if (!buyerRepository.existsBuyerByUsername(username) || !buyerApartmentPreferenceRepository.existsByTitle(title)){
            return false;
        }else{
            BuyerApartmentPreference buyerPreference = buyerApartmentPreferenceRepository.findByTitle(title);
            if (!buyerPreference.getBuyer().getUsername().equals(username)){
                return false;
            }

            buyerApartmentPreferenceRepository.deleteByTitle(title);
            buyerApartmentPreferenceRepository.flush();
            return true;
        }
    }


}
