package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.Geolocation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class RentalApartmentPreferenceDTO {

    private boolean childrenAllowed;

    private boolean petsAllowed;

    @NotNull
    @Positive
    private BigDecimal preferredSize;

    @NotEmpty
    @NotNull
    private String title;

    private Geolocation preferredGeolocation;

    private BigDecimal preferredMonthlyRent;

    public RentalApartmentPreferenceDTO() {
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public BigDecimal getPreferredMonthlyRent() {
        return preferredMonthlyRent;
    }

    public void setPreferredMonthlyRent(BigDecimal preferredMonthlyRent) {
        this.preferredMonthlyRent = preferredMonthlyRent;
    }
}
