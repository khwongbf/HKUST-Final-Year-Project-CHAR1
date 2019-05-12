package hk.ust.char1.server.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class TenantFlatmatePreference implements Serializable {

    @Id
    @GeneratedValue
    private long flatmatePreferenceID;

    // OneToOne
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Tenant.class, optional = false)
    @JoinColumn(name = "username")
    private Tenant tenant;

    // Assert: Positive, less than maximumAge
    @Positive
    private int minimumAge;

    // Assert: Positive, less than 150
    @Positive
    @Max(150)
    private int maximumAge;

    // optional
    @Enumerated(EnumType.STRING)
    private MarriageStatus marriageStatus;

    // optional
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // element collection
    @Valid
    @ElementCollection
    private List<Occupation> occupations;

    private boolean haveChildren;

    private boolean havePets;

    // Max Length = 250 characters
    @Column(length = 250)
    private String lifestyleDescription;

    public enum Gender{
        MALE, FEMALE
    }

    public enum MarriageStatus {
        SINGLE, COUPLE, DIVORCED, WIDOWED
    }

    public TenantFlatmatePreference() {
    }

    public TenantFlatmatePreference(Tenant tenant, @Positive int minimumAge, @Positive @Max(150) int maximumAge, MarriageStatus marriageStatus, Gender gender, List<Occupation> occupations, boolean haveChildren, boolean havePets, String lifestyleDescription) {
        this.tenant = tenant;
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.marriageStatus = marriageStatus;
        this.gender = gender;
        this.occupations = occupations;
        this.haveChildren = haveChildren;
        this.havePets = havePets;
        this.lifestyleDescription = lifestyleDescription;
    }

    public long getFlatmatePreferenceID() {
        return flatmatePreferenceID;
    }

    public void setFlatmatePreferenceID(long flatmatePreferenceID) {
        this.flatmatePreferenceID = flatmatePreferenceID;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    public MarriageStatus getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(MarriageStatus marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Occupation> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<Occupation> occupations) {
        this.occupations = occupations;
    }

    public boolean isHaveChildren() {
        return haveChildren;
    }

    public void setHaveChildren(boolean haveChildren) {
        this.haveChildren = haveChildren;
    }

    public boolean isHavePets() {
        return havePets;
    }

    public void setHavePets(boolean havePets) {
        this.havePets = havePets;
    }

    public String getLifestyleDescription() {
        return lifestyleDescription;
    }

    public void setLifestyleDescription(String lifestyleDescription) {
        this.lifestyleDescription = lifestyleDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantFlatmatePreference that = (TenantFlatmatePreference) o;
        return minimumAge == that.minimumAge &&
                maximumAge == that.maximumAge &&
                haveChildren == that.haveChildren &&
                havePets == that.havePets &&
                Objects.equals(tenant, that.tenant) &&
                marriageStatus == that.marriageStatus &&
                gender == that.gender &&
                Objects.equals(occupations, that.occupations) &&
                Objects.equals(lifestyleDescription, that.lifestyleDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, minimumAge, maximumAge, marriageStatus, gender, occupations, haveChildren, havePets, lifestyleDescription);
    }
}
