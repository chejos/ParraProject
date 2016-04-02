

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public class Main {

	public static void main(String[] args) throws IOException {
		
		 // Turn the byte array into a BufferedImage
		//Filename �ndern....
		BufferedImage sourceImg = ImageIO.read( new File("C:\\Users\\Hollow\\Google Drive\\StudiumDH\\_Semester6\\Parralleles Programmieren\\Projekt\\Beispiel.jpg"));

		
		//sourceImg = makeGrayBuffered(sourceImg);
		//sourceImg = makeGray(sourceImg);
		
		//BinaryPicture 
		/*
		BinaryPicture ozu = new BinaryPicture();
		sourceImg = ozu.createBinaryPicture(sourceImg);
		*/
		
		//Ordered Dither
		OrderedDither imageDither = new OrderedDither();
		sourceImg = imageDither.createBinaryPicture(sourceImg);
		
		//Output
	    ImageIO.write( sourceImg, "jpg", new File( "C:\\Users\\Hollow\\Google Drive\\StudiumDH\\_Semester6\\Parralleles Programmieren\\Projekt\\Beispiel_dither.jpg" ) );
	    
	    System.out.println("Programm ended");
	    
	}
	


	//Gray Scale ColorConvert Option
	//http://codehustler.org/blog/java-to-create-grayscale-images-icons/
	public static BufferedImage makeGrayBuffered(BufferedImage img)
	{
		//Gray Scale
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		BufferedImage image = op.filter(img, null);
		
		return image;
	}
	
	//Gray Scale Convert from http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
	public static BufferedImage makeGray(BufferedImage img)
	{
	    for (int x = 0; x < img.getWidth(); ++x) 
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int r = (rgb >> 16) & 0xFF;
	        int g = (rgb >> 8) & 0xFF;
	        int b = (rgb & 0xFF);

	        int grayLevel = (r + g + b) / 3;
	        int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
	        img.setRGB(x, y, gray);
	    }
	    
	    return img;
	}
	
	}		

