package hk.ust.char1.server.service;

import hk.ust.char1.server.repository.IndividualTenantApartmentPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndividualTenantPreferenceSearchingService {
	private final IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository;

	public IndividualTenantPreferenceSearchingService(IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository) {
		this.individualTenantApartmentPreferenceRepository = individualTenantApartmentPreferenceRepository;
	}

	public String GetUsernameByPreferenceTitle (String title){
		if (!individualTenantApartmentPreferenceRepository.existsIndividualTenantApartmentPreferenceByTitle(title)){
			return null;
		}else{
			return individualTenantApartmentPreferenceRepository.findIndividualTenantApartmentPreferenceByTitle(title).getTenant().getUsername();
		}
	}
}
