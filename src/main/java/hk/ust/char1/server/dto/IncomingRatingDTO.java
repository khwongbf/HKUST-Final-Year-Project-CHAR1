package hk.ust.char1.server.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class IncomingRatingDTO {
    @NotNull
    @NotEmpty
    private String senderUname;

    @NotNull
    @NotEmpty
    private String targetUname;


    private UserType userType;

    @NotNull
    @NotEmpty
    private BigDecimal rating;

    public String getSenderUname() {
        return senderUname;
    }

    public void setSenderUname(String senderUname) {
        this.senderUname = senderUname;
    }

    public String getTargetUname() {
        return targetUname;
    }

    public void setTargetUname(String targetUname) {
        this.targetUname = targetUname;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public enum UserType{
        TENANT,
        BUYER,
        SELLER;

        @Override
        public String toString() {
            return name();
        }
    }

}
