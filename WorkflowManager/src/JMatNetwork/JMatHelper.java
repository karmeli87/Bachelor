package JMatNetwork;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JMatHelper {

	//public static int[] clients = {0,2,2,4,4,15,15};
	public static int[] clients = {0,16,16,32,32,60,60};
	
	public static int sum(List<Integer> list){
		int sum=0;
		for(int i:list){
			sum+=clients[i];
		}
		return sum;
	}
	public static void getClientsList(int cores){
		List<Integer> out = new ArrayList<Integer>();
		List<Integer> best = null;
		
		List<Integer> max =new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6)); 
		
		if(cores > sum(max)){
			System.out.println(max+":"+sum(max));
			return;
		}
		
		int diff=100;
		List<List<Integer>> options = new ArrayList<List<Integer>>();
		getClientsList(cores,1,out,options);
		
		for(List<Integer> list:options){
			int sum=sum(list);
			int d=sum-cores;
			if(d>=0 && diff>d){
				best=list;
				diff=(sum-cores);
			}
		}
		System.out.println(best+":"+sum(best));
	}
	
	public static void getClientsList(int cores,int last,List<Integer> in,List<List<Integer>> options){
		if (last == clients.length || cores == 0){
			if(in.size()>0){
				options.add(in);
			}
			return;
		}
		
		getClientsList(cores,last+1,in,options);
		int rest=cores-clients[last];
		List<Integer> out= new ArrayList<Integer>(in);
		out.add(last);
		if(rest >= 0){
			getClientsList(rest,last+1,out,options);
		} else {
			options.add(out);
		}
	}
	public static void main(String[] args){
		getClientsList(Integer.parseInt(args[0]));
	}
}
