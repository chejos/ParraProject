import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class helper {

	public static BufferedImage workOnImage(matrix myMatrix, int yMatrix, int xMatrix, BufferedImage sourceImg,
			int xWorkingPixel, int yWokringPixel, int grayLevelOldPixel) {
		if (myMatrix.errorDistribution[yMatrix][xMatrix] != 0) {
			double distributionDivideSum = myMatrix.errorDistribution[yMatrix][xMatrix] / (double) myMatrix.errorSum;

			int[] workingRgbValue = helper.getRGB(sourceImg, xWorkingPixel, yWokringPixel);
			int workingRgbWithError = helper.coloursMultiplyError(workingRgbValue, grayLevelOldPixel,
					distributionDivideSum);
			sourceImg.setRGB(xWorkingPixel, yWokringPixel, workingRgbWithError);
		}
		return sourceImg;

	}

	public static int[] getRGB(BufferedImage img, int x, int y) {
		int[] rgb = img.getRaster().getPixel(x, y, new int[3]);
		int rgbAverage = (rgb[0] + rgb[1] + rgb[2]) / 3;
		return new int[] { rgbAverage, rgb[0], rgb[1], rgb[2] };

		// int color = img.getRGB(x, y);
		// int blue = color & 0xff;
		// int green = (color & 0xff00) >> 8;
		// int red = (color & 0xff0000) >> 16;
		// int sum = blue + green + red;
		// return new int[] { sum/3, blue, green, red};
	}

	public static int coloursMultiplyError(int[] rgbValues, int error, double matrixValue) {
		int ErrorMultipyMatrix = (int) (matrixValue * error);
		int newRGB = Math.min(Math.max(rgbValues[0] + ErrorMultipyMatrix, 0), 255);
		return (newRGB << 16) + (newRGB << 8) + newRGB;
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static void compareParaWithSec(String resultFilePara, String resultFileSec) throws IOException {
		BufferedImage image = ImageIO.read(new File(resultFileSec));
		BufferedImage imagePara = ImageIO.read(new File(resultFilePara));
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();

		// prüfe alle Pixel auf sw/ws
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {

				int colorSec = image.getRGB(x, y);
				int blueSec = colorSec & 0xff;
				int greenSec = (colorSec & 0xff00) >> 8;
				int redSec = (colorSec & 0xff0000) >> 16;
				int sumSec = blueSec + greenSec + redSec;

				int colorPara = imagePara.getRGB(x, y);
				int bluePara = colorPara & 0xff;
				int greenPara = (colorPara & 0xff00) >> 8;
				int redPara = (colorPara & 0xff0000) >> 16;
				int sumPara = bluePara + greenPara + redPara;

				if (sumSec != 255 * 3 && sumSec != 0) {
					System.out.println();
					System.out.println("sumPara != 0/255");
					System.out.println("error on seq x: " + x + " y: " + y);
					System.out.println("helligkeit: " + sumSec / 3);
					System.out.println("Rot: " + redSec);
					System.out.println("Green: " + greenSec);
					System.out.println("Blau: " + blueSec);
				}
				if (sumPara != 255 * 3 && sumPara != 0) {
					System.out.println();
					System.out.println("sumPara != 0/255");
					System.out.println("error on para (not black or white) x: " + x + " y: " + y);
					System.out.println("helligkeit: " + sumPara / 3);
					System.out.println("Rot: " + redPara);
					System.out.println("Green: " + greenPara);
					System.out.println("Blau: " + bluePara);
				}
				if (sumPara != sumSec) {
					System.out.println();
					System.out.println("sumPara != sumSec");
					System.out.println("error on seq x: " + x + " y: " + y);
					System.out.println("helligkeit: " + sumSec / 3);
					System.out.println("Rot: " + redSec);
					System.out.println("Green: " + greenSec);
					System.out.println("Blau: " + blueSec);

					System.out.println("error on para:");
					System.out.println("helligkeit: " + sumPara / 3);
					System.out.println("Rot: " + redPara);
					System.out.println("Green: " + greenPara);
					System.out.println("Blau: " + bluePara);

				}
			}
		}
	}
}
