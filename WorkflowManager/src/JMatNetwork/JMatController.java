package JMatNetwork;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import JMat.JMatFileInfo;
import JMat.MatlabCodeAssambler;

public class JMatController extends MatlabCodeAssambler implements Runnable{
	
	private List<JMatSLine> clients = new ArrayList<JMatSLine>();
	private Thread worker;
	
	public JMatController(String path) throws Exception {
		super(path);
	}
	
	public void addClient(JMatSLine client) throws InterruptedException{
		clients.add(client); 
		if(clients.size() == 1){
			prepareHeaderPart(clients.get(0));
			JMatTalker jmc = new JMatTalker(clients.get(0));
			clients.get(0).closeAfterCompletion = false;
			worker = new Thread(jmc);
			this.waitHeadWorker();
			worker.start();
		}
	}
	
	public void start() throws InterruptedException{
		JMatTalker jmc = new JMatTalker(clients);
		prepareFilesParts();
		worker.join();
		worker = new Thread(jmc);
		this.waitPartsWorkers();
		System.out.println("Files ready to transfer");
		worker.start();
		worker.join();
		
		JMatSLine client = clients.get(0);
		client.closeAfterCompletion = true;
		jmc = new JMatTalker(client);
		prepareMergePart(client);
		worker = new Thread(jmc);
		this.waitMergeWorker();
		worker.start();
		worker.join();
		System.out.println("Cleaning & Closing ... ");
		this.cleanFiles(true, true);
		System.out.println("Bye");
	}
	
	private void prepareHeaderPart(JMatSLine client){
		this._BuildStartup();
		this._BuildHeadFile();
		client.files.list.add(new JMatFileInfo("startup",headPart.fileName,initialStateFile));
	}
	
	private void prepareFilesParts(){
		int processes = 0;
		for(JMatSLine client : clients){
			processes += client.getCoresNum();
		}
		this.setProcessNum(processes);
		
		this._BuildProcessFile(false);
		this._BuildMergeFile();
		
		int j = 0;
		for(JMatSLine client : clients){
			for(int i=0; i<client.getCoresNum(); i++){
				client.files.list.add(new JMatFileInfo("startup",forPart[i+j].content,forPart[i+j].resultFile));
			}
			j+=client.getCoresNum();
		}
	}
	
	private void prepareMergePart(JMatSLine client){
		client.files.list.add(new JMatFileInfo("startup",mergePart.fileName,mergedStateFile));
	}
	
	@Override
	public void run() {
		try {
			start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
