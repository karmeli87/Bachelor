package JMatNetwork;

import java.util.ArrayList;
import java.util.List;

public class JMatTalker implements Runnable {

	private List<JMatSLine> clients  = new ArrayList<JMatSLine>();
	private int clientCoutner = 0;
	
	public JMatTalker(List<JMatSLine> clients){
		this.clients.addAll(clients);
	}
	
	public JMatTalker(JMatSLine client){
		this.clients.add(client);
	}
	
	private int state = 0;
	
	@Override
	public void run() {
		for(JMatSLine client : clients){
			client.setCtrl(this);
		}
		try {
			System.out.println("Waiting for all clients to be ready ... ");
			state++;
			waitForClients();
			state++;
			System.out.println("OK");
	    	System.out.println("Sending info to clients ... ");
	    	notifyClients();
	    	state++;
	    	waitForClients();
	    	state++;
	    	System.out.println("Done");
	    	System.out.println("Sending triggers ... ");
	    	notifyClients();
	    	state++;
	    	waitForClients();
	    	state++;
	    	System.out.println("Done");
	    	System.out.println("Wait for processes on all clients to complete ... ");
	    	waitForClients();
	    	state++;
	    	// TODO check if clients are alive
	    	System.out.println("Done");
	    	notifyEnd();	
	    	state = 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	private synchronized void waitForClients() throws InterruptedException{
		clientCoutner += clients.size();
		System.out.println("[wait,"+state+"] Current coutner value : " + clientCoutner);
		if(clientCoutner > 0){
			wait();
		}
	}
	public synchronized void notifyCtrl(){
		clientCoutner--;
		System.out.println("[notify,"+state+"] Current coutner value : " + clientCoutner);
		
		if(clientCoutner == 0){
			notify();
		}
	}
	
	private void notifyEnd(){
		for(JMatSLine client : clients){
			client.notifyEnd();
    	}
	}

	
	private void notifyClients(){
		for(JMatSLine client : clients){
			client.wakeup();
    	}
	}
	
}
