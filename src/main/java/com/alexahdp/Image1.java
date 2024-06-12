package com.alexahdp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Image1 {
    public static final String SOURCE_FILE = "./resources/IMG_7374.jpeg";
    public static final String DEST_FILE = "./out/IMG_7374.jpeg";

    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File(SOURCE_FILE));
            BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            long startTime = System.currentTimeMillis();
            // 71 ms
            recolorSingleThread(image, resultImage);

            // 73 ms
//            recolorMultiThreaded(image, resultImage, 6);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Duration: " + duration);

            File outputFile = new File(DEST_FILE);
            ImageIO.write(resultImage, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;
                recolorImage(originalImage, resultImage, leftCorner, topCorner, width, height);
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorSingleThread(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreed(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if (isShadeOfGrey(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGrey(int red, int green, int blue) {
        int threshold = 30;
        return Math.abs(red - green) < threshold && Math.abs(red - blue) < threshold && Math.abs(green - blue) < threshold;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= 0xFF000000; // set alpha to 255
        return rgb;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
    public static int getGreed(int rgb) {
        return rgb & 0x0000FF00;
    }
    public static int getRed(int rgb) {
        return rgb & 0x00FF0000;
    }
}
