package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.Geolocation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class BuyerPreferenceDTO {

    @Size(max = 32)
    private String title;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private BigDecimal preferredSize;

    @NotNull
    private Geolocation preferredGeolocation;

    private boolean petsAllowed;

    public BuyerPreferenceDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(BigDecimal preferredSize) {
        this.preferredSize = preferredSize;
    }

    public Geolocation getPreferredGeolocation() {
        return preferredGeolocation;
    }

    public void setPreferredGeolocation(Geolocation preferredGeolocation) {
        this.preferredGeolocation = preferredGeolocation;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
}
