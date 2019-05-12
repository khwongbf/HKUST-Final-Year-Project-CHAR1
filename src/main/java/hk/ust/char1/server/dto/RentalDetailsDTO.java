package hk.ust.char1.server.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class RentalDetailsDTO extends ApartmentDTO{

    @Size(max = 32)
    @NotNull
    @NotEmpty
    private String uniqueTag;

    @NotNull
    @NotEmpty
    private String title;

    @NotBlank
    @NotEmpty
    @NotNull
    private String address;
    // int. part = 6, fract. part = 2
    // Assert Max = 100,000.00
    @NotNull
    @Digits(integer = 6,fraction = 2)
    @DecimalMax("100000.00")
    private BigDecimal monthlyRent;

    private boolean childrenAllowed;

    private boolean groupOrNot;

    public RentalDetailsDTO() {
    }

    @Override
    public String getUniqueTag() {
        return uniqueTag;
    }

    @Override
    public void setUniqueTag(String uniqueTag) {
        this.uniqueTag = uniqueTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

    public boolean isGroupOrNot() {
        return groupOrNot;
    }

    public void setGroupOrNot(boolean groupOrNot) {
        this.groupOrNot = groupOrNot;
    }
}
