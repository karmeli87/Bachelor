import java.io.IOException;

import com.mathworks.toolbox.javabuilder.MWException;

import FPCIT.FPCIT;

public class Playground {
	
	public static void main(String[] args) throws MWException, InterruptedException, IOException{
		// TODO Auto-generated method stub
		/*FPCIT fpcit = new FPCIT();
		Object[] res;
		
		Object[] fh = fpcit.funcall(1, "fopen","test.log","w");  
		System.out.printf("fopen %s \n",fh[0]);
		
		//res = fpcit.funcall(1,"run","C:/Users/Karmeli/Desktop/Koop-HU-Informatik/myFunc/test.m");
		res = fpcit.funcall(1,"spm","Dir");
		res = fpcit.funcall(3,"fileparts",res[0]);
	
		System.out.printf("output: %s %s %s\n",res[0],res[1],res[2]);
		
		fpcit.funcall(0,"parpool","local", 2);
		fpcit.fpcit_batchrunner("C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/config.txt",4);
		fpcit.funcall(0,"parpool","close");*/
		
		
		System.out.printf("OS name: %s \n",System.getProperty("os.name").contains("Windows"));
		return;
	}

}
