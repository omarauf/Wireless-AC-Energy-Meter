package com.example.Wireless_AC_Energy_Meter.Data;

public class Period extends Day {

    private String period;

    public Period(){

    }

    public Period(double energy, double power, double current, double voltage, double powerFactor, String day, String period) {
        super(energy, power, current, voltage, powerFactor, day);
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
}
