package JMat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MatlabFileCreator implements Runnable{

	Path p;
	String path;
	public String fileName;
	String content;
	
	public MatlabFileCreator(String path,String content){
		this.path = path;
		this.content = content;
		
		this.p = Paths.get(path);
		this.fileName = this.p.getFileName().toString().split("\\.")[0];
	}
	
	public void clean(){
		new File(path).delete();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try(  PrintWriter out = new PrintWriter(this.path)  ){
		    out.println( this.content );
		    System.out.println("[Create file:" + fileName + "]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
