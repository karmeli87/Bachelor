package JMat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MatlabCodeContainer {
	
	public static boolean Debug = false;
	
	protected static String procInitPattern = "procinit";
	protected static String procForPattern = "procfor\\s*(\\w+)\\s*=\\s*(\\w+)\\s*:\\s*([^\\s]+)";
	protected static String procEndPattern = "procend";
	
	protected static String[] addGroupRegex(String[] str){
		for (int i = 0; i < str.length; i++) {
			str[i] = addGroupRegex(str[i]);
		}
		return str;
	}
	
	protected static String addGroupRegex(String str){
		return str + "(.*)";
	}
	
	protected String content;
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public MatlabCodeContainer(String path) throws IOException{
		this.content = readFile(path, StandardCharsets.UTF_8);
	}
}
