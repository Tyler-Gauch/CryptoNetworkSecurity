import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Client {
	private int portNumber;
	private String host;
	private Socket s;
	private PrintWriter out;
	private BufferedReader in;
	private BufferedReader stdIn;
	private RC4 crypt;
	private ByteArrayInputStream stream;
    private ByteArrayOutputStream baos;
	
	public Client(int portNumber, String host)
	{
		try{
		this.portNumber = portNumber;
		this.host = host;
		s = new Socket(host, portNumber);
		out = new PrintWriter(s.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		}catch(Exception e)
		{
			System.err.println("Couldn't Connect because: ");
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		try
		{
			String fromServer;
			DiffieHellmans dh = null;
			BigInteger q = null;
			BigInteger a = null;
			BigInteger ya = null;
			boolean hasKey = false;
			
			String outputLine;
			String serverResponse = "";
			while(true)
			{
				if((fromServer = in.readLine()) != null)
				{
					serverResponse += fromServer;
					if(serverResponse.contains("<END>"))
					{
						System.out.println("SERVER: "+serverResponse);
						serverResponse = serverResponse.replace("<END>", "");
						if(!hasKey)
						{
							String[] pairs = serverResponse.split("&");
							for(int i = 0; i < pairs.length; i++)
							{
								String[] pair = pairs[i].split("=");
								if(pair[0].equals("q"))
								{
									q = new BigInteger(pair[1]);
								}else if(pair[0].equals("a"))
								{
									a = new BigInteger(pair[1]);
								}else if(pair[0].equals("p"))
								{
									ya = new BigInteger(pair[1]);
								}
							}
							if(!hasKey && q != null && a != null)
							{
								dh = new DiffieHellmans(q,a);
								out.println("p="+dh.publicComponent);
							}
							if(ya != null && dh != null)
							{
								dh.calculateKey(ya);
								System.out.println(dh.printKey());
								hasKey = true;
								this.crypt = new RC4(dh.getKey().toString());
							}
						}
						else 
						{
							serverResponse = new String(Base64.decodeBase64(serverResponse.getBytes()));
							serverResponse = this.crypt.decrypt(serverResponse);
							System.out.println("DECRYPT: "+serverResponse);
						}					
						
						if(hasKey)
						{
							System.out.print(">");
							if((outputLine = stdIn.readLine()) != null)
							{
								String crypted = this.crypt.encrypt(outputLine);
								crypted = new String(Base64.encodeBase64(crypted.getBytes()));
								System.out.println("ENCRYPTED: |"+crypted+"|");
								out.println(crypted);
							}
						}
						serverResponse = "";
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		Client c = new Client(8888, "localhost");
		c.start();
	}
}
