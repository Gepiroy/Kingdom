package NameGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Liters {
	public char[][] liters;
	public int maxSame=1;
	
	public Liters(char[][] liters, int maxSame){
		this.liters=liters;
		this.maxSame=maxSame;
	}
	
	public char getC(char prev, Random r){
		List<Integer> av=new ArrayList<>();
		for(int i=0;i<liters.length;i++){
			boolean add=true;
			for(char c:liters[i]){
				if(c==prev)add=false;
			}
			if(add)av.add(i);
		}
		List<String> avret=new ArrayList<>();
		for(int i=0;i<liters.length;i++){
			if(av.contains(Integer.valueOf(i))){
				for(char c:liters[i]){
					avret.add(c+"");
				}
			}
		}
		return avret.get(r.nextInt(avret.size())).charAt(0);
	}
}
