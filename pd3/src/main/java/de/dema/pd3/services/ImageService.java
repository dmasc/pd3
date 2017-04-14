package de.dema.pd3.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageService {

    public static byte[] resize(byte[] imageData, String formatName, int scaledWidth, int scaledHeight) throws IOException {
    	if (formatName.toLowerCase().equals("svg")) {
    		return imageData;
    	}
    	
    	InputStream is = new ByteArrayInputStream(imageData);
        BufferedImage inputImage = ImageIO.read(is);
        is.close();
 
        double heightRate = (double) scaledHeight / inputImage.getHeight();
        double widthRate = (double) scaledWidth / inputImage.getWidth();

        return resize(inputImage, formatName, Math.min(heightRate, widthRate));
    }
 
    public static byte[] resize(BufferedImage inputImage, String formatName, double percent) throws IOException {
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
 
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(outputImage, formatName, os);
        return os.toByteArray();
    }

}
