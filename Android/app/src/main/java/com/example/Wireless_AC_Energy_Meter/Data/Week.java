package com.example.Wireless_AC_Energy_Meter.Data;

public class Week extends Data {

    private int week;

    public Week() {

    }

    public Week(double energy, double power, double current, double voltage, double powerFactor, int week) {
        super(energy, power, current, voltage, powerFactor);
        this.week = week;
    }

    public int getWeek() {
        return week;
    }
    public void setWeek(int week) {
        this.week = week;
    }
}
