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
