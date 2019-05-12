package hk.ust.char1.server.auxilary;

import hk.ust.char1.server.model.Geolocation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class DistanceCalculator {

	public  BigDecimal distance(Geolocation geolocation1, Geolocation geolocation2) {
		if ((geolocation1.getLatitude().equals(geolocation2.getLatitude())) && (geolocation1.getLongitude().equals(geolocation2.getLongitude()))) {
			return BigDecimal.ZERO;
		} else {
			BigDecimal theta = geolocation1.getLongitude().subtract(geolocation2.getLongitude());
			double distance = Math.sin(Math.toRadians(geolocation1.getLatitude().doubleValue())) * Math.sin(Math.toRadians(geolocation2.getLatitude().doubleValue()))
					+ Math.cos(Math.toRadians(geolocation1.getLatitude().doubleValue())) * Math.cos(Math.toRadians(geolocation2.getLatitude().doubleValue())) * Math.cos(Math.toRadians(theta.doubleValue()));
			distance = Math.acos(distance);
			distance = Math.toDegrees(distance);
			BigDecimal dist = new BigDecimal(distance);
			dist = dist.multiply(new BigDecimal(60.000)).multiply(new BigDecimal(1.1515)).multiply(new BigDecimal(1.609344)).multiply(BigDecimal.TEN.pow(3));

			return (dist).round(new MathContext(dist.precision() - dist.scale() + 2, RoundingMode.HALF_UP));
		}
	}
}