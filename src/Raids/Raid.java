package Raids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import Kingdom.main;

public class Raid {
	public List<Raider> raiders = new ArrayList<>();
	public final Location spawn;
	Random r=main.r;
	
	public Raid(Location spawn){
		this.spawn=spawn;
	}
	
	public boolean sec(){
		if(raiders.size()==0)return true;
		for(Raider r:new ArrayList<>(raiders)){
			if(Bukkit.getEntity(r.id)==null){
				raiders.remove(r);
				continue;
			}
			r.sec();
		}
		return false;
	}
}
