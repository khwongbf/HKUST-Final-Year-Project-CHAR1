package hk.ust.char1.server.model;

import javax.persistence.Embeddable;

@Embeddable
public class Facility {
    private String facilityName;

    public Facility() {
    }

    public Facility(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
