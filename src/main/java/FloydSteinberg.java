import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.SynchronousQueue;

public class FloydSteinberg {

	final int threshold;
	final int[][] errorDistribution;

	public FloydSteinberg(int threshold, int[][] errorDistribution) {
		this.threshold = threshold;
		this.errorDistribution = errorDistribution;
	}

	public FloydSteinberg(int threshold) {
		this(128, new int[][] { { 0, 0, 7 }, { 3, 5, 1 } });
	}

	public FloydSteinberg() {
		this(128);
	}

	// Hat noch Fehler
	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {

		int grayLevelOldPixel = 0;
		int imageWidth = sourceImg.getWidth();
		int imageHeight = sourceImg.getHeight();
		int errorSum = 0;
		int start = -1;
		//
		// float[][] grades = {{8.0, 7.5, 8.5, 9.0, 8.0}, {8.9, 9.0, 8.6, 8.4,
		// 8.0}, {6.8, 7.1, 7.0, 7.6, 6.5}};

		for (int[] line : errorDistribution) {
			boolean started = false;
			for (int ed : line) {
				errorSum += ed;
				if (started == false) {
					if (ed == 0) {
						start++;
					} else {
						started = true;
					}
				}
			}
		}

		System.out.println(start);
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {

				// rgbValues: { rgb, r, g, b }
				int[] rgbValues = getRGB(sourceImg, x, y);

				grayLevelOldPixel = rgbValues[0];

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

				int newRGBValue;
				for (int yMatrix = 0; yMatrix < errorDistribution.length; yMatrix++) {
					int yWokringPixel = y + yMatrix;
					if (yWokringPixel < imageHeight) {
						for (int xMatrix = 0; xMatrix < errorDistribution[yMatrix].length; xMatrix++) {
							int xWorkingPixel = xMatrix - start + x;
							if (xWorkingPixel >= 0 && xWorkingPixel < imageWidth) {
								double distributionDivideSum = (double) errorDistribution[yMatrix][xMatrix]
										/ (double) errorSum;

								rgbValues = this.getRGB(sourceImg, xWorkingPixel, yWokringPixel);
								newRGBValue = coloursMultiplyError(rgbValues, grayLevelOldPixel, distributionDivideSum);
								sourceImg.setRGB(xWorkingPixel, yWokringPixel, newRGBValue);
							}
						}
					}
				}
			}
		}
		return sourceImg;
	}

	private int[] getRGB(BufferedImage img, int x, int y) {
		int[] rgb = img.getRaster().getPixel(x, y, new int[3]);
		int rgbAverage = (rgb[0] + rgb[1] + rgb[2]) / 3;
		return new int[] { rgbAverage, rgb[0], rgb[1], rgb[2] };

		// int rgb = img.getRGB(x, y);
		// int r = (rgb >> 16) & 0xFF;
		// int g = (rgb >> 8) & 0xFF;
		// int b = (rgb & 0xFF);
		//
		// return new int[] { rgb, r, g, b };
	}

	private int coloursMultiplyError(int[] rgbValues, int error, double matrixValue) {
		int ErrorMultipyMatrix = (int) (matrixValue * error);

		// int r = Math.min(Math.max(rgbValues[1] + ErrorMultipyMatrix, 0),
		// 255);
		// int g = Math.min(Math.max(rgbValues[2] + ErrorMultipyMatrix, 0),
		// 255);
		// int b = Math.min(Math.max(rgbValues[3] + ErrorMultipyMatrix, 0),
		// 255);
		// return new Color(r, g, b).getRGB();

		int newRGB = Math.min(Math.max(rgbValues[0] + ErrorMultipyMatrix, 0), 255);
		return (newRGB << 16) + (newRGB << 8) + newRGB;
	}
}
