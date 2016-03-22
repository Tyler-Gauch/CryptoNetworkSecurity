/*
*
*   Tyler Gauch
*   Assignment 1
*   January 26, 2016
*
*/


package assignment1;

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

public class DesEncrypter {
	private Cipher ecipher;
	private Cipher dcipher;
	
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]{
        	(byte)0x8E, (byte)0x12, (byte)0x39, (byte)0x9C,
            	(byte)0x07, (byte)0x72, (byte)0x6F, (byte)0x5A
        	};


	



	DesEncrypter(String passPhrase, String ALG) {
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

            String filename = "output/cleartext.txt";
            int part = 1;

            for(String s : args)
            {
                String key = s;
                String value = null;
                if(s.indexOf("=") > -1)
                {
                    key = s.substring(0, s.indexOf("="));
                    value = s.substring(s.indexOf("=")+1);
                }

                if(key.equals("--help") || key.equals("-h"))
                {
                    System.out.println("");
                    System.out.println("====== Assignment 1 DES/AES Encryption =========");
                    System.out.println("");
                    System.out.println("USAGE: java assignment1/DesEncrypter [options]");
                    System.out.println("");
                    System.out.println("Options              |  Description");
                    System.out.println("====================================");
                    System.out.println("--part=NUMBER        |  Specifies which part to run 1-4");
                    System.out.println("");
                    System.out.println("--filename=FILENAME  |  Specifies what the filename to encrypt is");
                    System.exit(0);
                }

                if(key.equals("--part"))
                {
                    if(value == null)
                    {
                        System.err.println("--part must take in a number 1-3 as a parameter (i.e --part=1) defaulting to 1");
                        continue;
                    }
                    part = Integer.parseInt(value);
                }else if(key.equals("--filename"))
                {
                    if(value == null)
                    {
                        System.err.println("--filename must take in a parameter (i.e --filename=cleartext.txt) defaulting to cleartext.txt");
                        continue;
                    }
                    filename = value;
                }
            }
    
            String extension = filename.substring(filename.indexOf("."));
            String filewoExt = filename.substring(0, filename.indexOf("."));


            DesEncrypter DES = new DesEncrypter("test passphrase", "PBEWithMD5AndDES" );
            DesEncrypter TDES = new DesEncrypter("test passphrase", "PBEWithMD5AndTripleDES" );


            if(part == 1)
            {
                String encryptedDESFile = filewoExt + "_encrypted_DES_part_1" + extension;
                String decryptedDESFile = filewoExt + "_decrypted_DES_part_1" + extension;
                String encrypted3DESFile = filewoExt + "_encrypted_3DES_part_1" + extension;
                String decrypted3DESFile = filewoExt + "_decrypted_3DES_part_1" + extension;

                System.out.println("Running Part 1...");
                System.out.println();
                
                System.out.println("Encrypting file '"+filename+"' with DES into '"+encryptedDESFile+"'");
                DES.encrypt(new FileInputStream(filename), new FileOutputStream(encryptedDESFile));

                System.out.println("Decrypting file '"+encryptedDESFile+"' with DES into '"+decryptedDESFile+"'");
                DES.decrypt(new FileInputStream(encryptedDESFile), new FileOutputStream(decryptedDESFile));

                System.out.println("Encrypting file '"+filename+"' with 3DES into '"+encrypted3DESFile+"'");
                TDES.encrypt(new FileInputStream(filename), new FileOutputStream(encrypted3DESFile));

                System.out.println("Decrypting file '"+encrypted3DESFile+"' with 3DES into '"+decrypted3DESFile+"'");
                TDES.decrypt(new FileInputStream(encrypted3DESFile), new FileOutputStream(decrypted3DESFile));

                System.out.println("\nPart 1 Complete");
            }else if (part == 2)
            {

                String encryptedDESFile = filewoExt + "_encrypted_DES_1_part_2" + extension;
                String encryptedDES2File = filewoExt + "_encrypted_DES_2_part_2" + extension;
                String decryptedDESFile = filewoExt + "_decrypted_DES_1_part_2" + extension;
                String decryptedDES2File = filewoExt + "_decrypted_DES_2_part_2" + extension;

                System.out.println("Running Part 2...");
                System.out.println();

                System.out.println("Encrypting file '"+filename+"' with DES into '"+encryptedDESFile+"'");
                DES.encrypt(new FileInputStream(filename), new FileOutputStream(encryptedDESFile));

                System.out.println("Encrypting file '"+encryptedDESFile+"' with DES into '"+encryptedDES2File+"'");
                DES.encrypt(new FileInputStream(encryptedDESFile), new FileOutputStream(encryptedDES2File));
                
                System.out.println("Decypting file '"+encryptedDES2File+"' with DES into '"+decryptedDESFile+"'");
                DES.decrypt(new FileInputStream(encryptedDES2File), new FileOutputStream(decryptedDESFile));

                System.out.println("Decypting file '"+decryptedDESFile+"' with DES into '"+decryptedDES2File+"'");
                DES.decrypt(new FileInputStream(decryptedDESFile), new FileOutputStream(decryptedDES2File));

                System.out.println("Part 2 Complete");
            }else if(part == 3)
            {
                String encryptedDESFile = filewoExt + "_encrypted_DES_part_3" + extension;
                String decrypted3DESFile = filewoExt + "_decrypted_3DES_part_3" + extension;

                System.out.println("Running Part 3...");
                System.out.println();
                
                System.out.println("Encrypting file '"+filename+"' with DES into '"+encryptedDESFile+"'");
                DES.encrypt(new FileInputStream(filename), new FileOutputStream(encryptedDESFile));

                System.out.println("Decrypting file '"+encryptedDESFile+"' with 3DES into '"+decrypted3DESFile+"'");
                TDES.decrypt(new FileInputStream(encryptedDESFile), new FileOutputStream(decrypted3DESFile));

                System.out.println("Part 3 Complete");
            }else if(part == 4)
            {
                String encryptedAESFile = filewoExt + "_encrypted_AES_part_4" + extension;
                String decryptedAESFile = filewoExt + "_decrypted_AES_part_4" + extension;

                System.out.println("Running Part 4...");
                System.out.println();

                KeyGenerator    kgen    =   KeyGenerator.getInstance("AES");
                kgen.init(256); // or 128
                SecretKey key           =   kgen.generateKey();
                
                // Create encrypter/decrypter class
                AESEncrypter encrypter = new AESEncrypter(key);
                
                System.out.println("Encrypting file '"+filename+"' with AES into '"+encryptedAESFile+"'");
                encrypter.encrypt(new FileInputStream(filename),new FileOutputStream(encryptedAESFile));
                
                System.out.println("Decrypting file '"+encryptedAESFile+"' with AES into '"+decryptedAESFile+"'");
                encrypter.decrypt(new FileInputStream(encryptedAESFile),new FileOutputStream(decryptedAESFile));
            }else if(part == 5)
            {
                System.out.println("Running Part 5...");
                String toEncrypt = "I'm Going To Encrypt This!!!!";
                System.out.println("ENCYRPTING: "+toEncrypt);
                InputStream stream = new ByteArrayInputStream(toEncrypt.getBytes("UTF-8"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int amount = 1;

                for(int i = 0; i < amount; i++)
                {
                    DES.encrypt(stream, baos);
                    toEncrypt = new String(baos.toByteArray(), "UTF-8");
                    System.out.println("\t"+toEncrypt);
                    stream = new ByteArrayInputStream(toEncrypt.getBytes("UTF-8"));
                }

                for(int j = 0; j < amount; j++)
                {
                    DES.decrypt(stream, baos);
                    toEncrypt = new String(baos.toByteArray(), "UTF-8");
                    System.out.println("\t"+toEncrypt);
                    stream = new ByteArrayInputStream(toEncrypt.getBytes("UTF-8"));
                }

                System.out.println("DECRYPTED TO: "+toEncrypt);

            }
		} 
		catch (Exception e) { System.err.println(e); }
	}
}
