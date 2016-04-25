package handwrittensignatureverification;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Rgb2greyscale {

    BufferedImage image;
    int width;
    int height;

    public Rgb2greyscale() {

        try {
            String folderPath = "C:\\Users\\NguyenVanDung\\Desktop\\Base line slant angle\\";
            String imagePath = folderPath + "notepad.png";
            File input = new File(imagePath);
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();

            for (int i = 0; i < height; i++) {

                for (int j = 0; j < width; j++) {

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.299);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(red + green + blue,
                            red + green + blue, red + green + blue);

                    image.setRGB(j, i, newColor.getRGB());
                }
            }
            String outImage = folderPath + "grayscale.png";
            File ouptut = new File(outImage);
            ImageIO.write(image, "png", ouptut);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void main(String args[]) throws Exception {
        Rgb2greyscale obj = new Rgb2greyscale();
    }
}
