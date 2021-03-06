package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.*;
import hk.ust.char1.server.security.SecurityConstants;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.security.jwt.JWTTokenGenerator;
import hk.ust.char1.server.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static hk.ust.char1.server.security.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Endpoint class that contains functions for Tenants.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {

	private final RentalApartmentFindingService rentalApartmentFindingService;

	private final TenantFlatmateSearchingService tenantFlatmateSearchingService;

	private final TenantFlatmateIndicationService tenantFlatmateIndicationService;

	private final TenantGroupingService tenantGroupingService;

	private final TenantIndividualPreferenceIndicationService tenantIndividualPreferenceIndicationService;

	private final UserDetailsService userDetailsService;

	private final JWTTokenGenerator jwtTokenGenerator;

	private final JWTDecoder jwtDecoder;

	private static final String ROLE_NAME = "TENANT";

	public TenantController(RentalApartmentFindingService rentalApartmentFindingService,
	                        TenantFlatmateSearchingService tenantFlatmateSearchingService,
	                        TenantFlatmateIndicationService tenantFlatmateIndicationService,
	                        TenantGroupingService tenantGroupingService,
	                        TenantIndividualPreferenceIndicationService tenantIndividualPreferenceIndicationService,
	                        JWTDecoder jwtDecoder, UserDetailsService userDetailsService, JWTTokenGenerator jwtTokenGenerator) {
		this.rentalApartmentFindingService = rentalApartmentFindingService;
		this.tenantFlatmateSearchingService = tenantFlatmateSearchingService;
		this.tenantFlatmateIndicationService = tenantFlatmateIndicationService;
		this.tenantGroupingService = tenantGroupingService;
		this.tenantIndividualPreferenceIndicationService = tenantIndividualPreferenceIndicationService;
		this.jwtDecoder = jwtDecoder;
		this.userDetailsService = userDetailsService;
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	/**
	 * With provision of a raw preference, searches for apartments under lease.
	 * @param rentalApartmentPreferenceDTO the object that indicates the tenant's preference.
	 * @param webRequest the web request that contains the user's info.
	 * @return HTTP response that contains the query results as body.
	 */
	@PostMapping("/findApartmentScratch")
	public ResponseEntity<List<RentalApartmentDTO>> findApartmentByDTO(
			@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<RentalApartmentDTO> result = rentalApartmentFindingService.filterAndOrderByNearestLocation(username,
					rentalApartmentPreferenceDTO);
			return (result==null) ? ResponseEntity.badRequest().build(): ResponseEntity.ok().body(result);
		}
	}

	/**
	 * Using the specified preference, search for apartment under lease.
	 * @param rentalApartmentPreferenceDTO the object that indicates which preference to use.
	 * @param webRequest the web request that contains the user's info at header "Authorization".
	 * @return HTTP Response that contains query results in body.
	 */
	@PostMapping("/findApartmentByStoredPreference")
	public ResponseEntity<List<RentalApartmentDTO>> findApartmentByPreference(
			@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<RentalApartmentDTO> result = rentalApartmentFindingService.filterUsingPreferenceAndOrderByNearestLocation(username,
					rentalApartmentPreferenceDTO.getTitle());
			return (result==null) ? ResponseEntity.badRequest().build(): ResponseEntity.ok().body(result);
		}
	}

	/**
	 * Adds an individual apartment preference on the user.
	 * @param rentalApartmentPreferenceDTO the object that contains the preference.
	 * @param webRequest the web request that contains the user's info at header "Authorization".
	 * @return HTTP Response that indicates whether the addition is successful.
	 */
	@PostMapping("/apartmentPreferences/add")
	public ResponseEntity addNewApartmentPreference(
			@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantIndividualPreferenceIndicationService.addNewPreference(username,
					rentalApartmentPreferenceDTO) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Modifies the apartment preference on the given user.
	 * @param rentalApartmentPreferenceDTO the object that contains the preference, with {@link RentalApartmentPreferenceDTO#getTitle()} as the identifier.
	 * @param webRequest the web request that contians the user's info at header "Authorization".
	 * @return HTTP response that indicates whether the modification is successful.
	 */
	@PutMapping("/apartmentPreference/modify")
	public ResponseEntity modifyApartmentPreference(
			@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantIndividualPreferenceIndicationService.modifyPreference(username,
					rentalApartmentPreferenceDTO.getTitle(),
					rentalApartmentPreferenceDTO) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * List the apartment preferences that the tenant has.
	 * @param webRequest the HTTP request that contains a header "Authorization" for user's info.
	 * @return HTTP response that contains query results in body, and a status code which indicates whether the query is successful.
	 */
	@GetMapping("/apartmentPreference/listSelf")
	public ResponseEntity<List<RentalApartmentPreferenceDTO>> findAllSelfPreference(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<RentalApartmentPreferenceDTO> result = tenantIndividualPreferenceIndicationService.getAllSelfPreferences(username);

			return result != null ?
					ResponseEntity.ok(result)
					: ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Deletes the apartment preference by the tenant.
	 * @param apartmentPreferenceTitle The title of the preference, as in request the body.
	 * @param webRequest the web request that contains the user's info in header "Authorization".
	 * @return HTTP response that indicates whether the deletion is successful.
	 */
	@DeleteMapping("/apartmentPreference/delete")
	public ResponseEntity deleteApartmentPreference(
			@NotNull @NotBlank @RequestBody String apartmentPreferenceTitle, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantIndividualPreferenceIndicationService.deletePreference(username,
					apartmentPreferenceTitle) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Attach a flatmate preference to the tenant. It will overwrite the current preference.
	 * @param flatmatePreferenceDTO The object that contains the flatmate preference.
	 * @param webRequest The web request that contains the header "Authorization" for the user's info.
	 * @return HTTP response that indicates whether the attachment is successful.
	 */
	@PostMapping("/flatmatePreference/add")
	public ResponseEntity addNewFlatmatePreference(
			@NotNull @RequestBody FlatmatePreferenceDTO flatmatePreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantFlatmateIndicationService.addPreference(username,flatmatePreferenceDTO) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Modifies the current flatmate preference.
	 * @param flatmatePreferenceDTO The object that contains the flatemate preference.
	 * @param webRequest The web request that contains the header "Authorization" for user's info.
	 * @return HTTP response that indicates whether the modification is successful.
	 */
	@PutMapping("/flatmatePreference/modify")
	public ResponseEntity modifyFlatmatePreference(
			@NotNull @RequestBody FlatmatePreferenceDTO flatmatePreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantFlatmateIndicationService.modifyPreference(username,flatmatePreferenceDTO) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Finds the flatmate preference of the given tenant.
	 * @param webRequest The web request that contains the tenant's information.
	 * @return HTTP response that indicates whether the finding is successful, and a body containing query results if successful.
	 */
	@GetMapping("/flatmatePreference/listSelf")
	public ResponseEntity<TenantFlatmatePreferenceDTO> findSelfFlatmatePreferences(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			TenantFlatmatePreferenceDTO result = tenantFlatmateIndicationService.findSelfTenantFlatematePreferences(username);

			return result != null ?
					ResponseEntity.ok(result)
					: ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Removes the flatmate preference from the user.
	 * @param webRequest the HTTP request that contains the header "Authorization" for user's info.
	 * @return HTTP response of whether the removal is successful.
	 */
	@DeleteMapping("/flatmatePreference/delete")
	public ResponseEntity removePreference(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantFlatmateIndicationService.removePreference(username) ?
					ResponseEntity.ok().build() :
					ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Searches other flatmates using flatmate preference.
	 * @param flatmatePreferenceDTO Flatmate preference object in the HTTP request body.
	 * @param webRequest The web request.
	 * @return HTTP response that contains the query results.
	 */
	@PostMapping("/flatmatePreference/find")
	public ResponseEntity<List<TenantFlatmatePreferenceDTO>> findFlatmateUsingDTO(
			@RequestBody FlatmatePreferenceDTO flatmatePreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<TenantFlatmatePreferenceDTO> result = tenantFlatmateSearchingService.findFlatmateByPreference(username, flatmatePreferenceDTO);
			return result != null ?
					ResponseEntity.ok(result)
					: ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Initializes a group, and set the initializing tenant as the tenant group owner.
	 * @param tenantGroupDTO The object that contains the details of the tenant group.
	 * @param webRequest The web request that contains the header "Authorization" for user's info
	 * @return HTTP response that contains a new "Authorization" header value. Please replace the old header value with the value returned by the request in future use.
	 */
	@PostMapping("/group/init")
	public ResponseEntity initializeGroup(@Valid @RequestBody TenantGroupDTO tenantGroupDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			String newJWT = jwtTokenGenerator.generate(username, userDetails.getAuthorities());

			return tenantGroupingService.initiateGroup(username, tenantGroupDTO) ?
					ResponseEntity.status(OK).header(HEADER_STRING, TOKEN_PREFIX + newJWT).build() :
					ResponseEntity.badRequest().build();
		}
	}

}
