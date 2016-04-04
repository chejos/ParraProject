import java.awt.image.BufferedImage;

import javax.xml.namespace.QName;

public class FloydSteinberg {

	final int threshold;

	public FloydSteinberg(int threshold) {
		this.threshold = threshold;
	}

	// Hat noch Fehler
	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {

		int gray = 0;
		int grayLevelOldPixel = 0;
		int quant_error;
		int imageWidth = sourceImg.getWidth();
		int imageHeight = sourceImg.getHeight();

		double w1 = (7.0 / 16.0);
		double w2 = (3.0 / 16.0);
		double w3 = (5.0 / 16.0);
		double w4 = (1.0 / 16.0);

		int gr_null = 0;
		int kl_null = 0;
		
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {

				int rgb = sourceImg.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				grayLevelOldPixel = (r + g + b) / 3;

				// Entscheide ob Pixel Weiﬂ oder Schwarz
				if (grayLevelOldPixel < threshold) {
					gray = 0x0;
					grayLevelOldPixel = (r + g + b) / 3;
				} else {
					gray = 0xFFFFFF;
					grayLevelOldPixel = (r + g + b) / 3 - 0xFF;
				}
				sourceImg.setRGB(x, y, gray);

				// Floy-Steinberg Fehlerverteilung
				// PseudoCode von
				// https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
				
				int temp = grayLevelOldPixel;
				grayLevelOldPixel = (grayLevelOldPixel << 16) + (grayLevelOldPixel << 8) + grayLevelOldPixel;
				
				int xRight = x + 1;
				int xLeft = x - 1;
				int yDown = y + 1;

				if (xRight < imageWidth) {
					rgb = sourceImg.getRGB(xRight, y);
					r = (rgb >> 16) & 0xFF;
					g = (rgb >> 8) & 0xFF;
					b = (rgb & 0xFF);
					
					r = (int) (r + w1 * temp);
					g = (int) (g + w1 * temp);
					b = (int) (b + w1 * temp);
					
					rgb = (r + g + b) / 3;
					rgb = (rgb << 16) + (rgb << 8) + rgb;
					
					sourceImg.setRGB(xRight, y, rgb);
				}
				if (yDown < imageHeight) {

					// Pixel links unten
					if (xLeft > 0) {

						rgb = sourceImg.getRGB(xLeft, yDown);
						r = (rgb >> 16) & 0xFF;
						g = (rgb >> 8) & 0xFF;
						b = (rgb & 0xFF);
						
						r = (int) (r + w2 * temp);
						g = (int) (g + w2 * temp);
						b = (int) (b + w2 * temp);
						
						rgb = (r + g + b) / 3;
						rgb = (rgb << 16) + (rgb << 8) + rgb;
						
						sourceImg.setRGB(xLeft, yDown, rgb);
					}

					// Pixel unten

					rgb = sourceImg.getRGB(x, yDown);
					r = (rgb >> 16) & 0xFF;
					g = (rgb >> 8) & 0xFF;
					b = (rgb & 0xFF);
					
					r = (int) (r + w3 * temp);
					g = (int) (g + w3 * temp);
					b = (int) (b + w3 * temp);
					
					rgb = (r + g + b) / 3;
					rgb = (rgb << 16) + (rgb << 8) + rgb;
					sourceImg.setRGB(x, yDown, rgb);

					// Pixel rechts unten
					if (xRight < imageWidth) {
						rgb = sourceImg.getRGB(xRight, yDown);
						r = (rgb >> 16) & 0xFF;
						g = (rgb >> 8) & 0xFF;
						b = (rgb & 0xFF);
						

//						System.out.println(r);
//						System.out.println(g);
//						System.out.println(b);
//
//						System.out.println("rgb1: " + Integer.toHexString(rgb));
						
						r = (int) (r + w4 * temp);
						g = (int) (g + w4 * temp);
						b = (int) (b + w4 * temp);
						

//						System.out.println(r);
//						System.out.println(g);
//						System.out.println(b);
						
						rgb = (r + g + b) / 3;
						rgb = (rgb << 16) + (rgb << 8) + rgb;
						
//						System.out.println("rgb: " + Integer.toHexString(rgb));
						sourceImg.setRGB(xRight, yDown, rgb);						
					}
				}
			}
		}
		System.out.println("grˆﬂer:" + gr_null);
		System.out.println("kleiner: " + kl_null);
		return sourceImg;
	}

}
