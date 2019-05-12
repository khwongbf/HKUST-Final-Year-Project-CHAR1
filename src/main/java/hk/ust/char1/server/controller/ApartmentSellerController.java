package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.ListingDetailsDTO;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.service.ApartmentListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Endpoint class that contains functions for Apartment Sellers.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
@RequestMapping("/seller")
public class ApartmentSellerController {

	private static final String ROLE_NAME = "SELLER";
	private final ApartmentListingService apartmentListingService;

	private final JWTDecoder jwtDecoder;

	public ApartmentSellerController(ApartmentListingService apartmentListingService, JWTDecoder jwtDecoder) {
		this.apartmentListingService = apartmentListingService;
		this.jwtDecoder = jwtDecoder;
	}

	@PostMapping("/list")
	public ResponseEntity listApartment(@Valid @RequestBody ListingDetailsDTO listingDetailsDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Seller.");
			}

			return apartmentListingService.listNewApartment(username, listingDetailsDTO.getUniqueTag(), listingDetailsDTO)?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/findSelf")
	public ResponseEntity<List<ListingDetailsDTO>> getSelfListings(WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<ListingDetailsDTO> listingDetailsDTOS= apartmentListingService.getListings(username);

			return ResponseEntity.ok(listingDetailsDTOS);
		}
	}

	@PutMapping("/modifyListing")
	public ResponseEntity modifyListing(@Valid @RequestBody ListingDetailsDTO listingDetailsDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Seller.");
			}

			return apartmentListingService.updateListing(username, listingDetailsDTO.getUniqueTag(), listingDetailsDTO)?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/removeSale")
	public ResponseEntity removeListing(@Valid @RequestBody ListingDetailsDTO listingDetailsDTO, WebRequest webRequest){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(webRequest);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).body("You are not Seller.");
			}

			return apartmentListingService.removeListing(username, listingDetailsDTO.getUniqueTag())?
					ResponseEntity.ok().build():
					ResponseEntity.badRequest().build();
		}
	}

	public ResponseEntity matchBuyerPreference(){
		//TODO
		return null;
	}
}
