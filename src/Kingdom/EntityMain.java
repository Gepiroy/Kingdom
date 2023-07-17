package Kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;

import UtilsKingdom.ItemUtil;
import net.minecraft.server.v1_16_R3.EntityCreature;
import territories.TerPoint;

public class EntityMain {
	public HashMap<UUID,Savage> savagers = new HashMap<>();
	Random r=new Random();
	void spawnSavage(Location loc){
		ZombieVillager en=(ZombieVillager) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE_VILLAGER);
		en.setCustomName(ChatColor.GOLD+"Дикарь");
		en.getEquipment().clear();
		en.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
		en.setAdult();
		if(r.nextDouble()<=0.3){
			en.getEquipment().setItemInMainHand(ItemUtil.create(Material.WOODEN_SWORD, 1, ChatColor.GOLD+"Дикарская дубина", null, null, 0));
		}
	}
	
	public void tick(){
		for(UUID id:new ArrayList<>(savagers.keySet())){
			if(Bukkit.getEntity(id)==null){
				savagers.remove(id);
				continue;
			}
			Savage sav=savagers.get(id);
			sav.tick();
			if(sav.mode.equals("run"));
		}
	}
	
	public boolean canSpawn(Location l, int distPt, int distPl){
		TerPoint tp=main.ters.nearestPoint(l);
		if(tp!=null&&tp.getCenterFromSky(l.getWorld()).distance(l)<distPt)return false;
		Player p=main.instance.nearestPlayer(l, null);
		if(p!=null&&p.getLocation().distance(l)<distPl)return false;
		return true;
	}
	
	public void moveTo(Creature en, Location l){
		EntityCreature cr=((EntityCreature) ((CraftEntity) en).getHandle());
		cr.getNavigation().a(l.getX(), l.getY(), l.getZ(), 1);
	}
}
