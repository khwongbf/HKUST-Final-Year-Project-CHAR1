package hk.ust.char1.server.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Basic Details for an apartment, where variables are fixed.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class Apartment implements Serializable {

    @Id
    @Column(name = "TAG", length = 32)
    private String uniqueTag;

    @Column(name = "ADDRESS")
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
    @Lob
    @Column(name = "Photo", columnDefinition = "BLOB")
    private byte[] photo;

    /**
     * The location of the flat, provided by Google Maps.
     */
    @Valid
    @Embedded
    private Geolocation geolocation;

    /**
     * Facilities that are within the flat
     */
    @ElementCollection
    private List<Facility> facilities;


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
    @ManyToOne(targetEntity = ApartmentOwner.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username")
    private ApartmentOwner apartmentOwner;

    public Apartment() {
    }

    public Apartment( String uniqueTag , @Digits(integer = 5, fraction = 2) BigDecimal size, String address, byte[] photo, @Valid Geolocation geolocation, List<Facility> facilities, boolean petsAllowed, ApartmentOwner apartmentOwner) {
        this.uniqueTag = uniqueTag;
        this.size = size;
        this.address = address;
        this.photo = photo;
        this.geolocation = geolocation;
        this.facilities = facilities;
        this.petsAllowed = petsAllowed;
        this.apartmentOwner = apartmentOwner;
    }

    public String getUniqueTag() {
        return uniqueTag;
    }

    public void setUniqueTag(String uniqueTag) {
        this.uniqueTag = uniqueTag;
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

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public ApartmentOwner getApartmentOwner() {
        return apartmentOwner;
    }

    public void setApartmentOwner(ApartmentOwner apartmentOwner) {
        this.apartmentOwner = apartmentOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return  petsAllowed == apartment.petsAllowed &&
                Objects.equals(address, apartment.address) &&
                Objects.equals(size, apartment.size) &&
                Arrays.equals(photo, apartment.photo) &&
                Objects.equals(geolocation, apartment.geolocation) &&
                Objects.equals(facilities, apartment.facilities) &&
                Objects.equals(apartmentOwner, apartment.apartmentOwner);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(address, size, geolocation, facilities, petsAllowed, apartmentOwner);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}
