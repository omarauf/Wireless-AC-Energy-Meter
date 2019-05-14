package com.example.Wireless_AC_Energy_Meter.Data;

public class Day  extends Data{

    private String day;

    public Day(){

    }

    public Day(double energy, double power, double current, double voltage, double powerFactor, String day) {
        super(energy, power, current, voltage, powerFactor);
        this.day = day;
    }


    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
}
