/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwrittensignatureverification;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author NguyenVanDung
 */
public class FeatureExtraction {

    private String imageLink;
    private int[][] matrix;
    private int[][] bbMatrix;   //Bounding box matrix
    private int M;
    private int N;

    public FeatureExtraction(int[][] matrix, String imageLink) {
        this.matrix = matrix;
        this.imageLink = imageLink;
    }

    public FeatureExtraction() {
    }

    public static void main(String[] args) throws IOException {
        int[][] data = new int[6][6];
        data[0][1] = 1;
        data[1][1] = 1;
        data[2][1] = 1;
        data[3][1] = 1;
        data[4][1] = 1;

        data[1][4] = 1;
        data[2][4] = 1;
        data[3][4] = 1;
        data[4][4] = 1;
        data[5][4] = 1;
        data[3][3] = 1;
        data[3][5] = 1;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println("");
        }
        //int[][] result = new FeatureExtraction().makeBoundingBoxMatrix(data);
        String link = "C:\\Users\\NguyenVanDung\\Desktop\\Base line slant angle\\out.png";
        double[] r = new FeatureExtraction().getFeatureVector(data, link);

        for (double ele : r) {
            System.out.print(ele + "|");
        }

    }

    public double[] getFeatureVector(int[][] matrix, String imageLink) throws IOException {
        double[] vector = new double[8];
        vector[0] = getBaselineSlantAngle(imageLink);
        vector[1] = getAspectRatio(matrix);
        vector[2] = getNormalizeArea(matrix);
        vector[3] = getCenterGravity(matrix)[0];
        vector[4] = getCenterGravity(matrix)[1];
        vector[5] = getSlopeJoinCenterGravity(matrix);
        vector[6] = getEdgePoint(matrix);
        vector[7] = getCrossPoint(matrix);

        return vector;
    }

    /**
     *
     * @param imageLink : Đường link đến ảnh cần xoay
     * @return : Góc baseline slant angle
     * @throws IOException : Lỗi vào ra.
     */
    public double getBaselineSlantAngle(String imageLink) throws IOException {
        BufferedImage image = ImageIO.read(new File(imageLink));
        AffineTransform at = new AffineTransform();

        double angle1 = 0; //Luu giu goc co projection lon nhat voi buoc nhay bang 1
        double angle = 0; //Luu giu goc co projection lon nhat voi buoc nhay bang 0.1
        //Tìm góc để có ảnh hình chiếu có giá trị lớn nhất với bước nhảy là 5
        int MAX = 0;
        for (int i = 0; i <= 90; i += 1) {
            at.translate(image.getWidth() / 2, image.getHeight() / 2);
            at.rotate(Math.PI * i / 180);
            at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            op.filter(image, img);
            Raster raster = img.getData();
            int wid = raster.getWidth();
            int hei = raster.getHeight();
            int pixels[][] = new int[wid][hei];
            for (int x = 0; x < wid; x++) {
                for (int y = 0; y < hei; y++) {
                    pixels[x][y] = raster.getSample(x, y, 0);
                }
            }
            int maxrow = getMaxSumOfRow(pixels);
            if (MAX < maxrow) {
                angle1 = i;
                MAX = maxrow;
            }
        }
        //Tìm góc để có ảnh hình chiếu có giá trị lớn nhất với bước nhảy là 1
        MAX = 0;
        for (double i = angle1 - 0.9; i <= angle1 + 0.9; i += 0.1) {
            at.translate(image.getWidth() / 2, image.getHeight() / 2);
            at.rotate(Math.PI * i / 180);
            at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            op.filter(image, img);
            Raster raster = img.getData();
            int wid = raster.getWidth();
            int hei = raster.getHeight();
            int pixels[][] = new int[wid][hei];
            for (int x = 0; x < wid; x++) {
                for (int y = 0; y < hei; y++) {
                    pixels[x][y] = raster.getSample(x, y, 0);
                }
            }
            int maxrow = getMaxSumOfRow(pixels);
            if (MAX < maxrow) {
                angle = i;
                MAX = maxrow;
            }
        }
        return angle;
    }

    /**
     *
     * @param matrix Mảng hai chiều các số thuộc kiểu int
     * @return Giá trị lớn nhất của hàng có hình chiếu lên trên trục y
     */
    private static int getMaxSumOfRow(int[][] matrix) {
        int numRow = matrix.length;
        int numCol = matrix[0].length;
        int max = 0;
        for (int i = 0; i < numRow; i++) {
            int sum = 0;
            for (int j = 0; j < numCol; j++) {
                sum += matrix[i][j];
            }
            if (max < sum) {
                max = sum;
            }
        }
        return max;
    }

    /**
     *
     * @param image : Mảng bounding box của ảnh nhị phân
     * @return Số lượng điểm cross point
     */
    public int getCrossPoint(int[][] image) {
        int M = image.length;
        int N = image[0].length;
        int count = 0;
        int[][] boundImage = new int[M + 2][N + 2];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                boundImage[i + 1][j + 1] = image[i][j];
            }
        }
//        System.out.println("");
//        for (int i = 0; i < M + 2; i++) {
//            for (int j = 0; j < N + 2; j++) {
//                System.out.print(boundImage[i][j] + " ");
//            }
//            System.out.println("");
//        }

        for (int i = 1; i < M + 1; i++) {
            for (int j = 1; j < N + 1; j++) {
                int sum = 0;
                if (boundImage[i][j] != 0) {
                    for (int l = i - 1; l <= i + 1; l++) {
                        for (int k = j - 1; k <= j + 1; k++) {
                            sum += boundImage[l][k];
                        }
                    }

                    //sum lon hon bang 4 vi tinh ca tong diem boundImage[i][j]
                    if (sum >= 4) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     *
     * @param image : Mảng bounding box của ảnh nhị phân
     * @return Số lượng điểm edge point
     */
    public int getEdgePoint(int[][] image) {
        int M = image.length;
        int N = image[0].length;
        int count = 0;
        int[][] boundImage = new int[M + 2][N + 2];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                boundImage[i + 1][j + 1] = image[i][j];
            }
        }
//        System.out.println("");
//        for (int i = 0; i < M + 2; i++) {
//            for (int j = 0; j < N + 2; j++) {
//                System.out.print(boundImage[i][j] + " ");
//            }
//            System.out.println("");
//        }

        for (int i = 1; i < M + 1; i++) {
            for (int j = 1; j < N + 1; j++) {
                int sum = 0;
                if (boundImage[i][j] != 0) {
                    for (int l = i - 1; l <= i + 1; l++) {
                        for (int k = j - 1; k <= j + 1; k++) {
                            sum += boundImage[l][k];
                        }
                    }

                    //sum bang 2 vi tinh ca tong diem boundImage[i][j]
                    if (sum == 2) {
                        count++;
                    }
                }

            }
        }
        return count;
    }

    /**
     *
     * @param image : Mảng bounding box của ảnh nhị phân
     * @return : Góc giữa hai điểm trọng tâm của hai nửa ảnh.Đơn vị radian
     *
     */
    public double getSlopeJoinCenterGravity(int[][] image) {
        int M = image.length;
        int N = image[0].length;
        int Mid = N / 2;
        int Weight = 0;
        int weiX = 0;
        int weiY = 0;
        double[] center = new double[4];
        for (int j = 0; j < Mid; j++) {
            for (int i = 0; i < M; i++) {
                if (image[i][j] != 0) {
                    Weight++;
                    weiX += j;
                    weiY += i;
                }
            }
        }
        if (Weight == 0) {
            return -1;
        }
        center[0] = (double) weiX / Weight;
        center[1] = (double) weiY / Weight;

        Weight = 0;
        weiX = 0;
        weiY = 0;

        for (int j = Mid; j < N; j++) {
            for (int i = 0; i < M; i++) {
                if (image[i][j] != 0) {
                    Weight++;
                    weiX += j;
                    weiY += i;
                }
            }
        }

        if (Weight == 0) {
            return -1;
        }
        center[2] = (double) weiX / Weight;
        center[3] = (double) weiY / Weight;

        double Y = center[3] - center[1] + 1;
        double X = center[2] - center[0] + 1;
        double tan = Y / X;
        return (float) Math.atan(tan);
    }
    /**
     *
     * @param image : Mảng bounding box của ảnh nhị phân
     * @return : Mảng một chiều 2 phần tử chứa tọa độ trên trục x và trục y của
     * chữ kí
     */
    public double[] getCenterGravity(int[][] image) {
        int standard = 1000; //Chuẩn hóa các ảnh bounding box thành ảnh 1000x1000
        int Weight = 0;
        int weiX = 0;
        int weiY = 0;
        int M = image.length;
        int N = image[0].length;
        double[] crdnt = new double[2];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (image[i][j] != 0) {
                    Weight++;
                    weiX += j;
                    weiY += i;
                }
            }
        }

        if (Weight == 0) {
            return null;
        }

        crdnt[0] = (double) weiX / Weight;
        crdnt[1] = (double) weiY / Weight;

        //Nhân với chuẩn hóa ảnh là 1000 x 1000
        crdnt[0] = crdnt[0] * standard / M;
        crdnt[1] = crdnt[1] * standard / N;

        return crdnt;
    }

    /**
     * @param matrix: Mảng bounding box của ảnh nhị phân
     * @return Tỉ lệ giữa những điểm ảnh khác 0 và những điểm ảnh bằng 0
     */
    public double getNormalizeArea(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;
        int count = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (matrix[i][j] != 0) {
                    count++;
                }
            }
        }
        return (double) count / (M * N);
    }

    /**
     * @param matrix : Mảng matran kiểu int[][]
     * @return aspect ratio của bounding box
     */
    public float getAspectRatio(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;
        return (float) M / (float) N;
    }

    /**
     * @return : Mảng ảnh bounding box.
     * @param matrix : Mảng ảnh hai chiều nhị phân.
     */
    public int[][] makeBoundingBoxMatrix(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;
        int X1 = M - 1;
        int X2 = 0;
        int Y1 = N - 1;
        int Y2 = 0;

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (matrix[i][j] != 0) {
                    X1 = X1 > i ? i : X1;
                    X2 = X2 < i ? i : X2;
                    Y1 = Y1 > j ? j : Y1;
                    Y2 = Y2 < j ? j : Y2;
                }
            }
        }

        int[][] boundingbox = new int[X2 - X1 + 1][Y2 - Y1 + 1];
        for (int i = X1; i <= X2; i++) {
            for (int j = Y1; j <= Y2; j++) {
                boundingbox[i - X1][j - Y1] = matrix[i][j];
            }
        }

        return boundingbox;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getM() {
        return M;
    }

    public void setM(int M) {
        this.M = M;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

}
