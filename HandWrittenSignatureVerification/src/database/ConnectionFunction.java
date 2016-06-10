/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
        String link = "C:\\Users\\NguyenVanDung\\Desktop\\back.png";
        ConnectionFunction connect = new ConnectionFunction();
        connect.getConnection();

//        connect.writeUserInfo("222222", "My name is Nguyen Van Nam");
//        String info = connect.getUserInfo("222222");
//        System.out.println(info);
        ArrayList<Image> re = connect.getImage("111111");

        try {
            if (re != null && re.size() != 0) {
                JFrame frame = new JFrame();
                JLabel label = new JLabel(new ImageIcon(re.get(6)));
                frame.getContentPane().add(label, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            } else {
                System.out.println("You're suck");
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
//        ArrayList<double[]> data = connect.getEnrollData("111111");
//        for (int i = 0; i < data.size(); i++) {
//            System.out.println(data.get(i)[1]);
//        }
//        double[] ww = new double[18];
//        for (int i = 0; i < ww.length; i++) {
//            ww[i] = i;
//        }

        //connect.writeEnrollData(ww);
    }

    /**
     * @param identity Số chứng minh thư nhân dân
     * @return Nếu identity number có trong cơ sở dữ liệu thì sẽ trả lại toàn bộ
     * vector dữ liệu Nếu identity number không tồn tại sẽ trả lại null
     */
    public ArrayList<double[]> getEnrollData(String identity) {

        ArrayList<double[]> data = new ArrayList<double[]>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM handwritten_signature_verification.enrollment WHERE identity_number = '" + identity + "'";
        //String sql = identity;
        try {
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                double[] field = new double[15];
                for (int i = 0; i < field.length; i++) {
                    field[i] = rs.getFloat(i + 3);
                }

                data.add(field);
            }
            return data;
        } catch (SQLException | NumberFormatException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id : định danh của bộ ảnh chữ ký
     * @return trả lại bộ ảnh chữ ký nếu thành công hoặc trả lại null nếu thất
     * bại
     */
    public ArrayList<Image> getImage(String id) {
        String sql = "SELECT image FROM handwritten_signature_verification.image WHERE identifier = " + id;
        ArrayList<Image> images = new ArrayList<Image>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Blob imageBlob = rs.getBlob(1);
                InputStream binaryStream = imageBlob.getBinaryStream();
                Image img = ImageIO.read(binaryStream);
                images.add(img);
            }
            return images;
        } catch (SQLException | NumberFormatException | IOException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param id : định danh của bộ ảnh chữ ký
     * @return trả lại thông tin người dùng nếu thành công hoặc trả lại null nếu
     * thất bại.
     */
    public String getUserInfo(String id) {
        String sql = "SELECT user_info FROM handwritten_signature_verification.user_information WHERE identifier = " + id;
        String info = "";
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                info = rs.getString(1);
            }
            return info;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * @see Hàm kết nối với bảng Enroll và thêm một dòng vào trong bảng
     * @param data Một thể hiện của lớp EnrollDatatype tương ứng với một hàng
     * trong cơ sở dữ liệu
     * @return true nếu insert thành công,false nếu ngược lại
     */
    public boolean writeEnrollData(double[] data) {
        PreparedStatement prepare = null;
        try {
            String sql = "INSERT INTO handwritten_signature_verification.enrollment VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            prepare = con
                    .prepareStatement(sql);
            for (int i = 1; i <= data.length; i++) {
                prepare.setDouble(i, data[i - 1]);
            }
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
        ArrayList<ThresholdDatatype> re = getThresholdData(data.getIdentityNumber());
        if (re == null || re.size() == 0) {
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
        } else {
            try {
                String sql = "UPDATE handwritten_signature_verification.threshold SET threshold = "
                        + String.valueOf(data.getThreshold())
                        + " WHERE identity_number = '"
                        + data.getIdentityNumber()
                        + "'";
                prepare = con
                        .prepareStatement(sql);
                prepare.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }

        }
        return true;
    }

    /**
     *
     * @param id: định danh của bộ ảnh chữ ký
     * @param link: đường link url đến ảnh chữ ký
     * @return: true nếu lưu ảnh thành công, false nếu thất bại.
     */
    public boolean writeImage(String id, String link) {
        PreparedStatement prepare = null;
        FileInputStream fis = null;

        try {
            String sql = "INSERT INTO handwritten_signature_verification.image VALUES(DEFAULT,?,?)";
            prepare = con
                    .prepareStatement(sql);
            prepare.setString(1, id);
            File file = new File(link);
            fis = new FileInputStream(file);
            prepare.setBlob(2, fis, (int) file.length());
            prepare.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                prepare.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    /**
     *
     * @param id: định danh của bộ ảnh chữ ký
     * @param info: thông tin về người dùng
     * @return: true nếu lưu thông tin thành công, false nếu thất bại.
     */
    public boolean writeUserInfo(String id, String info) {
        PreparedStatement prepare = null;
        try {
            String sql = "INSERT INTO handwritten_signature_verification.user_information VALUES(DEFAULT,?,?)";
            prepare = con
                    .prepareStatement(sql);
            prepare.setString(1, id);
            prepare.setString(2, info);
            prepare.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                prepare.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        return true;
    }

}
