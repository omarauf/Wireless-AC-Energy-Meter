package com.example.Wireless_AC_Energy_Meter.Data;

public class Month extends Data {

    private String month;

    public Month(){

    }

    public Month(double energy, double power, double current, double voltage, double powerFactor, String month) {
        super(energy, power, current, voltage, powerFactor);
        this.month = month;
    }

    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
}
