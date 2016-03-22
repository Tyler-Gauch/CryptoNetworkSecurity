
public class StringCheck {

	public static String encode(String x)
	{
		x = x.replace("\n", "<NEWLINE>");
		x = x.replace("\t", "<TAB>");
		x = x.replace("\r", "<CARRIAGE>");
		x = x.replace(" ", "<SPACE>");
		x = x.replace("\b", "<BACKSPACE>");
		x = x.replace("\f", "<FORMFEED>");
		x = x.replace("\'", "<QUOTE>");
		x = x.replace("\"", "<DOUBLEQUOTE>");
		x = x.replace("\\", "<BACKSLASH>");
		x += "<END>";
		return x;
	}
	
	public static String decode(String x)
	{
		x = x.replace("<NEWLINE>", "\n");
		x = x.replace("<TAB>", "\t");
		x = x.replace("<CARRIAGE>", "\r");
		x = x.replace("<SPACE>", " ");
		x = x.replace("<BACKSPACE>", "\b");
		x = x.replace("<FORMFEED>", "\f");
		x = x.replace("<QUOTE>", "\'");
		x = x.replace("<DOUBLEQUOTE>", "\"");
		x = x.replace("<BACKSLASH>", "\\");
		x = x.replace("<END>", "");
		return x;
	}
}
