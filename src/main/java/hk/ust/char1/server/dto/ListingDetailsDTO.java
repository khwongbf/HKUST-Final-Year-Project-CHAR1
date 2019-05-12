package hk.ust.char1.server.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class ListingDetailsDTO extends ApartmentDTO{

    @NotNull
    @NotEmpty
    @Size(max = 32)
    private String uniqueTag;

    @NotNull
    @NotEmpty
    @NotBlank
    private String address;

    @NotNull
    @Positive
    @Digits(integer = 15, fraction = 2)
    private BigDecimal price;

    public ListingDetailsDTO() {
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
