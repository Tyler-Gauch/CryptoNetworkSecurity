import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RC4 {

	private byte[] password;
	private byte[] state;
	public RC4(String password)
	{
		this.password = password.getBytes();
		state = new byte[256]; 
		this.ksa(state);
	}
	
	public void ksa(byte state[]) {
   		int i,j=0; 
		byte t;
   
   		for (i=0; i < 256; ++i)
      			state[i] = (byte) i; 
   		for (i=0; i < 256; ++i) {
      			j = (j + state[i] + this.password[i % this.password.length]) % 256; 
      			// now swap
      			i &=0xff;
      			j &=0xff;
      			t = state[i]; 
      			state[i] = state[j]; 
      			state[j] = t; 
   		}   
	}

	public void prga(InputStream inStream, OutputStream outStream, byte state[]) { 
   		byte key; 
		byte t;
		int read = 0;
		byte[] buf = new byte[1];
		int i=0, j = 0;
   		try {

			while((read = inStream.read(buf)) > 0){
					i = (i + 1) % 256; 
					j = (j + state[i]) % 256; 

					i &=0xff;
					j &=0xff;

					// now swap
					t = state[i]; 
					state[i] = state[j]; 
					state[j] = t; 
					
					key = state[ (((state[i] + state[j]) % 256) &0xff)];
					buf[0] ^= key;
					outStream.write(buf);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}  

	public String prga(String text, byte state[]) { 
   		byte key; 
		byte t;
		int i = 0, j = 0;
		byte[] string = text.getBytes();

		for(int x = 0; x < string.length; x++){
			i = (i + 1) % 256; 
			j = (j + state[i]) % 256; 
				
			i &=0xff;
			j &=0xff;
			// now swap
			t = state[i]; 
			state[i] = state[j]; 
			state[j] = t; 
			
			key = state[ (((state[i] + state[j]) % 256) &0xff)];
			string[x] ^= key;
		} 
		
		return new String(string);
	}  
	
	public void encrypt(File in, File out) throws FileNotFoundException
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		this.prga(new FileInputStream(in), new FileOutputStream(out), state);
	}
	
	public void decrypt(File in, File out) throws FileNotFoundException
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		this.prga(new FileInputStream(in), new FileOutputStream(out), state);
	}

	public void encrypt(InputStream in, OutputStream out)
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		this.prga(in, out, state);
	}
	
	public void decrypt(InputStream in, OutputStream out)
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		this.prga(in, out, state);
	}
	
	public String encrypt(String text)
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		return this.prga(text, state);
	}
	
	public String decrypt(String text)
	{
		byte[] state = new byte[256]; 
		this.ksa(state);
		return this.prga(text, state);
	} 
	
	public static void main(String args[]) {
		
		String test = "I'm sorry I don't understand :(";
		System.out.println("ORIGINAL: "+test);
		RC4 rc4 = new RC4("MyPassword");
		RC4 rc42 = new RC4("MyPassword");
		for(int i = 0; i < 10; i++)
		{
			test = rc4.encrypt(test);
			System.out.println("ENCRYPT: " + test);
			test = rc42.decrypt(test);
			System.out.println("DECRYPT: " + test);
		}
		
		/*try {
			
			if(args.length < 1)
			{
				System.err.println("USAGE: required FILENAME");
				System.exit(-1);
			}
			
			String fileName = args[0];
			String extension = fileName.substring(fileName.indexOf("."), fileName.length());
			String fileNameChopped = fileName.substring(0, fileName.indexOf("."));
			long instance = System.nanoTime();
			File orig = new File(fileName);
			File encFile = new File(fileNameChopped+"_encrypted_"+instance+extension);
			encFile.createNewFile();
			File decFile = new File(fileNameChopped+"_decrypted_"+instance+extension);
			decFile.createNewFile();
		
			RC4 rc4 = new RC4("MyPassword");	
	    	 	// ENCRYPTING
			rc4.encrypt(orig, encFile);
			rc4.decrypt(encFile, decFile);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}