import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import javax.imageio.ImageIO;

/**
 * Helper-Klasse zur Vermeidung von Redundanz zwischen sequenziellen- und
 * parallelen- Vorgehen
 * 
 * @author Tribus
 *
 */
public class Helper {

	/**
	 * Methode für den Floy-Steinberg Algorithmus. Bei der Übergabe wird
	 * entschieden ob es die Methode sequentiell oder Parallel ausgeführt werden
	 * soll Mit wie vielen Threads die Parallelmethode gestartet wird und in
	 * welchem Matrix Modus.
	 * 
	 * @param seqPara
	 *            Falls True -> Paralleles ansonsten sequentielles Vorgehen
	 * @param thread
	 *            Anzahl der zu verwendenen Threads, falls parallel
	 * @param mode
	 *            Zu wählende Matrix (0, 1 oder 2)
	 * @throws IOException
	 *             Falls die Quell-Datei nicht existiert
	 */
	public static void startFlodySteinberg(boolean seqPara, int thread, int mode) throws IOException {

		final String PATH = "src/main/resources/";
		final String SOURCE = "example.jpg";
		final String RESULTTYPE = "png";

		String dstName = null;

		final int processors = Runtime.getRuntime().availableProcessors();
		boolean parallel = seqPara;
		int threadQuantity = thread;

		BufferedImage image = ImageIO.read(new File(PATH + SOURCE));
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();

		BufferedImage newImage = null;

		int threshold = 128;
		Matrix myMatrix = null;

		switch (mode) {
		case (0):
			myMatrix = new Matrix(new int[][] { { 0, 0, 7 }, { 3, 5, 1 } }, threshold);
			break;
		case (1):
			myMatrix = new Matrix(new int[][] { { 0, 0, 4, 1 }, { 1, 4, 1, 0 }, { 0, 1, 0, 0 } }, threshold);
			break;
		case (2):
			myMatrix = new Matrix(new int[][] { { 0, 0, 0, 8, 4 }, { 2, 4, 8, 4, 2 }, { 1, 2, 4, 2, 1 } }, threshold);
			break;
		default:
			myMatrix = new Matrix(new int[][] { { 0, 0, 7 }, { 3, 5, 1 } }, threshold);
			break;
		}
		System.out.println("Start Mode: " + mode + " with parallel: " + parallel);
		System.out.println(
				Integer.toString(processors) + " processor" + (processors != 1 ? "s are " : " is ") + "available");
		System.out.println("Source image path: " + PATH + SOURCE);
		System.out.println("imageWidth: " + imageWidth + " | imageHeight: " + imageHeight);
		System.out.println("Threshold is " + myMatrix.threshold);
		System.out.println("Matrix-X-pos is " + myMatrix.start + " from left");

		final long startTime = System.currentTimeMillis();

		try {
			// maxThreads zur Info, kann später gelöscht werden - auch
			// in den Objekten fuer die Paralelleverarveitung
			int maxThreads = 0;
			if (parallel) {
				ForkJoinPool pool = new ForkJoinPool(threadQuantity);
				for (int x = 0; x < Math.max(imageHeight, imageWidth * 10); x++) {
					WorkerParaDiagonal fb = new WorkerParaDiagonal(image, myMatrix, x, 0, maxThreads);
					pool.invoke(fb);
				}
				dstName = mode + "_example_para." + RESULTTYPE;
				newImage = image;
			} else {
				for (int x = 0; x < Math.max(imageHeight, imageWidth * 10); x++) {
					WorkerSeq mySeqWorker = new WorkerSeq(image, myMatrix, x, 0);
					mySeqWorker.getDiagonalAndWork();
					newImage = mySeqWorker.getSourceImg();
				}

				dstName = mode + "_example_sec." + RESULTTYPE;
			}
			if (maxThreads < java.lang.Thread.activeCount())
				maxThreads = java.lang.Thread.activeCount();
			System.out.println("active threads (inkl. Main): " + maxThreads);
		} catch (Exception e) {
			System.out.println(" in main:");
			System.out.println(e.getMessage());
		}

		final long endTime = System.currentTimeMillis();

		ImageIO.write(newImage, RESULTTYPE, new File(PATH + dstName));

		System.out.println("Process took " + (endTime - startTime) + " milliseconds.");
		System.out.println("Output image: " + dstName);
		System.out.println();
	}

	/**
	 * Rechne die Fehlerwerte auf die benachbarten Pixel um und gib das
	 * bearbeitete Bild wieder zurück
	 * 
	 * @param myMatrix
	 *            Die ausgewählte Matrix (Mode 0, 1 oder 2)
	 * @param yMatrix
	 *            Die y-Koordinate (/Zeile) der Matrix
	 * @param xMatrix
	 *            Die x-Koordinate (/Spalte) der Matrix
	 * @param sourceImg
	 *            Das zu verarbeitende Bild
	 * @param yWorkingPixel
	 *            Die y-Koordinate (/Zeile) des Bilds, also der gerade zu
	 *            verarbeitende Pixel-Nachbar
	 * @param xWokringPixel
	 *            Die x-Koordinate (/Spalte) des Bilds, also der gerade zu
	 *            verarbeitende Pixel-Nachbar
	 * 
	 * @return Rückgabe des Bildes mit den geänderten Nachbarn in abhängigkeit
	 *         zu den Fehlerwerten
	 */
	public static BufferedImage workOnImage(Matrix myMatrix, int yMatrix, int xMatrix, BufferedImage sourceImg,
			int xWorkingPixel, int yWokringPixel, int grayLevelOldPixel) {
		double distributionDivideSum = myMatrix.errorDistribution[yMatrix][xMatrix] / (double) myMatrix.errorSum;

		int[] workingRgbValue = Helper.getRGB(sourceImg, xWorkingPixel, yWokringPixel);

		int workingRgbWithError = Helper.coloursMultiplyError(workingRgbValue, grayLevelOldPixel,
				distributionDivideSum);

		sourceImg.setRGB(xWorkingPixel, yWokringPixel, workingRgbWithError);
		return sourceImg;
	}

	/**
	 * Liest den RGB-Wert des Pixels heraus, berechnet den Grauwert und gibt ein
	 * Array mit dem Grau-, Rot-, Grün- und Blau- Wert zurück
	 * 
	 * @param img
	 *            Bild von dem die Farbe des Pixels herausgelesen wird
	 * @param x
	 *            Die x-Koordinate
	 * @param y
	 *            Die y-Koordinate
	 * 
	 * @return Ein Int-Array mit 4 Einträgen (Grau-, Rot-, Grün- und Blau- Wert)
	 */
	public static int[] getRGB(BufferedImage img, int x, int y) {
		int[] rgb = img.getRaster().getPixel(x, y, new int[3]);
		int rgbAverage = (rgb[0] + rgb[1] + rgb[2]) / 3;
		return new int[] { rgbAverage, rgb[0], rgb[1], rgb[2] };
	}

	/**
	 * Multipliziert den Grauwert mit der Fehlerkonstanten und gibt den neuen
	 * Grauwert zurück (min 0 max 255)
	 * 
	 * @param rgbValues
	 *            Beinhaltet den Grauwert des aktuellen Nachbarns
	 * @param error
	 *            Beinhaltet die den aktuell abgearbeiteten Pixel-Grauwert
	 * @param matrixValue
	 *            Die Konstante in der jewiligen Matrixzelle
	 * 
	 * @return Den neuen RGB-Wert
	 */
	public static int coloursMultiplyError(int[] rgbValues, int error, double matrixValue) {
		int ErrorMultipyMatrix = (int) (matrixValue * error);
		int newRGB = Math.min(Math.max(rgbValues[0] + ErrorMultipyMatrix, 0), 255);
		return (newRGB << 16) + (newRGB << 8) + newRGB;
	}

	/**
	 * Methode für das Testen bzw. Debugging. Prüft jeden Pixel auf schwarz/weiß
	 * und vergleicht alle sequenziellen mit den parallelen Bildern. Als Ausgabe
	 * kommt entweder die Anzahl der gefunden Fehler oder eine "Without Errors"
	 * -Meldung
	 * 
	 */
	public static void compareParaWithSec() throws IOException {
		for (int i = 0; i != 3; i++) {
			String resultFilePara = "src/main/resources/" + Integer.toString(i) + "_example_para.png";
			String resultFileSec = "src/main/resources/" + Integer.toString(i) + "_example_para.png";

			BufferedImage image = ImageIO.read(new File(resultFileSec));
			BufferedImage imagePara = ImageIO.read(new File(resultFilePara));
			final int imageWidth = image.getWidth();
			final int imageHeight = image.getHeight();

			int counterNotBlackOrWhiteSec = 0;
			int counterNotBlackOrWhitePara = 0;
			int counterNotEqParaAndSeq = 0;

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
						counterNotBlackOrWhiteSec++;
					}
					if (sumPara != 255 * 3 && sumPara != 0) {
						counterNotBlackOrWhitePara++;
					}
					if (sumPara != sumSec) {
						counterNotEqParaAndSeq++;
					}

				}
			}
			if (counterNotBlackOrWhitePara != 0 || counterNotBlackOrWhiteSec != 0 || counterNotEqParaAndSeq != 0) {
				System.out.println("Errors found!!");
				System.out.println("counterNotBlackOrWhitePara: " + counterNotBlackOrWhitePara);
				System.out.println("counterNotBlackOrWhiteSec: " + counterNotBlackOrWhiteSec);
				System.out.println("counterNotEqParaAndSeq: " + counterNotEqParaAndSeq);
			} else {
				System.out.println("Without Errors");
			}
		}
	}

}
