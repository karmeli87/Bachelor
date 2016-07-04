package MatlabPackage;


import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MatlabExecutor implements Runnable{
	
	private static String MatlabPath = "matlab";
	private static String OS = "Linux";  
	private String program = "";
	
	
	public static void setMatlabPath(String path){
		MatlabExecutor.MatlabPath = path;
	}
	public static void setOS(String os){
		MatlabExecutor.OS = os;
	}
	
	public MatlabExecutor (String program){
		this.program = program;
	}
	
	public static boolean isWindows(){
		return System.getProperty("os.name").indexOf("Windows") > -1;
	}
	
	public List<String> getCmd(){
		 List<String> command = new ArrayList<String>();
		List<String> arg1 = !isWindows() ? 
				Arrays.asList("-nosplash","-nodisplay","-nodesktop") :
				Arrays.asList("-nosplash","-nodisplay","-nodesktop","-minimize","-wait");
		List<String> arg2 = Arrays.asList("-r",program);
		List<String> arg3 = !isWindows() ? 
				Arrays.asList(">","/dev/null") :
				Arrays.asList("");
		command.add(MatlabExecutor.MatlabPath);
		command.addAll(arg1);
		command.addAll(arg2);
		command.addAll(arg3);
		//if()
		return command;
	}
	public void run(){
		
		if (program == ""){
			return;
		}
		
		Process process;
		List<String> cmd = getCmd();
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			System.out.printf("run command: %s\n",cmd);
			process = pb.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
