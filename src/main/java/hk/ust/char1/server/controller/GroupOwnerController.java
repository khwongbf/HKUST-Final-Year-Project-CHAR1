package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.RentalApartmentPreferenceDTO;
import hk.ust.char1.server.repository.TenantRepository;
import hk.ust.char1.server.security.SecurityConstants;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.security.jwt.JWTTokenGenerator;
import hk.ust.char1.server.service.GroupPreferenceIndicationService;
import hk.ust.char1.server.service.TenantGroupingService;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.Arrays;
import java.util.List;

import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static hk.ust.char1.server.security.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/owner")
public class GroupOwnerController {
	private final TenantGroupingService tenantGroupingService;

	private final GroupPreferenceIndicationService groupPreferenceIndicationService;

	private final TenantRepository tenantRepository;

	private final JWTDecoder jwtDecoder;

	private final UserDetailsService userDetailsService;

	private final JWTTokenGenerator jwtTokenGenerator;

	private static final String ROLE_NAME = "GROUP_OWNER";

	public GroupOwnerController(TenantGroupingService tenantGroupingService, JWTDecoder jwtDecoder, TenantRepository tenantRepository, GroupPreferenceIndicationService groupPreferenceIndicationService, UserDetailsService userDetailsService, JWTTokenGenerator jwtTokenGenerator) {
		this.tenantGroupingService = tenantGroupingService;
		this.jwtDecoder = jwtDecoder;
		this.tenantRepository = tenantRepository;
		this.groupPreferenceIndicationService = groupPreferenceIndicationService;
		this.userDetailsService = userDetailsService;
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	@PostMapping("/transfer")
	public ResponseEntity transferOwnership(@NotBlank @RequestBody String newOwnerName, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String oldOwnerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			if (tenantGroupingService.changeGroupOwner(oldOwnerName, newOwnerName, tenantRepository.findTenantByUsername(oldOwnerName).getOwnerGroup().getGroupName())) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(oldOwnerName);
				String newJWT = jwtTokenGenerator.generate(oldOwnerName, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();
			} else {
				return ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
			}
		}
	}

	@PostMapping("/addNewMember")
	public ResponseEntity addToGroup(@RequestBody @NotBlank String newMemberName, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String ownerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return tenantGroupingService.addToGroup(ownerName, newMemberName, tenantRepository.findTenantByUsername(ownerName).getOwnerGroup().getGroupName())?
					ResponseEntity.ok().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build():
					ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
		}
	}

	@DeleteMapping("/removeMember")
	public ResponseEntity removeFromGroup(@RequestBody @NotBlank String memberName, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String ownerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			if (tenantGroupingService.removeFromGroup(ownerName, memberName, tenantRepository.findTenantByUsername(ownerName).getOwnerGroup().getGroupName())) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(ownerName);
				String newJWT = jwtTokenGenerator.generate(ownerName, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();

			}
			else {
				return ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
			}
		}
	}

	@PostMapping("/addPreference")
	public ResponseEntity addNewGroupPreference(@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String ownerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return groupPreferenceIndicationService.addNewPreference(ownerName, tenantRepository.findTenantByUsername(ownerName).getOwnerGroup().getGroupName(), rentalApartmentPreferenceDTO)?
					ResponseEntity.ok().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build():
					ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
		}
	}

	@PutMapping("/modifyPreference")
	public ResponseEntity modifyGroupPreference(@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String ownerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return groupPreferenceIndicationService.modifyPreference(ownerName, tenantRepository.findTenantByUsername(ownerName).getOwnerGroup().getGroupName(), rentalApartmentPreferenceDTO.getTitle(), rentalApartmentPreferenceDTO)?
					ResponseEntity.ok().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build():
					ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
		}
	}

	@DeleteMapping("/deletePreference")
	public ResponseEntity deleteGroupPreference(@Valid @RequestBody RentalApartmentPreferenceDTO rentalApartmentPreferenceDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String ownerName = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return groupPreferenceIndicationService.deletePreference(ownerName, tenantRepository.findTenantByUsername(ownerName).getOwnerGroup().getGroupName(), rentalApartmentPreferenceDTO.getTitle())?
					ResponseEntity.ok().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build():
					ResponseEntity.badRequest().header(webRequest.getHeaderNames().next(), webRequest.getHeader(webRequest.getHeaderNames().next())).build();
		}
	}
}
