package com.cepmuvakkit.times.posAlgo;

public class SunMoonPosition {

    private Horizontal sunPosition, moonPosition;
    private double moonPhase;
    private EarthHeading qiblaInfo;
    private SolarPosition solar;
    private LunarPosition lunar;
    private Ecliptic moonPosEc, solarPosEc;
    private Equatorial moonPosEq, solarPosEq;

    public SunMoonPosition(double jd, double latitude, double longitude, double altitude, double ΔT) {
        solar = new SolarPosition();
        lunar = new LunarPosition();
        EarthPosition earth = new EarthPosition(latitude, longitude);
        double tau_Sun = 8.32 / (1440.0);
        moonPosEc = lunar.calculateMoonEclipticCoordinates(jd, ΔT);
        solarPosEc = solar.calculateSunEclipticCoordinatesAstronomic(jd - tau_Sun, ΔT);
        double E = Math.toRadians(solarPosEc.λ - moonPosEc.λ);
        moonPosEq = lunar.calculateMoonEqutarialCoordinates(moonPosEc, jd, ΔT);
        solarPosEq = solar.calculateSunEquatorialCoordinates(solarPosEc, jd, ΔT);
        moonPosition = moonPosEq.Equ2Topocentric(longitude, latitude, altitude, jd, ΔT);
        sunPosition = solarPosEq.Equ2Topocentric(longitude, latitude, altitude, jd, ΔT);
        EarthPosition qibla = new EarthPosition(21.416666667, 39.816666);
        qiblaInfo = earth.toEarthHeading(qibla);
        moonPhase = (1 + Math.cos(Math.PI - E)) / 2;
    }

    public Horizontal getSunPosition() {
        return sunPosition;
    }

    public Horizontal getMoonPosition() {
        return moonPosition;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public EarthHeading getDestinationHeading() {
        return qiblaInfo;
    }

    public double illumunatedFractionofMoon(double jd, double ΔT) {
        return moonPhase;

    }

}
