import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class CalculateDiagonal extends RecursiveAction {

	private int imageWidth;
	private int imageHeight;
	private int workingX;
	private int workingY;
	private matrix myMatrix;
	protected BufferedImage sourceImg;
	private int maxThreads;

	public CalculateDiagonal(BufferedImage sourceImg, matrix myMatrix, int workingX, int workingY, int maxThreads) {
		this.sourceImg = sourceImg;
		this.imageWidth = sourceImg.getWidth();
		this.imageHeight = sourceImg.getHeight();
		this.myMatrix = myMatrix;
		this.workingX = workingX;
		this.workingY = workingY;
		this.maxThreads = maxThreads;
	}

	@Override
	protected void compute() {
		List<RecursiveAction> tasks = new ArrayList<RecursiveAction>();
		while (workingY >= 0 && workingY < imageHeight && workingX >= 0) {
			if(workingX < imageWidth)
				tasks.add(new CalculatePixel(sourceImg, myMatrix, workingX, workingY, maxThreads));
			workingY = (workingY + 1);
			workingX = workingX - (myMatrix.start + 2);
			if (maxThreads < java.lang.Thread.activeCount())
				maxThreads = java.lang.Thread.activeCount();
		}
		invokeAll(tasks);
	}
}