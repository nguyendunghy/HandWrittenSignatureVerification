/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NguyenVanDung
 */
public class ConnectionFunction {

    private static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/handwritten_signature_verification?"
                    + "user=root");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
        return con;
    }

    public static void main(String[] args) {
        String sql = "SELECT *\n"
                + "FROM threshold;";
        ConnectionFunction.getConnection();
        ArrayList<ThresholdDatatype> data = ConnectionFunction.getThresholdData(sql);
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).getIdentityNumber());
        }
        ConnectionFunction.writeThresholdData(data.get(0));

    }

    /**
     * @param sql Cậu lệnh sql lấy dữ liệu từ bảng Enrollment có identity number
     * cho trước
     * @return Nếu identity number có trong cơ sở dữ liệu thì sẽ trả lại toàn bộ
     * vector dữ liệu Nếu identity number không tồn tại sẽ trả lại null
     */
    public static ArrayList<EnrollDatatype> getEnrollData(String sql) {

        ArrayList<EnrollDatatype> data = new ArrayList<EnrollDatatype>();
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String identityNumber = rs.getString(2);
                float baselineSlantAngle = rs.getFloat(3);
                float aspectRatio = rs.getFloat(4);
                float normalizedAre = rs.getFloat(5);
                int centerGravity_X = rs.getInt(6);
                int centerGravity_Y = rs.getInt(7);
                float jointedCenterAngle = rs.getFloat(8);
                int edgePoint = rs.getInt(9);
                int crossPoint = rs.getInt(10);

                EnrollDatatype e = new EnrollDatatype(
                        id, identityNumber,
                        baselineSlantAngle, aspectRatio,
                        normalizedAre, centerGravity_X,
                        centerGravity_Y, jointedCenterAngle,
                        edgePoint, crossPoint);
                data.add(e);
            }
            return data;
        } catch (SQLException | NumberFormatException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     *
     * @param sql Cậu lệnh sql lấy dữ liệu từ bảng Threshold có identity number
     * cho trước
     * @return Nếu identity number có trong cơ sở dữ liệu thì sẽ trả lại toàn bộ
     * vector dữ liệu Nếu identity number không tồn tại sẽ trả lại null
     */
    public static ArrayList<ThresholdDatatype> getThresholdData(String sql) {
        ArrayList<ThresholdDatatype> data = new ArrayList<ThresholdDatatype>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String identityNumber = rs.getString(2);
                double threshold = rs.getDouble(3);

                ThresholdDatatype tt = new ThresholdDatatype(id, identityNumber, threshold);
                data.add(tt);
            }
            return data;
        } catch (SQLException | NumberFormatException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * @see Hàm kết nối với bảng Enroll và thêm một dòng vào trong bảng
     * @param data Một thể hiện của lớp EnrollDatatype tương ứng với một hàng
     * trong cơ sở dữ liệu
     * @return true nếu insert thành công,false nếu ngược lại
     */
    public static boolean writeEnrollData(EnrollDatatype data) {
        PreparedStatement prepare = null;
        try {
            String sql = "INSERT INTO handwritten_signature_verification.enrollment VALUES(DEFAULT,?,?,?,?,?,?,?,?,?)";
            prepare = con
                    .prepareStatement(sql);
            prepare.setString(1, data.getIdentityNumber());
            prepare.setFloat(2, data.getBaselineSlantAngle());
            prepare.setFloat(3, data.getAspectRatio());
            prepare.setFloat(4, data.getNormalizedAre());
            prepare.setInt(5, data.getCenterGravity_X());
            prepare.setInt(6, data.getCenterGravity_Y());
            prepare.setFloat(7, data.getJointedCenterAngle());
            prepare.setInt(8, data.getEdgePoint());
            prepare.setInt(9, data.getCrossPoint());
            prepare.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @see Hàm kết nối với bảng Threshold và thêm một dòng vào trong bảng
     * @param data Một thể hiện của lớp ThresholdDatatype tương ứng với một hàng
     * trong cơ sở dữ liệu
     * @return true nếu insert thành công,false nếu ngược lại
     */
    public static boolean writeThresholdData(ThresholdDatatype data) {
        PreparedStatement prepare = null;
        try {
            String sql = "INSERT INTO handwritten_signature_verification.threshold VALUES(DEFAULT,?,?)";
            prepare = con
                    .prepareStatement(sql);
            prepare.setString(1, data.getIdentityNumber());
            prepare.setDouble(2, data.getThreshold());
            prepare.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    private static class ThresholdDatatype {

        private int id;
        private String identityNumber;
        private double threshold;

        public ThresholdDatatype(int id, String identityNumber, double threshold) {
            this.id = id;
            this.identityNumber = identityNumber;
            this.threshold = threshold;
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

        public double getThreshold() {
            return threshold;
        }

        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }

    }

    /**
     * @author Nguyễn Văn Dũng
     * @see Cấu trúc dữ liệu để lưu và lấy ra trong bảng Enrollment
     */
    private static class EnrollDatatype {

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

}
