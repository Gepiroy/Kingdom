package katorga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import Kingdom.main;
import UtilsKingdom.ItemUtil;
import objKingdom.Conf;
import objKingdom.Role;
import rooms.SlaveInfo;
import rooms.sitem;

public class Fishing extends Building{
	
	Random r=main.r;
	
	int maxGuards=1;
	public List<SlaveInfo> guards = new ArrayList<>();
	int maxWorkers=4;
	public List<SlaveInfo> workers = new ArrayList<>();
	
	public Fishing(Katorga kat){
		super(kat);
	}
	
	public Fishing(FishingInfo info, Katorga kat){
		super(info, kat);
	}
	
	public Fishing(Katorga kat, Conf conf, String st){
		super(kat, conf, st);
	}
	
	@Override
	public void close(){
		super.close();
		guards.clear();
		workers.clear();
	}
	
	
	double whereAdd=0;
	public void sec(){
		for(int i=0;i<workers.size();i++){
			if(r.nextDouble()<=0.015){
				sitem sit=new sitem(Material.COD, 1);
				kat.add(sit);
			}
		}
		//Тип червей ловят, так шо не 20, а 15.
	}
	
	public void updateMembers(Katorga kat){
		guards.clear();
		workers.clear();
		List<SlaveInfo> kats=new Katorga.Find(kat).workPlace(id).find();
		for(SlaveInfo sinf:kats){
			KatInfo kinf=kat.members.get(sinf);
			if(kinf.role==Role.GUARD)guards.add(sinf);
			else workers.add(sinf);
		}
	}
	
	public void hire(){
		if(closed)return;
		//TextUtil.debug("kat="+kat);
		for(SlaveInfo sinf:kat.findFreePawnsByRole(Role.GUARD, true)){
			if(guards.size()>=maxGuards)break;
			KatInfo kinf=kat.members.get(sinf);
			kinf.workPlace=id;
			guards.add(sinf);
		}
		for(SlaveInfo sinf:kat.findFreePawnsByRole(Role.SLAVE, true)){
			if(workers.size()>=maxWorkers)break;
			KatInfo kinf=kat.members.get(sinf);
			kinf.workPlace=id;
			workers.add(sinf);
		}
	}
	
	@Override
	public ItemStack displayItem() {
		List<String> lore=new ArrayList<>();
		if(id==null)lore.add("&cerr: id is null!");
		//lore.add("&8id="+id);
		if(toReady<=0){
			if(kat!=null){
				lore.add("&fОхрана &8(&f"+guards.size()+"&8/&f"+maxGuards+"&8)&f:");
				if(guards.size()==0)lore.add("&cОхраны нет.");
				else for(SlaveInfo sinf:guards){
					KatInfo kinf = kat.members.get(sinf);
					if(kinf.isHere)lore.add("&f - &9"+sinf.name);
					else lore.add("&f - &8"+sinf.name);
				}
				lore.add("&fРабочие &8(&f"+workers.size()+"&8/&f"+maxWorkers+"&8)&f:");
				for(SlaveInfo sinf:workers){
					KatInfo kinf = kat.members.get(sinf);
					if(kinf.isHere)lore.add("&f - &6"+sinf.name);
					else lore.add("&f - &8"+sinf.name);
				}
			}else{
				lore.add("&cerr: kat is null!");
			}
		}else{
			lore.add("&6Строительство... &8"+toReady);
		}
		super.addToLore(lore);
		//lore=TextUtil.multiLore(lore, new ArrayList<>(lore));
		return ItemUtil.create(info.displMat, 1, ChatColor.DARK_AQUA+info.name, lore, null, 0);
	}
	
	@Override
	public void save(Conf conf, String st){
		super.save(conf,st);
	}
}
