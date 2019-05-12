package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.TenantFlatmatePreference;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

public class FlatmatePreferenceDTO {

	@Positive
	private int minimumAge;

	// Assert: Positive, less than 150
	@Positive
	@Max(150)
	private int maximumAge;

	// optional
	// 0 : no preference, 1 : Single, 2 : Couple, 3: Divorced, 4: Widowed
	/**
	 * Indicates the preferred {@link TenantFlatmatePreference.MarriageStatus Marriage Status} .
	 * <p>
	 *     The value is represented by an integer, which the value represented is as follows:
	 *     <table>
	 *         <th>
	 *             <td align="center">Integer</td>
	 *             <td align="center">Represented Value</td>
	 *         </th>
	 *         <tr>
	 *             <td align="center">0</td>
	 *             <td align="center">No Preference</td>
	 *         </tr>
	 *         <tr>
	 *              <td align="center">1</td>
	 *              <td align="center">Single</td>
	 *         </tr>
	 *         <tr>
	 *              <td align="center">2</td>
	 *              <td align="center">Couple</td>
	 *         </tr>
	 *         <tr>
	 *              <td align="center">3</td>
	 *              <td align="center">Divorced</td>
	 *         </tr>
	 *         <tr>
	 *             <td align="center">4</td>
	 *             <td align="center">Widowed</td>
	 *         </tr>
	 *     </table>
	 *     Note that the integer must not be less than 0 or greater than 4, or a validation error will be prompted out.
	 * </p>
	 */
	@Max(4)
	@PositiveOrZero
	private int marriageStatus;

	// optional
	// 0: No
	/**
	 * Indicates the preferred {@link TenantFlatmatePreference.Gender}.
	 * <p>
	 *     The gender is represented by an integer, which the value represented is as follows:
	 *     <table>
	 *         <th>
	 *             <td align="center">Integer</td>
	 *             <td align="center">Represented Value</td>
	 *         </th>
	 *         <tr>
	 *             <td align="center">0</td>
	 *             <td align="center">No Preference</td>
	 *         </tr>
	 *         <tr>
	 *              <td align="center">1</td>
	 *              <td align="center">Male</td>
	 *         </tr>
	 *         <tr>
	 *              <td align="center">2</td>
	 *              <td align="center">Female</td>
	 *         </tr>
	 *     </table>
	 *     Note that the integer must not be less than 0 or greater than 2, or a validation error will be prompted out.
	 * </p>
	 */
	private int gender;

	private List<String> occupations;

	private boolean haveChildren;

	private boolean havePets;

	// Max Length = 250 characters
	@Size(max = 250)
	private String lifestyleDescription;

	public FlatmatePreferenceDTO() {
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

	public int getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(int marriageStatus) {
		this.marriageStatus = marriageStatus;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FlatmatePreferenceDTO that = (FlatmatePreferenceDTO) o;
		return minimumAge == that.minimumAge &&
				maximumAge == that.maximumAge &&
				marriageStatus == that.marriageStatus &&
				gender == that.gender &&
				haveChildren == that.haveChildren &&
				havePets == that.havePets &&
				Objects.equals(occupations, that.occupations) &&
				Objects.equals(lifestyleDescription, that.lifestyleDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(minimumAge, maximumAge, marriageStatus, gender, occupations, haveChildren, havePets, lifestyleDescription);
	}
}
