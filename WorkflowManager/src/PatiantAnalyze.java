import com.mathworks.toolbox.javabuilder.MWException;

import FPCIT.FPCIT;
import FPCIT.Function;

public class PatiantAnalyze{

	private Object[] patiant;
	private FPCIT core = new FPCIT();
	private Function operations = new Function(); 
	
	public PatiantAnalyze(String file,Object params) throws MWException{
		core.funcall(0,"spm", "defaults", "PET");
		core.funcall(0,"spm_jobman", "initcfg");
		create(file,params);
	}
	
	private void create(String file,Object params) throws MWException{
		this.patiant = core.fpcit_obj(1, file, params,1);	
	}
	
	public void convert() throws MWException {
		operations.convert(0,this.patiant[0]);
	}
	
	public void coregister() throws MWException {
		operations.coregister(0,this.patiant[0]);
	}

	public void run() throws MWException {
		// TODO Auto-generated method stub
		this.convert();
		this.coregister();
	}

	public static void main(String[] args) throws MWException {
		// TODO Auto-generated method stub
		FPCIT main = new FPCIT();
		Object[] params = main.fpcit_set_params(1,"C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/config.txt");
		PatiantAnalyze test = new PatiantAnalyze("config.txt",params[0]);
		test.run();
	}
	
}
