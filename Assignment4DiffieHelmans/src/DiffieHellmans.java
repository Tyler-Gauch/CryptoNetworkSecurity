import java.math.BigInteger;
import java.security.SecureRandom;


public class DiffieHellmans {

	public BigInteger a;
	public BigInteger q;
	public BigInteger publicComponent;
	private BigInteger secretComponent;
	private BigInteger key;
	
	public DiffieHellmans(){
		this.q = randomPrime(80);
		this.a = new BigInteger("3");
		this.init();
	}
	
	public DiffieHellmans(int bitLength){
		this.q = randomPrime(bitLength);
		this.a = new BigInteger("3");
		this.init();
	}
	
	public DiffieHellmans(BigInteger q, BigInteger a)
	{
		this.q = q;
		this.a = a;
		this.init();
	}

	private void init()
	{
		this.selectSecretComponent();
		this.calculatePublicComponent();
	}
	
	public void selectSecretComponent()
	{
		do{
			this.secretComponent = randomPrime(q.bitLength());
		}while(this.secretComponent.compareTo(this.q) > -1);
	}
	
	public void calculatePublicComponent()
	{
		this.publicComponent = this.a.modPow(this.secretComponent, this.q);
	}
	
	public void calculateKey(BigInteger otherPublic)
	{
		this.key = otherPublic.modPow(secretComponent, this.q);
	}
	
	public String printSecretComponent()
	{
		return "SECRET: "+this.secretComponent;
	}
	public String printPublicComponent()
	{
		return "PUBLIC: "+this.publicComponent;
	}
	public String printKey()
	{
		return "KEY: "+this.key;
	}
	
	public BigInteger getKey()
	{
		return this.key;
	}
	
	private BigInteger randomPrime(int N)
	{
		SecureRandom random = new SecureRandom();
		BigInteger prime = BigInteger.probablePrime(N, random);
		return prime;
	}
	
	public static void main(String[] args) {

		DiffieHellmans UserA = new DiffieHellmans();
		System.out.println("UserA started with creating q="+UserA.q+" and a="+UserA.a);
		System.out.println("\tUserA: "+UserA.printSecretComponent());
		System.out.println("\tUserA: "+UserA.printPublicComponent());
		DiffieHellmans UserB = new DiffieHellmans(UserA.q, UserA.a);
		System.out.println("UserB recieves a and q and computes: ");
		System.out.println("\tUserB: "+UserB.printSecretComponent());
		System.out.println("\tUserB: "+UserB.printPublicComponent());
		System.out.println("UserA and UserB swap public components");
		UserA.calculateKey(UserB.publicComponent);
		UserB.calculateKey(UserA.publicComponent);
		System.out.println("UserA: "+UserA.printKey());
		System.out.println("UserB: "+UserB.printKey());
		
		
	}

}
