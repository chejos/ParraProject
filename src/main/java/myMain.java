import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import javax.imageio.ImageIO;

public class myMain {

	// Bild-Datei-Verwaltung, Abfrage der zu verwendeten Matrix und Entscheidung
	// parallel/sec in der Main
	public static void main(String[] args) throws IOException {
		final String PATH = "src/main/resources/";
		final String SOURCE = "example.jpg";
		final String RESULTTYPE = "png";

		String dstName = null;

		final int processors = Runtime.getRuntime().availableProcessors();
		boolean parallel = false;
		int threadQuantity = 4;

		BufferedImage image = ImageIO.read(new File(PATH + SOURCE));
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();

		BufferedImage newImage = null;

		int mode = 0;
		int threshold = 128;
		matrix myMatrix = null;
		System.out.println("todo: l�sche thread counter");
		System.out.println("type mode, matrix and threshold - todo!! -");

		switch (mode) {
		case (1):
			myMatrix = new matrix(new int[][] { { 0, 0, 4, 1 }, { 1, 4, 1, 0 }, { 0, 1, 0, 0 } }, threshold);
			break;
		case (2):
			myMatrix = new matrix(new int[][] { { 0, 0, 0, 8, 4 }, { 2, 4, 8, 4, 2 }, { 1, 2, 4, 2, 1 } }, threshold);
			break;
		case (3):
			System.out.println("toDo");
			break;
		default:
			myMatrix = new matrix(new int[][] { { 0, 0, 7 }, { 3, 5, 1 } }, threshold);
			break;
		}

		System.out.println(
				Integer.toString(processors) + " processor" + (processors != 1 ? "s are " : " is ") + "available");
		System.out.println("parallel: " + parallel);
		System.out.println("Threshold is " + myMatrix.threshold);
		System.out.println("Matrix-X-pos is " + myMatrix.start + " from left");
		System.out.println("Source image: " + PATH + SOURCE);
		System.out.println("imageWidth: " + imageWidth);
		System.out.println("imageHeight: " + imageHeight);

		final long startTime = System.currentTimeMillis();

		try {
			// maxThreads zur Info, kann (soll!) sp�ter gel�scht werden - auch
			// in den Objekten f�r die Paralelleverarveitung
			int maxThreads = 0;
			if (parallel) {

				ForkJoinPool pool = new ForkJoinPool(threadQuantity);
				for (int x = 0; x < Math.max(imageHeight, imageWidth * 4); x++) {
					CalculateDiagonal fb = new CalculateDiagonal(image, myMatrix, x, 0, maxThreads);
					pool.invoke(fb);
				}
				dstName = mode + "_example_para." + RESULTTYPE;
				newImage = image;
			} else {
				for (int x = 0; x < Math.max(imageHeight, imageWidth * 4); x++) {
					newImage = new FirstPixelOfDiagonal(image, myMatrix, x, 0).getDiagonalAndWork();
				}

				dstName = mode + "_example_sec." + RESULTTYPE;
			}
			if (maxThreads < java.lang.Thread.activeCount())
				maxThreads = java.lang.Thread.activeCount();
			System.out.println("active threads (inkl. Main): " + maxThreads);
		} catch (Exception e) {
			System.out.println("Error in main:");
			System.out.println(e.getMessage());
		}

		final long endTime = System.currentTimeMillis();

		ImageIO.write(newImage, RESULTTYPE, new File(PATH + dstName));

		System.out.println("Process took " + (endTime - startTime) + " milliseconds.");
		System.out.println("Output image: " + dstName);

		// compare sequential and parallel results
		helper.compareParaWithSec(PATH + mode + "_example_para." + RESULTTYPE,
				PATH + mode + "_example_sec." + RESULTTYPE);
	}

}
