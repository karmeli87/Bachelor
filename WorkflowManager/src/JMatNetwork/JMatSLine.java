package JMatNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import JMat.JMatFileGroup;
import JMat.JMatFileInfo;

public class JMatSLine implements Runnable{
	
	private Socket clientSocket;
	private JMatTalker myCtrl;
	private int ID;
	private static int counter = 0;
	private int cores;
	private BufferedReader in;
	private BufferedWriter out;
	
	public JMatFileGroup files = new JMatFileGroup();
	public boolean closeAfterCompletion = true;
	
	public JMatSLine(Socket sock) throws IOException{
		this.clientSocket = sock;
		this.ID = counter++;
		in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));		
	}
	
	public synchronized void setCtrl(JMatTalker jmc){
		this.myCtrl = jmc;
		notify();
	}
	
	public int getCoresNum(){
		return this.cores;
	}
	
	private void rcvCores() throws IOException{
		System.out.print("receiving cores ... ");
		this.cores = Integer.parseInt(in.readLine());
	    System.out.println(this.cores);
	}
	
	public void sendInfo() throws IOException{
		for(JMatFileInfo file : files.list){
			//System.out.println("[Sending: " + file.myFile +"]");
			out.write(JMatFileInfo.stringfy(file));
			out.newLine();
		}
		files.list.clear();
		
		out.write("end");
		out.newLine();
		out.flush();
	}
	
	public void sendTrigger() throws IOException{
		out.write("start");
		out.newLine();
		out.flush();
	}
	
	public void waitToComplete() throws IOException{
		while(!"done".equals(in.readLine()));
		System.out.println("Processes on client "+ ID +" are completed");
	}
	
	private synchronized void notifyCtrl() throws InterruptedException{
		if(this.myCtrl == null){
			wait();
		}
		this.myCtrl.notifyCtrl();
	}
	
	public synchronized void hold() throws InterruptedException{
		wait();
	}
	
	public synchronized void wakeup(){
		notify();
	}
	
	public synchronized void notifyEnd(){
		if(!closeAfterCompletion){
			loopClient();
		}
		this.myCtrl = null;
		notify();
	}
	
	private void loopClient(){
		try {
			out.write("more");
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void cleanup(){
		try {
			out.write("close");
			out.newLine();
			out.flush();
			in.close();
			out.close();
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			this.rcvCores();
			do{
				notifyCtrl();
				hold();
				this.sendInfo();
				notifyCtrl();
				hold();
				this.sendTrigger();
				notifyCtrl();
				this.waitToComplete();
				notifyCtrl();
				hold();
			}while(!closeAfterCompletion);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		cleanup();
	}
	
}
