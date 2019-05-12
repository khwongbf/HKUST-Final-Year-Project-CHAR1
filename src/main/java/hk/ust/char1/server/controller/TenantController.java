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

	@DeleteMapping("/apartmentPreference/delete")
	public ResponseEntity deleteApartmentPreference(
			@NotNull @NotBlank @NotEmpty String apartmentPreferenceTitle, WebRequest webRequest){
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
