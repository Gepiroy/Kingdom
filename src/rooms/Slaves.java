package rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ZombieVillager;

import Kingdom.main;
import UtilsKingdom.TextUtil;
import katorga.Katorga;
import objKingdom.Conf;

public class Slaves {
	static final String path="/slaves/slaves.yml";
	public static List<SlaveInfo> slaves=new ArrayList<>();
	
	private Slaves(){}
	
	public static void save(){
		Conf conf = new Conf(main.instance.getDataFolder()+path);
		conf.set("a", null);
		int i=0;
		for(SlaveInfo sinf:slaves){
			sinf.save(conf, "a."+i);
			i++;
		}
		conf.save();
	}
	
	public static List<SlaveInfo> findByOwner(String owner){
		List<SlaveInfo> ret=new ArrayList<>();
		for(SlaveInfo sinf:slaves){
			if(sinf.owner.equals(owner))ret.add(sinf);
		}
		return ret;
	}
	
	public static SlaveInfo findOneByName(String name){
		for(SlaveInfo sinf:slaves){
			if(sinf.name.equals(name))return sinf;
		}
		return null;
	}
	
	public static List<SlaveInfo> findAllPrisoners(){
		List<SlaveInfo> ret=new ArrayList<>();
		for(SlaveInfo sinf:slaves){
			if(sinf.live instanceof ProfPrisoner)ret.add(sinf);
		}
		return ret;
	}
	
	public static SlaveInfo findByMobId(UUID mobId){
		for(SlaveInfo sinf:slaves){
			if(sinf.live!=null && sinf.live.mobId.equals(mobId)){
				return sinf;
			}
		}
		return null;
	}
	
	public static SlaveInfo createNewSlave(String owner){
		SlaveInfo sinf=new SlaveInfo(owner);
		slaves.add(sinf);
		return sinf;
	}
	
	public static SlaveInfo createNewPrisoner(Location loc, String owner){
		SlaveInfo sinf=new SlaveInfo(owner);
		sinf.name=ChatColor.GOLD+"Заключённый";
		ProfPrisoner pris=new ProfPrisoner();
		ZombieVillager z=pris.makeNewZombie(loc);
		z.setCustomName(sinf.name);
		sinf.home=loc;
		sinf.live=pris;
		slaves.add(sinf);
		return sinf;
	}
	
	public static void regDeath(SlaveInfo sinf){
		regDeath(sinf, null);
	}
	
	public static void regDeath(SlaveInfo sinf, String say){
		if(say!=null)sinf.sayToOwner(say);
		TextUtil.debug("Death of "+sinf.name+" registered. Say: "+say);
		slaves.remove(sinf);
	}
	
	public static Katorga findKatByBuildingId(UUID id){
		for(Katorga kat:main.prisons.kats.values()){
			if(kat.buildings.containsKey(id))return kat;
		}
		return null;
	}
	
	public static void loadSlave(Conf conf, String st){
		SlaveInfo sinf=new SlaveInfo(conf,"a."+st);
		/*Location spawn=null;
		if(sinf.live!=null){
			Entity en=Bukkit.getEntity(sinf.live.mobId);
			if(en==null){
				spawn=sinf.home;
				//TextUtil.debug("&eSpawning living pawn at his home because entity by ID isn't found. &f(&6"+sinf.name+"&f).");
			}else{
				spawn=en.getLocation();
				en.remove();
				TextUtil.debug("&fRe-spawning living pawn correctly. &f(&6"+sinf.name+"&f).");
			}
			ZombieVillager z=main.prisons.spawnNMS(spawn);
			z.setCustomName(ChatColor.GOLD+sinf.name);
			sinf.live.mobId=z.getUniqueId();
			TextUtil.debug("&fNew live.mobId=&6"+sinf.live.mobId+"&f.");
			TextUtil.debug("&fEntity="+sinf.live.getEntity()+"&f.");
		}else{*/
		//	TextUtil.debug("&8Pawn is not living, so we skip spawning it. &f(&6"+sinf.name+"&f).");
		//}
		slaves.add(sinf);
	}
	
	public static void saveSlaves(Conf conf){
		int i=0;
		for(SlaveInfo sinf:slaves){
			sinf.save(conf, "a."+i);
			i++;
		}
	}
	
	public static SlaveInfo findById(UUID id){//TODO check!!!
		for(SlaveInfo sinf:slaves){
			if(sinf.id.equals(id))return sinf;
		}
		return null;
	}
	
	public static class Find{
		public Find(){}
		
		private String owner=null;
		private boolean lived=false;
		private boolean live=false;
		private Class<?> instance=null;
		private Class<?> unstance=null;
		private Class<?> only=null;
		
		public Find live(boolean live){
			lived=true;
			this.live=live;
			return this;
		}
		public Find owner(String owner){
			this.owner=owner;
			return this;
		}
		public Find instance(Class<?> c){
			this.instance=c;
			return this;
		}
		public Find unstance(Class<?> c){
			this.unstance=c;
			return this;
		}
		public Find only(Class<?> c){
			this.only=c;
			return this;
		}
		public List<SlaveInfo> find(){
			List<SlaveInfo> guys=new ArrayList<>();
			for(SlaveInfo sinf:slaves){
				if(owner!=null&&sinf.owner!=null&&!sinf.owner.equals(owner))continue;
				if(lived&&live!=(sinf.live!=null))continue;
				if(only!=null){
					if(sinf.live==null||only!=sinf.live.getClass())continue;
				}
				if(instance!=null){
					if(sinf.live==null||!instance.isInstance(sinf.live))continue;
				}
				if(unstance!=null){
					if(sinf.live!=null&&unstance.isInstance(sinf.live))continue;
				}
				guys.add(sinf);
			}
			return guys;
		}
	}
}
