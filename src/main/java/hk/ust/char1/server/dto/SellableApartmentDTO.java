package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.model.Facility;
import hk.ust.char1.server.model.Geolocation;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SellableApartmentDTO {

	@Size(max = 32)
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

	// Medium Large Object in DB
	/**
	 * The photo of the flat, stored as a reference.
	 */
	private byte[] photo;

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

	public SellableApartmentDTO() {
	}

	public BigDecimal getDistanceInMeters() {
		return distanceInMeters;
	}

	public void setDistanceInMeters(BigDecimal distanceInMeters) {
		this.distanceInMeters = distanceInMeters;
	}

	public String getUniqueTag() {
		return uniqueTag;
	}

	public void setUniqueTag(String uniqueTag) {
		this.uniqueTag = uniqueTag;
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

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SellableApartmentDTO that = (SellableApartmentDTO) o;
		return petsAllowed == that.petsAllowed &&
				Objects.equals(distanceInMeters, that.distanceInMeters) &&
				Objects.equals(price, that.price) &&
				Objects.equals(address, that.address) &&
				Objects.equals(size, that.size) &&
				Arrays.equals(photo, that.photo) &&
				Objects.equals(geolocation, that.geolocation) &&
				Objects.equals(facilities, that.facilities) &&
				Objects.equals(apartmentOwnerName, that.apartmentOwnerName);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(distanceInMeters, price, address, size, geolocation, facilities, petsAllowed, apartmentOwnerName);
		result = 31 * result + Arrays.hashCode(photo);
		return result;
	}
}
