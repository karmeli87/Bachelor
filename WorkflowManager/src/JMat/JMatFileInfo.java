package JMat;

public class JMatFileInfo {
	
	public String startupFile;
	public String myFile;
	public String resultFile;
	
	private static CharSequence delimiter = "!";
	
	public JMatFileInfo(String startup,String file,String res){
		this.startupFile = startup;
		this.myFile = file;
		this.resultFile = res;
	}
	
	public static String stringfy(JMatFileInfo jfi){
		return String.join(delimiter, jfi.startupFile,jfi.myFile,jfi.resultFile);
	}
	
	public static JMatFileInfo destringfy(String str){
		String[] temp = str.split((String) delimiter);
		return new JMatFileInfo(temp[0],temp[1],temp[2]);
	}
}
