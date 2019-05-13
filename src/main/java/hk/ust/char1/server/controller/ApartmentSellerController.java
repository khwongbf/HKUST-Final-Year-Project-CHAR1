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

	/**
	 * Puts an apartment on sale.
	 * @param listingDetailsDTO The object that contains details for selling the apartment.
	 * @param webRequest The web request that contains "Authorization" header with a JWT token as the value for obtaining the username and roles.
	 * @return HTTP response that indicates whether the listing process is successful.
	 */
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

	/**
	 * Finds the apartments that the given owner is selling at the market.
	 * @param webRequest The web request that contains "Authorization" header with a JWT token as the value for obtaining the username and roles.
	 * @return HTTP response that contains the status code which indicates whether the query is successful, and the query results in the body if the query is successful.
	 */
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

	/**
	 * Modifies a listing of apartment, where the apartment is owned by a seller.
	 * @param listingDetailsDTO The details where the listing should be changed to. The unique tag is used to search for the current apartment.
	 * @param webRequest The web request that contains "Authorization" header with a JWT token as the value for obtaining the username and roles.
	 * @return HTTP response that indicates whether the modification is successful.
	 */
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

	/**
	 * Removes a sale from the current market. This action must be done by the seller who owns the apartment.
	 * @param listingDetailsDTO The object that contains the listing details of the apartment. Only the unique tag is used in this method.
	 * @param webRequest The web request that contains "Authorization" header with a JWT token as the value for obtaining the username and roles.
	 * @return HTTP status that indicates whether the removal is successful.
	 */
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
