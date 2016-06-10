/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwrittensignatureverification;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author NguyenVanDung
 */
public class Preprocessing {

    private int[][] matrix;
    private int M;
    private int N;
    private String link;
    private float thres;

    public Preprocessing() {
    }

    public Preprocessing(String link) {
        this.matrix = getImageMatrix(new File(link));
        this.M = matrix.length;
        this.N = matrix[0].length;
    }

    public static void main(String[] args) {
        String link = "C:\\Users\\NguyenVanDung\\Desktop\\Base line slant angle\\Baseline\\9.png";
        Preprocessing pre = new Preprocessing();
        pre.getImageMatrix(new File(link));
        
//        int X = pre.getM();
//        int Y = pre.getN();

        int[][] image = new int[5][5];
        int X = image.length;
        int Y = image[0].length;
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                image[i][j] = 0;
            }
        }
        image[2][2] = 125;
        image[3][3] = 200;
        image[4][4] = 150;

        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                System.out.print(image[i][j] + "  ");
            }
            System.out.println("");
        }

//        int[][] matrix = pre.smothenImage(image);
//        for (int i = 0; i < X; i++) {
//            for (int j = 0; j < Y; j++) {
//                System.out.print(matrix[i][j] + " ");
//            }
//            System.out.println("");
//        }
        float thres = pre.thresCalculate(image);
        System.out.println(thres);

    }

    /**
     *
     * @return Mảng ảnh nhị phân
     */
    public int[][] getBinaryImage() {
        this.matrix = invertImage(matrix);
        this.matrix = makeIdealImge(matrix);
        this.matrix = smothenImage(matrix);
        this.thres = thresCalculate(matrix);
        this.matrix = makeBinaryImage(matrix, thres);
        return this.matrix;
    }

    /**
     *
     * @param image Mảng ảnh dạng int[][]
     * @param Thres Ngưỡng để nhị phân hóa ảnh
     * @return
     */
    public int[][] makeBinaryImage(int[][] image, float Thres) {
        int X = image.length;
        int Y = image[0].length;
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                image[i][j] = image[i][j] > Thres ? 1 : 0;
            }
        }
        return image;
    }

    /**
     * @param image : Mảng ảnh int[][]
     * @return : Ngưỡng để tạo ảnh nhị phân
     */
    public float thresCalculate(int[][] image) {
        int X = image.length;
        int Y = image[0].length;
        float diff = 5;
        float T1 = 100;
        float T2 = 0;
        //Khoi tao T1 bang gia tri trung binh do sang diem anh
        long sum = 0;
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                sum += image[i][j];
            }
        }
        T1 = sum / (X * Y);

        while (diff >= 0.5) {
            int sum1 = 0;
            int sum2 = 0;
            int count1 = 0;
            int count2 = 0;

            for (int i = 0; i < X; i++) {
                for (int j = 0; j < Y; j++) {
                    if (image[i][j] > T1) {
                        sum1 += image[i][j];
                        count1++;
                    } else {
                        sum2 += image[i][j];
                        count2++;
                    }
                }
            }

            float avr1 = (float) sum1 / count1;
            float avr2 = (float) sum2 / count2;

            T2 = (avr1 + avr2) / 2;
            diff = Math.abs(T2 - T1);
            T1 = T2;

        }
        System.out.println("Image threshold : " + T1);
        return T1;
    }

    /**
     * @param image : Mảng ảnh int[][]
     * @return : Mảng ảnh dạng int[][] đã được làm trơn
     */
    public int[][] smothenImage(int[][] image) {
        int X = image.length;
        int Y = image[0].length;
        int[][] mtr = new int[X][Y];
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                int sum = 0;
                for (int l = i - 1; l <= i + 1; l++) {
                    for (int k = j - 1; k <= j + 1; k++) {
                        sum += image[l][k];
                    }
                }
                mtr[i][j] = sum / 9;
            }
        }
        return mtr;
    }

    /**
     * @param image : Mảng ảnh int
     * @return : Mảng ảnh dạng int[][] mà mỗi điểm ảnh đã trừ đi trung bình cột
     */
    public int[][] makeIdealImge(int[][] image) {
        int X = image.length;
        int Y = image[0].length;
        for (int j = 0; j < Y; j++) {
            int sum = 0;
            for (int i = 0; i < X; i++) {
                sum += image[i][j];
            }
            int avr = sum / X;
            for (int i = 0; i < X; i++) {
                int tmp = image[i][j] - avr;
                image[i][j] = tmp > 0 ? tmp : 0;
            }
        }
        return image;
    }

    /**
     * @param image : mảng ảnh int
     * @return : mảng ảnh đã đảo ngược
     * @see Đảo ngược giá trị của ảnh
     */
    public int[][] invertImage(int[][] image) {
        int X = image.length;
        int Y = image[0].length;
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                image[i][j] = 255 - image[i][j];
            }
        }
        return image;
    }

    //Lay matran do sang cua anh
    public int[][] getImageMatrix(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            Raster raster = img.getData();
            int numCol = raster.getWidth();
            int numRow = raster.getHeight();
            int pixels[][] = new int[numRow][numCol];
            for (int x = 0; x < numRow; x++) {
                for (int y = 0; y < numCol; y++) {
                    pixels[x][y] = raster.getSample(y, x, 0);
                }
            }

            return pixels;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public float getThres() {
        return thres;
    }

    public void setThres(float thres) {
        this.thres = thres;
    }

}
