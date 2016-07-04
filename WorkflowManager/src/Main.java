import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mathworks.toolbox.javabuilder.MWException;
import FPCIT.FPCIT;

public class Main {

	public static void main(String[] args) throws MWException, InterruptedException, IOException{
		// TODO Auto-generated method stub
		FPCIT fpcit = new FPCIT();
		Object[] res;
		
		res = fpcit.fpcit_set_params(1,"C:/Users/Karmeli/Desktop/Koop-HU-Informatik/FPCIT_d3s/config.txt");
		//System.out.println(res[0]);
		String paths[] ={
				"config.txt",
				"config2.txt",
				"config3.txt",
				"config4.txt"
		};
		
		long start_time = System.nanoTime();
		for(int i=0;i<4;i++){
			fpcit.fpcit_main(paths[i],res[0]);	
		}
		System.out.printf("elapsed  time: %f\n",(System.nanoTime() - start_time)/1e9);	
	}


}
