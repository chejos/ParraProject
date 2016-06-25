import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.awt.color.ColorSpace;

import javax.imageio.ImageIO;

public class myMain {

	// Bild-Datei-Verwaltung, Abfrage der zu verwendeten Matrix und Entscheidung
	// parallel/sec in der Main
	public static void main(String[] args) throws IOException {

		System.out.println("todo: Objektnamen sollen mit einem großen Buchstaben beginnen");
		System.out.println("todo: (String?) Konstanten ausfindig machen, diese Groß schreiben und mit final versehen");
		System.out.println("todo: type filename?, threadcounter, mode, matrix and threshold");
		System.out.println("todo: tests");
		System.out.println("todo: Kommentare/Dokumentation!!!");
		System.out.println("todo - sobald alles andere läuft: lösche thread counter aus der Main und den anderen Objekten");
		System.out.println("todo - Überprüft nochmal die Matrix!!!!!!!!!!!!!!!!!!!!!!!!!!!! Meine bei mir gehen nicht alle");

		boolean quit = false;
		boolean seqPara;
		Scanner inputReader = new Scanner(System.in);
		String input = null;

		while (!quit) {
			System.out.println("Bitte Verfahren wählen:");
			System.out.println("1: FloydSteinberg algorithmus sequenziell");
			System.out.println("2: FloydSteinberg algorithmus parallel");
			System.out.println(
					"3: Verschiedene Test Bilder \n Verfahren: GrayScale, GrayScale mit ColorConverter, BinaryPicture, Ordered Dither ");
			input = inputReader.nextLine();

			if (input.equals("1")) {
				input = inputReader.nextLine();
				seqPara = false;
				startFloySteinberg(seqPara, 0, 0);

			} else if (input.equals("2")) {
				// Nachfragen nach Methoden
				int mode = 0;
				int thread = 0;
				System.out.println("Anzahl Threads angeben:");
				input = inputReader.nextLine();
				thread = Integer.parseInt(input);

				System.out.println("Auswahl Modus:");
				input = inputReader.nextLine();
				mode = Integer.parseInt(input);

				seqPara = true;
				startFloySteinberg(seqPara, thread, mode);

			} else if (input.equals("3")) {
				createGrayScalePics();

			} else if (input.equals("exit"))
				inputReader.close();
			quit = true;

		}

	}

	/**
	 * Erzeugt die zusätzlichen Bilder die nicht Teil der Aufgabe waren Diese
	 * werden der Reihe nach einfach ausgegeben Zuerst Binary Picture Dann ein
	 * GrayScale Picture mit dem Colour Converter Ordered Dither
	 * 
	 * @throws IOException
	 */
	public static void createGrayScalePics() throws IOException {
		final String PATH = "src/main/resources/";
		final String SOURCE = "example.jpg";
		String RESULTNAME = null;
		final String RESULTTYPE = "jpg";

		BufferedImage sourceImg = ImageIO.read(new File(PATH + SOURCE));
		BufferedImage resultImg;

		// Binary Picture
		RESULTNAME = "BinaryPicture";
		resultImg = new BinaryPicture().createBinaryPicture(sourceImg);
		ImageIO.write(resultImg, RESULTTYPE, new File(PATH + RESULTNAME + "." + RESULTTYPE));
		System.out.println("BinaryPicture: " + RESULTNAME + "." + RESULTTYPE);

		// GrayScale ColorConverter Picture
		RESULTNAME = "GrayScaleColorConverter";
		resultImg = makeGrayBuffered(sourceImg);
		ImageIO.write(resultImg, RESULTTYPE, new File(PATH + RESULTNAME + "." + RESULTTYPE));
		System.out.println("GrayScale: " + RESULTNAME + "." + RESULTTYPE);

		// GrayScale Picture
		RESULTNAME = "GrayScale";
		resultImg = makeGray(sourceImg);
		ImageIO.write(resultImg, RESULTTYPE, new File(PATH + RESULTNAME + "." + RESULTTYPE));
		System.out.println("GrayScale: " + RESULTNAME + "." + RESULTTYPE);

		// Ordered Dither
		RESULTNAME = "OrderedDither";
		resultImg = new OrderedDither().createBinaryPicture(sourceImg);
		ImageIO.write(resultImg, RESULTTYPE, new File(PATH + RESULTNAME + "." + RESULTTYPE));
		System.out.println("OrderedDither: " + RESULTNAME + "." + RESULTTYPE);

	}

	/**
	 * Gray Scale Converter Testen anderer Methode um Umgang mit BufferedImage
	 * zu bekommen
	 * http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-
	 * grayscale-image-reducing-the-memory-in-java
	 * 
	 * @param img
	 * @return
	 */
	public static BufferedImage makeGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				int grayLevel = (r + g + b) / 3;
				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				img.setRGB(x, y, gray);
			}

		return img;
	}

	/**
	 * Gray Scale ColorConvert Option zum Testen verschiedener Grayscale
	 * Methoden
	 * http://codehustler.org/blog/java-to-create-grayscale-images-icons/
	 * 
	 * @param img
	 * @return Grayscale Bild
	 */
	public static BufferedImage makeGrayBuffered(BufferedImage img) {
		// Gray Scale
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		BufferedImage image = op.filter(img, null);

		return image;
	}

	/**
	 * Methode für den Floy-Steinberg Algorithmus Bei der Übergabe wird
	 * entschieden ob es die Methode sequentiell oder Parallel ausgeführt werden
	 * soll Mit wie vielen Thread die Parallelmethode gestartet wird und in
	 * welchem Matrix Modus.
	 * 
	 * @param seqPara
	 * @param thread
	 * @param mode
	 * @throws IOException
	 */
	public static void startFloySteinberg(boolean seqPara, int thread, int mode) throws IOException {

		final String PATH = "src/main/resources/";
		final String SOURCE = "example.jpg";
		final String RESULTTYPE = "png";

		String dstName = null;

		final int processors = Runtime.getRuntime().availableProcessors();
		boolean parallel = seqPara;
		int threadQuantity = 4;

		BufferedImage image = ImageIO.read(new File(PATH + SOURCE));
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();

		BufferedImage newImage = null;

		int threshold = 128;
		matrix myMatrix = null;

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
			// maxThreads zur Info, kann (soll!) spÃ¤ter gelÃ¶scht werden - auch
			// in den Objekten fuer die Paralelleverarveitung
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
