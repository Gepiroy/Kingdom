package katorga;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kats {
	
	public static List<Katorga> kats = new ArrayList<>();
	
	private Kats(){}
	
	public static Katorga findKatByBuildingId(UUID id){
		for(Katorga kat:kats){
			if(kat.buildings.containsKey(id))return kat;
		}
		return null;
	}
}
