
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Main {
	
	static final int threshold = 128;
	
	
	public static void main(String[] args) throws IOException {
		final String PATH = "src/main/resources/";
		final String SOURCE = "Beispiel.jpg";
		final String RESULTNAME = "BeispielResult";
		final String RESULTTYPE = "jpg";

		// Turn the byte array into a BufferedImage
		// Filename ändern....
		BufferedImage sourceImg = ImageIO.read(new File(PATH + SOURCE));
		BufferedImage resultImg;

		// sourceImg = makeGrayBuffered(sourceImg);
		// sourceImg = makeGray(sourceImg);

		// BinaryPicture
		/*
		 * sourceImg = = new BinaryPicture().createBinaryPicture(sourceImg);
		 * 
		 */

		
		// Ordered Dither
		//resultImg = new OrderedDither().createBinaryPicture(sourceImg);
		
		//Floyd-Steinberg
		resultImg = new FloydSteinberg(threshold).createBinaryPicture(sourceImg);
		

		// Output
		ImageIO.write(resultImg, RESULTTYPE, new File(PATH + RESULTNAME + "." + RESULTTYPE));

		System.out.println("Programm ended");

	}

	// Gray Scale ColorConvert Option
	// http://codehustler.org/blog/java-to-create-grayscale-images-icons/
	public static BufferedImage makeGrayBuffered(BufferedImage img) {
		// Gray Scale
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		BufferedImage image = op.filter(img, null);

		return image;
	}

	// Gray Scale Convert from
	// http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
	public static BufferedImage makeGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
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
