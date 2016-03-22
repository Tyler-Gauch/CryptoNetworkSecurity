
public class Decrypt {
	
	public static void main(String[] args) {
		if( args.length != 2 ) {
            System.err.println("Provide 2 filenames: encrypted decrypted.");
            System.exit(-1);
	     }
	
	     RSA_WIT rsa = new RSA_WIT("977447",  "649487", "6359");         // see the constructor for details.
	                             //  p            q     publicKey
	
	     rsa.PrintParameters();
	
	     System.out.println(rsa);

	     rsa.DecryptFile(args[0], args[1]);      // Decrypt file
	
	     System.out.println("FINISHED");
	}
	
}
