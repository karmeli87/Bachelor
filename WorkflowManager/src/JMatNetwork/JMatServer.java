package JMatNetwork;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class JMatServer {
	public static final int Server_Port = 40000;
	protected ServerSocket sock;
	private int TIMEOUT = 10000;
	
	private Thread ctrlThread;
	private JMatController ctrl;
	
	public JMatServer(String mainFile) throws Exception{
		ctrl = new JMatController(mainFile);
		
		sock = new ServerSocket(Server_Port);
		sock.setSoTimeout(TIMEOUT);
		System.out.println("Server started");
		try {
			for(;;){
	    		Socket clientSocket = sock.accept();
	    		JMatSLine client = new JMatSLine(clientSocket);
		    	ctrl.addClient(client);
		    	System.out.println("New client registered");
		    	new Thread(client).start();
			} 
	    }catch (SocketTimeoutException e) {
			// no new connections, ready to roll
	    	System.out.println("Timeout: No new connections");
	    	ctrlThread = new Thread(ctrl);
			ctrlThread.start();
		} finally{
			sock.close();
			System.out.println("Server closed");	
		}
	}
	
	public boolean aliveTest(){
		return true;
	}
	
	public static void main(String[] args){
		
		if(args.length == 0){
			System.out.println("Argument Error: Please specify matlab file");
			return;
		}
		
		try {
			new JMatServer(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
