package JMat;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import MatlabPackage.MatlabExecutor;

public class JMat extends MatlabCodeAssambler {
	
	JMatExecutor head;
	JMatExecutor merge;
	JMatExecutor[] parts;
	
	public JMat(String mainScriptFile) throws Exception {
		super(mainScriptFile);
		// TODO Auto-generated constructor stub
	}
	
	public JMat(String mainScriptFile,int pNum) throws Exception {
		super(mainScriptFile);
		if(pNum > 0){
			this.ProcessNum = pNum;
		}
		// TODO Auto-generated constructor stub
	}
	
	private void setExecutors(){
		head = new JMatExecutor("startup",headPart.fileName,initialStateFile);
		merge = new JMatExecutor("startup",mergePart.fileName,mergedStateFile);
		
		parts = new JMatExecutor[forPart.length];
		for(int i=0; i<forPart.length; i++){
			parts[i] = new JMatExecutor("startup",forPart[i].fileName,forPart[i].fileName + "_State.mat");
		}
	}
	
	private void work() throws InterruptedException{
		ExecutorService exec = Executors.newFixedThreadPool(this.ProcessNum);
		
		generateFiles();
		setExecutors();
		
		System.out.println("Running common part..");
		head.run();
		System.out.println("Start process..");	
		for (int i = 0; i < this.ProcessNum; i++) {
				exec.execute(parts[i]);
		}		
		exec.shutdown();
		if(exec.awaitTermination(1000, TimeUnit.SECONDS)){
			// all jobs were completed in the specified time
			System.out.println("Merging data and complete the script...");	
			merge.run();
		};
	}
	
	public enum CleanMode {
	    all(true,true),
	    none(false,false),
	    code(true,false),
	    data(false,true);
		
		public final boolean codeMode;  
	    public final boolean dataMode; 
		CleanMode(boolean code,boolean data){
			this.codeMode = code;
			this.dataMode = data;
		}
	}
	// java -jar workflow.jar <path to file> [-m <path to matlab>] [-n <processes num>] [-clean <all|none|data|code>
	public static void main(String args[]){
		List<String> myArgs = Arrays.asList(args);
		String mainScriptFile = "";
		
		if(args.length == 0){
			System.out.println("Error : Can\'t execute -> Not enought arguments!");
			return;
		}
		if(!new File(args[0]).exists()){
			System.out.println("Error : Can\'t execute -> First argument must be the matlab script!");
			return;
		}
		
		boolean isLinux = System.getProperty("os.name").contains("Linux");
		MatlabExecutor.setOS(isLinux ? "Linux" : "Windows");
		int pathIndex = myArgs.indexOf("-m");
		String matlabPath = pathIndex > -1 ? myArgs.get(pathIndex+1) : 
			(isLinux ? "matlab" : "C:/Program Files/MATLAB/R2014b/bin/matlab.exe");
		
		if(!new File(matlabPath).canExecute()){
			System.out.println("Error : Can\'t execute -> Matlab path is wrong!");
			return;
		}
		MatlabExecutor.setMatlabPath(matlabPath);
		
		int processIndex = myArgs.indexOf("-p");
		int processes = processIndex > -1 ? Integer.parseInt(myArgs.get(processIndex+1)) : 0;
		
		int cleanIndex = myArgs.indexOf("-clean");
		String cleanString = cleanIndex > -1 ? myArgs.get(cleanIndex+1) : "all";
		CleanMode cleanMode = CleanMode.valueOf(cleanString);
		
		mainScriptFile = args[0];
		//int scriptIndex = myArgs.indexOf("-s");
		//String mainScriptFile = scriptIndex > -1 ? myArgs.get(scriptIndex+1) : "C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/main.m";
		
		try {
			JMat workflow = new JMat(mainScriptFile,processes);
			workflow.work();
			if(cleanMode != CleanMode.none){
				System.out.println("Cleaning temp files...");
				workflow.cleanFiles(cleanMode.codeMode,cleanMode.dataMode);	
			}
			System.out.println("Work done!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
}
