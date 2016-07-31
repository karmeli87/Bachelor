package JMatNetwork;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JMatTime {

	public static void PrintWithTime(String str){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		synchronized(System.out){
			System.out.println(dateFormat.format(date) + " " + str);		
		}
	}	
}
