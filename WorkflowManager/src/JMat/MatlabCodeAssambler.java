package JMat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MatlabCodeAssambler{
	protected int ProcessNum = Runtime.getRuntime().availableProcessors();
	private MatlabParser mp;
	
	public MatlabFileCreator headPart;
	public MatlabFileCreator mergePart;
	public MatlabFileCreator[] forPart;
	
	private int filesNum;
	private Thread workers[];
	private Thread headWorker;
	private Thread mergeWorker;
	
	private static String filePrefix = "JMat_";
	protected String initialStateFile = addFilePrefix("InitialState.mat");
	protected String mergedStateFile = addFilePrefix("MergedState.mat");
	
	private Path mainScript;
	private Path localFolder = Paths.get("\\home\\tmp\\");
	
	public MatlabCodeAssambler(String path) throws Exception{
		this.mainScript = Paths.get(path);
		mp = new MatlabParser(path);
		mp.findSections();
	}
	
	private String getGlobalCatchErrorCode(String fileName){
		return "catch ME\n"
				+ "errFile = fopen('"+fileName+"','w');\n"
				+ "fprintf(errFile,'%s',getReport(ME));\n"
				+ "fclose(errFile);\n"
				+ "exit;\n"
				+ "end\n" ;
	}
	
	private String getLocalCatchErrorCode(String fileName){
		return "catch ME\n"
				+ "errFile = fopen('"+fileName+"','a');\n"
				+ "fprintf(errFile,'When running iteration %d occured an error:\\n%s',"+ mp.iterator +",getReport(ME));\n"
				+ "fclose(errFile);\n"
				+ "end\n" ;
	}
	
	public String addFilePrefix(String str){
		return filePrefix + str;
	}
	
	public int getProcessNum(){
		return this.ProcessNum;
	}
	public void setProcessNum(int num){
		this.ProcessNum = num;
	}
	protected void _BuildHeadFile(){
		String headScriptFile = addFilePrefix("HeadPart.m");
		String code = "try\n" + mp.head + "\n save('"+initialStateFile+"');exit;" + getGlobalCatchErrorCode("err_"+headScriptFile+".log");
		headPart = new MatlabFileCreator(headScriptFile,code,initialStateFile);
		headWorker = new Thread(headPart);
		headWorker.start();
	}
	
	public String getIndexString (int index){
		return "ceil((" + mp.endIndex + " - " + mp.startIndex +")*"+ index +"/" + this.ProcessNum + "+" + mp.startIndex +")";
	}
	protected void _BuildProcessFile(){
		_BuildProcessFile(true);
	}
	protected void _BuildProcessFile(boolean create){
		// procRes and procId are special vars
		workers = new Thread[this.ProcessNum];
		forPart = new MatlabFileCreator[this.ProcessNum];
		String initPart = mp.init + "procRes={};\n";
		
		
		for(int i=0; i<this.ProcessNum;i++){
			String lastLoop = i == this.ProcessNum-1 ? "" : "-1";
			String fileName = addFilePrefix("Process" + i);
			String processFileName = fileName + ".m";
			String localProcVars = "\n procId = " + i +";\n";
			String resultFile = fileName + "_State.mat";
			if(create && Files.exists(localFolder)){
				//resultFile = localFolder.toString() + "\\" + resultFile;
			}
			String startIndex = getIndexString(i);
			String endIndex = getIndexString(i+1);
			String forHead = "for " + mp.iterator + "=" + startIndex + ":" + endIndex + lastLoop+"\n";
			String forBlock = "try\n" + mp.loop + getLocalCatchErrorCode("err_"+fileName+".log") + "end\n";
			String save = "save('" + resultFile + "','procRes');";
			String code = 	"try\n"  
							+ "load('"+initialStateFile+"');\n" 
							+ localProcVars + initPart + forHead + forBlock  
							+ getGlobalCatchErrorCode("err_"+fileName+".log")
							+ save
							+ "exit;";
			forPart[i] = new MatlabFileCreator(processFileName,code,resultFile);
			if(create){
				workers[i] = new Thread(forPart[i]);
				workers[i].start();		
			}
		}
	}
	
	protected void _BuildMergeFile(){
		String mergeScriptFile = addFilePrefix("MergePart.m");
		String loadInitialState = "load('"+initialStateFile+"');\n";
		String mergingCode = "try\n";
		for(int i=0; i<this.ProcessNum;i++){
			String load = "procRes{"+(i+1) +"} = load('"+forPart[i].fileName+"_State.mat','procRes');\n";
			mergingCode += load + "procRes{"+(i+1) +"}=procRes{"+(i+1) +"}.procRes;\n";
		}
		mergingCode += getGlobalCatchErrorCode("err_JMat_MergePart.log");
				
		String code = loadInitialState + mergingCode + mp.tail + "\n save('"+mergedStateFile+"','procRes');exit;";
		mergePart = new MatlabFileCreator(mergeScriptFile,code,mergedStateFile);
		mergeWorker = new Thread(mergePart);
		mergeWorker.start();
	}
	
	protected void _BuildStartup(){
		String abPath = mainScript.toAbsolutePath().toString();
		String str = "addpath('"+abPath.substring(0,abPath.lastIndexOf(File.separator))+"')";
		
		try(  PrintWriter out = new PrintWriter("startup.m")  ){
			out.println(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cleanFiles(boolean cleanCode,boolean cleanData){
		if(cleanCode){
			headPart.clean();
			mergePart.clean();	
			new File("startup.m").delete();
		}
		if(cleanData){
			new File(initialStateFile).delete();
			new File(mergedStateFile).delete();	
		}
		for(int i=0; i<this.ProcessNum;i++){
			String fileName = addFilePrefix("Process" + i);
			String processFileName = fileName + ".m";
			if(cleanData){
				new File(fileName + "_State.mat").delete();	
			}
			if(cleanCode){
				new File(processFileName).delete();
				forPart[i].clean();	
			}
		}
	}
	
	public void waitHeadWorker() throws InterruptedException{
		headWorker.join();
	}
	public void waitMergeWorker() throws InterruptedException{
		mergeWorker.join();
	}
	public void waitPartsWorkers() throws InterruptedException{
		for(int i=0; i<filesNum;i++){
			workers[i].join();;
		}
	}
	
	public boolean generateFiles(){
		try{
			
			this._BuildStartup();
			this._BuildHeadFile();
			this._BuildProcessFile();
			this._BuildMergeFile();
			
			this.waitHeadWorker();
			this.waitPartsWorkers();
			this.waitMergeWorker();
			
		}catch(Exception e){
			return false;
		}
		return true;
	}
	public static void main(String[] args){
		try {
			MatlabCodeAssambler mca = new MatlabCodeAssambler("C:/Users/Karmeli/Desktop/Koop-HU-Informatik/matlab.txt");
			mca.generateFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
