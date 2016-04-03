import java.awt.image.BufferedImage;

public class FloydSteinberg {

	final int threshold;
	
	public FloydSteinberg(int threshold) {
		this.threshold = threshold;
	}
	
	
	// Hat noch Fehler
	public BufferedImage createBinaryPicture(BufferedImage sourceImg) {
		
		int gray = 0;
		int grayLevel = 0;
		int quant_error;
		
		
		
		
		for (int x = 0; x < sourceImg.getWidth(); ++x) {
			for (int y = 0; y < sourceImg.getHeight(); ++y) {

				float w1 = (float) (7.0/16);
				float w2 = (float) (3.0/16);
				float w3 = (float) (5.0/16);
				float w4 = (float) (1.0/16);
				
				int rgb = sourceImg.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				grayLevel = (r + g + b) / 3;
				
						
				//Entscheide ob Pixel Weiﬂ oder Schwarz
				if (grayLevel < threshold) {
					gray = 0x0;
				} else {
					gray = 0xFFFFFF;
				
				}
				sourceImg.setRGB(x, y, gray);
				
				
				//Floy-Steinberg Fehelrverteilung
				//PseudoCode von https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
				grayLevel = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				quant_error = grayLevel - gray;
				if(x+1 < sourceImg.getWidth()) {sourceImg.setRGB(x+1, y, (int) (sourceImg.getRGB(x+1, y)+ w1 * quant_error));}
				if(x-1 > sourceImg.getWidth() && y+1 < sourceImg.getHeight()) {sourceImg.setRGB(x-1, y+1, (int) (sourceImg.getRGB(x-1, y+1)+ w2 * quant_error));}
				if(y+1 < sourceImg.getHeight()){sourceImg.setRGB(x, y+1, (int) (sourceImg.getRGB(x, y+1)+ w3 * quant_error));}
				if(x+1 < sourceImg.getWidth() && y+1 < sourceImg.getHeight()) {sourceImg.setRGB(x+1, y+1, (int) (sourceImg.getRGB(x+1, y+1)+ w4 * quant_error));}
				//
				

			}
		}
		return sourceImg;
	}

}
