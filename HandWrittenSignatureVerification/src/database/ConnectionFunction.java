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

    private Connection con;

    public ConnectionFunction() {
    }

    public Connection getConnection() {
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
        String sql = "SELECT * FROM enrollment WHERE identity_number = '111111'";
        ConnectionFunction connect = new ConnectionFunction();
        connect.getConnection();
        ArrayList<ThresholdDatatype> data = connect.getThresholdData("111111");
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).getId());
        }
    }

    /**
     * @param identity Số chứng minh thư nhân dân
     * @return Nếu identity number có trong cơ sở dữ liệu thì sẽ trả lại toàn bộ
     * vector dữ liệu Nếu identity number không tồn tại sẽ trả lại null
     */
    public ArrayList<EnrollDatatype> getEnrollData(String identity) {

        ArrayList<EnrollDatatype> data = new ArrayList<EnrollDatatype>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM handwritten_signature_verification.enrollment WHERE identity_number = '" + identity + "'";
        //String sql = identity;
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
    public ArrayList<ThresholdDatatype> getThresholdData(String identity) {
        ArrayList<ThresholdDatatype> data = new ArrayList<ThresholdDatatype>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM threshold WHERE identity_number = '" + identity + "'";
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
    public boolean writeEnrollData(EnrollDatatype data) {
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
    public boolean writeThresholdData(ThresholdDatatype data) {
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

}
