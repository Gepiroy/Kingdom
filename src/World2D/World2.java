package World2D;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import Kingdom.main;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;

public class World2 {
	public static World naturalWorld;
	public static World world;
	public static HashMap<Loc2, Block2> blocks = new HashMap<>();
	public static List<Obj> objs = new ArrayList<>();
	public static ArrayList<Material> ignoresNatural=new ArrayList<>();
	
	public static void load(){
		Server s=main.instance.getServer();
		TextUtil.debug("worlds: "+s.getWorlds());
		naturalWorld=s.createWorld(new WorldCreator("naturalWorld"));
		TextUtil.debug("&enaturalWorld: &f"+naturalWorld);
		world=Bukkit.getWorld("world");
		TextUtil.debug("worlds after: "+s.getWorlds());
		ignoresNatural.add(Material.WATER);
		ignoresNatural.add(Material.LAVA);
		ignoresNatural.add(Material.DIRT);
		ignoresNatural.add(Material.GRASS_BLOCK);
		Conf conf=new Conf(main.instance.getDataFolder()+"/world2.yml");
		for(String key:conf.getKeys("blocks")){
			Loc2 l=new Loc2(conf, "blocks."+key);
			blocks.put(l, Block2.PlacedBlock);
		}
	}
	
	public static void placeb(Location loc){
		addBlock(new Loc2(loc), Block2.PlacedBlock);
	}
	
	public static void breakb(Location loc){
		new BukkitRunnable(){
			@Override
			public void run() {
				update();
			}
		}.runTaskLater(main.instance, 1);
	}
	
	static void addBlock(Loc2 l, Block2 b){
		if(!blocks.containsKey(l))blocks.put(l, b);
	}
	
	public static void save(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/world2.yml");
		conf.set("blocks", null);
		int i=0;
		for(Loc2 l:blocks.keySet()){
			l.save(conf, "blocks."+i);
			i++;
		}
		conf.save();
	}
	
	public static void update(){
		new BukkitRunnable(){
			@Override
			public void run() {
				long start=new Date().getTime();
				for(Loc2 l:new ArrayList<>(blocks.keySet())){
					if(l.changes(true)==0){
						blocks.remove(l);
						continue;
					}
				}
				TextUtil.debug("&bПроизводительность проверки &e"+blocks.size()+"&b блоков: &f"+(new Date().getTime()-start)+" мс.");
			}
		}.runTaskAsynchronously(main.instance);
	}
	
	static void algoriatm(){
		new Algoritm().algoritm();
		for(Obj o:objs){
			o.shootToCenter();
		}
	}
	
	public static List<Loc2> findNearLocs(Loc2 l, double r, List<Loc2> where){
		List<Loc2> ret = new ArrayList<>();
		for(Loc2 l2:where){
			if(l2!=l&&l.dist(l2.x, l2.z)<=r)ret.add(l2);
		}
		return ret;
	}
	
	public static List<Loc2> findNearLocs(Loc2 l, double r){
		return findNearLocs(l, r, new ArrayList<>(blocks.keySet()));
	}
}
