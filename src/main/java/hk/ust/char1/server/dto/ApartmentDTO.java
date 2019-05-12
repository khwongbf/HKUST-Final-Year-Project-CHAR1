package hk.ust.char1.server.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class ApartmentDTO {
    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal size;

    @NotNull
    @NotEmpty
    @Size(max = 32)
    private String uniqueTag;

    @NotNull
    @NotEmpty
    private String address;

    private byte[] photo;

    // precision = 8, scale = 6
    @NotNull
    @Digits(integer = 2, fraction = 6)
    private BigDecimal latitude;

    // precision = 9, scale = 6
    @NotNull
    @Digits(integer = 3, fraction = 6)
    private BigDecimal longitude;

    private List<String> facilities;

    private boolean petsAllowed;

    public ApartmentDTO() {
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUniqueTag() {
        return uniqueTag;
    }

    public void setUniqueTag(String uniqueTag) {
        this.uniqueTag = uniqueTag;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
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
}
