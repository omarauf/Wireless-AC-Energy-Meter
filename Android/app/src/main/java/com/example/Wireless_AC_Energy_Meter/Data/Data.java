package com.example.Wireless_AC_Energy_Meter.Data;

public abstract class Data {

    private double energy;
    private double power;
    private double current;
    private double voltage;
    private double powerFactor;

    Data(){

    }


    Data(double energy, double power, double current, double voltage, double powerFactor) {
        this.energy = energy;
        this.power = power;
        this.current = current;
        this.voltage = voltage;
        this.powerFactor = powerFactor;
    }

    /* Getter and Setter */
    public double getEnergy() {
        return energy;
    }
    public void setEnergy(double energy) {
        this.energy = energy;
    }
    public double getPower() {
        return power;
    }
    public void setPower(double power) {
        this.power = power;
    }
    public double getCurrent() {
        return current;
    }
    public void setCurrent(double current) {
        this.current = current;
    }
    public double getVoltage() {
        return voltage;
    }
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
    public double getPowerFactor() {
        return powerFactor;
    }
    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }


}
