package hk.ust.char1.server.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class TenantGroupDTO {

	@NotNull
	@NotBlank
	private String groupName;

	public TenantGroupDTO(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TenantGroupDTO that = (TenantGroupDTO) o;
		return Objects.equals(groupName, that.groupName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupName);
	}
}
