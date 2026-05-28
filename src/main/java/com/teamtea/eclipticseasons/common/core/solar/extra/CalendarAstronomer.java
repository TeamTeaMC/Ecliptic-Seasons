// © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html
/*
 *******************************************************************************
 * Copyright (C) 1996-2011, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */

package com.teamtea.eclipticseasons.common.core.solar.extra;

import java.util.Date;


/**
 *
 * See also {@link com.ibm.icu.impl.CalendarAstronomer}
 *
 * @author Laura Werner
 * @author Alan Liu
 */
public class CalendarAstronomer {

    //-------------------------------------------------------------------------
    // Astronomical constants
    //-------------------------------------------------------------------------

    /**
     * The average number number of days between successive vernal equinoxes.
     * Due to the precession of the earth's
     * axis, this is not precisely the same as the sidereal year.
     * Approximately 365.24
     *

     */
    public static final double TROPICAL_YEAR = 365.242191;


    //-------------------------------------------------------------------------
    // Time-related constants
    //-------------------------------------------------------------------------

    /**
     * The number of milliseconds in one second.
     *

     */
    public static final int SECOND_MS = 1000;

    /**
     * The number of milliseconds in one minute.
     *

     */
    public static final int MINUTE_MS = 60 * SECOND_MS;

    /**
     * The number of milliseconds in one hour.
     *

     */
    public static final int HOUR_MS = 60 * MINUTE_MS;

    /**
     * The number of milliseconds in one day.
     *

     */
    public static final long DAY_MS = 24 * HOUR_MS;

    /**
     * The start of the julian day numbering scheme used by astronomers, which
     * is 1/1/4713 BC (Julian), 12:00 GMT.  This is given as the number of milliseconds
     * since 1/1/1970 AD (Gregorian), a negative number.
     * Note that julian day numbers and
     * the Julian calendar are <em>not</em> the same thing.  Also note that
     * julian days start at <em>noon</em>, not midnight.
     *

     */
    public static final long JULIAN_EPOCH_MS = -210866760000000L;

    //-------------------------------------------------------------------------
    // Assorted private data used for conversions
    //-------------------------------------------------------------------------

    // My own copies of these so compilers are more likely to optimize them away
    static private final double PI = 3.14159265358979323846;
    static private final double PI2 = PI * 2.0;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Construct a new <code>CalendarAstronomer</code> object that is initialized to
     * the current date and time.
     *

     */
    public CalendarAstronomer() {
        this(System.currentTimeMillis());
    }

    /**
     * Construct a new <code>CalendarAstronomer</code> object that is initialized to
     * the specified time.  The time is expressed as a number of milliseconds since
     * January 1, 1970 AD (Gregorian).
     *

     * @see Date#getTime()
     */
    public CalendarAstronomer(long aTime) {
        time = aTime;
    }


    //-------------------------------------------------------------------------
    // Time and date getters and setters
    //-------------------------------------------------------------------------


    public void setTime(long aTime) {
        time = aTime;
        clearCache();
    }


    public void setDate(Date date) {
        setTime(date.getTime());
    }


    /**
     * Get the current time of this <code>CalendarAstronomer</code> object,
     * expressed as a "julian day number", which is the number of elapsed
     * days since 1/1/4713 BC (Julian), 12:00 GMT.
     *

     * @see #JULIAN_EPOCH_MS
     */
    public double getJulianDay() {
        if (julianDay == INVALID) {
            julianDay = (double) (time - JULIAN_EPOCH_MS) / (double) DAY_MS;
        }
        return julianDay;
    }

    //-------------------------------------------------------------------------
    // The Sun
    //-------------------------------------------------------------------------

    //
    // Parameters of the Sun's orbit as of the epoch Jan 0.0 1990
    // Angles are in radians (after multiplying by PI/180)
    //
    static final double JD_EPOCH = 2447891.5; // Julian day of epoch

    static final double SUN_ETA_G = 279.403303 * PI / 180; // Ecliptic longitude at epoch
    static final double SUN_OMEGA_G = 282.768422 * PI / 180; // Ecliptic longitude of perigee
    static final double SUN_E = 0.016713;          // Eccentricity of orbit


    /**
     * The longitude of the sun at the time specified by this object.
     * The longitude is measured in radians along the ecliptic
     * from the "first point of Aries," the point at which the ecliptic
     * crosses the earth's equatorial plane at the vernal equinox.
     * <p>
     * Currently, this method uses an approximation of the two-body Kepler's
     * equation for the earth and the sun.  It does not take into account the
     * perturbations caused by the other planets, the moon, etc.
     *

     */
    public double getSunLongitude() {
        // See page 86 of "Practical Astronomy with your Calculator",
        // by Peter Duffet-Smith, for details on the algorithm.

        if (sunLongitude == INVALID) {
            double[] result = getSunLongitude(getJulianDay());
            sunLongitude = result[0];
        }
        return sunLongitude;
    }

    /**
     * TODO Make this public when the entire class is package-private.
     */
    /*public*/ double[] getSunLongitude(double julian) {
        // See page 86 of "Practical Astronomy with your Calculator",
        // by Peter Duffet-Smith, for details on the algorithm.

        double day = julian - JD_EPOCH;       // Days since epoch

        // Find the angular distance the sun in a fictitious
        // circular orbit has travelled since the epoch.
        double epochAngle = norm2PI(PI2 / TROPICAL_YEAR * day);

        // The epoch wasn't at the sun's perigee; find the angular distance
        // since perigee, which is called the "mean anomaly"
        double meanAnomaly = norm2PI(epochAngle + SUN_ETA_G - SUN_OMEGA_G);

        // Now find the "true anomaly", e.g. the real solar longitude
        // by solving Kepler's equation for an elliptical orbit
        // NOTE: The 3rd ed. of the book lists omega_g and eta_g in different
        // equations; omega_g is to be correct.
        return new double[]{
                norm2PI(trueAnomaly(meanAnomaly, SUN_E) + SUN_OMEGA_G),
                meanAnomaly
        };
    }


    //-------------------------------------------------------------------------
    // Other utility methods
    //-------------------------------------------------------------------------

    /***
     * Given 'value', add or subtract 'range' until 0 <= 'value' < range.
     * The modulus operator.
     */
    private static final double normalize(double value, double range) {
        return value - range * Math.floor(value / range);
    }

    /**
     * Normalize an angle so that it's in the range 0 - 2pi.
     * For positive angles this is just (angle % 2pi), but the Java
     * mod operator doesn't work that way for negative numbers....
     */
    private static final double norm2PI(double angle) {
        return normalize(angle, PI2);
    }


    /**
     * Find the "true anomaly" (longitude) of an object from
     * its mean anomaly and the eccentricity of its orbit.  This uses
     * an iterative solution to Kepler's equation.
     *
     * @param meanAnomaly  The object's longitude calculated as if it were in
     *                     a regular, circular orbit, measured in radians
     *                     from the point of perigee.
     * @param eccentricity The eccentricity of the orbit
     * @return The true anomaly (longitude) measured in radians
     */
    private double trueAnomaly(double meanAnomaly, double eccentricity) {
        // First, solve Kepler's equation iteratively
        // Duffett-Smith, p.90
        double delta;
        double E = meanAnomaly;
        do {
            delta = E - eccentricity * Math.sin(E) - meanAnomaly;
            E = E - delta / (1 - eccentricity * Math.cos(E));
        }
        while (Math.abs(delta) > 1e-5); // epsilon = 1e-5 rad

        return 2.0 * Math.atan(Math.tan(E / 2) * Math.sqrt((1 + eccentricity)
                / (1 - eccentricity)));
    }



    private long time;

    static final private double INVALID = Double.MIN_VALUE;

    private transient double julianDay = INVALID;
    private transient double sunLongitude = INVALID;


    private void clearCache() {
        julianDay = INVALID;
        sunLongitude = INVALID;
    }


}
