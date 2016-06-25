import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class CalculatePixel extends RecursiveAction {
	private int imageWidth;
	private int imageHeight;
	private int workingX;
	private int workingY;
	private matrix myMatrix;
	protected BufferedImage sourceImg;
	private int maxThreads;

	public CalculatePixel(BufferedImage sourceImg, matrix myMatrix, int workingX, int workingY, int maxThreads) {
		this.sourceImg = sourceImg;
		this.imageWidth = sourceImg.getWidth();
		this.imageHeight = sourceImg.getHeight();
		this.myMatrix = myMatrix;
		this.workingX = workingX;
		this.workingY = workingY;
		this.maxThreads = maxThreads;
	}

	// Average pixels from source, write results into destination.
	protected void computeDirectly() {
		// rgbValues: { rgb, r, g, b }
		int[] rgbValue = helper.getRGB(sourceImg, workingX, workingY);
		int grayLevelOldPixel = rgbValue[0];
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
				for (int xMatrix = (yWokringPixel == 0) ? myMatrix.start
						: 0; xMatrix < myMatrix.errorDistribution[yMatrix].length; xMatrix++) {
					int xWorkingPixel = xMatrix - myMatrix.start + workingX;
					if (xWorkingPixel >= 0 && xWorkingPixel < imageWidth) {
						helper.workOnImage(myMatrix, yMatrix, xMatrix, sourceImg, xWorkingPixel, yWokringPixel,
								grayLevelOldPixel);
					}
				}
			}
		}
	}

	@Override
	protected void compute() {
		if (maxThreads < java.lang.Thread.activeCount())
			maxThreads = java.lang.Thread.activeCount();
		computeDirectly();
	}

}
