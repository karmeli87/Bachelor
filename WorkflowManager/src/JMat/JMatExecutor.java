package JMat;

import MatlabPackage.MatlabExecutor;
import MatlabPackage.MatlabExternalCmd;

public class JMatExecutor extends JMatFileInfo implements Runnable{
	
	private MatlabExecutor matlab;
	private MatlabExternalCmd cmd = new MatlabExternalCmd();
	
	public JMatExecutor(JMatFileInfo jfi){
		super(jfi.startupFile,jfi.myFile,jfi.resultFile);
	}
	
	public JMatExecutor(String startup, String file, String res) {
		super(startup, file, res);
	}
	
	private void addJobs(){
		cmd.addJob(startupFile);
		cmd.addJob(myFile);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		addJobs();
		matlab = new MatlabExecutor(cmd.stringfyJobs());
		matlab.run();
	}
	
	
}
