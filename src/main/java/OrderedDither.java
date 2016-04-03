import java.awt.image.BufferedImage;

public class OrderedDither {
	final int threshold = 128;

	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {

		int matrix[][] = { 
				{ 1, 9, 3, 11 },
				{ 13, 5, 15, 7 }, 
				{ 4, 12, 2, 10 },
				{ 16, 8, 14, 6 } };

		int gray;

		for (int x = 0; x < sourceImg.getWidth(); ++x) {
			for (int y = 0; y < sourceImg.getHeight(); ++y) {

				int rgb = sourceImg.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				int grayLevel = (r + g + b) / 3;

				gray = grayLevel + (grayLevel * matrix[x % 4][y % 4]) / 17;

				if (gray < threshold) {
					gray = 0x0;
				} else {
					gray = 0xFFFFFF;
					
				}

				// gray = (gray << 16) + (gray << 8) + gray;
				sourceImg.setRGB(x, y, gray);

			}
		}
		return sourceImg;

	}

}
