
public class Encrypt {

	public static void main(String[] args) {
		if( args.length != 2 ) {
            System.err.println("Provide 2 filenames: plain encrypted.");
            System.exit(-1);
	     }
	
	     RSA_WIT rsa = new RSA_WIT("977447",  "649487", "6359");         // see the constructor for details.
	                             //  p            q     publicKey
	
	     rsa.PrintParameters();
	
	     System.out.println(rsa);
	
	     //rsa.EncryptDecrypt(args[0], args[2]); // encrypt/decrypt on the fly
	
	     rsa.EncryptFile(args[0], args[1]);      // Encrypt file
	
	     System.out.println("FINISHED");
	}

}
