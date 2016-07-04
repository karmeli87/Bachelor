import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassExecutor implements Runnable {

	private String file;
	private String root;
	
	public ClassExecutor(String root,String file){
		this.file = file;
		this.root = root;
	}
	public void run(){
		try {
			ClassExecutor.exec(Compiled.class,root,file);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	static String paths[] ={
			"config.txt",
			"config2.txt",
			"config3.txt",
			"config4.txt"
	};
	static String rootDefault = "C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/config.txt";
	
	
	public static int exec(Class<Compiled> klass,String arg1,String arg2) throws IOException,InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
		File.separator + "bin" +
		File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		
		ProcessBuilder builder = new ProcessBuilder(
		javaBin, "-cp", classpath, className,arg1,arg2);
		builder.redirectOutput(Redirect.INHERIT);
		builder.redirectError(Redirect.INHERIT);
		Process process = builder.start();
		process.waitFor();
		return process.exitValue();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.printf("Main process Warm-up... \n");
		String root = (args.length > 0) ? args[0] : ClassExecutor.rootDefault;
		int cores = (args.length > 1) ? Integer.parseInt(args[1]) : 4;
		
		ExecutorService exec = Executors.newFixedThreadPool(cores);
		ClassExecutor[] runnables = new ClassExecutor[cores];
		
		for(int i=0; i<cores; i++){
			runnables[i] = new ClassExecutor(root,paths[i]);
		}
	    for(ClassExecutor m : runnables) {
	        exec.execute(m);
	    }
	    for(int i=0; i<cores; i++){
			//runnables[i].run();
		}
	}

}
