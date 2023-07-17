package katorga;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.ZombieVillager;

import objKingdom.Conf;
import rooms.ProfLiveRunner;
import rooms.SlaveInfo;
import rooms.Slaves;

public class Caravan {
	public SlaveInfo sinf;
	public UUID kid;
	public int timer=10;
	public List<String> messages = new ArrayList<>();
	public boolean toOwner=true;
	public String hello="&cerr hello caravan.";
	
	public Caravan(SlaveInfo sinf, UUID kid){
		this.sinf=sinf;
		this.kid=kid;
	}
	
	public Caravan(Conf conf, String st){
		sinf=Slaves.findById(conf.getUUID(st+".sid"));
		kid=conf.getUUID(st+".kid");
		timer=conf.getInt(st+".timer");
		toOwner=conf.conf.getBoolean(st+".toOwner");
		messages=conf.getStringList(st+".messages");
		hello=conf.getString(st+".hello");
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".sid", sinf.id.toString());
		conf.set(st+".kid", kid.toString());
		conf.set(st+".timer", timer);
		conf.set(st+".toOwner", toOwner);
		conf.set(st+".messages", messages);
		conf.set(st+".hello", hello);
	}
	
	public boolean sec(){
		timer--;
		if(timer<=0){
			if(toOwner){
				ProfLiveRunner pr=new ProfLiveRunner();
				pr.messages=messages;
				pr.kid=kid;
				ZombieVillager z=pr.makeNewZombie(sinf.home);
				z.setCustomName(ChatColor.GOLD+sinf.name);
				sinf.live=pr;
				sinf.sayToOwner(hello.replace("%", sinf.name));
				return true;
			}else{
				return true;
			}
		}
		return false;
	}
}
