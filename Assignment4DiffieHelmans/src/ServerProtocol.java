
public class ServerProtocol {
	
	public String getResponse(String input)
	{
		input = input.toLowerCase();
		System.out.println("PROT: "+input);
		if(input.equals("hello") || input.equals("hi") || input.equals("hey"))
		{
			return "Hello Welcome Ecryption Success!";
		}else if(input.equals("commands") 
				|| input.equals("cmds") 
				|| input.equals("help") 
				|| input.equals("cmd") 
				|| input.equals("h")
		)
		{
			String help = "=====Server Help Dialogue=====\n";
			help += "\n";
			help += "hello|hi|hey\t\tGreetings\n";
			help += "commands|cmds|cmd|help|h\t\tShow This Help Dialogue";
			help += "joke|tell me a joke\t\tShow a funny joke";
			return help;
		}else
		{
			return "I'm Sorry I don't Understand :(";
		}
	}
}
