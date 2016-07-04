package JMat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatlabParser extends MatlabScanner {

	public String head;
	public String init;
	public String iterator;
	public String startIndex;
	public String endIndex;
	public String loop;
	public String tail;
	
	protected MatlabParser(String path) throws IOException {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	protected Pattern getPattern(){
		String[] patterns = {procInitPattern,procForPattern,procEndPattern};
		String pattern = String.join("", addGroupRegex(patterns));
		return Pattern.compile("(.*)" + pattern,Pattern.DOTALL);
	}
	
	public String findSections() throws Exception{
			
		checkSyntax();
		Matcher matcher =getPattern().matcher(content);
		if(!matcher.matches()){
			return "No split needed!";
		}
		this.head = matcher.group(1);
		this.init = matcher.group(2);
		this.iterator = matcher.group(3);
		this.startIndex = matcher.group(4);
		this.endIndex = matcher.group(5);
		this.loop = matcher.group(6);
		this.tail = matcher.group(7);
		
		//TODO: recursive on the head
		if(Debug){
			System.out.println("head : " + head);
			System.out.println("init : " + init);
			System.out.println("startIndex : " + startIndex);
			System.out.println("endIndex : " + endIndex);
			System.out.println("loop : " + loop);
			System.out.println("tail : " + tail);
		}
		
		return "";
	}
	public static void main(String[] args){
		Debug = true;
		try {
			MatlabParser mp = new MatlabParser("C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/main.m");
			mp.findSections();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
