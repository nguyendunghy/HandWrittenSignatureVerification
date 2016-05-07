/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwrittensignatureverification;

import com.sun.xml.internal.ws.server.sei.InvokerTube;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author NguyenVanDung
 */
public class FeatureExtraction {

    private int[][] matrix;
    private int[][] bbMatrix;   //Bounding box matrix
    private int M;
    private int N;

    public FeatureExtraction(int[][] matrix) {
        this.matrix = matrix;
    }

    public FeatureExtraction() {
    }

    public static void main(String[] args) throws IOException {

//        String link = "C:\\Users\\NguyenVanDung\\Desktop\\Base line slant angle\\Baseline\\9.png";
//        int[][] data = new Preprocessing(link).getBinaryImage();
//        FeatureExtraction feature = new FeatureExtraction();
//
//        //int[][] edgeData = feature.filter(data, "sobel");
//        //feature.saveImage(edgeData, "edgeImage");
//        JFrame frame = new JFrame("Test");
//
//        frame.add(new JComponent() {
//
//            BufferedImage image = ImageIO.read(new File(link));
//
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//
//                double angle = feature.getBaselineSlantAngle(data);
//                //double angle = 0;
//                feature.drawChart(data, (float) angle);
//
//                System.out.println("Baseline slant angle : " + angle);
//                AffineTransform att = new AffineTransform();
//
//                // 4. translate it to the center of the component
//                att.translate(getWidth() / 2, getHeight() / 2);
//                // 3. do the actual rotation
//                att.rotate(angle);
//
//                // 2. just a scale because this image is big
//                att.scale(0.5, 0.5);
//
//                // 1. translate the object so that you rotate it around the 
//                //    center (easier :))
//                att.translate(-image.getWidth() / 2, -image.getHeight() / 2);
//                // draw the image
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.drawImage(image, att, null);
//
//            }
//        });
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 800);
//        frame.setVisible(true);
        int[][] test = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                test[i][j] = i == j || i == 8 - j ?  0 : 0;
            }
        }

        test[0][1] = 1;
        test[1][0] = 1;
        test[1][2] = 1;
        test[2][1] = 1;
        test[2][4] = 1;
        test[3][3] = 1;
        test[3][5] = 1;
        test[4][4] = 1;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(test[i][j] + " ");
            }
            System.out.println("");
        }

        FeatureExtraction fe = new FeatureExtraction();
        double[] re = fe.getSixFoldSurface(test);
        for (int i = 0; i < re.length; i++) {
            System.out.println(re[i]);
        }

    }

    /**
     *
     * @param matrix : Ma trận bounding box của ảnh chữ ký.
     * @return : Vector đặc trưng.
     */
    public double[] getFeatureVector(int[][] image) {
        double[] vector = new double[8];
        int[][] matrix = getBoundingBoxMatrix(image);
        //vector[0] = getBaselineSlantAngle(matrix);
        vector[0] = 1;
        vector[1] = getAspectRatio(matrix);
        vector[2] = getNormalizeArea(matrix);
        vector[3] = getCenterGravity(matrix)[0];
        vector[4] = getCenterGravity(matrix)[1];
        vector[5] = getSlopeJoinCenterGravity(matrix);
        vector[6] = getEdgePoint(matrix);
        vector[7] = getCrossPoint(matrix);
        return vector;
    }

    public double[] getSixFoldSurface(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;

        if (M == 0 || N < 3) {
            return null;
        }
        double[] re = new double[6];
        int N1 = N / 3;
        int N2 = 2 * N / 3;
        int M1, M2, M3;

        int pro = 0;
        int weigh = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N1; j++) {
                if (matrix[i][j] != 0) {
                    weigh += matrix[i][j];
                    pro += matrix[i][j] * i;
                }
            }
        }

        M1 = weigh == 0 ? M / 2 : (int) pro / weigh;

        pro = 0;
        weigh = 0;
        for (int i = 0; i < M; i++) {
            for (int j = N1; j < N2; j++) {
                if (matrix[i][j] != 0) {
                    weigh += matrix[i][j];
                    pro += matrix[i][j] * i;
                }
            }
        }
        M2 = weigh == 0 ? M / 2 : (int) pro / weigh;

        pro = 0;
        weigh = 0;
        for (int i = 0; i < M; i++) {
            for (int j = N2; j < N; j++) {
                if (matrix[i][j] != 0) {
                    weigh += matrix[i][j];
                    pro += matrix[i][j] * i;
                }
            }
        }
        M3 = weigh == 0 ? M / 2 : (int) pro / weigh;

        System.out.println("M1,M2,M3 = " + M1 + " " + M2 + " " + M3);

        for (int i = 0; i < M1; i++) {
            for (int j = 0; j < N1; j++) {
                re[0] = matrix[i][j] != 0 ? re[0] + 1 : re[0];
            }
        }
        re[0] = M1 * N1 == 0 ? 0 : re[0] / (M1 * N1);

        for (int i = M1; i < M; i++) {
            for (int j = 0; j < N1; j++) {
                re[1] = matrix[i][j] != 0 ? re[1] + 1 : re[1];
            }
        }
        re[1] = ((M - M1) * N1) == 0 ? 0 : re[1] / ((M - M1) * N1);

        for (int i = 0; i < M2; i++) {
            for (int j = N1; j < N2; j++) {
                re[2] = matrix[i][j] != 0 ? re[2] + 1 : re[2];
            }
        }
        re[2] = (M2 * (N2 - N1)) == 0 ? 0 : re[2] / (M2 * (N2 - N1));

        for (int i = M2; i < M; i++) {
            for (int j = N1; j < N2; j++) {
                re[3] = matrix[i][j] != 0 ? re[3] + 1 : re[3];
            }
        }
        re[3] = ((M - M2) * (N2 - N1)) == 0 ? 0 : re[3] / ((M - M2) * (N2 - N1));

        for (int i = 0; i < M3; i++) {
            for (int j = N2; j < N; j++) {
                re[4] = matrix[i][j] != 0 ? re[4] + 1 : re[4];
            }
        }
        re[4] = (M3 * (N - N2)) == 0 ? 0 : re[4] / (M3 * (N - N2));

        for (int i = M3; i < M; i++) {
            for (int j = N2; j < N; j++) {
                re[5] = matrix[i][j] != 0 ? re[5] + 1 : re[5];
            }
        }
        re[5] = ((M - M3) * (N - N2)) == 0 ? 0 : re[5] / ((M - M3) * (N - N2));

        return re;
    }

    /**
     *
     * @param matrix Matran anh bounding box
     * @return Ti le diem anh khac 0 tren ba phan
     */
    public double[] getTriSurface(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;

        if (M == 0 || N < 3) {
            return null;
        }
        double[] re = new double[3];
        int N1 = N / 3;
        int N2 = 2 * N / 3;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N1; j++) {
                re[0] = matrix[i][j] != 0 ? re[0] + 1 : re[0];
            }
        }
        re[0] = re[0] / (M * N1);

        for (int i = 0; i < M; i++) {
            for (int j = N1; j < N2; j++) {
                re[1] = matrix[i][j] != 0 ? re[1] + 1 : re[1];
            }
        }
        re[1] = re[1] / (M * (N2 - N1));

        for (int i = 0; i < M; i++) {
            for (int j = N2; j < N; j++) {
                re[2] = matrix[i][j] != 0 ? re[2] + 1 : re[2];
            }
        }
        re[2] = re[2] / (M * (N - N2));

        return re;
    }

    /**
     *
     * @param matrix : Mảng hai chiều bounding box của ảnh
     * @return : Góc baseline slant angle
     */
    public double getBaselineSlantAngle(int[][] matrix) {
        int M = matrix.length;
        int N = matrix[0].length;
        int numPeak = 5;
        int seg = 20;

        BufferedImage image = new BufferedImage(M, N, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                int value = matrix[i][j] << 16 | matrix[i][j] << 8 | matrix[i][j];
                image.setRGB(i, j, value);
            }
        }
        AffineTransform at = new AffineTransform();

        double angle1 = 0; //Luu giu goc co projection lon nhat voi buoc nhay bang 1
        double angle = 0; //Luu giu goc co projection lon nhat voi buoc nhay bang 0.1
        //Tìm góc để có ảnh hình chiếu có giá trị lớn nhất với bước nhảy là 5
        double MAX = 0;
        for (int i = 0; i <= 90; i++) {
            at.translate(image.getWidth() / 2, image.getHeight() / 2);
            at.rotate(Math.PI * i / 180);
            at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
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

            double maxrow = getMaxAveragePeakInSegment(pixels, seg, numPeak);
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
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
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
            double maxrow = getMaxAveragePeakInSegment(pixels, seg, numPeak);
            if (MAX < maxrow) {
                angle = i;
                MAX = maxrow;
            }
        }
        return (angle * Math.PI) / 180;
    }

    public double getMaxAverageInSegment(int[][] matrix, int segLength, int numMax) {
        //Kiem tra dieu kien cua bien
        if (matrix == null) {
            return -1;
        }
        if (segLength < 1 || segLength > matrix.length) {
            return -1;
        }

        int M = matrix.length;
        int N = matrix[0].length;
        int[] proVec = new int[M];
        double averageMax = 0;
        int countMax = 0;

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                proVec[i] += matrix[i][j];
            }
        }
        for (int i = 0; i <= M - segLength; i = i + segLength) {
            int[] tmp = new int[segLength];
            for (int j = i; j < i + segLength; j++) {
                tmp[j - i] = proVec[j];
            }
            tmp = SelectionSort(tmp);
            int num = numMax > segLength ? segLength : numMax;
            for (int t = 0; t < num; t++) {
                averageMax += tmp[segLength - 1 - t];
                countMax++;
            }
        }

        if (M % segLength != 0) {
            int odd = M % segLength;
            int[] tmp = new int[segLength];
            for (int j = M - odd; j < M; j++) {
                tmp[j - M + odd] = proVec[j];
            }
            tmp = SelectionSort(tmp);
            int num = numMax > segLength ? segLength : numMax;
            for (int t = 0; t < num; t++) {
                averageMax += tmp[segLength - 1 - t];
                countMax++;
            }
        }

        if (countMax == 0) {
            return -1;
        }

        return (double) averageMax / countMax;

    }

    /**
     *
     * @param matrix
     * @param num Do dai doan
     * @return
     */
    public double getMaxAveragePeakInSegment(int[][] matrix, int segLength, int numPeak) {
        //Kiem tra dieu kien cua bien
        if (matrix == null) {
            return -1;
        }
        if (segLength < 1 || segLength > matrix.length) {
            return -1;
        }

        int M = matrix.length;
        int N = matrix[0].length;
        int[] proVec = new int[M];
        int countPeak = 0;
        int averagePeak = 0;

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                proVec[i] += matrix[i][j];
            }
        }
        for (int i = 0; i <= M - segLength; i = i + segLength) {
            int[] tmpPeak = new int[segLength];
            int count = 0;
            for (int j = i; j < i + segLength; j++) {
                if (j == 0 || j == M - 1) {
                    continue;
                }
                if (proVec[j] > proVec[j - 1] && proVec[j] > proVec[j + 1]) {
                    tmpPeak[count] = proVec[j];
                    count++;
                }
            }
            tmpPeak = SelectionSort(tmpPeak);
            int num = count > numPeak ? numPeak : count;
            for (int t = 0; t < num; t++) {
                averagePeak += tmpPeak[segLength - 1 - t];
                countPeak++;
            }

        }

        if (M % segLength != 0) {
            int odd = M % segLength;
            int[] tmpPeak = new int[segLength];
            int count = 0;
            for (int j = M - odd; j < M - 1; j++) {
                if (proVec[j] > proVec[j - 1] && proVec[j] > proVec[j + 1]) {
                    tmpPeak[count] = proVec[j];
                    count++;
                }
            }
            tmpPeak = SelectionSort(tmpPeak);
            int num = count > numPeak ? numPeak : count;
            for (int t = 0; t < num; t++) {
                averagePeak += tmpPeak[segLength - 1 - t];
                countPeak++;
            }
        }

        if (countPeak == 0) {
            return -1;
        }

        return (double) averagePeak / countPeak;
    }

    /**
     *
     * @param matrix Mảng hai chiều các số thuộc kiểu int
     * @param num Số dòng quét ngang ảnh
     * @return Giá trị lớn nhất của hàng có hình chiếu lên trên trục y
     */
    public int getMaxAverageOfPeak(int[][] matrix, int num) {
        //Kiem tra dieu kien cua bien
        if (matrix == null) {
            return -1;
        }
        if (num < 1 || num > matrix.length) {
            return -1;
        }

        int numRow = matrix.length;
        int numCol = matrix[0].length;
        int[] proVec = new int[numRow];
        int countPeak = 0;
        int averageMaxPeak = 0;
        int[] Peak = new int[numRow];
        int MIN_PEAK = 0;
        int countBigPeak = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                proVec[i] += matrix[i][j];
            }
        }

        for (int i = 1; i < numRow - 1; i++) {
            if (proVec[i] > proVec[i - 1] && proVec[i] > proVec[i + 1]) {
                Peak[countPeak] = proVec[i];
                countPeak++;
            }
        }

        int[] largePeak = new int[countPeak];
        for (int i = 0; i < countPeak; i++) {
            if (Peak[i] >= MIN_PEAK) {
                largePeak[countBigPeak] = Peak[i];
                countBigPeak++;
            }
        }
        System.out.println("Numbers of peaks are : " + countPeak);
        System.out.println("Numbers of big peaks are : " + countBigPeak);
        if (countPeak == 0) {
            return 0;
        }

        largePeak = SelectionSort(largePeak);
        num = num > countBigPeak ? countBigPeak : num;
        // num = countAverPeak / 2;
        for (int i = 0; i < num; i++) {
            averageMaxPeak += largePeak[countPeak - 1 - i];
        }
        System.out.println("Average Max Peak : " + averageMaxPeak);
        if (num == 0) {
            return -1;
        }
        averageMaxPeak = averageMaxPeak / num;

//        for(int i=0; i< proVec.length; i++){
//            System.out.print(proVec[i] + " ");
//        }
//        System.out.println("");
//        
//        for (int i = 0; i <= numRow - num; i = i + num) {
//            int sum = 0;
//            for (int t = i; t < i + num; t++) {
//                sum += proVec[t];
//            }
//            max = max < sum ? sum : max;
//        }
//
//        if (numRow % num != 0) {
//            int sum = 0;
//            for (int i = numRow - num; i < numRow; i++) {
//                sum += proVec[i];
//            }
//            max = max < sum ? sum : max;
//        }
        return averageMaxPeak;
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

        double Y = center[3] - center[1];
        double X = center[2] - center[0];
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
        crdnt[0] = crdnt[0] * standard / N;
        crdnt[1] = crdnt[1] * standard / M;

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
    public int[][] getBoundingBoxMatrix(int[][] matrix) {
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

    public int[] SelectionSort(int[] A) {
        int N = A.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i + 1; j < N; j++) {
                if (A[j] < A[min]) {
                    min = j;
                }
            }
            int tmp = A[min];
            A[min] = A[i];
            A[i] = tmp;
        }

        return A;
    }

    //Lay matran do sang cua anh
    public int[][] getImageMatrix(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            Raster raster = img.getData();
            int wi = raster.getWidth();
            int he = raster.getHeight();
            int pixels[][] = new int[wi][he];
            for (int x = 0; x < wi; x++) {
                for (int y = 0; y < he; y++) {
                    pixels[x][y] = raster.getSample(x, y, 0);
                }
            }

            return pixels;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void drawChart(int[][] data, float Ag) {
        int M = data.length;
        int N = data[0].length;
        BufferedImage image = new BufferedImage(M, N, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                int value = data[i][j] << 16 | data[i][j] << 8 | data[i][j];
                image.setRGB(i, j, value);
            }
        }
        AffineTransform at = new AffineTransform();
        at.translate(M / 2, N / 2);
        at.rotate(Ag);
        at.translate(-M / 2, -N / 2);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage img = new BufferedImage(M, N, BufferedImage.TYPE_INT_RGB);
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

        float[] proVec = new float[wid];
        for (int i = 0; i < wid; i++) {
            for (int j = 0; j < hei; j++) {
                proVec[i] += pixels[i][j];
            }
        }

        FrequencyChart chart = new FrequencyChart("Biểu đồ", "Biểu đồ hình chiếu ngang", proVec, "dòng", "điểm");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }

    public int[][] filter(int[][] I, String name) {
        int M = I.length;
        int N = I[0].length;
        if (name.equalsIgnoreCase("sobel")) {
            int[][] soX = new int[M][N];
            int[][] soY = new int[M][N];
            int[][] result = new int[M][N];
            for (int i = 1; i < M - 1; i++) {
                for (int j = 1; j < N - 1; j++) {
                    soX[i][j] = I[i - 1][j - 1] - I[i - 1][j + 1] + 2 * I[i][j - 1] - 2 * I[i][j + 1] + I[i + 1][j - 1] - I[i + 1][j + 1];
                    soY[i][j] = I[i - 1][j - 1] + 2 * I[i - 1][j] + I[i - 1][j + 1] - I[i + 1][j - 1] - 2 * I[i + 1][j] - I[i + 1][j + 1];
                    result[i][j] = soX[i][j] + +soY[i][j] > 0 ? 1 : 0;
                }
            }
            return result;

        }

        if (name.equalsIgnoreCase("prewitt")) {
            int[][] PreX = new int[M][N];
            int[][] PreY = new int[M][N];
            int[][] result = new int[M][N];
            for (int i = 1; i < M - 1; i++) {
                for (int j = 1; j < N - 1; j++) {
                    PreX[i][j] = I[i - 1][j - 1] - I[i - 1][j + 1] + I[i][j - 1] - I[i][j + 1] + I[i + 1][j - 1] - I[i + 1][j + 1];
                    PreY[i][j] = I[i - 1][j - 1] + I[i - 1][j] + I[i - 1][j + 1] - I[i + 1][j - 1] - I[i + 1][j] - I[i + 1][j + 1];
                    result[i][j] = (PreX[i][j] + PreY[i][j]) > 0 ? 1 : 0;
                }
            }
            return result;
        }
        return null;
    }

    /**
     *
     * @param matrix
     * @param name
     * @return
     */
    private boolean saveImage(int[][] matrix, String name) {
        int M = matrix.length;
        int N = matrix[0].length;
        try {
            BufferedImage theImage = new BufferedImage(M, N, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < M; y++) {
                for (int x = 0; x < N; x++) {
                    int tmp = matrix[y][x] != 0 ? 255 : 0;
                    int value = tmp << 16 | tmp << 8 | tmp;
                    theImage.setRGB(y, x, value);
                }
            }
            name = name + ".png";
            File outputfile = new File(name);
            ImageIO.write(theImage, "png", outputfile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
