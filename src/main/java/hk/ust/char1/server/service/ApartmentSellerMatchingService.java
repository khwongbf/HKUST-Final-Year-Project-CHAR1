package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.BuyerPreferenceDTO;
import hk.ust.char1.server.model.ApartmentSeller;
import hk.ust.char1.server.model.BuyerApartmentPreference;
import hk.ust.char1.server.model.SellableApartment;
import hk.ust.char1.server.repository.ApartmentSellerRepository;
import hk.ust.char1.server.repository.BuyerApartmentPreferenceRepository;
import hk.ust.char1.server.repository.SellableApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentSellerMatchingService {
    private final ApartmentSellerRepository apartmentSellerRepository;

    private final BuyerApartmentPreferenceRepository buyerApartmentPreferenceRepository;

    private final SellableApartmentRepository sellableApartmentRepository;

    @Autowired
    public ApartmentSellerMatchingService(ApartmentSellerRepository apartmentSellerRepository, BuyerApartmentPreferenceRepository buyerApartmentPreferenceRepository, SellableApartmentRepository sellableApartmentRepository) {
        this.apartmentSellerRepository = apartmentSellerRepository;
        this.buyerApartmentPreferenceRepository = buyerApartmentPreferenceRepository;
        this.sellableApartmentRepository = sellableApartmentRepository;
    }

    /**
     * List out all relevant {@link BuyerApartmentPreference} given in the serarching {@link BuyerPreferenceDTO}.
     * @param username The username of the {@link ApartmentSeller}.
     * @param buyerPreferenceDTO The DTO that defines the minimum conditions for the seller's scope of search.
     * @return The list of the preferences each parsed into {@link BuyerPreferenceDTO}.
     */
    public List<BuyerPreferenceDTO> listBuyerApartmentPreferences(String username, BuyerPreferenceDTO buyerPreferenceDTO){
        if (!apartmentSellerRepository.existsApartmentSellerByUsername(username)){
            return null;
        }else{
            return buyerApartmentPreferenceRepository.findAllByPriceGreaterThanEqual(buyerPreferenceDTO.getPrice())
                    .stream()
                    .map(buyerApartmentPreference -> {
                        var instance =  new BuyerPreferenceDTO();
                        instance.setPrice(buyerApartmentPreference.getPrice());
                        instance.setPreferredGeolocation(buyerApartmentPreference.getPreferredGeolocation());
                        instance.setTitle(buyerApartmentPreference.getTitle());
                        instance.setPreferredSize(buyerApartmentPreference.getPreferredSize());
                        instance.setPetsAllowed(buyerApartmentPreference.isPetsAllowed());
                        return instance;
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * Matches the buyer preference specified by the {@link ApartmentSeller}.
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>The username belongs to a {@link ApartmentSeller}.</li>
     *         <li>The address belongs to a {@link SellableApartment}</li>
     *         <li>The owner of the {@link SellableApartment} has the same username as given.</li>
     *         <li>There exists a buyer preference with the given unique title.</li>
     *     </ol>
     * </p>
     * @param username The username of the owner of the {@link SellableApartment}.
     * @param address The address of the {@link SellableApartment}.
     * @param title The title of the {@link BuyerApartmentPreference} to be matched.
     * @return <code>true</code> if the matching is successful, <code>false</code> otherwise.
     */
    @Transactional
    public boolean matchBuyerPreference(String username, String address, String title){
        if (!apartmentSellerRepository.existsApartmentSellerByUsername(username) || !sellableApartmentRepository.existsByUniqueTag(address) || buyerApartmentPreferenceRepository.existsByTitle(title)){
            return false;
        }else{
            SellableApartment sellableApartment = sellableApartmentRepository.findByUniqueTag(address);
            if (!sellableApartment.getApartmentOwner().getUsername().equals(username)){
                return false;
            }

            BuyerApartmentPreference buyerApartmentPreference = buyerApartmentPreferenceRepository.findByTitle(title);
            return true;
        }
    }
}
