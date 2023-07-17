package rooms;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ZombieVillager;

import objKingdom.Conf;

public class HuntQuest{
	Random r=new Random();
	
	public int estTime=600;
	
	public SlaveInfo sinf;
	
	public HuntQuest(SlaveInfo sinf){
		this.sinf=sinf;
		estTime=120+r.nextInt(121);
	}
	
	public HuntQuest(Conf conf, String st){
		estTime=conf.getInt(st+".estTime");
		sinf=Slaves.findById(conf.getUUID(st+".sid"));
	}
	
	public boolean sec(){
		estTime--;
		if(estTime<=0){
			endQuest();
			return true;
		}
		return false;
	}
	
	void endQuest(){
		int type=r.nextInt(5);
		if(type==0){//курица
			sinf.addToInv(new sitem(Material.CHICKEN,1));
			sinf.addToInv(new sitem(Material.FEATHER,r.nextInt(5)));
		}else if(type==1){//корова
			sinf.addToInv(new sitem(Material.BEEF,1+r.nextInt(4)));
			sinf.addToInv(new sitem(Material.LEATHER,1+r.nextInt(3)));
		}else if(type==2){//свинья
			sinf.addToInv(new sitem(Material.PORKCHOP,2+r.nextInt(3)));
		}else if(type==3){//кролик
			sinf.addToInv(new sitem(Material.RABBIT,1));
			if(r.nextDouble()<=0.2)sinf.addToInv(new sitem(Material.RABBIT_FOOT,1));
			sinf.addToInv(new sitem(Material.RABBIT_HIDE,1+r.nextInt(3)));
		}else if(type==4){//овца
			sinf.addToInv(new sitem(Material.WHITE_WOOL,1));
			sinf.addToInv(new sitem(Material.MUTTON,1+r.nextInt(3)));
		}
		ProfSlave ps=new ProfSlave();
		ZombieVillager z=ps.makeNewZombie(sinf.home);
		z.setCustomName(ChatColor.GOLD+sinf.name);
		sinf.live=ps;
		sinf.sayToOwner("&6"+sinf.name+" &fвернулся с охоты.");
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".estTime", estTime);
		conf.set(st+".sid", sinf.id.toString());
	}
}
