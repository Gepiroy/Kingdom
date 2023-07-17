package katorga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;
import objKingdom.Conf;
import rooms.SlaveInfo;
import rooms.Slaves;

public class House extends Building{
	public List<UUID> members=new ArrayList<>();
	
	public void updateMembers(HashMap<SlaveInfo,KatInfo> kats){
		members.clear();
		for(SlaveInfo sinf:kats.keySet()){
			KatInfo kinf=kats.get(sinf);
			if(kinf.home!=null&&kinf.home.equals(id))members.add(sinf.id);
		}
	}
	
	public HouseInfo info(){
		return (HouseInfo) info;
	}
	
	public boolean isFull(){
		return members.size()>=info().slots;
	}

	@Override
	public ItemStack displayItem() {
		List<String> lore=new ArrayList<>();
		if(id==null)lore.add("&cerr: id is null!");
		lore.add("&8id="+id);
		if(toReady<=0){
			if(members.size()==0){
				lore.add("&8Жителей нет.");
			}else{
				lore.add("&fЖители &8(&f"+members.size()+"&8/&f"+((HouseInfo)info).slots+"&8)&f:");
				Katorga kat=Slaves.findKatByBuildingId(id);
				if(kat!=null){
					for(UUID kid:members){
						SlaveInfo sinf = Slaves.findById(kid);
						KatInfo kinf = kat.members.get(sinf);
						if(kinf.isHere)lore.add("&f - &6"+sinf.name+" &f(&e"+kinf.role+"&f)");
						else lore.add("&f - &8"+sinf.name+" &f(&8"+kinf.role+"&f)");
					}
				}else{
					lore.add("&cerr: kat is null!");
				}
			}
			
		}else{
			lore.add("&6Строительство... &8"+toReady);
		}
		//return new ItemUtil.BuildItem(displMat).name(ChatColor.DARK_AQUA+name).lore(lore).build();
		return ItemUtil.create(info.displMat, 1, ChatColor.DARK_AQUA+info.name, lore, null, 0);
	}
	
	@Override
	public void save(Conf conf, String st){
		super.save(conf,st);
	}
	
	public House(HouseInfo info, Katorga kat){
		super(info, kat);
	}
	
	public House(Katorga kat, Conf conf, String st){
		super(kat, conf, st);
	}
}
