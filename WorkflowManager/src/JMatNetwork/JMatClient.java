package JMatNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import JMat.JMatExecutor;
import JMat.JMatFileGroup;
import JMat.JMatFileInfo;

public class JMatClient {
	
	
	private int myCores = Runtime.getRuntime().availableProcessors();
	
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
		
		JMatTime.PrintWithTime("Sending cores number");
		
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
		
		JMatTime.PrintWithTime("Close client...");
	}
	
	private void awaitTrigger() throws IOException{
		JMatTime.PrintWithTime("Awaiting trigger ... ");
		while(!in.readLine().equals("start"));
		System.out.println("start");
	}
	
	private void getProcessInfo() throws IOException{
		String line,input;
		JMatTime.PrintWithTime("Retrive info ... ");
		while(!(line = in.readLine()).equals("end")){	
			input=line;
			while(!(line = in.readLine()).equals("###")){
				input+=line;
			}
			group.list.add(JMatFileInfo.destringfy(input));
		}
	}
	private void signalCompletion() throws IOException{
		JMatTime.PrintWithTime("Sending singal to server");
		out.write("done");
		out.newLine();
		out.flush();
	}
	private void spawnProcesses() throws InterruptedException{
		processes = new Thread[group.list.size()];
		JMatTime.PrintWithTime("Running processes ... ");
		
		for(int i=0;i<group.list.size();i++){
			processes[i] = new Thread(new JMatExecutor(group.list.get(i)));
			processes[i].start();
		}
		for(Thread t : processes){
			t.join();
		}
		JMatTime.PrintWithTime("All done");
	}
	
	private void sendFile(String fn) throws IOException{
		File file = new File(fn);
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream inf = new FileInputStream(file);
        OutputStream outf = mySocket.getOutputStream();
        int count;
        while ((count = inf.read(bytes)) > 0) {
            outf.write(bytes, 0, count);
        }
        outf.close();
        inf.close();
	}
	
	public JMatClient(String server) throws IOException, InterruptedException{
		this(server,4);
	}

	public JMatClient(String server,int coresNum) throws IOException, InterruptedException{
		if (coresNum > 0){
			this.myCores = coresNum;	
		}
		this.Server_Addr = server;
		JMatTime.PrintWithTime("Start client with " + this.myCores + "  available cores");
		this.register();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		String svr = "localhost";
		int myCores=0;
		
		switch(args.length){
			case 2: myCores=Integer.parseInt(args[1]);
			case 1: svr = args[0]; break;
			case 0: System.out.println("Warning: no server address entered -> server is localhost");
		}
		
		new JMatClient(svr,myCores);
	}	
}
