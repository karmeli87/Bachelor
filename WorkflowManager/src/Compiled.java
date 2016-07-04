import com.mathworks.toolbox.javabuilder.MWException;
import FPCIT.FPCIT;

public class Compiled {

	static Object[] params = null;
	String file;
	
	public Compiled(String file){
		this.file = file;	
	}
	public void run(){
		try {
			FPCIT fpcit = new FPCIT();
			System.out.printf("starting... %s\n",this.file);
			fpcit.fpcit_main(this.file, params);
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws MWException{
		if(args.length < 2){
			System.out.printf("Error: Not enought arguments... \n");
			return;
		}
		// arg[0] - root path
		// arg[1] - file name
		System.out.printf("Initilazing Matlab-Runtime...\n");
		FPCIT fpcit = new FPCIT();
		fpcit.funcall(0,"spm", "defaults", "PET");
		fpcit.funcall(0,"spm_jobman", "initcfg");
		System.out.printf("Set params...\n ");
		params = fpcit.fpcit_set_params(1,args[0]);
		System.out.printf("Set the class ...\n ");
		Compiled myRun = new Compiled(args[1]);
		System.out.printf("starting... %s\n",myRun.file);
		long start_time = System.nanoTime();
		fpcit.fpcit_main(myRun.file, params[0]);
		System.out.printf("finished %s in %f seconds\n",myRun.file,(System.nanoTime() - start_time)/1e9);	
	}
}
