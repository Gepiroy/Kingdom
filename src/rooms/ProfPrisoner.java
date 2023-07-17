package rooms;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import UtilsKingdom.NMSUtil;
import objKingdom.Conf;

public class ProfPrisoner extends LiveSlave{
	public float food=20;
	public float recruit=0;//100 = recruited.
	public RoomInfo room;
	//public UUID mobId;
	
	public ProfPrisoner(){}

	@Override
	public void save(Conf conf, String st) {
		conf.set(st+".type", "ProfPrisoner");
		conf.set(st+".id", mobId.toString());
		conf.set(st+".food", food);
		conf.set(st+".recruit", recruit);
	}

	@Override
	public void load(Conf conf, String st) {
		super.load(conf, st);
		food=(float) conf.getDouble(st+".food", 10);
		recruit=(float) conf.getDouble(st+".recruit", 0);
	}
	Random r=new Random();
	@Override
	public String sec() {
		if(mobId==null){
			return "IdIsNull";
		}
		LivingEntity en=(LivingEntity) Bukkit.getEntity(mobId);
		if(en==null){
			return "EntityIsNull";
		}
		food-=0.01f;//2000 sec to death (~33.3 mins)
		recruit+=1.0*(2.0/20*food);
		if(food<=5){
			NMSUtil.move((Creature) en, room.doors.get(0));
			if(r.nextDouble()<=0.1){
				en.getWorld().playSound(en.getLocation(), Sound.ENTITY_PLAYER_BURP, 2, 0);
			}
		}
		if(food<=-5){
			en.setHealth(0);
			return "DiedByHunger";
		}
		return null;
	}
	
	public void updateRoom(Location l){
		if(room==null){
			room=new RoomInfo();
		}
		room.updateRoom(l);
	}
}
