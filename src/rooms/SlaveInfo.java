package rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Kingdom.main;
import NameGenerator.NameGenerator;
import UtilsKingdom.TextUtil;
import invsUtil.InvEvents;
import invsUtil.Invs;
import objKingdom.Conf;
import objKingdom.Food;

public class SlaveInfo {
	public Location home;
	public String name="Женякас";
	public List<sitem> inv = new ArrayList<>();
	public String owner=null;
	public UUID id;
	//public boolean moveToOwner=false;
	
	public LiveSlave live;
	
	Random r=new Random();
	
	public SlaveInfo(String owner){
		this.owner=owner;
		id=UUID.randomUUID();
	}
	
	public SlaveInfo(Conf conf, String st){
		home=conf.getLoc(st+".home");
		name=conf.getString(st+".name","Женякас");
		owner=conf.getString(st+".owner","null");
		for(String key:conf.getKeys(st+".inv")){
			inv.add(new sitem(conf, st+".inv."+key));
		}
		id=conf.getUUID(st+".id");
		if(id==null)id=UUID.randomUUID();
		loadProf(conf, st+".live");
	}
	
	public void save(Conf conf, String st){
		conf.setLoc(st+".home", home);
		conf.set(st+".name", name);
		conf.set(st+".owner", owner);
		int i=0;
		conf.set(st+".inv", null);
		conf.set(st+".id", id.toString());
		for(sitem sit:inv){
			sit.save(conf, st+".inv."+i);
			i++;
		}
		if(live!=null){
			live.save(conf, st+".live");
		}
	}
	
	public int invAm(){
		int ret=0;
		for(sitem sit:inv){
			ret+=sit.am;
		}
		return ret;
	}
	public int invFoodAm(){
		int ret=0;
		for(sitem sit:inv){
			int f=Food.getFoodLevel(sit.mat)*sit.am;
			if(f>0)ret+=f;
		}
		return ret;
	}
	public void addToInv(sitem nit){
		for(sitem sit:inv){
			if(sit.mat.equals(nit.mat)){
				sit.am+=nit.am;
				return;
			}
		}
		inv.add(nit);
	}
	
	public ZombieVillager spawn(Location loc){
		ZombieVillager z=main.prisons.spawnNMS(loc);
		z.setCustomName(ChatColor.GOLD+name);
		return z;
	}
	
	public void sayToOwner(String mes){
		Player p=Bukkit.getPlayer(owner);
		if(p!=null){
			TextUtil.mes(p, "&6Рабы", mes);
		}
	}
	
	public void GUI(Player p){
		Invs.open(p, InvEvents.Slave);
		/*PlayerInfo pi=Events.plist.get(p.getName());
		Inventory inv=Bukkit.createInventory(null, 27, ChatColor.GOLD+"Раб");
		inv.setItem(1, ItemUtil.create(Material.CHEST, 1, ChatColor.GOLD+"Инвентарь", new String[]{
				ChatColor.AQUA+""+invAm()+ChatColor.WHITE+" предметов",
				ChatColor.GREEN+""+invFoodAm()+ChatColor.WHITE+" ед. пищи"}, null, 0));
		inv.setItem(4, ItemUtil.create(Material.BOW, 1, ChatColor.GOLD+"На охоту", new String[]{"Тест)"}, null, 0));
		inv.setItem(7, ItemUtil.create(Material.BLUE_BED, 1, ChatColor.GOLD+"Sethome", new String[]{home.getBlockX()+";"+home.getBlockY()+";"+home.getBlockZ()+";"}, null, 0));
		inv.setItem(9, ItemUtil.create(Material.NAME_TAG, 1, ChatColor.GOLD+"Зарандомить имя", new String[]{"&e"+name}, null, 0));
		
		inv.setItem(13, ItemUtil.create(Material.LEAD, 1, ChatColor.GOLD+"Идти за владельцем", new String[]{live.moveToOwner+""}, null, 0));
			
		if(pi.orgLabor!=null){
			Role role=Role.SLAVE;
			if(pi.orgLabor.roles.containsKey(pi.invS))role=pi.orgLabor.roles.get(pi.invS);
			inv.setItem(23, ItemUtil.create(Material.IRON_PICKAXE, 1, ChatColor.GOLD+"Изменить роль на каторге", new String[]{"&fТекущая роль: &e"+role}, null, 0));
		}
		
		p.openInventory(inv);*/
	}
	
	public void inventory(Player p){
		Inventory oinv = Bukkit.createInventory(null, 27, ChatColor.GOLD+"Инвентарь раба");
		int i=0;
		for(sitem sit:inv){
			int am=sit.am;
			while(am>0){
				int setam=am;
				if(setam>sit.mat.getMaxStackSize())setam=sit.mat.getMaxStackSize();
				oinv.setItem(i, new ItemStack(sit.mat,setam));
				am-=setam;
				i++;
				if(i>=27)break;
			}
			if(i>=27)break;
		}
		p.openInventory(oinv);
	}
	
	void loadProf(Conf conf, String st){
		String type=conf.getString(st+".type");
		TextUtil.debug("[loadProf] type="+type+" for "+name);
		if(type==null)return;
		switch(type){
			case "ProfPrisoner":
				live=new ProfPrisoner();
				TextUtil.debug("loaded profPrisoner for "+name);
				break;
			case "ProfSlave":
				live=new ProfSlave();
				TextUtil.debug("loaded profSlave for "+name);
				break;
			case "ProfLiveRunner":
				live=new ProfLiveRunner();
				TextUtil.debug("loaded profLiveRunner for "+name);
				break;
			default:
				break;
		}
		live.load(conf, st);
	}
	public String randName(){
		return NameGenerator.randName();
	}
	
	public ProfPrisoner asPrisoner(){
		if(live instanceof ProfPrisoner)return (ProfPrisoner)live;
		return null;
	}
}
