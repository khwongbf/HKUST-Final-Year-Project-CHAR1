package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.Geolocation;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class SellableApartmentDTOPhotoConverted {

	@Size(max = 32)
	@NotEmpty
	@NotNull
	private String uniqueTag;

	private BigDecimal distanceInMeters;

	private BigDecimal price;

	private String address;

	// integer part = 5, fractional part = 2
	/**
	 *  Size of the flat, in sq. feet.
	 *  <p>
	 *      This number accepts a maximum of 5 integer digits, and 2 fractional digits.
	 *  </p>
	 */
	@Digits(integer = 5, fraction = 2)
	private BigDecimal size;

	/**
	 * The photo of the flat, stored as a reference.
	 */
	private CommonsMultipartFile photo;

	/**
	 * The location of the flat, provided by Google Maps.
	 */
	private Geolocation geolocation;

	/**
	 * Facilities that are within the flat
	 */
	private List<String> facilities;

	/**
	 * Whether pets are allowed for the apartment.
	 * <p>
	 *     <code>true</code> indicates pets are allowed, <code>false</code> otherwise.
	 * </p>
	 */
	private boolean petsAllowed;

	//ManyToOne
	/**
	 * Owner of the Apartment.
	 */
	private String apartmentOwnerName;

	public SellableApartmentDTOPhotoConverted() {
	}

	public BigDecimal getDistanceInMeters() {
		return distanceInMeters;
	}

	public void setDistanceInMeters(BigDecimal distanceInMeters) {
		this.distanceInMeters = distanceInMeters;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getSize() {
		return size;
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public CommonsMultipartFile getPhoto() {
		return photo;
	}

	public void setPhoto(CommonsMultipartFile photo) {
		this.photo = photo;
	}

	public Geolocation getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(Geolocation geolocation) {
		this.geolocation = geolocation;
	}

	public List<String> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<String> facilities) {
		this.facilities = facilities;
	}

	public boolean isPetsAllowed() {
		return petsAllowed;
	}

	public void setPetsAllowed(boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}

	public String getApartmentOwnerName() {
		return apartmentOwnerName;
	}

	public void setApartmentOwnerName(String apartmentOwnerName) {
		this.apartmentOwnerName = apartmentOwnerName;
	}
}
