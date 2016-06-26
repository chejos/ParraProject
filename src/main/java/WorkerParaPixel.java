import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * Worker-Klasse für die Verarbeitung des parallelen Vorgehens mit Hilfe
 * RecursiveAction. Hier liegt der Schwerpunkt bei der Bearbeitung der einzelnen
 * Pixel. Entscheidet also ob der zur Zeit bearbeitete Pixel sw/ws wird und
 * wälzt die Abweichung auf die Nachbarn ab (workOnImage).
 *
 */
public class WorkerParaPixel extends RecursiveAction {
	private int imageWidth;
	private int imageHeight;
	private int workingX;
	private int workingY;
	private Matrix myMatrix;
	protected BufferedImage sourceImg;
	private int maxThreads;

	public WorkerParaPixel(BufferedImage sourceImg, Matrix myMatrix, int workingX, int workingY, int maxThreads) {
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
		int[] rgbValue = Helper.getRGB(sourceImg, workingX, workingY);
		int grayLevelOldPixel = rgbValue[0];

		// Ändere den RGB-Wert des "X"-Pixels auf sw/ws
		if (grayLevelOldPixel < myMatrix.threshold) {
			sourceImg.setRGB(workingX, workingY, 0x0);
		} else {
			sourceImg.setRGB(workingX, workingY, 0xFFFFFF);
			grayLevelOldPixel -= 0xFF;
		}

		// Bearbeite die Nachbarn des "X"-Pixels in Abhängigkeit der Matrix
		// Gehe jede Zeile der Matrix durch
		for (int yMatrix = 0; yMatrix < myMatrix.errorDistribution.length; yMatrix++) {
			// yWokringPixel -> yWert des zu bearbeitenden Pixels auf dem Bild
			int yWokringPixel = workingY + yMatrix;

			// Abfrage ob yWert nicht bereits außerhalb des Bilds liegt
			if (yWokringPixel < imageHeight) {

				// xKoordinate der Matrix beginnt in erster Zeile rechts neben
				// "X" ansonsten ganz links
				int xMatrix = (yMatrix == 0) ? myMatrix.start : 0;

				// für jede Spalte der Zeile in der Matrix..
				for (; xMatrix < myMatrix.errorDistribution[yMatrix].length; xMatrix++) {

					// berechne die xKoordinate den gerade zu bearbeitenden
					// Nachbar-Pixels des "X" auf dem Bild
					int xWorkingPixel = xMatrix - myMatrix.start + workingX;

					// Wenn Pixel sich noch auf dem Bild befindet, bearbeite den
					// jeweiligen Nachbarn
					if (xWorkingPixel >= 0 && xWorkingPixel < imageWidth) {
						Helper.workOnImage(myMatrix, yMatrix, xMatrix, sourceImg, xWorkingPixel, yWokringPixel,
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
