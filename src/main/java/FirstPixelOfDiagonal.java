import java.awt.image.BufferedImage;

public class FirstPixelOfDiagonal {

	private final int imageWidth;
	private final int imageHeight;
	private int workingX;
	private int workingY;
	private int grayLevelOldPixel;
	private final matrix myMatrix;
	protected BufferedImage sourceImg;

	protected FirstPixelOfDiagonal(BufferedImage myImage, matrix myMatrix, int workingX, int workingY) {
		this.sourceImg = myImage;
		this.myMatrix = myMatrix;
		this.imageHeight = myImage.getHeight();
		this.imageWidth = myImage.getWidth();
		this.workingX = workingX;
		this.workingY = workingY;
	}

	// Average pixels from source, write results into destination.
	protected void work() {
		// rgbValues: { rgb, r, g, b }

		int[] rgbValue = helper.getRGB(sourceImg, workingX, workingY);
		grayLevelOldPixel = rgbValue[0];
		// Entscheide ob Pixel Weiﬂ oder Schwarz
		if (grayLevelOldPixel < myMatrix.threshold) {
			sourceImg.setRGB(workingX, workingY, 0x0);
		} else {
			sourceImg.setRGB(workingX, workingY, 0xFFFFFF);
			grayLevelOldPixel -= 0xFF;
		}
		for (int yMatrix = 0; yMatrix < myMatrix.errorDistribution.length; yMatrix++) {
			int yWokringPixel = workingY + yMatrix;
			if (yWokringPixel < imageHeight) {
				// ¸berspringe in der ersten Spalte die ersten Werte bis "X"
				// erreicht in der Matrix
				for (int xMatrix = (yWokringPixel == 0) ? myMatrix.start
						: 0; xMatrix < myMatrix.errorDistribution[yMatrix].length; xMatrix++) {
					int xWorkingPixel = xMatrix - myMatrix.start + workingX;
					if (xWorkingPixel >= 0 && xWorkingPixel < imageWidth) {
						this.sourceImg = helper.workOnImage(myMatrix, yMatrix, xMatrix, sourceImg, xWorkingPixel,
								yWokringPixel, grayLevelOldPixel);
					}
				}
			}
		}
	}

	// Verteile Workload
	protected BufferedImage getDiagonalAndWork() {
		while (workingY >= 0 && workingY < imageHeight && workingX >= 0) {
			if (workingX < imageWidth)
				work();
			workingY++;
			workingX = workingX - (myMatrix.start + 2);
		}
		return sourceImg;
	}
}