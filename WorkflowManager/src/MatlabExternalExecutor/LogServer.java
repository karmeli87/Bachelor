package MatlabExternalExecutor;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LogServer implements Runnable{
	
	private static int processNum = 0;
	private static ServerSocket serverSocket;
	static String LogFile = "stats2.log";
	 
	static int incProcess(){
		return processNum++;
	}
	
	synchronized static void decProcess() throws IOException{
		processNum--;
		if(processNum == 0){
			LogWorker.writeTotal();
			System.out.println("disconnected...");    
			serverSocket.close();
		}
	}
	public LogServer() throws IOException{
		File yourFile = new File(LogServer.LogFile);
		if(!yourFile.exists()) {
		    yourFile.createNewFile();
		} 
	}
	@Override
	public void run(){
		// TODO Auto-generated method stub
		
		try{
			serverSocket = new ServerSocket(4444);
			Socket clientSocket;
			System.out.println("waiting...");
		    for(;;){
		    	clientSocket = serverSocket.accept();
		    	new Thread(new LogWorker(clientSocket,incProcess())).start();
		    }
		 } catch(IOException ex) {
			System.out.println("exiting...");    
		 }
		    
	}
	public static void main(String[] args) throws IOException{
		LogServer ls = new LogServer();
		ls.run();
	}
}
