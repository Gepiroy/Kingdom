package rooms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.GepUtil;
import UtilsKingdom.NMSUtil;
import UtilsKingdom.TextUtil;
import invsUtil.InvEvents;
import invsUtil.Invs;
import katorga.BuildingInfo;
import katorga.FishingInfo;
import katorga.HouseInfo;
import katorga.Katorga;
import objKingdom.Conf;
import objKingdom.PlayerInfo;

public class PrisonMain {
	
	public ArrayList<HuntQuest> quests = new ArrayList<>();
	
	public HashMap<UUID, Katorga> kats = new HashMap<>();
	
	public ArrayList<BuildingInfo> buildings = new ArrayList<>();
	
	Random r=main.r;
	
	
	
	public PrisonMain(){
			buildings.add(
					new HouseInfo.Builder("Барак",Material.JUNGLE_DOOR).health(2).comfort(5).neighbourCoef(2).
					slots(5).toReady(100).lore(new String[]{
							"&fЖильё для людей с нечеловеческими",
							"&fусловиями. Лучше, чем жить на улице."
					}).build());
			buildings.add(
					new HouseInfo.Builder("Домик",Material.OAK_DOOR).health(3).comfort(8).
					slots(1).toReady(150).lore(new String[]{
							"&fПростейший вариант для элиты. Соседи",
							"&fне докучают, дверь можно запереть,",
							"&fокна - заколотить... Самое то для",
							"&fтех, кто 'мотивирует' остальных жить",
							"&f'дружно'."
					}).build());
			buildings.add(
					new HouseInfo.Builder("Дворец для Путина",Material.NETHER_STAR).health(200).comfort(500).neighbourCoef(0).
					slots(1).toReady(1100000000).lore(new String[]{
							"&fИстория самой большой взятки..."
					}).build());
			buildings.add(
					new FishingInfo.Builder("Рыбалка",Material.FISHING_ROD).toReady(120).lore(new String[]{
							"&fОборудовать место, где люди смогут",
							"&fрыбачить, и не смогут ничего украсть",
							"&fили сбежать."
					}).build());
	}
	
	public void load(){
		Conf slavesConf = new Conf(main.instance.getDataFolder()+"/slaves/slaves.yml");
		for(String st:slavesConf.getKeys("a")){
			Slaves.loadSlave(slavesConf, st);
		}
		Conf questsConf = new Conf(main.instance.getDataFolder()+"/slaves/quests.yml");
		for(String st:questsConf.getKeys("a")){
			quests.add(new HuntQuest(questsConf, "a."+st));
		}
		
		Conf laborsConf = new Conf(main.instance.getDataFolder()+"/slaves/labors.yml");
		for(String st:laborsConf.getKeys("a")){
			kats.put(UUID.fromString(st),new Katorga(laborsConf, "a."+st));
		}
	}
	
	public void disable(){
		try{
			Conf conf=new Conf(main.instance.getDataFolder()+"/slaves/tmpslaves.yml");
			//slavesConf.set("a", null);
			Slaves.saveSlaves(conf);
			conf.changeFile(main.instance.getDataFolder()+"/slaves/slaves.yml");
			conf.save();
			for(SlaveInfo sinf:Slaves.slaves){
				if(sinf.live!=null&&sinf.live.getEntity()!=null){
					sinf.live.getEntity().remove();
				}
			}
		}catch(Exception e){
			TextUtil.sdebug("&cНе сохранился конфиг рабов!");
			e.printStackTrace();
		}
		
		try{
			Conf conf = new Conf(main.instance.getDataFolder()+"/slaves/tmpquests.yml");
			int i=0;
			conf.set("a", null);
			for(HuntQuest quest:quests){
				quest.save(conf, "a."+i);
				i++;
			}
			conf.changeFile(main.instance.getDataFolder()+"/slaves/quests.yml");
			conf.save();
		}catch(Exception e){
			TextUtil.sdebug("&cНе сохранился конфиг квестов!");
			e.printStackTrace();
		}
		
		try{
			Conf conf = new Conf(main.instance.getDataFolder()+"/slaves/tmplabors.yml");
			conf.set("a", null);
			for(UUID id:kats.keySet()){
				Katorga kat=kats.get(id);
				kat.save(conf, "a."+id);
			}
			conf.changeFile(main.instance.getDataFolder()+"/slaves/labors.yml");
			conf.save();
		}catch(Exception e){
			TextUtil.sdebug("&cНе сохранился конфиг каторг!");
			e.printStackTrace();
		}
	}
	
	int rate=0;
	public void tick(){
		rate++;
		if(rate%20==0){//~Раз в секунду
			for(HuntQuest quest:new ArrayList<>(quests)){
				if(quest.sec()){
					quests.remove(quest);
				}
			}
			for(SlaveInfo sinf:new ArrayList<>(Slaves.slaves)){
				if(sinf.live!=null){
					if(!sinf.live.loaded){
						if(GepUtil.nearestPlayer(sinf.home, 20)!=null){
							TextUtil.sdebug("&eSpawning living pawn at his home &f(&6"+sinf.name+"&f).");
							ZombieVillager z=main.prisons.spawnNMS(sinf.home);
							z.setCustomName(ChatColor.GOLD+sinf.name);
							sinf.live.mobId=z.getUniqueId();
							TextUtil.debug("&fNew live.mobId=&6"+sinf.live.mobId+"&f.");
							TextUtil.debug("&fEntity="+sinf.live.getEntity()+"&f.");
							sinf.live.loaded=true;
						}
					}else{
						Entity en=sinf.live.getEntity();
						if(en!=null){
							if(GepUtil.nearestPlayer(en.getLocation(), 100)==null){
								TextUtil.sdebug("&eDespawning living pawn &f(&6"+sinf.name+"&f).");
								en.remove();
								sinf.live.mobId=null;
								sinf.live.loaded=false;
								continue;
							}
						}
					}
					String sec=sinf.live.sec();
					if(sec!=null){
						Slaves.regDeath(sinf, "&cРегистрация смерти &6"+sinf.name+"&c: "+sec);
						continue;
					}
					if(sinf.live.moveToOwner){
						Player owner=Bukkit.getPlayer(sinf.owner);
						if(owner==null){
							sinf.live.moveToOwner=false;
							continue;
						}
						LivingEntity en=sinf.live.getEntity();
						double dist=en.getLocation().distance(owner.getLocation());
						if(dist>=20){
							sinf.live.moveToOwner=false;
							en.teleport(sinf.home);
							sinf.sayToOwner("Раб вернулся домой, т. к. вы убежали слишком далеко.");
							continue;
						}
						if(dist>=3)NMSUtil.move((Creature) en, owner.getLocation());
						if(dist<3)NMSUtil.move((Creature) en, en.getLocation());
					}
				}
			}
			for(Katorga kat:kats.values()){
				kat.sec();
			}
			if(rate%(20*60)==0){//~Раз в минуту
				List<SlaveInfo> prisoners = Slaves.findAllPrisoners();
				for(SlaveInfo sinf:prisoners){
					ProfPrisoner prof=(ProfPrisoner) sinf.live;
					Entity en=Bukkit.getEntity(prof.mobId);
					prof.updateRoom(en.getLocation());
				}
			}
		}
	}
	
	public void buildGUI(Player p){
		Invs.open(p, InvEvents.Building);
	}
	
	public boolean Interaction(InventoryClickEvent e){
		if(e.getClickedInventory().equals(e.getView().getTopInventory())){
			if(e.getCurrentItem()!=null){
				return Invs.event(e);
			}
		}
		return false;
	}
	
	public void closed(InventoryCloseEvent e){
		Player p=(Player) e.getPlayer();
		PlayerInfo pi=Events.plist.get(p.getName());
		SlaveInfo sinf=pi.invS;
		sinf.inv.clear();
		for(ItemStack item:e.getInventory()){
			if(item!=null)sinf.addToInv(new sitem(item.getType(), item.getAmount()));
		}
	}
	
	public ZombieVillager spawnNMS(Location loc){
		EntityPrisoner ep=new EntityPrisoner(loc.getWorld());
		ZombieVillager z=ep.spawn(loc);
		z.setRemoveWhenFarAway(false);
		return z;
	}
}
