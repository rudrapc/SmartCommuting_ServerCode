package com.ibm.bluemix.demo.util;

//import org.springframework.util.Assert;

//mport com.ibm.kosa.KosaToStringBuilder;

public class GeoCoordinate {

	private static final double WGS84_EQUATORIALRADIUS = 6378137.0;

	private static final double LATITUDE_MAX = 90;

	private static final double LATITUDE_MIN = -90;

	private static final double LONGITUDE_MAX = 180;

	private static final double LONGITUDE_MIN = -180;

	private double mLatitude;

	private double mLongitude;


	/**
	 * Constructs GeoCoordinate.
	 * 
	 * @param pLongitude
	 *        longitude
	 * @param pLatitude
	 *        latitude
	 */
	public GeoCoordinate(final double pLongitude, final double pLatitude) {

		mLongitude = validateLongitude(pLongitude);
		mLatitude = validateLatitude(pLatitude);
	}


	/**
	 * validateLongitude.
	 * 
	 * @param pLongitude
	 *        longitude
	 * @return validateLongitude
	 */
	private double validateLongitude(final double pLongitude) {

		if (pLongitude < LONGITUDE_MIN) {
			throw new IllegalArgumentException("longitude=[" + pLongitude + "] is below min value");
		} else if (pLongitude > LONGITUDE_MAX) {
			throw new IllegalArgumentException("longitude=[" + pLongitude + "] is above max value");
		}
		return pLongitude;
	}


	/**
	 * validateLatitude.
	 * 
	 * @param pLatitude
	 *        latitude
	 * @return validateLatitude
	 */
	private double validateLatitude(final double pLatitude) {

		if (pLatitude < LATITUDE_MIN) {
			throw new IllegalArgumentException("latitude=[" + pLatitude + "] is below min value");
		} else if (pLatitude > LATITUDE_MAX) {
			throw new IllegalArgumentException("latitude=[" + pLatitude + "] is above max value");
		}
		return pLatitude;
	}


	/**
	 * @return the latitude
	 */
	public double getLatitude() {

		return mLatitude;
	}


	/**
	 * @return the longitude
	 */
	public double getLongitude() {

		return mLongitude;
	}


	/**
	 * Returns the distance to another <code>GeoCoordinate</code> in meters.
	 * 
	 * @param pGeoCoordinate
	 *        geo coordinates
	 * @return the distance in meters
	 */
	public double distanceTo(final GeoCoordinate pGeoCoordinate) {

		//Assert.notNull(pGeoCoordinate);

		double lLat = Math.toRadians(pGeoCoordinate.getLatitude() - this.mLatitude);
		double lLon = Math.toRadians(pGeoCoordinate.getLongitude() - this.mLongitude);
		double lA = Math.sin(lLat / 2) * Math.sin(lLat / 2) + Math.cos(Math.toRadians(this.mLatitude))
				* Math.cos(Math.toRadians(pGeoCoordinate.getLatitude())) * Math.sin(lLon / 2) * Math.sin(lLon / 2);
		double lC = 2 * Math.atan2(Math.sqrt(lA), Math.sqrt(1 - lA));
		return lC * WGS84_EQUATORIALRADIUS;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 
	@Override
	public String toString() {

		KosaToStringBuilder lSb = new KosaToStringBuilder(this);
		lSb.append("longitude", mLongitude);
		lSb.append("latitude", mLatitude);
		return lSb.toString();
	}*/
}
