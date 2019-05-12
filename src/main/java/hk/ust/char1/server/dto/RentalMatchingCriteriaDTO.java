package hk.ust.char1.server.dto;

import hk.ust.char1.server.model.Geolocation;

import java.math.BigDecimal;
import java.util.Objects;

public class RentalMatchingCriteriaDTO {
	private BigDecimal maxPreferredSize;

	private BigDecimal minPreferredRent;

	public RentalMatchingCriteriaDTO() {
	}

	public BigDecimal getMaxPreferredSize() {
		return maxPreferredSize;
	}

	public void setMaxPreferredSize(BigDecimal maxPreferredSize) {
		this.maxPreferredSize = maxPreferredSize;
	}

	public BigDecimal getMinPreferredRent() {
		return minPreferredRent;
	}

	public void setMinPreferredRent(BigDecimal minPreferredRent) {
		this.minPreferredRent = minPreferredRent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RentalMatchingCriteriaDTO that = (RentalMatchingCriteriaDTO) o;
		return Objects.equals(maxPreferredSize, that.maxPreferredSize) &&
				Objects.equals(minPreferredRent, that.minPreferredRent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maxPreferredSize, minPreferredRent);
	}
}
