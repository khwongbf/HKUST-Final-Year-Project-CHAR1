package hk.ust.char1.server.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import hk.ust.char1.server.dto.BuyerPreferenceDTO;
import hk.ust.char1.server.dto.SellableApartmentDTO;
import hk.ust.char1.server.security.jwt.JWTDecoder;
import hk.ust.char1.server.service.ApartmentBuyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Endpoint class that contains functions for Buyers.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
@RequestMapping("/buyer")
public class BuyerController {

	private final ApartmentBuyerService apartmentBuyerService;

	private final JWTDecoder jwtDecoder;

	private static final String ROLE_NAME ="BUYER";

	public BuyerController(ApartmentBuyerService apartmentBuyerService, JWTDecoder jwtDecoder) {
		this.apartmentBuyerService = apartmentBuyerService;
		this.jwtDecoder = jwtDecoder;
	}

	/**
	 * Searches for apartments by using raw request, and orders them by nearest location.
	 * @param buyerPreferenceDTO The object that contains details of buyer preference.
	 * @param request The request that contains the user's info in header "Authorization" with value as a JWT token.
	 * @return HTTP response that contains query results in the body.
	 */
	@PostMapping("/find")
	public ResponseEntity<List<SellableApartmentDTO>> findApartmentByDTO( @Valid @RequestBody BuyerPreferenceDTO buyerPreferenceDTO, WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			List<SellableApartmentDTO> queryResults = apartmentBuyerService.findApartmentByDTOAndOrderByNearestLocation(username, buyerPreferenceDTO);
			return queryResults == null?
					ResponseEntity.badRequest().header(HEADER_STRING, request.getHeader(HEADER_STRING)).body(null) :
					ResponseEntity.ok().header(HEADER_STRING, request.getHeader(HEADER_STRING)).body(queryResults);
		}
	}

	/**
	 * Adds a new buyer preference on apartments they would like to buy.
	 * @param buyerPreferenceDTO Object that contains details of the buyer preference.
	 * @param request The incoming web request that contains header "Authorization" with the buyer's JWT token as value.
	 * @return HTTP response of whether the addition is successful.
	 */
	@PostMapping("/add")
	public ResponseEntity addNewPreference( @Valid @RequestBody BuyerPreferenceDTO buyerPreferenceDTO, WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return apartmentBuyerService.addNewPreference(username, buyerPreferenceDTO) ?
					ResponseEntity.ok().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build() :
					ResponseEntity.badRequest().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build();
		}
	}

	/**
	 * Modifies the buyer preference on apartments they would like to buy.
	 * @param buyerPreferenceDTO Object that contains the details of the buyer preference, with {@link BuyerPreferenceDTO#getTitle()} as the identifier.
	 * @param request The incoming web request that contains the header "Authorization" with value of the JWT token of the user.
	 * @return HTTP response of whether the modification is successful.
	 */
	@PutMapping("/modify")
	public ResponseEntity modifyPreference(@Valid @RequestBody BuyerPreferenceDTO buyerPreferenceDTO, WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return apartmentBuyerService.modifyCurrentPreference(username, buyerPreferenceDTO.getTitle(), buyerPreferenceDTO)?
					ResponseEntity.ok().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build() :
					ResponseEntity.badRequest().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build();
		}

	}

	/**
	 * Deletes the buyer preference on apartments the would like to buy.
	 * @param buyerPreferenceDTO Object that contains the details of the buyer preference. Only {@link BuyerPreferenceDTO#getTitle()} is used here.
	 * @param request The incoming web request that contains the header "Authorization" with value of the JWT token of the user.
	 * @return HTTP response of whether the deletion is successful.
	 */
	@DeleteMapping("/delete")
	public ResponseEntity deletePreference(@Valid @RequestBody BuyerPreferenceDTO buyerPreferenceDTO, WebRequest request){
		DecodedJWT decodedJWT = jwtDecoder.decodeFromRequest(request);
		if (decodedJWT == null){
			return ResponseEntity.status(UNAUTHORIZED).build();
		}else{
			String username = decodedJWT.getSubject().split(":")[0];

			List<String> authorityString = Arrays.asList(decodedJWT.getSubject().split(":")[1].split(","));
			if (!authorityString.contains(ROLE_NAME)){
				return ResponseEntity.status(UNAUTHORIZED).build();
			}

			return apartmentBuyerService.deletePreference(username, buyerPreferenceDTO.getTitle())?
					ResponseEntity.ok().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build() :
					ResponseEntity.badRequest().header(HEADER_STRING, request.getHeader(HEADER_STRING)).build();
		}
	}
}
