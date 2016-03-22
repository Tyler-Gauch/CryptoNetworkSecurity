import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;

import java.net.*;
import javax.imageio.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


public class StegoImage {

	private int counter = -1;
	private boolean moreImageData=true;

	private static int R = 17;
	private static int G = 9;
	private static int B = 1;


	public int hiddenX = 0;
	public int hiddenY = 0;
	public int currentRGB = 0;
	public int bitCount = 24;
	public int currentValue = 0;
	public int currentBit = 32;
	public int bitBound = 1;
	public int IW, IH;
	public BufferedImage hiddenImage = null;
	public int llll = 0;
	public int numberOfPixels = 0;

	private byte nextBit(BufferedImage bi2) {
		counter++;
		int w2 = bi2.getWidth();
    		int h2 = bi2.getHeight();


		int ROW = counter / (w2*32);
		int COL = (counter/32) % w2;		



		int tbit = counter%32;
		tbit = 31 - tbit;	// first comes the MSB (this is the amount of shift)


		int rgb = bi2.getRGB(COL, ROW);

		int rr = ((rgb >> tbit) & 0x00000001);

		if( counter >= w2*h2*32-1)
			moreImageData=false;

		return (byte)rr;

	}

	/*********************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 *
	 *
	 *	                       THIS IS THE FUNCTION YOU NEED TO IMPLEMENT
	 *
	 *
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 *********************************************************************************************************/
	public BufferedImage showHiddenImage(BufferedImage b) {

		hiddenX = 0;
		hiddenY = 0;
		currentRGB = 0;
		bitCount = 24;
		currentValue = 0;
		currentBit = 32;
		bitBound = 1;
		hiddenImage = null;
		llll = 0;
		numberOfPixels = 0;

		int w=b.getWidth();
		int h=b.getHeight();


		IW = getImageHeaderW(b);
		IH = getImageHeaderH(b);
		int n = getImageBitsinByte(b);	// number of hidden bits in a byte

		//System.out.println(IW + " " + IH);

		if( IW <= 0 || IH <= 0 )
			return null;

    	hiddenImage = new BufferedImage(IW, IH, b.getType());	// assume same type as the passed one.
		int rows = (int)Math.ceil((IH*IW*8)/(w*1.0))+1;

		for(int y = 1; y < h; y++)
		{
			for(int x = 0; x < w; x++)
			{
				int pixel = b.getRGB(x,y);

				// System.out.println("\tPIXEL: " + Integer.toBinaryString(pixel));
				//System.out.println(Integer.toHexString(pixel)+" "+x+" "+y+" "+hiddenX+" "+hiddenY+" "+currentBit+" "+Integer.toHexString(currentRGB)+" "+IW+"x"+IH+" "+rows+" "+h+"x"+w);		

				if(checkCurrentRGB())
				{
					return hiddenImage;
				}

				updateCurrentRGB(R, pixel);

				if(checkCurrentRGB())
				{
					return hiddenImage;
				}

				updateCurrentRGB(G, pixel);
				
				if(checkCurrentRGB())
				{
					return hiddenImage;
				}

				updateCurrentRGB(B, pixel);
			} 
		}


		return hiddenImage;
	}

	public void updateCurrentRGB(int bit, int pixel)
	{
		int currentValue = 0;
		currentValue = getBit(pixel, bit);
		// System.out.println(Integer.toBinaryString(currentValue));
		currentRGB = setBit(currentRGB, currentBit, currentValue);
		currentBit--;
	}

	public boolean checkCurrentRGB()
	{
		if(currentBit < bitBound)
		{
			// System.out.println("NEW RGB: " + Integer.toBinaryString(currentRGB));
			hiddenImage.setRGB(hiddenX, hiddenY, currentRGB);
			numberOfPixels++;
			if(hiddenX + 1 >= IW)
			{
				hiddenX=0;
				hiddenY++;
				if(hiddenY >= IH)
				{
					return true;
				}
			}else
			{
				hiddenX++;
			}

			currentBit = 32;
			currentRGB = 0;
			/*if(llll > 1){
				System.exit(-1);
			}
			else{
				llll++;
			}*/
		}	
		return false;
	}

	//must be a bit between 1-32
	public int getBit(int pixel, int bit)
	{
		if(bit > 32)
		{
			System.err.println("Invalid argument: '"+bit+"' is not between 1-32");
			System.err.println("Defaulting to 32");
			bit = 32;
		}else if(bit < 1)
		{
			System.err.println("Invalid argument: '"+bit+"' is not between 1-32");
			System.err.println("Defaulting to 1");
			bit = 1;
		}

		bit = bit-1;
		int comp = 1;

		comp = comp << bit;

		comp &= pixel;

		return comp >>> bit;

	}

	public int setBit(int a, int bit, int to)
	{
		if(bit > 32)
		{
			System.err.println("Invalid argument: '"+bit+"' is not between 1-32");
			System.err.println("Defaulting to 32");
			bit = 32;
		}else if(bit < 1)
		{
			System.err.println("Invalid argument: '"+bit+"' is not between 1-32");
			System.err.println("Defaulting to 1");
			bit = 1;
		}

		bit = bit-1;
		int comp = 1 << bit;
		
		if(to == 1)
		{
			a |= comp;	
		}else {
			a &= ~comp;
		}
		
		return a;
	}

	/*
	 * first write the Width, then the Height and then insert the number of bits per byte and then continue to fill in the rest of the first row unaltered.
	 */
	private void insertImageHeader(BufferedImage bi, BufferedImage bi2, BufferedImage bnew, int n) {
		int w2 = bi2.getWidth();
    		int h2 = bi2.getHeight();

		int w = bi.getWidth();

		int num = w2;

		int iter = 0;
      		for (int x = 0, f=0; x < w; f+=4, x++) {
			int rgb = bi.getRGB(x,0);	// header goes in first raw

			int a = ((rgb >> 24) & 0xff);
			int r = ((rgb >> 16) & 0xff);
			int g = ((rgb >>  8) & 0xff);
			int b = ((rgb >>  0) & 0xff);

			if( f >= 32 && iter == 0) {	// was doing width, now will do height
				//System.out.println("________________________HEIGHT________________________" + f + " " + x);
				f =0;
				num=h2;
				iter = 1;
			}
			else if( f >= 32 && iter == 1) {// was doing height, now will do bits per byte 
				//System.out.println("________________________BITS PER BYTE________________________" + f + " " + x);
				f =0;
				num=n;
				iter = 2;
			}
			else if( f >= 32 && iter == 2) {	// now will do the rest
				iter = 3;
			}

			if( f<32 && iter <= 2) {	// insert the number, but continue to complete the first raw of bixels unchanged!


				byte abit = (byte) ((num >> 31-f-0) & 0x01);
				byte bbit = (byte) ((num >> 31-f-1) & 0x01);
				byte cbit = (byte) ((num >> 31-f-2) & 0x01);
				byte dbit = (byte) ((num >> 31-f-3) & 0x01);

				//System.out.println("HIDE: " + abit + " " +bbit+ " " +cbit + " " +dbit);

				if(abit == (byte)1) 	a = a | (1<<0);
				else 			a = a & ~(1<<0);

				if(bbit == (byte)1) 	r = r | (1<<0);
				else 			r = r & ~(1<<0);

				if(cbit == (byte)1) 	g = g | (1<<0);
				else 			g = g & ~(1<<0);

				if(dbit == (byte)1) 	b = b | (1<<0);
				else 			b = b & ~(1<<0);

				int new_rgb = (rgb & 0x00000000) | (a << 24) | (r << 16) | (g << 8) | (b << 0);
        			bnew.setRGB(x, 0, new_rgb);
			}
			else {
				// now continue to fill in the rest of first row unaltered
				int new_rgb = (rgb & 0x00000000) | (a << 24) | (r << 16) | (g << 8) | (b << 0);
        			bnew.setRGB(x, 0, new_rgb);
			}

		}
	}

	// hide image bi2 into bi
	// Store in header size of hiden image and its dimensions (OR only its dimensions, I think)
	public BufferedImage hideImage(BufferedImage bi, BufferedImage bi2, int nbitsPerByte) {
		int w = bi.getWidth();
    		int h = bi.getHeight();

		int w2 = bi2.getWidth();
    		int h2 = bi2.getHeight();

		// convert bi2 into an array of bytes
		/*
		byte[] bytesOut=null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi2, "png", baos);
			baos.flush();
			bytesOut = baos.toByteArray();
			baos.close();
		}
		catch(IOException e){}

		byte[] TEMPbytesOut=null;
		try{
			ByteArrayOutputStream TEMPbaos = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", TEMPbaos);
			TEMPbaos.flush();
			TEMPbytesOut = TEMPbaos.toByteArray();
			TEMPbaos.close();
		}
		catch(IOException e){}
		*/
		
		//System.out.println("NEED  " + (bytesOut.length*8) + " bytes to hide this image which is: " + bytesOut.length + " bytes big.  (" + w2 +"x"+h2+")");
		//System.out.println("COVER " + TEMPbytesOut.length + " bytes big.  (" + w +"x"+h+")");

    		BufferedImage bnew = new BufferedImage(w, h, bi.getType());

		int n = nbitsPerByte; 	// number of bits to hide in a byte

		insertImageHeader(bi, bi2, bnew, n);

		// reset the nextBit
		counter = -1;
		moreImageData=true;




    		for (int y = 1; y < h; y++){
      			for (int x = 0; x < w; x++) {
				int rgb = bi.getRGB(x,y);

				int alpha = (rgb >> 24) & 0xff;
				alpha = 255;
				
				int r = ((rgb >> 16) & 0xff);
				int g = ((rgb >>  8) & 0xff);
				int b = ((rgb >>  0) & 0xff);

				if( moreImageData ) {
					byte abit = (byte)0;

					for(int i = 0; i < n; i++) {
						if(moreImageData) {
							abit = nextBit(bi2);
							if(abit == (byte)1) 	r = r | (1<<i);
							else 			r = r & ~(1<<i);
						}
					}

					for(int i = 0; i < n; i++) {
						if(moreImageData) {
							abit = nextBit(bi2);
							if(abit == (byte)1) 	g = g | (1<<i);
							else 			g = g & ~(1<<i);
						}
					}

					for(int i = 0; i < n; i++) {
						if(moreImageData) {
							abit = nextBit(bi2);
							if(abit == (byte)1) 	b = b | (1<<i);		// set
							else 			b = b & ~(1<<i);	// clear
						}
					}
				}

				int new_rgb = (rgb & 0x00000000) | (alpha << 24) | (r << 16) | (g << 8) | (b << 0);

        			bnew.setRGB(x, y, new_rgb);
			}
		}

		//
		// Mark Stego Image as an image that contains another image as the secret.
		//
		int q = bnew.getRGB(0,0);
		//System.out.println("q is: " + q);
		q |=  (1<<1);	// set bit 1 (0 is the LSB) of the blue
		//System.out.println("q is: " + q);
		bnew.setRGB(0,0,q);


		if(moreImageData) {
			JOptionPane.showMessageDialog(null, "Couldn't hide entire image in this cover image!", "WARNING" , JOptionPane.WARNING_MESSAGE );
			
		}
		return bnew;
	}




	/*
	 * Return the width of the hidden image which is stored in the header
	 */
	private int getImageHeaderW(BufferedImage bi) {
		int w = bi.getWidth();
    		int h = bi.getHeight();
		int n=0;
		//System.out.println("\n------- W --------------------");
      		for (int x = 0, f=0; x < w && f < 32; f+=4, x++) {
			int rgb = bi.getRGB(x,0);	// header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);	n = (n | (a << 31-f-0));
			int r = ((rgb >> 16) & 0x00000001);	n = (n | (r << 31-f-1));
			int g = ((rgb >>  8) & 0x00000001);	n = (n | (g << 31-f-2));
			int b = ((rgb >>  0) & 0x00000001);	n = (n | (b << 31-f-3));
			//System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}

	/*
	 * Return the height of the hidden image which is stored in the header
	 */
	private int getImageHeaderH(BufferedImage bi) {
		int w = bi.getWidth();
    		int h = bi.getHeight();
		int n=0;
		//System.out.println("\n------- H --------------------");
      		for (int x = 8, f=0; x < w && f < 32; f+=4, x++) {
			int rgb = bi.getRGB(x,0);	// header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);	n = (n | (a << 31-f-0));
			int r = ((rgb >> 16) & 0x00000001);	n = (n | (r << 31-f-1));
			int g = ((rgb >>  8) & 0x00000001);	n = (n | (g << 31-f-2));
			int b = ((rgb >>  0) & 0x00000001);	n = (n | (b << 31-f-3));

			//System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}

	/*
	 * Return the number of bits hidden in a byte
	 */
	private int getImageBitsinByte(BufferedImage bi) {
		int w = bi.getWidth();
    		int h = bi.getHeight();
		int n=0;
		//System.out.println("\n------- Bits in Byte --------------------");
      		for (int x = 16, f=0; x < w && f < 32; f+=4, x++) {
			int rgb = bi.getRGB(x,0);	// header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);	n = (n | (a << 31-f-0));
			int r = ((rgb >> 16) & 0x00000001);	n = (n | (r << 31-f-1));
			int g = ((rgb >>  8) & 0x00000001);	n = (n | (g << 31-f-2));
			int b = ((rgb >>  0) & 0x00000001);	n = (n | (b << 31-f-3));

			//System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}
}
