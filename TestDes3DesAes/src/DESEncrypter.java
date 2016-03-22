/*
*
*   Tyler Gauch
*   Assignment 1
*   January 26, 2016
*
*/

import java.io.*;
import java.security.spec.AlgorithmParameterSpec; 
import java.security.spec.KeySpec;
import javax.crypto.spec.*;
//import java.security.Provider;


import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;



import javax.crypto.*;

public class DESEncrypter {
	private Cipher ecipher;
	private Cipher dcipher;
	
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]{
        	(byte)0x8E, (byte)0x12, (byte)0x39, (byte)0x9C,
            	(byte)0x07, (byte)0x72, (byte)0x6F, (byte)0x5A
        	};


	



	DESEncrypter(String passPhrase, String ALG) {
            	// Prepare the parameter to the ciphers
    		int iterationCount = 19; // Iteration count
            	AlgorithmParameterSpec paramSpec = new PBEParameterSpec(iv, iterationCount);


 		try {
            		// Create the key
            		KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), iv, iterationCount);
            		SecretKey key = SecretKeyFactory.getInstance(ALG).generateSecret(keySpec);	


            		ecipher = Cipher.getInstance(key.getAlgorithm());
            		dcipher = Cipher.getInstance(key.getAlgorithm());


            		// Create the ciphers
            		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);


        	} 
		catch (java.security.InvalidAlgorithmParameterException e) { System.err.println(e); } 
		catch (java.security.spec.InvalidKeySpecException e) 	   { System.err.println(e); } 
		catch (javax.crypto.NoSuchPaddingException e) 		   { System.err.println(e); } 
		catch (java.security.NoSuchAlgorithmException e) 	   { System.err.println(e); } 
		catch (java.security.InvalidKeyException e) 		   { System.err.println(e); }
	}


	/*
    	 * Create a key like: SecretKey key = KeyGenerator.getInstance("DES").generateKey();
	 * and pass the key to this constructor.
	 */
	/*
	DesEncrypter(SecretKey key) {

        	AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        	try {
            		ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            		dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            		// CBC requires an initialization vector
            		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        	} 
		catch (java.security.InvalidAlgorithmParameterException e) { System.err.println(e); } 
		catch (javax.crypto.NoSuchPaddingException e) 		   { System.err.println(e); } 
		catch (java.security.NoSuchAlgorithmException e) 	   { System.err.println(e); } 
		catch (java.security.InvalidKeyException e) 		   { System.err.println(e); }
    	}
	*/




    	public void encrypt(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
        	try {
            		// Bytes written to out will be encrypted
            		out = new CipherOutputStream(out, ecipher);

            		// Read in the cleartext bytes and write to out to encrypt
            		int numRead = 0;
            		while ((numRead = in.read(buf)) >= 0) {
                		out.write(buf, 0, numRead);
            		}
            		out.close();
        	} 
		catch (java.io.IOException e) { System.err.println("encrypt: " + e);}
    	}

    	public void decrypt(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
        	try {
            		// Bytes read from in will be decrypted
            		in = new CipherInputStream(in, dcipher);

            		// Read in the decrypted bytes and write the cleartext to out
            		int numRead = 0;
            		while ((numRead = in.read(buf)) >= 0) {
                		out.write(buf, 0, numRead);
            		}
            		out.close();
        	} 
		catch (java.io.IOException e) { System.err.println("decrypt: " + e);}
    	}


	//
	// This method is implemented in Listing All Available Cryptographic Services
	//
	private static void printCryptoImpls(String serviceType) {
		String[] names;
        	Set<String> result = new HashSet<String>();

        	// All providers
        	Provider[] providers = Security.getProviders();
        	for (int i=0; i<providers.length; i++) {

            		// Get services provided by each provider
            		Set keys = providers[i].keySet();


            		/*for (Iterator it=keys.iterator(); it.hasNext(); ) {
                		String key = (String)it.next();
				System.out.println(key);
			}*/

            		for (Iterator it=keys.iterator(); it.hasNext(); ) {

                		String key = (String)it.next();

                		key = key.split(" ")[0];

                		if (key.startsWith(serviceType + ".")) {
                    			result.add(key.substring(serviceType.length() + 1));
                		} 
				else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
                    			// This is an alias
                    			result.add(key.substring(serviceType.length() + 11));
                		}
            		}
        	}

        	names =  (String[])result.toArray(new String[result.size()]);
		for(int i=0; i < names.length; i++)
			System.out.println("\t"+names[i]);
    	}


	



	public static void main(String [] args) {

		try {
			/*
			System.out.println("Crypto Implementations:");
			printCryptoImpls("Cipher");
			System.out.println("Key Generators:");
			printCryptoImpls("KeyGenerator");
			*/
			
            /*
			 * Pass it either "PBEWithMD5AndDES" or "PBEWithMD5AndTripleDES" for DES or 3DES respectively
			 */

            DESEncrypter DES = new DESEncrypter("test passphrase", "PBEWithMD5AndDES" );
            DESEncrypter TDES = new DESEncrypter("test passphrase", "PBEWithMD5AndTripleDES" );
            
            KeyGenerator    kgen    =   KeyGenerator.getInstance("AES");
            kgen.init(256); // or 128
            SecretKey key           =   kgen.generateKey();
            
            AESEncrypter AES = new AESEncrypter(key);
	        
	       
	        int amount = 100;
	
	        for(int k = 0; k < 200; k++)
	        {
	        	String toEncrypt = "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";
		        ByteArrayInputStream stream;
		        ByteArrayOutputStream baos;
	        	long time = 0;
		        for(int i = 0; i < amount; i++)
		        {
		        	long start = System.nanoTime();
		        	
			        stream = new ByteArrayInputStream(toEncrypt.getBytes());
			        baos = new ByteArrayOutputStream();
			        //TDES.encrypt(stream, baos);
			        //DES.encrypt(stream, baos);
			        AES.encrypt(stream, baos);
			        toEncrypt = new String(baos.toByteArray());
			        
			        long end = System.nanoTime();
			        time += end-start;
		        }
		        
		        System.out.print((time/100)+":");
	        
	        	long dectime = 0;
		        
		        for(int j = 0; j < amount; j++)
		        {
		        	long start = System.nanoTime();
		            stream = new ByteArrayInputStream(toEncrypt.getBytes());
		            baos = new ByteArrayOutputStream();
		            //TDES.decrypt(stream, baos);
		            //DES.decrypt(stream, baos);
		            AES.decrypt(stream, baos);
		            toEncrypt = new String(baos.toByteArray());
		            
		            long end = System.nanoTime();
		            dectime += end-start;
		        }
		        
		        System.out.println(dectime/100);
	        }
		} 
		catch (Exception e) { System.err.println(e); }
	}
}

//a ®z\ÁHÇòysl+TÓ?°!ùÜEÍì\½_ÅáQº9 ¬fÊ
//a ®z\ÁHÇòysl+TÓ?°!ùÜEÍì\½_ÅáQº9 ¬fÊ

//×~ìÂL2P7ïPÞ™èý>‡¡àô“2r‚~DAì‘
//×~ìÂL2P7ïPÞ™è)K…vcš?2Ì‚~DAì‘
