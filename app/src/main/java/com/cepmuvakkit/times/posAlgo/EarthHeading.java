package com.cepmuvakkit.times.posAlgo;

public class EarthHeading {

    private double mHeading;
    private long mMetres;

    public EarthHeading(double heading, long metres) {
        mHeading = heading;
        mMetres = metres;
    }

    public double getHeading() {
        return mHeading;
    }

    public long getKiloMetres() {
        return mMetres / 1000;
    }
}
