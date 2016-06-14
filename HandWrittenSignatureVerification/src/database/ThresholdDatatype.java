/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author NguyenVanDung
 */
public class ThresholdDatatype {

    private int id;
    private String identityNumber;
    private double threshold1;
    private double threshold2;
    private double threshold3;

    public ThresholdDatatype(int id, String identityNumber, double threshold1, double threshold2, double threshold3) {
        this.id = id;
        this.identityNumber = identityNumber;
        this.threshold1 = threshold1;
        this.threshold2 = threshold2;
        this.threshold3 = threshold3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public double getThreshold1() {
        return threshold1;
    }

    public void setThreshold1(double threshold1) {
        this.threshold1 = threshold1;
    }

    public double getThreshold2() {
        return threshold2;
    }

    public void setThreshold2(double threshold2) {
        this.threshold2 = threshold2;
    }

    public double getThreshold3() {
        return threshold3;
    }

    public void setThreshold3(double threshold3) {
        this.threshold3 = threshold3;
    }

}
