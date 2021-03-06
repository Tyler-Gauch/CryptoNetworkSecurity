import java.math.BigInteger;
import org.apache.commons.codec.binary.Base64;
import java.net.*;
import java.io.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Server {

	private int portNumber;
	private boolean connected;
	private ServerSocket serverSocket;
	private RC4 crypt;
	private ByteArrayInputStream stream;
    private ByteArrayOutputStream baos;
	private int keySize = 80;
	public Server(int portNumber)
	{
		this.portNumber = portNumber;
		connected = false;
	}
	
	public void start(){
		
		try{
			if(!connected)
			{
				this.serverSocket = new ServerSocket(this.portNumber);
				connected = true;
			}
			
			System.out.println("Waiting For Client on Port "+this.portNumber);
			Socket clientSocket = this.serverSocket.accept();
			DiffieHellmans dh = new DiffieHellmans(keySize);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String inputLine;
			ServerProtocol sp = new ServerProtocol();
			
			out.println("q="+dh.q+"&a="+dh.a+"&p="+dh.publicComponent+"<END>");
			boolean hasKey = false;
			while(!this.serverSocket.isClosed())
			{
				while((inputLine = in.readLine()) != null)
				{
					if(!hasKey)
					{
						String[] pairs = inputLine.split("&");
						for(int i = 0; i < pairs.length; i++)
						{
							String[] pair = pairs[i].split("=");
							if(pair[0].equals("p"))
							{
								dh.calculateKey(new BigInteger(pair[1]));
								System.out.println(dh.printKey());
								this.crypt = new RC4(dh.getKey().toString());
								hasKey = true;
							}
						}
					}else
					{
						System.out.println("CLIENT: "+inputLine);
						inputLine = new String(Base64.decodeBase64(inputLine));
						inputLine = this.crypt.decrypt(inputLine);
						System.out.println("DECRYPT: "+inputLine);
						
						String outputLine = this.crypt.encrypt(sp.getResponse(inputLine));
						outputLine = new String(Base64.encodeBase64(outputLine.getBytes()))+"<END>";
						System.out.println("ENCRYPTED: |"+outputLine+"|");
				        out.println(outputLine);
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	
	
	public static void main(String[] args) {
		Server s = new Server(8888);
		try{
			int keySize = Integer.parseInt(args[0]);
			System.out.println("Setting keySize to "+keySize);
			s.setKeySize(keySize);
		}catch(Exception e){
			System.out.println("No keySize defaulting to 80");
			s.setKeySize(80);
		};
		s.start();

	}
}
