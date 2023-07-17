package katorga;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import objKingdom.Conf;
import objKingdom.Food;
import rooms.sitem;

public class Storage {
	public List<sitem> storage=new ArrayList<>();
	
	public Storage(Conf conf, String st){
		for(String s:conf.getKeys(st)){
			storage.add(new sitem(conf, st+"."+s));
		}
	}
	
	public void save(Conf conf, String st){
		conf.set(st, null);
		int i=0;
		for(sitem s:storage){
			s.save(conf, st+"."+i);
			i++;
		}
	}
	
	public int am(){
		int ret=0;
		for(sitem sit:storage){
			ret+=sit.am;
		}
		return ret;
	}
	
	public int am(Material mat){
		for(sitem sit:storage){
			if(sit.mat==mat)return sit.am;
		}
		return 0;
	}
	
	public void add(sitem nit){
		for(sitem sit:storage){
			if(sit.mat.equals(nit.mat)){
				sit.am+=nit.am;
				return;
			}
		}
		storage.add(nit);
	}
	public void add(Material mat, int am){
		for(sitem sit:new ArrayList<>(storage)){
			if(sit.mat.equals(mat)){
				sit.am+=am;
				if(sit.am<=0)storage.remove(sit);
				return;
			}
		}
		if(am>0)storage.add(new sitem(mat,am));
	}
	public List<Food> avaliableFood(){
		List<Food> ret=new ArrayList<>();
		for(sitem sit:storage){
			Food f=Food.getFood(sit.mat);
			if(f!=null)ret.add(f);
		}
		return ret;
	}
	public int stacks(){
		int i=0;
		for(sitem sit:storage){
			int am=sit.am;
			while(am>0){
				int setam=am;
				if(setam>sit.mat.getMaxStackSize())setam=sit.mat.getMaxStackSize();
				am-=setam;
				i++;
			}
		}
		return i;
	}
}
