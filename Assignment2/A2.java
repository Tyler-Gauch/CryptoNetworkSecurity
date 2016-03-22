import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

/*
 * To compile: javac A2.java
 * To execute: java A2 (you need to have the sample.png image)
 */

public class A2 {
	public A2 (String inFileName, String outFileName) {
		BufferedImage inI  = null;
		BufferedImage outI = null;
		try {
			inI = ImageIO.read(new File(inFileName));	// Load input image
			int W = inI.getWidth();				// width of image
			int H = inI.getHeight();			// height of image
			
			// Create same type output image as the input (but has no content)
			outI = new BufferedImage(W, H, inI.getType());  

			//
			// Note: LSB is the bit 0
			//
			// WHAT YOU NEED TO DO:
			// 1. Get the RGB color of a pixel (code given).
			// 2. Extract the Red, Green, Blue channels from the input image.
			// 3. For each channel, if the Most significant bit is 1, set it to 0
			//    and if it is 0, set it to 1.
			// 4. For each channel, set to 1 bit 6 (bit 0 is the LSB)
			// 5. For each channel, set to 0 bit 3 (bit 0 is the LSB)
			// 6. Construct an new RGB color where:
			//    a) The Red   channel from the input image becomes the Green channel. 
			//    b) The Blue  channel from the input image becomes the Red   channel.
			//    c) The Green channel from the input image becomes the Blue  channel.
			//    d) The alpha channel should be 0xff.
			//    Remember that the format for each pixel is:  ARGB
			for(int y=0; y<H; y++){
				for(int x=0; x<W; x++) {
					int rgb = inI.getRGB(x,y);	// (1) Getting RGB of a pixel (done)

					// NEED TO IMPLEMENT (2) (replace the 128,64,32 below)
					int RED   = (rgb << 8) >>> 24;
					int GREEN = (rgb << 16) >>> 24;
					int BLUE  = (rgb << 24) >>> 24;

					// int mostSig = 0b10000000;
					int mostSig = (1 << 7);

					RED ^= mostSig;
					GREEN ^= mostSig;
					BLUE ^= mostSig;

					// int setSix = 0b01000000;
					int setSix = (1 << 6);

					RED |= setSix;
					GREEN |= setSix;
					BLUE |= setSix;

					// int clearThree = 0b11110111;
					int clearThree = ~(1 << 3);

					RED &= clearThree;
					GREEN &= clearThree;
					BLUE &= clearThree;

					RED = RED << 8;
					BLUE = BLUE << 16;

					int new_rgb = 0xff000000;

					new_rgb |= RED | GREEN | BLUE;

					// System.out.println("1: " + Integer.toBinaryString(rgb));
					// System.out.println("2: " + Integer.toBinaryString(new_rgb));

					outI.setRGB(x,y,new_rgb); // set the pixel of the output image to new_rgb
				}
			}

			ImageIO.write(outI, "png", new File(outFileName));	// write the image to the output file
		}
		catch(IOException ee) {
			System.err.println(ee);
			System.exit(-1);
		}
	}


	public static void main(String[] args) {
		new A2("sample.png", "out.png");
  	}
}

