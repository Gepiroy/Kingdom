package Hunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Kingdom.Events;
import UtilsKingdom.GeomUtil;
import objKingdom.PlayerInfo;

public class HuntingManager {
	
	public static HashMap<EntityType, DeadAnimalPreset> presets = new HashMap<>();
	
	public static List<DeadAnimal> bodies = new ArrayList<>();
	
	public static final PartsType CowParts = new PartsType()
			.add(new ItemStack(Material.BEEF), 0.03f)
			.add(new ItemStack(Material.LEATHER), 0.015f);
	public static final PartsType SheepParts = new PartsType()
			.add(new ItemStack(Material.MUTTON), 0.03f)
			.add(new ItemStack(Material.STRING), 0.015f);
	
	public static void init(){
		presets.put(EntityType.COW, new DeadAnimalPreset(15, 20, CowParts));
		presets.put(EntityType.SHEEP, new DeadAnimalPreset(15, 20, SheepParts));
		
	}
	
	public static void die(LivingEntity en){
		en.setAI(false);
		//en.setInvulnerable(true);
		en.setSilent(true);
		bodies.add(new DeadAnimal(en));
	}
	
	public static void tick(){//10ps
		for(Player p:Bukkit.getOnlinePlayers()){
			PlayerInfo pi=Events.plist.get(p.getName());
			if(pi.handBody!=null){
				Entity en=Bukkit.getEntity(pi.handBody);
				if(en==null){
					pi.handBody=null;
				}else{
					Location l=GeomUtil.LookingPreBlock(p, 0.35f, 2.5f);
					en.teleport(l.subtract(0, 0.75, 0));
				}
			}
		}
		for(DeadAnimal an:new ArrayList<>(bodies)){
			LivingEntity en=(LivingEntity) Bukkit.getEntity(an.id);
			if(en!=null){
				if(en.getLocation().add(0, 0.75, 0).getBlock().getType().equals(Material.AIR)){
					en.teleport(en.getLocation().add(0, -0.1, 0));
				}
			}else{
				bodies.remove(an);
			}
		}
	}
}
