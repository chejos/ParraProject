import java.awt.image.BufferedImage;

public class FloydSteinberg {

	final int threshold;

	public FloydSteinberg(int threshold) {
		this.threshold = threshold;
	}

	// Hat noch Fehler
	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {

		int grayLevelOldPixel = 0;
		int imageWidth = sourceImg.getWidth();
		int imageHeight = sourceImg.getHeight();

		double w1 = (7.0 / 16.0);
		double w2 = (3.0 / 16.0);
		double w3 = (5.0 / 16.0);
		double w4 = (1.0 / 16.0);

		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {

				// rgbValues: { rgb, r, g, b }
				int[] rgbValues = getRGB(sourceImg, x, y);

				grayLevelOldPixel = (rgbValues[1] + rgbValues[2] + rgbValues[3]) / 3;

				// Entscheide ob Pixel Weiß oder Schwarz
				if (grayLevelOldPixel < threshold) {
					sourceImg.setRGB(x, y, 0x0);
				} else {
					sourceImg.setRGB(x, y, 0xFFFFFF);
					grayLevelOldPixel -= 0xFF;
				}

				// Floy-Steinberg Fehlerverteilung
				// PseudoCode von
				// https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering

				int xRight = x + 1;
				int xLeft = x - 1;
				int yDown = y + 1;
				int newRGBValue;

				// Pixel rechts daneben
				if (xRight < imageWidth) {
					rgbValues = this.getRGB(sourceImg, xRight, y);
					newRGBValue = coloursMultiplyError(rgbValues, grayLevelOldPixel, w1);
					sourceImg.setRGB(xRight, y, newRGBValue);
				}
				
				// zweite Reihe
				if (yDown < imageHeight) {
					// Pixel links unten
					if (xLeft > 0) {
						rgbValues = this.getRGB(sourceImg, xLeft, yDown);
						newRGBValue = coloursMultiplyError(rgbValues, grayLevelOldPixel, w2);
						sourceImg.setRGB(xLeft, yDown, newRGBValue);
					}

					// Pixel darunter
					rgbValues = this.getRGB(sourceImg, x, yDown);
					newRGBValue = coloursMultiplyError(rgbValues, grayLevelOldPixel, w3);
					sourceImg.setRGB(x, yDown, newRGBValue);

					// Pixel rechts unten
					if (xRight < imageWidth) {
						rgbValues = this.getRGB(sourceImg, xRight, yDown);
						newRGBValue = coloursMultiplyError(rgbValues, grayLevelOldPixel, w4);
						sourceImg.setRGB(xRight, yDown, newRGBValue);
					}
				}
			}
		}
		return sourceImg;
	}

	private int[] getRGB(BufferedImage img, int x, int y) {
		int rgb = img.getRGB(x, y);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb & 0xFF);

		return new int[] { rgb, r, g, b };
	}

	private int coloursMultiplyError(int[] rgbValues, int error, double matrixValue) {
		int r = (int) (rgbValues[1] + matrixValue * error);
		int g = (int) (rgbValues[2] + matrixValue * error);
		int b = (int) (rgbValues[3] + matrixValue * error);
		int rgb = (r + g + b) / 3;

		return (rgb << 16) + (rgb << 8) + rgb;
	}

}
