package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.*;
import hk.ust.char1.server.model.RentalApartment;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.security.jwt.JWTTokenGenerator;
import hk.ust.char1.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hk.ust.char1.server.model.RentalApartment.RentalMode.INDIVIDUAL;
import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static hk.ust.char1.server.security.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Endpoint class that contains methods that can be used by Landlords.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
@RequestMapping("/owner")
public class ApartmentOwnerController {
	private static final String ROLE_NAME = "LANDLORD";
	private final RentalApartmentRegistrationService rentalApartmentRegistrationService;

	private final ApartmentRegistrationService apartmentRegistrationService;

	private final LandlordMatchingService landlordMatchingService;

	private final IndividualTenantPreferenceSearchingService individualTenantPreferenceSearchingService;

	private final UserDetailsService userDetailsService;

	private final ApartmentListingService apartmentListingService;

	private final JWTTokenGenerator jwtTokenGenerator;

	private final JWTDecoder jwtDecoder;

	public ApartmentOwnerController(RentalApartmentRegistrationService rentalApartmentRegistrationService, ApartmentRegistrationService apartmentRegistrationService, LandlordMatchingService landlordMatchingService, JWTDecoder jwtDecoder, IndividualTenantPreferenceSearchingService individualTenantPreferenceSearchingService, UserDetailsService userDetailsService, ApartmentListingService apartmentListingService, JWTTokenGenerator jwtTokenGenerator) {
		this.rentalApartmentRegistrationService = rentalApartmentRegistrationService;
		this.apartmentRegistrationService = apartmentRegistrationService;
		this.landlordMatchingService = landlordMatchingService;
		this.jwtDecoder = jwtDecoder;
		this.individualTenantPreferenceSearchingService = individualTenantPreferenceSearchingService;
		this.userDetailsService = userDetailsService;
		this.apartmentListingService = apartmentListingService;
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	private static boolean test(Map.Entry<RentalApartmentPreferenceDTO, RentalApartment.RentalMode> rentalApartmentPreferenceDTORentalModeEntry) {
		return rentalApartmentPreferenceDTORentalModeEntry.getValue() == INDIVIDUAL;
	}

	/**
	 * Registers a new apartment for current landlords.
	 * <p>
	 *     Returns HTTP response where status code is:
	 *     <ul>
	 *         <li><code>200</code> if the registration is successful.</li>
	 *         <li><code>400</code> if the registration is unsuccessful, but the user is still authorized to register a new apartment.</li>
	 *         <li><code>401</code> if the user is not authorized to perform this action.</li>
	 *     </ul>
	 * </p>
	 * @param apartmentDTO the object that contains the details of the apartment.
	 * @param webRequest the incoming HTTP request, containing the header for authorization.
	 * @return the HTTP response that indicates the result.
	 */
	@PostMapping("/newApartment")
	public ResponseEntity registerNewApartment(@Valid @RequestBody ApartmentDTO apartmentDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}

			return apartmentRegistrationService.addNewApartment(username, apartmentDTO)?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/updateApartment")
	public ResponseEntity updateApartmentDetails(@Valid @RequestBody ApartmentDTO apartmentDTO,@RequestParam("photo") MultipartFile multipartFile, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}

			try {
				apartmentDTO.setPhoto(multipartFile.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return apartmentRegistrationService.updateApartment(username, apartmentDTO.getUniqueTag(), apartmentDTO)?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/removeApartment")
	public ResponseEntity removeApartment(@Valid @RequestBody ApartmentDTO apartmentDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}

			return apartmentRegistrationService.removeApartment(username, apartmentDTO.getUniqueTag())?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/leaseApartments")
	public ResponseEntity leaseNewApartments(@Valid @RequestBody List<RentalDetailsDTO> rentalDetailsDTOList, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}

			for (RentalDetailsDTO rentalDetailsDTO : rentalDetailsDTOList){
				if (!rentalApartmentRegistrationService.registerToLease(username, rentalDetailsDTO.getUniqueTag(), rentalDetailsDTO)){
					return ResponseEntity.badRequest().build();
				}
			}
			return ResponseEntity.ok().build();
		}
	}

	@PutMapping("/modifyLease")
	public ResponseEntity modifyLeasingApartment(@Valid @RequestBody RentalDetailsDTO rentalDetailsDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}

			return rentalApartmentRegistrationService.updateLeasing(username, rentalDetailsDTO.getUniqueTag(), rentalDetailsDTO)?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}


	/**
	 * Finds the apartments that are current at lease by the authorized apartment owner.
	 * @param webRequest request that contains authorization of the current user
	 * @return The list of Rental Apartments and its details contained in {@link ResponseEntity}, or <code>null</code> if the user is not a landlord.
	 */
	@GetMapping("/findSelfLease")
	public ResponseEntity<List<RentalDetailsDTO>> findSelfLeasingApartments(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			var queryResults = rentalApartmentRegistrationService.getSelfLeasingDetails(username);

			return queryResults != null? ResponseEntity.ok().body(queryResults):ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/deleteLease")
	public ResponseEntity removeApartmentFromLeasing(@Valid @RequestBody RentalDetailsDTO rentalDetailsDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];
			return rentalApartmentRegistrationService.removeFromLeasing(username, rentalDetailsDTO.getUniqueTag())?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/searchMatchingTenants")
	public ResponseEntity<List<UserDTO>> searchTenant(@Valid @RequestBody RentalMatchingCriteriaDTO rentalMatchingCriteriaDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			Map<RentalApartmentPreferenceDTO, RentalApartment.RentalMode> queryResults = landlordMatchingService.matchTenantPreference(username, rentalMatchingCriteriaDTO);
			List<UserDTO> resultBody = queryResults.entrySet().stream()
					.filter(ApartmentOwnerController::test)
					.map(this::apply)
					.map(s ->{
						UserDTO userDTO = new UserDTO();
						User user = userDetailsService.findUserByUsername(s);
						userDTO.setEmail(user.getEmail());
						userDTO.setUsername(s);
						userDTO.setPassword("*");
						userDTO.setPhone(user.getPhoneNumber());
						return userDTO;
					})
					.distinct()
					.collect(Collectors.toList());

			return ResponseEntity.ok(resultBody);
		}
	}

	@GetMapping("/promoteToSeller")
	public ResponseEntity promoteToSeller(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Landlord.");
			}else if (authorityString.contains("SELLER")){
				return ResponseEntity.status(BAD_REQUEST).body("You are already Seller.");
			}

			if (apartmentListingService.registerAsNewSeller(username)) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String newJWT = jwtTokenGenerator.generate(username, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();
			} else {
				return ResponseEntity.badRequest().build();
			}
		}
	}

	private String apply(Map.Entry<RentalApartmentPreferenceDTO, RentalApartment.RentalMode> rentalApartmentPreferenceDTORentalModeEntry) {
		return individualTenantPreferenceSearchingService.GetUsernameByPreferenceTitle(rentalApartmentPreferenceDTORentalModeEntry.getKey().getTitle());
	}
}
