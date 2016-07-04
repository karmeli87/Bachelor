package MatlabPackage;

import java.util.ArrayList;
import java.util.List;


public class MatlabExternalCmd {
	private List<String> jobs = new ArrayList<String>();
	
	public MatlabExternalCmd(){
		
	}
	
	public MatlabExternalCmd(String jn){
		this.addJob(jn);
	}
	
	public MatlabExternalCmd(List<String> jns){
		this.jobs.addAll(jns);
	}
	
	public void addJob(String jn){
		this.jobs.add(jn);
	}
	
	public int getJobsNum(){
		return jobs.size();
	}
	
	public String stringfyJobs(){
		String jobString = "";
		//jobString = "\"";
		for (String job : this.jobs)
		{
			jobString += job + ";";
		}
		jobString = jobString.substring(0,jobString.length()-1);
		//jobString += "\"";
		return jobString;
	}
}
