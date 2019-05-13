package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.ApartmentDTO;
import hk.ust.char1.server.security.SecurityConstants;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.security.jwt.JWTTokenGenerator;
import hk.ust.char1.server.service.ApartmentRegistrationService;
import hk.ust.char1.server.service.BuyerRegistrationService;
import hk.ust.char1.server.service.TenantRegistrationService;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static hk.ust.char1.server.security.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Endpoint class that contains functions for <b>all authorized</b> users.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
@RequestMapping("/user")
public class GeneralUserController {

	private static final String roleName = "USER";

	private final TenantRegistrationService tenantRegistrationService;

	private final BuyerRegistrationService buyerRegistrationService;

	private final ApartmentRegistrationService apartmentRegistrationService;

	private final UserDetailsService userDetailsService;

	private final JWTTokenGenerator jwtTokenGenerator;

	private final JWTDecoder jwtDecoder;

	public GeneralUserController(JWTDecoder jwtDecoder, ApartmentRegistrationService apartmentRegistrationService, BuyerRegistrationService buyerRegistrationService, TenantRegistrationService tenantRegistrationService, UserDetailsService userDetailsService, JWTTokenGenerator jwtTokenGenerator) {
		this.jwtDecoder = jwtDecoder;
		this.apartmentRegistrationService = apartmentRegistrationService;
		this.buyerRegistrationService = buyerRegistrationService;
		this.tenantRegistrationService = tenantRegistrationService;
		this.userDetailsService = userDetailsService;
		this.jwtTokenGenerator = jwtTokenGenerator;
	}

	/**
	 * Registers the user as a new buyer.
	 * @param request The web request that contains a header "Authorization", which contains the JWT token of the user.
	 * @return HTTP response that contains a new "Authorization" header. Please replace the current header with this header to access buyer functions.
	 */
	@PostMapping("/registerAsNewBuyer")
	public ResponseEntity registerAsBuyer(WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(roleName)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			if (buyerRegistrationService.registerAsNewBuyer(username)) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String newJWT = jwtTokenGenerator.generate(username, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();
			} else {
				return ResponseEntity.badRequest().build();
			}
		}
	}

	/**
	 * Registers the user as a new tenant.
	 * @param request The web request that contains a header "Authorization", which contains the JWT token of the user.
	 * @return HTTP response that contains a new "Authorization" header. Please replace the current header with this header to access tenant functions.
	 */
	@PostMapping("/registerAsNewTenant")
	public ResponseEntity registerAsTenant(WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else {
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(roleName)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			if (tenantRegistrationService.registerAsNewTenant(username)) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String newJWT = jwtTokenGenerator.generate(username, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();
			} else {
				return ResponseEntity.badRequest().build();
			}
		}
	}

	/**
	 * Registers the user as a new landlord.
	 * @param apartmentDTO the apartment information that the new owner owns.
	 * @param request The web request that contains a header "Authorization", which contains the JWT token of the user.
	 * @return HTTP response that contains a new "Authorization" header. Please replace the current header with this header to access landlord functions.
	 */
	@PostMapping("/registerAsNewLandlord")
	public ResponseEntity registerAsNewOwner(@Valid @RequestBody ApartmentDTO apartmentDTO, WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(roleName)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}
			if (apartmentRegistrationService.addNewApartment(username, apartmentDTO)) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String newJWT = jwtTokenGenerator.generate(username, userDetails.getAuthorities());
				return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + newJWT).build();
			} else {
				return ResponseEntity.badRequest().build();
			}
		}
	}
}
