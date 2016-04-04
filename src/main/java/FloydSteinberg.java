import java.awt.image.BufferedImage;

public class FloydSteinberg {

	final int threshold;

	public FloydSteinberg(int threshold) {
		this.threshold = threshold;
	}

	// Hat noch Fehler
	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {

		int gray = 0;
		int grayLevelOldPixel = 0;
		int quant_error;
		int imageWidth = sourceImg.getWidth();
		int imageHeight = sourceImg.getHeight();

		float w1 = (float) (7.0 / 16);
		float w2 = (float) (3.0 / 16);
		float w3 = (float) (5.0 / 16);
		float w4 = (float) (1.0 / 16);

		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {

				int rgb = sourceImg.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				grayLevelOldPixel = (r + g + b) / 3;

				// Entscheide ob Pixel Weiﬂ oder Schwarz
				if (grayLevelOldPixel < threshold) {
					gray = 0x0;
				} else {
					gray = 0xFFFFFF;
				}
				sourceImg.setRGB(x, y, gray);

				// Floy-Steinberg Fehlerverteilung
				// PseudoCode von
				// https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
				grayLevelOldPixel = (grayLevelOldPixel << 16) + (grayLevelOldPixel << 8) + grayLevelOldPixel;

				quant_error = grayLevelOldPixel - gray;
				
				int xRight = x + 1;
				int xLeft = x - 1;
				int yDown = y + 1;

				if (xRight < imageWidth) {
					sourceImg.setRGB(xRight, y, (int) (sourceImg.getRGB(xRight, y) + w1 * quant_error));
				}
				if (yDown < imageHeight) {

					// Pixel links unten
					if (xLeft > 0) {
						sourceImg.setRGB(xLeft, yDown, (int) (sourceImg.getRGB(xLeft, yDown) + w2 * quant_error));
					}

					// Pixel unten
					sourceImg.setRGB(x, yDown, (int) (sourceImg.getRGB(x, yDown) + w3 * quant_error));

					// Pixel rechts unten
					if (xRight < imageWidth) {
						sourceImg.setRGB(xRight, yDown, (int) (sourceImg.getRGB(xRight, yDown) + w4 * quant_error));
					}
				}
			}
		}
		return sourceImg;
	}

}
