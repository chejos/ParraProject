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
		System.out.println(
				"todo - sobald alles andere läuft: lösche thread counter aus der Main und den anderen Objekten");
		System.out.println(
				"todo - Beim Testen aufpassen, dass beide Bilder vorhanden sind. Wenn parra, muss sequentiel erzeugt werden und"
						+ " umgekehrt -- mit Befehl \"testMe\" können alle Modi getestet werden");
		System.out.println("todo - Klassendiagramm");
		System.out.println("todo - Anwenderdoku");
		System.out.println("todo - Catch IOException -> soll nicht auf die Nase fliegen, nur wenn eine Datei fehlt.."
				+ " also gebe ein Text aus und bleibe in der Whileschleife");

		boolean quit = false;
		boolean seqPara;
		Scanner inputReader = new Scanner(System.in);
		String input = null;
		int mode = 0;

		while (!quit) {
			System.out.println("\n Bitte Verfahren wählen:");
			System.out.println("1: FloydSteinberg algorithmus sequenziell");
			System.out.println("2: FloydSteinberg algorithmus parallel");
			System.out.println(
					"3: Verschiedene Test Bilder \n Verfahren: GrayScale, GrayScale mit ColorConverter, BinaryPicture, Ordered Dither ");
			System.out.print("Eingabe: ");
			input = inputReader.nextLine();

			if (input.equals("1")) {

				System.out.println("FloydSteinberg sequenziell:");
				System.out.print("Auswahl Modus:");

				input = inputReader.nextLine();
				mode = Integer.parseInt(input);
				seqPara = false;
				Helper.startFlodySteinberg(seqPara, 0, mode);

			} else if (input.equals("2")) {
				// Nachfragen nach Matrix und Thread Anzahl
				int thread = 0;
				System.out.println("FloydSteinberg parallel:");
				System.out.print("Anzahl Threads angeben:");
				input = inputReader.nextLine();
				thread = Integer.parseInt(input);

				System.out.print("Auswahl Modus:");
				input = inputReader.nextLine();
				mode = Integer.parseInt(input);

				seqPara = true;
				Helper.startFlodySteinberg(seqPara, thread, mode);

			} else if (input.equals("3")) {
				createGrayScalePics();

			} else if (input.equals("exit")) {
				inputReader.close();
				quit = true;
			} else if (input.equals("testMe")) {
				Helper.startFlodySteinberg(false, 0, 0);
				Helper.startFlodySteinberg(false, 0, 1);
				Helper.startFlodySteinberg(false, 0, 2);
				Helper.startFlodySteinberg(true, Runtime.getRuntime().availableProcessors(), 0);
				Helper.startFlodySteinberg(true, Runtime.getRuntime().availableProcessors(), 1);
				Helper.startFlodySteinberg(true, Runtime.getRuntime().availableProcessors(), 2);
				Helper.compareParaWithSec();
			}
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
}
