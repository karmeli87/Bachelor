package JMat;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatlabScanner extends MatlabCodeContainer{

	
	public MatlabScanner(String path) throws IOException {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	protected int machtCount(String text,String pattern){
		Matcher match = Pattern.compile(pattern,Pattern.DOTALL).matcher(text);
		int count = 0;
		while (match.find())
		    count++;
		return count;
	}
	
	protected boolean isMatchOnce(String text,String pattern){
		return machtCount(text,pattern) == 1;
	}
	
	protected int locateLineNum(int position){
		String part = content.substring(0, position);
		return machtCount(part,"\n");
	}
	public void removeComments(){
		content = Pattern.compile("%\\{.*%\\}",Pattern.DOTALL).matcher(content).replaceAll("");
		content = Pattern.compile("%.*").matcher(content).replaceAll("");
		content = Pattern.compile("(?m)^\\s+$",Pattern.DOTALL).matcher(content).replaceAll("");
	}
	public String checkSyntax() throws Exception{
		removeComments();
		Scanner scanner = new Scanner("\n"+content);
		scanner.useDelimiter(procInitPattern);
		boolean blockFound = false;
		// empty line was added to the scanned text so the first next will result in everything before the delimiter.
		scanner.next();
		while(scanner.hasNext()){
			blockFound = true;
			String next = scanner.next();
			if(Debug){
				System.out.println("check section : " + next);
			}
			if(!isMatchOnce(next,procForPattern) || !isMatchOnce(next,procEndPattern)){
				scanner.close();
				throw new Exception(next +"\nsyntax error at line:" + locateLineNum(scanner.match().end()));
			}
		}
		scanner.close();
		if(!blockFound){
			throw new Exception("procinit was not found!");
		}
		return "pass";
	}
	
	public static void main(String[] args) throws Exception{
		try {
			Debug = true;
			MatlabScanner ms = new MatlabScanner("C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT/main.m");
			System.out.println("syntax check : " + ms.checkSyntax());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
