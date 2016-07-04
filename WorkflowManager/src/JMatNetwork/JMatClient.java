package JMatNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import JMat.JMatExecutor;
import JMat.JMatFileGroup;
import JMat.JMatFileInfo;

public class JMatClient {
	
	
	private final int myCores = 4;//Runtime.getRuntime().availableProcessors();
	
	private Socket mySocket = null;
	private String Server_Addr;
	private Thread[] processes;
	
	private JMatFileGroup group = new JMatFileGroup();
	
	private BufferedReader in;
	BufferedWriter out;
	
	public void register() throws IOException, InterruptedException {
		System.out.println("Connecting...");
		mySocket = new Socket(Server_Addr, JMatServer.Server_Port);
		
		out = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		
		System.out.println("Sending cores number");
		
		out.write(Integer.toString(myCores));
		out.newLine();
		out.flush();
		
		do{
			group.clear();
			getProcessInfo();
			awaitTrigger();
			spawnProcesses();
			signalCompletion();	
		} while(!in.readLine().equals("close"));
		
		System.out.println("Close client...");
	}
	
	private void awaitTrigger() throws IOException{
		System.out.print("Awaiting trigger ... ");
		while(!in.readLine().equals("start"));
		System.out.println("start");
	}
	
	private void getProcessInfo() throws IOException{
		String line;
		System.out.println("Retrive info ... ");
		while(!(line = in.readLine()).equals("end")){
			System.out.println(line);
			group.list.add(JMatFileInfo.destringfy(line));
		}
	}
	private void signalCompletion() throws IOException{
		System.out.println("Sending singal to server");
		out.write("done");
		out.newLine();
		out.flush();
	}
	private void spawnProcesses() throws InterruptedException{
		processes = new Thread[group.list.size()];
		System.out.println("Running processes ... ");
		
		for(int i=0;i<group.list.size();i++){
			processes[i] = new Thread(new JMatExecutor(group.list.get(i)));
			processes[i].start();
		}
		for(Thread t : processes){
			t.join();
		}
		System.out.println("All done");
	}
	
	public JMatClient(String server) throws IOException, InterruptedException{
		this.Server_Addr = server;
		System.out.println("Start client...");
		this.register();
	}

	public static void main(String[] args) throws IOException, InterruptedException{
		String svr = "localhost";
		if(args.length == 0){
			System.out.println("Warning: no server address entered -> server is localhost");
		} else {
			svr = args[0];
		}
		new JMatClient(svr);
	}
	
}
