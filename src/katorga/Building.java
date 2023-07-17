package katorga;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import objKingdom.Conf;

public class Building {
	public final Katorga kat;
	public UUID id;
	public int toReady=100;
	public BuildingInfo info;
	public boolean closed=false;
	
	public ItemStack displayItem(){
		return new ItemStack(Material.BARRIER);
	}
	
	public void addToLore(List<String> lore){
		lore.add("closed: "+closed);
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".name", info.name);
		conf.set(st+".id", id.toString());
		conf.set(st+".toReady", toReady);
		conf.set(st+".kat", kat.kid.toString());
	}
	
	public Building(Katorga kat){this.kat=kat;}
	
	public Building(BuildingInfo info, Katorga kat){
		this.info=info;
		this.kat=kat;
	}
	
	public Building(Katorga kat, Conf conf, String st){
		String name=conf.getString(st+".name","Хата Женякаса");
		info=Buildings.findBuildingInfoByName(name);
		id=conf.getUUID(st+".id");
		toReady=conf.getInt(st+".toReady");
		this.kat=kat;
	}
	
	public void close(){
		for(KatInfo kinf:kat.members.values()){
			if(kinf.workPlace!=null&&kinf.workPlace.equals(id))kinf.workPlace=null;
			if(kinf.home!=null&&kinf.home.equals(id))kinf.home=null;
		}
		closed=true;
	}
	
	public void open(){
		closed=false;
	}
}
