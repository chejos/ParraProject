import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * Worker-Klasse für die Verarbeitung des parallelen Vorgehens mit Hilfe
 * RecursiveAction.
 *
 */
public class WorkerParaDiagonal extends RecursiveAction {

	private int imageWidth;
	private int imageHeight;
	private int workingX;
	private int workingY;
	private Matrix myMatrix;
	protected BufferedImage sourceImg;
	private int maxThreads;

	public WorkerParaDiagonal(BufferedImage sourceImg, Matrix myMatrix, int workingX, int workingY, int maxThreads) {
		this.sourceImg = sourceImg;
		this.imageWidth = sourceImg.getWidth();
		this.imageHeight = sourceImg.getHeight();
		this.myMatrix = myMatrix;
		this.workingX = workingX;
		this.workingY = workingY;
		this.maxThreads = maxThreads;
	}

	/**
	 * Entscheide welche Pixel gleichzeitig bearbeitet werden können und übergib
	 * diese invokeAll (/RecursiveAction)
	 * 
	 */
	@Override
	protected void compute() {
		List<RecursiveAction> tasks = new ArrayList<RecursiveAction>();
		while (workingY >= 0 && workingY < imageHeight && workingX >= 0) {
			if (workingX < imageWidth)
				tasks.add(new WorkerParaPixel(sourceImg, myMatrix, workingX, workingY, maxThreads));
			workingY++;
			workingX = workingX - (myMatrix.start + 6);

			// thread counter
			if (maxThreads < java.lang.Thread.activeCount())
				maxThreads = java.lang.Thread.activeCount();
		}
		invokeAll(tasks);
	}
}