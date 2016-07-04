package MatlabExternalExecutor;
import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatlabExecutorStandAlone implements Runnable{
	
	private List<String> jobs = new ArrayList<String>();
	private StringBuilder prog = new StringBuilder();
	private static String MatlabPath = "matlab";
	private static String OS = "matlab";  
	public MatlabExecutorStandAlone(){
		prog.append("params=load('params.mat');"
				+ "fpcit_init(params);");
		//jobs.add("params=load('params.mat');");
		//jobs.add("fpcit_init(params);");
	}
	
	public void addJob(String jn){
		jobs.add(jn);
	}
	private String getMatlabProgramm(){
		String jobString = "";
		for (String job : jobs)
		{
			jobString +="'" + job + "',";
		}
		jobString = jobString.substring(0,jobString.length()-1);
		prog.append("process_spawn(params," + jobString + ");exit;"); 
		return prog.toString();
	}
	private static void setMatlabPath(String path){
		MatlabExecutorStandAlone.MatlabPath = path;
	}
	private static void setOS(String os){
		MatlabExecutorStandAlone.OS = os;
	}
	public List<String> getCmd(){
		 List<String> command = new ArrayList<String>();
		List<String> arg1 = MatlabExecutorStandAlone.OS == "Linux" ? 
				Arrays.asList("-nosplash","-nodisplay","-nodesktop") :
				Arrays.asList("-nosplash","-nodisplay","-nodesktop","-minimize");
		List<String> arg2 = Arrays.asList("-r",getMatlabProgramm());
		command.add(MatlabExecutorStandAlone.MatlabPath);
		command.addAll(arg1);
		command.addAll(arg2);
		return command;
	}
	public void run(){
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
	public static void main(String args[]) throws IOException, InterruptedException{
		// java -jar MatlabExecutor.jar -p -c ./config.txt -it 20 -n 2
		String paths[] ={
				"config1.txt",
				"config2.txt",
				"config3.txt",
				"config4.txt",
				"config5.txt",
				"config6.txt",
				"config7.txt",
				"config8.txt"
		};
		List<String> myArgs = Arrays.asList(args);
		boolean isLinux = System.getProperty("os.name").contains("Linux");
		MatlabExecutorStandAlone.setMatlabPath(isLinux ? "matlab" : "C:/Program Files/MATLAB/R2014b/bin/matlab");
		MatlabExecutorStandAlone.setOS(isLinux ? "Linux" : "Windows");
		
		int configIndex = myArgs.indexOf("-c");
		String mainConfigFile = configIndex > -1 ? myArgs.get(configIndex+1) : "C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/config.txt";
		
		int logIndex = myArgs.indexOf("-log");
		if(logIndex > -1){
			LogServer.LogFile = myArgs.get(logIndex+1);
		}
		
		
		int iterationsIndex = myArgs.indexOf("-it");
		int iterations = iterationsIndex > -1 ? Integer.parseInt(myArgs.get(iterationsIndex+1)) : 1;
		
		int processIndex = myArgs.indexOf("-n");
		int processes = processIndex > -1 ? Integer.parseInt(myArgs.get(processIndex+1)) : 4;
		ExecutorService exec = Executors.newFixedThreadPool(processes);
		
		boolean isParallel = myArgs.indexOf("-p") > -1; 
		
		
		for(int it = 0; it < iterations; it++){
			Thread t = new Thread(new LogServer());
			t.start();
			MatlabExecutorStandAlone runners[] = new MatlabExecutorStandAlone[processes];
			for (int i = 0; i < runners.length; i++) {
				runners[i] = new MatlabExecutorStandAlone();
			}
			
			for(int i=0; i<paths.length; i++){
				int assignTo = i % processes;
				runners[assignTo].addJob(paths[i]);
			}
			
			for (int i = 0; i < runners.length; i++) {
				if(isParallel){
					exec.execute(runners[i]);
				} else {
					runners[i].run();	
				}
			}
			
			t.join();
		}
		exec.shutdown();
		System.out.println("Done, go to sleep!");
	}
}
