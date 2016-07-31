package MatlabExternalExecutor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogWorker implements Runnable{
		private static double total = 0;
		private int processNum;
		
		static void resetTotal(){
			total = 0;
		}
		
		public static void writeTotal() throws IOException{
			List<String> totalLine = new ArrayList<String>();
			totalLine.add("process total " + total);
			Path file = Paths.get(LogServer.LogFile);
			Files.write(file, totalLine, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		}
		synchronized private static void setTotal(double totalCandidat){
			if(total <totalCandidat){
				total = totalCandidat;
			}
		}
		synchronized private static void writeToFile(List<String> data) throws IOException{
			Path file = Paths.get(LogServer.LogFile);
			Files.write(file, data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		}
	
		private Socket clientSocket;
		public LogWorker(Socket clientSocket,int pNum){
			System.out.println("connected...");
			this.processNum = pNum;
			this.clientSocket = clientSocket;
		}
		private void log() throws IOException{
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(this.clientSocket.getInputStream()));
			    
			    String inputLine;
			    List<String> data = new ArrayList<String>();
			    System.out.println("receiving...");
			    while ((inputLine = in.readLine()) != null) {
			    	data.add(inputLine);
			       //System.out.println(inputLine);
			    }
			    int lastIndex = data.size()-1;
			    String lastLine = data.get(lastIndex);
			    double myTotal = Double.parseDouble(lastLine.split(" ")[2]);
			    setTotal(myTotal);
			    data.remove(lastIndex);
			    
			    writeToFile(data);
		}
		@Override
		public void run() {
			try {
				this.log();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				LogServer.decProcess();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
