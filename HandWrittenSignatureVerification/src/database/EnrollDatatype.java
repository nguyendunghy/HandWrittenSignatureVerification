/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 * @author Nguyễn Văn Dũng
 * @see Cấu trúc dữ liệu để lưu và lấy ra trong bảng Enrollment
 */
public class EnrollDatatype {

    private int id;
    private String identityNumber;
    private float baselineSlantAngle;
    private float aspectRatio;
    private float normalizedAre;
    private int centerGravity_X;
    private int centerGravity_Y;
    private float jointedCenterAngle;
    private int edgePoint;
    private int crossPoint;

    public EnrollDatatype() {
    }

    public EnrollDatatype(int id, String identityNumber, float baselineSlantAngle, float aspectRatio, float normalizedAre, int centerGravity_X, int centerGravity_Y, float jointedCenterAngle, int edgePoint, int crossPoint) {
        this.id = id;
        this.identityNumber = identityNumber;
        this.baselineSlantAngle = baselineSlantAngle;
        this.aspectRatio = aspectRatio;
        this.normalizedAre = normalizedAre;
        this.centerGravity_X = centerGravity_X;
        this.centerGravity_Y = centerGravity_Y;
        this.jointedCenterAngle = jointedCenterAngle;
        this.edgePoint = edgePoint;
        this.crossPoint = crossPoint;
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

    public float getBaselineSlantAngle() {
        return baselineSlantAngle;
    }

    public void setBaselineSlantAngle(float baselineSlantAngle) {
        this.baselineSlantAngle = baselineSlantAngle;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getNormalizedAre() {
        return normalizedAre;
    }

    public void setNormalizedAre(float normalizedAre) {
        this.normalizedAre = normalizedAre;
    }

    public int getCenterGravity_X() {
        return centerGravity_X;
    }

    public void setCenterGravity_X(int centerGravity_X) {
        this.centerGravity_X = centerGravity_X;
    }

    public int getCenterGravity_Y() {
        return centerGravity_Y;
    }

    public void setCenterGravity_Y(int centerGravity_Y) {
        this.centerGravity_Y = centerGravity_Y;
    }

    public float getJointedCenterAngle() {
        return jointedCenterAngle;
    }

    public void setJointedCenterAngle(float jointedCenterAngle) {
        this.jointedCenterAngle = jointedCenterAngle;
    }

    public int getEdgePoint() {
        return edgePoint;
    }

    public void setEdgePoint(int edgePoint) {
        this.edgePoint = edgePoint;
    }

    public int getCrossPoint() {
        return crossPoint;
    }

    public void setCrossPoint(int crossPoint) {
        this.crossPoint = crossPoint;
    }

}
