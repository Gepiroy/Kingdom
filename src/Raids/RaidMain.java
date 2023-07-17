package Raids;

import java.util.ArrayList;
import java.util.List;

public class RaidMain {
	public static List<Raid> raids = new ArrayList<>();
	
	public static void sec(){
		for(Raid r:new ArrayList<>(raids)){
			if(r.sec())raids.remove(r);
		}
	}
}
