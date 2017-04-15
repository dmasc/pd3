package de.dema.pd3.services;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.dema.pd3.persistence.Image;
import de.dema.pd3.persistence.ImageRepository;
import de.dema.pd3.persistence.User;

/**
 * Service für die Verarbeitung von Bilddaten, die in Form von Bytes vorliegen.
 */
@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepo;
	
	/**
	 * Ändert die Größe eines Bildes proportional zu dessen aktueller Größe und speichert es in der Datenbank. Siehe
	 * {@linkplain #resize(byte[], String, int, int)} für nähere Details.
	 * 
	 * @param owner der Besitzer des Bildes
	 * @param originalImage die Bilddaten in Form von Bytes
	 * @param type der Name des Bildformats, bspw. "jpg", "svg" oder "png".
     * @param maxWidth die gewünschte Breite. Wenn die Höhe des Ausgangsbilds größer ist als die Breite, dann wird dieser Wert ignoriert.
     * @param maxHeight die gewünschte Höhe. Wenn die Breite des Ausgangsbilds größer ist als die Höhe, dann wird dieser Wert ignoriert.
     * @return das gespeicherte skalierte Bild als {@linkplain Image}-Entität.
	 * @throws IOException wenn es beim Einlesen des Bildes zu einem Fehler kam. 
	 * @see {@linkplain #resize(byte[], String, int, int)}
	 */
	public Image save(User owner, byte[] originalImage, String type, int maxWidth, int maxHeight) throws IOException {
		byte[] resizedImage = ImageService.resize(originalImage, type, 300, 300);
		if (resizedImage != null) {
			Image image = new Image.Builder(resizedImage).owner(owner).type(type).build();
			return imageRepo.save(image);
		}
		
		return null;
	}
	
	public void delete(Image image) {
		imageRepo.delete(image);
	}
	
    /**
     * Ändert die Größe eines Bildes proportional zu dessen aktueller Größe. Um eine bessere Qualität zu erzielen, wird die Größe
     * nach und nach halbiert, bis keine weitere Halbierung mehr möglich ist, um die gewünschte Größe zu erreichen. Dann wird
     * das Bild noch ein letztes Mal so skaliert, dass es anschließend der Wunschgröße entspricht.<br>
     * <br>
     * Beispiel: Ein Ausgangsbild der Größe 1000x800 wird wie folgt auf 10%, also 100x80 skaliert:<br>
     * 1000x800 -> 500x400 -> 250x200 -> 125x100 -> 100x80
     * 
     * @param inputImage das Ausgangsbild als Byte Array.
     * @param formatName der Name des Bildformats, bspw. "jpg", "svg" oder "png".
     * @param maxWidth die gewünschte Breite. Wenn die Höhe des Ausgangsbilds größer ist als die Breite, dann wird dieser Wert ignoriert.
     * @param maxHeight die gewünschte Höhe. Wenn die Breite des Ausgangsbilds größer ist als die Höhe, dann wird dieser Wert ignoriert.
     * @return das skalierte Bild als Byte Array.
     */
    public static byte[] resize(byte[] imageData, String formatName, int maxWidth, int maxHeight) throws IOException {
    	if (formatName.toLowerCase().equals("svg")) {
    		return imageData;
    	}
    	
    	InputStream is = new ByteArrayInputStream(imageData);
        BufferedImage inputImage = ImageIO.read(is);
        is.close();
 
        if (inputImage == null) {
        	return null;
        }
        
        double heightRate = (double) maxHeight / inputImage.getHeight();
        double widthRate = (double) maxWidth / inputImage.getWidth();

        BufferedImage outputImage = resize(inputImage, formatName, Math.min(heightRate, widthRate));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(outputImage, formatName, os);
        
        return os.toByteArray();
    }
 
    /**
     * Ändert die Größe eines Bildes prozentual zu seiner Ausgangsgröße. Um eine bessere Qualität zu erzielen, wird die Größe
     * nach und nach halbiert, bis keine weitere Halbierung mehr möglich ist, um die gewünschte Größe zu erreichen. Dann wird
     * das Bild noch ein letztes Mal so skaliert, dass es anschließend der Wunschgröße entspricht.<br>
     * <br>
     * Beispiel: Ein Ausgangsbild der Größe 1000x800 wird wie folgt auf 10%, also 100x80 skaliert:<br>
     * 1000x800 -> 500x400 -> 250x200 -> 125x100 -> 100x80
     * 
     * @param inputImage das Ausgangsbild.
     * @param formatName der Name des Bildformats, bspw. "jpg", "svg" oder "png".
     * @param percent prozentuale Angabe der gewünschten Skalierung, wobei 100% dem Wert 1 entspricht.
     * @return das skalierte Bild.
     */
    public static BufferedImage resize(BufferedImage inputImage, String formatName, double percent) {
        int scaledWidth = (int) (inputImage.getWidth() * Math.max(percent, .5));
        int scaledHeight = (int) (inputImage.getHeight() * Math.max(percent, .5));
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return percent < .5 ? resize(outputImage, formatName, percent * 2) : outputImage;
    }

}
