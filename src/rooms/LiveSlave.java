package rooms;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ZombieVillager;

import Kingdom.main;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;

public class LiveSlave {
	
	public UUID mobId;
	public boolean moveToOwner=false;
	
	public boolean loaded=true;
	
	public LivingEntity getEntity(){
		if(mobId==null){
			TextUtil.debug("&cmobId==null! &8(&fgetEntity()&8)");
			return null;
		}
		return (LivingEntity) Bukkit.getEntity(mobId);
	}
	
	public ZombieVillager reMakeZombie(Location defLoc){
		Location loc=null;
		if(mobId==null){
			TextUtil.debug("(zvil) &chas null id!");
			loc=defLoc;
		}else{
			Entity en=Bukkit.getEntity(mobId);
			if(en==null){
				TextUtil.debug("&6"+mobId+" (zvil) &chas null entity in world.");
				loc=defLoc;
			}else{
				loc=en.getLocation();
				en.remove();
			}
		}
		return makeNewZombie(loc);
	}
	
	public ZombieVillager makeNewZombie(Location loc){
		ZombieVillager z=main.prisons.spawnNMS(loc);
		mobId=z.getUniqueId();
		return z;
	}
	
	public String sec(){return null;}
	public void save(Conf conf, String st){
		conf.set(st+".type", "ProfSlave");
		//conf.set(st+".id", mobId.toString());
	}
	public void load(Conf conf, String st){
		//mobId=conf.getUUID(st+".id");
		loaded=false;
	}
	
	
}