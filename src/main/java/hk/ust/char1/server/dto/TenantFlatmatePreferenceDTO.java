package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.TenantFlatmatePreference.Gender;
import hk.ust.char1.server.model.TenantFlatmatePreference.MarriageStatus;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

public class TenantFlatmatePreferenceDTO {


	private String tenantUsername;
	// Assert: Positive, less than maximumAge
	@Positive
	private int minimumAge;

	// Assert: Positive, less than 150
	@Positive
	@Max(150)
	private int maximumAge;

	// optional
	// 0 : no preference, 1 : Single, 2 : Couple, 3: Divorced, 4: Widowed
	/**
	 * Indicates the preferred {@link MarriageStatus Marriage Status} .
	 *<p>
	 *     The string must be in all CAPS.
	 *     Note that if this field is empty, it means that there is no preference over the {@link MarriageStatus Marriage Status}.
	 *</p>
	 */
	private String marriageStatus;

	/**
	 * Indicates the preferred {@link Gender}.
	 * <p>
	 *     The string must be in all CAPS.
	 *     Note that if this field is empty, it means that there is no preference over the {@link Gender}.
	 * </p>
	 */
	private String gender;

	private List<String> occupations;

	private boolean haveChildren;

	private boolean havePets;

	// Max Length = 250 characters
	@Size(max = 250)
	private String lifestyleDescription;

	public TenantFlatmatePreferenceDTO() {
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

	public String getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(String marriageStatus) {
		this.marriageStatus = marriageStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getOccupations() {
		return occupations;
	}

	public void setOccupations(List<String> occupations) {
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

	public String getTenantUsername() {
		return tenantUsername;
	}

	public void setTenantUsername(String tenantUsername) {
		this.tenantUsername = tenantUsername;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TenantFlatmatePreferenceDTO that = (TenantFlatmatePreferenceDTO) o;
		return minimumAge == that.minimumAge &&
				maximumAge == that.maximumAge &&
				haveChildren == that.haveChildren &&
				havePets == that.havePets &&
				Objects.equals(marriageStatus, that.marriageStatus) &&
				Objects.equals(gender, that.gender) &&
				Objects.equals(occupations, that.occupations) &&
				Objects.equals(lifestyleDescription, that.lifestyleDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(minimumAge, maximumAge, marriageStatus, gender, occupations, haveChildren, havePets, lifestyleDescription);
	}
}
