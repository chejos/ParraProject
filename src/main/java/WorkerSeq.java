import java.awt.image.BufferedImage;

/**
 * Worker-Klasse für die Verarbeitung des sequentiellen Vorgehens.
 *
 */
public class WorkerSeq {

	private final int imageWidth;
	private final int imageHeight;
	private int workingX;
	private int workingY;
	private int grayLevelOldPixel;
	private final Matrix myMatrix;
	protected BufferedImage sourceImg;

	protected WorkerSeq(BufferedImage myImage, Matrix myMatrix, int workingX, int workingY) {
		this.sourceImg = myImage;
		this.myMatrix = myMatrix;
		this.imageHeight = myImage.getHeight();
		this.imageWidth = myImage.getWidth();
		this.workingX = workingX;
		this.workingY = workingY;
	}

	/**
	 * Arbeitet im eigenen Objekt den entsprechenden Pixel der Diagonale ab.
	 * Entscheidet also ob der zur Zeit bearbeitete Pixel sw/ws wird und wälzt
	 * die Abweichung auf die Nachbarn ab (workOnImage).
	 *
	 */
	protected void work() {
		// rgbValues[]: { rgb, r, g, b }

		int[] rgbValue = Helper.getRGB(sourceImg, workingX, workingY);
		grayLevelOldPixel = rgbValue[0];

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
				int xMatrix = (yMatrix == 0) ? myMatrix.start + 1 : 0;

				// für jede Spalte der Zeile in der Matrix..
				for (; xMatrix < myMatrix.errorDistribution[yMatrix].length; xMatrix++) {
					// berechne die xKoordinate den gerade zu bearbeitenden
					// Nachbar-Pixels des "X" auf dem Bild
					int xWorkingPixel = xMatrix - myMatrix.start + workingX;

					// Wenn Pixel sich noch auf dem Bild befindet, bearbeite den
					// jeweiligen Nachbarn// Wenn Pixel sich noch auf dem Bild
					// befindet, bearbeite den jeweiligen Nachbarn
					if (xWorkingPixel >= 0 && xWorkingPixel < imageWidth) {
						this.sourceImg = Helper.workOnImage(myMatrix, yMatrix, xMatrix, sourceImg, xWorkingPixel,
								yWokringPixel, grayLevelOldPixel);
					}
				}
			}
		}
	}

	/**
	 * Entscheide welcher Pixel als nächstes bearbeitet werden soll und stoße
	 * work() an
	 * 
	 */
	protected void getDiagonalAndWork() {
		while (workingY >= 0 && workingY < imageHeight && workingX >= 0) {
			if (workingX < imageWidth)
				work();
			workingY++;
			workingX = workingX - (myMatrix.start + 6);
		}
	}

	protected BufferedImage getSourceImg() {
		return sourceImg;
	}
}