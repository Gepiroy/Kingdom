package Kingdom;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.codingforcookies.armorequip.ArmorEquipEvent;

import HandlyRecipes.HRManager;
import Hunting.DeadAnimal;
import Hunting.HuntingManager;
import UtilsKingdom.GeomUtil;
import UtilsKingdom.GepUtil;
import UtilsKingdom.InventoryUtil;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import World2D.World2;
import invsUtil.InvEvents;
import invsUtil.Invs;
import net.minecraft.server.v1_16_R3.IBlockData;
import objKingdom.Conf;
import objKingdom.Food;
import objKingdom.PlayerInfo;
import objKingdom.Waiting;
import rooms.ProfLiveRunner;
import rooms.ProfPrisoner;
import rooms.ProfSlave;
import rooms.RoomInfo;
import rooms.SlaveInfo;
import rooms.Slaves;

public class Events implements Listener{
	public static HashMap<String,PlayerInfo> plist=new HashMap<>();
	static HashMap<String, Integer> chatInfluence=new HashMap<>();
	public static List<Location> blockUsage=new ArrayList<>();
	public HashMap<Location, String> doors=new HashMap<>();
	List<Material> CraftBAN = new ArrayList<>();
	
	Random r=new Random();
	
	public void enable(){
		chatInfluence.put("AIR", 0);
		chatInfluence.put("LONG", 0);
		chatInfluence.put("FLOWER", 0);
		chatInfluence.put("ROSE", 0);
		chatInfluence.put("FENCE", 0);
		chatInfluence.put("TORCH", 0);
		chatInfluence.put("PLATE", 0);
		chatInfluence.put("BUTTON", 0);
		chatInfluence.put("SNOW", 0);
		chatInfluence.put("SLAB", 17);
		chatInfluence.put("LEAVES", 20);
		chatInfluence.put("DOOR", 27);
		chatInfluence.put("STAIRS", 30);
		chatInfluence.put("GLASS", 35);
		
		CraftBAN.add(Material.GOLDEN_HELMET);
		CraftBAN.add(Material.GOLDEN_CHESTPLATE);
		CraftBAN.add(Material.GOLDEN_LEGGINGS);
		CraftBAN.add(Material.GOLDEN_BOOTS);
		CraftBAN.add(Material.GOLDEN_SWORD);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void login(AsyncPlayerPreLoginEvent e){
		String p=e.getName();
		Conf conf=new Conf(main.instance.getDataFolder()+"/players/"+p+".yml");
		if(conf.conf.contains("realTimers.died")){
			int onl=Bukkit.getOnlinePlayers().size();
			int secTime=onl*30;
			long died=conf.conf.getLong("realTimers.died");
			secTime-=(new Date().getTime()-died)/1000;
			if(secTime<=0){
				conf.file.delete();
				return;
			}
			e.disallow(Result.KICK_BANNED, TextUtil.string("&4&lВЫ УМЕРЛИ.\n&6Время вашего возрождения зависит от населения.\nСейчас онлайн &f"+onl+"&e, ждать примерно &6"+secTime/60+" мин. "+secTime%60+" сек."));
			return;
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e){
		Player p=e.getPlayer();
		doJoin(p);
		String ip = p.getAddress().getAddress().getHostAddress().toString();
		for(Player pl:Bukkit.getOnlinePlayers()){
			String ipa = pl.getAddress().getAddress().getHostAddress().toString();
			if(ipa.equals(ip)){
				TextUtil.sdebug("&c"+p.getName()+" &elogged in from &6"+pl.getName()+"&e's ip. |(&f"+ip+"|)");
			}
		}
		PlayerInfo pi=plist.get(p.getName());
		if(pi.ip!=null){
			if(!pi.ip.equals(ip)){
				TextUtil.sdebug("&c"+p.getName()+" &elogged in from wrong ip.");
			}
		}
		new BukkitRunnable(){
			@Override
			public void run() {
				for(PlayerInfo pi:getAllPIs()){
					if(pi.ip!=null&&pi.ip.equals(ip)){
						TextUtil.sdebug("&c"+p.getName()+"&e's ip equals &6"+pi.pname+"&e's.");
					}
				}
			}
		}.runTaskLaterAsynchronously(main.instance, 1);
	}
	
	
	
	public static void doJoin(Player p){
		for(int i=9;i<31;i++){
			p.getInventory().setItem(i, new ItemStack(Material.BARRIER));
		}
		File file=new File(main.instance.getDataFolder()+"/players/"+p.getName()+".yml");
		plist.put(p.getName(), new PlayerInfo(p.getName()));
		if(!file.exists()){
			Location l=main.instance.findLocToSpawn(p.getWorld(), p);
			if(l==null){
				TextUtil.mes(p, "&cKingdom", "Не удалось найти подходящее место спавна с первого раза, идёт расширенный поиск места...");
				p.setGameMode(GameMode.SPECTATOR);
				new BukkitRunnable() {
					int timer=0;
					@Override
					public void run() {
						timer++;
						Location l=main.instance.findLocToSpawn(p.getWorld(), p);
						if(l!=null){
							p.setGameMode(GameMode.SURVIVAL);
							p.teleport(l);
							TextUtil.mes(p, "&6Kingdom", "Точка спавна была найдена с &b"+timer+" &fобработки.");
							this.cancel();
							return;
						}
					}
				}.runTaskTimer(main.instance, 10, 10);
			}else{
				p.teleport(l);
			}
		}
		PlayerInfo pi=plist.get(p.getName());
		pi.fastTimers.put("nopvp", 150);
	}
	
	static void afterSpawn(Player p){
		TextUtil.globMessage("&6Kingdom", "Новый житель королевства! &b"+p.getDisplayName()+"&f! Помогите ему найти свой жизненный путь.", Sound.BLOCK_BELL_RESONATE, 3, 0, null, null, 0, 0, 0);
		
		new BukkitRunnable() {
			int timer=0;
			@Override
			public void run() {
				timer++;
				if(timer==60)TextUtil.persGlob(p, "&6Kingdom", "Добро пожаловать в &6королевство&f!", 
						Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1,
						"|Gepiroy's &4hardcore &6Kingdom &eRP", "&aДобро пожаловать!", 20, 40, 20);
				if(timer==140){
					if(main.instance.deathType==2)TextUtil.persGlob(p, "&6Kingdom", "&cСмерть&f - это &4&lНАВСЕГДА&f! У вас только &c&lОДНА &fжизнь.", 
						Sound.ENTITY_WITHER_DEATH, 1, 1,
						"&cСмерть&f - это &4&lНАВСЕГДА!", "У вас только &c&lОДНА &fжизнь.", 20, 40, 20);
					else if(main.instance.deathType==1)TextUtil.persGlob(p, "&6Kingdom", "&cСмерть&f - это &c&lБОЛЬНО&f! При смерти вы теряете все вещи и отправляетесь в бан на длительное время.", 
							Sound.ENTITY_WITHER_DEATH, 1, 1,
							"&cСмерть&f - это &c&lБОЛЬНО!", "Возрождения длятся около 20 минут + вы теряете роль и весь прогресс.", 20, 40, 20);
				}
				if(timer==220)TextUtil.persGlob(p, "&6Kingdom", "Это &aRolePlay &fсервер. Играйте так, будто это &4реальная &fжизнь!",
						Sound.ENTITY_VILLAGER_TRADE, 1, 0, 
						"Это &aRolePlay&f!", "Представьте, что это &4реальный&f мир!", 20, 40, 20);
				if(timer==300)TextUtil.persGlob(p, "&6Kingdom", "Если вы не знаете &6правил&f, &bособых механик&f или &bособенностей игры&f, прочитайте о них в Discord.",
						Sound.ENTITY_PLAYER_LEVELUP, 1, 2, 
						"Плохо ориентируетесь?", "&aПомощь в &bчате&a!", 20, 40, 20);
				if(timer==380)TextUtil.persGlob(p, "&6Kingdom", "Желаем успехов!",
						Sound.ENTITY_VILLAGER_YES, 1, 2, 
						"|Gepiroy's &4hardcore &6Kingdom &eRP", "Желаем узбеков!", 20, 10, 10);
			}
		}.runTaskTimer(main.instance, 1, 1);
	}
	
	@EventHandler
	public void leave(PlayerQuitEvent e){
		Player p=e.getPlayer();
		PlayerInfo pi=plist.get(p.getName());
		if(pi.timers.containsKey("PvP")){
			TextUtil.globMessage("&cСмерть", p.getDisplayName()+" &cвышел из жизни в бою.", Sound.BLOCK_BELL_USE, 3, 0.5f, null, null, 0, 0, 0);
			die(p,false);
		}
		else if(pi.jailTo!=null){
			TextUtil.globMessage("&cСмерть", p.getDisplayName()+" &cпокончил с собой, будучи связанным.", Sound.BLOCK_BELL_USE, 3, 0.5f, null, null, 0, 0, 0);
			die(p,false);
		}
		doLeave(p);
	}
	public static void doLeave(Player p){
		PlayerInfo pi=plist.get(p.getName());
		pi.save();
		plist.remove(p.getName());
	}
	
	net.minecraft.server.v1_16_R3.Item axe = CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_AXE)).getItem();
	net.minecraft.server.v1_16_R3.Item pickaxe = CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_PICKAXE)).getItem();
	net.minecraft.server.v1_16_R3.Item shovel = CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_SHOVEL)).getItem();
	
	
	public IBlockData ibdFromMaterial(Material mat){
		net.minecraft.server.v1_16_R3.Block nmsBlock = CraftMagicNumbers.getBlock(mat);
	    return nmsBlock.getBlockData();
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e){
		Block b=e.getBlock();
		Player p=e.getPlayer();
		if(b.getType().getHardness()!=0){
			net.minecraft.server.v1_16_R3.Item our = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getItem();
			IBlockData ibd = ibdFromMaterial(b.getType());
			float ourspeed=our.getDestroySpeed(null, ibd);
			if(ourspeed<=1){
				if(axe.getDestroySpeed(null, ibd)>1){
					p.sendMessage("Нужен топор.");
					e.setCancelled(true);
					return;
				}else if(pickaxe.getDestroySpeed(null, ibd)>1){
					p.sendMessage("Нужна кирка.");
					e.setCancelled(true);
					return;
				}else if(shovel.getDestroySpeed(null, ibd)>1){
					p.sendMessage("Нужна лопата.");
					e.setCancelled(true);
					return;
				}
			}
			if(b.getType().toString().contains("_LEAVES")){
				if(r.nextFloat()<=0.1){
					b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STICK));
				}
			}
		}
		
		if(p.getLocation().subtract(0, 1, 0).getBlock().getLocation().equals(b.getLocation())){
			TextUtil.mes(p, "&6Kingdom", "Вы не можете копать под собой.");
			e.setCancelled(true);
			return;
		}
		PlayerInfo pi=plist.get(p.getName());
		if(pi.bools.contains("jailed")){
			e.setCancelled(true);
			return;
		}
		if(b.getType().equals(Material.CHEST)){
			Chest chest=(Chest) b.getState();
			for(ItemStack item:chest.getInventory().getContents()){
				if(item!=null&&!item.getType().equals(Material.AIR)){
					TextUtil.mes(p, "&6Kingdom", "Вы не можете сломать сундук, пока в нём что-то есть.");
					e.setCancelled(true);
					return;
				}
			}
		}
		if(b.getType().equals(Material.REDSTONE_ORE)){
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE));
		}
		ItemStack tool=p.getInventory().getItemInMainHand();
		if(tool.getType().toString().contains("PICKAXE")){
			e.setCancelled(true);
			return;
		}
		if(!e.isCancelled())World2.breakb(b.getLocation());
	}
	@EventHandler
	public void blockPlace(BlockPlaceEvent e){
		Player p=e.getPlayer();
		PlayerInfo pi=Events.plist.get(p.getName());
		ItemStack hitem=p.getInventory().getItemInMainHand();
		Block b=e.getBlock();
		if(p.getLocation().subtract(0, 1, 0).getBlock().getLocation().equals(b.getLocation())){
			TextUtil.mes(p, "&6Kingdom", "Вы не можете строить под собой.");
			e.setCancelled(true);
			return;
		}
		if(b.getType().equals(Material.SWEET_BERRY_BUSH)){
			if(r.nextDouble()>0.15){
				b.setType(Material.AIR);
				return;
			}
		}
		/*if(b.getType().toString().contains("LOG")){
			if(pi.getTmp("beam")!=null){
				pi.remTmp("beam");
			}else{
				pi.setTmp("beam", new PrepareBeam(p, b.getLocation().add(0.5, 0.5, 0.5)));
			}
			e.setCancelled(true);
		}*/
		if(p.isSneaking())TextUtil.debug("BlockPlacedType="+b.getType());
		Location l=p.getEyeLocation();
		Material[] mats={Material.STONE,Material.DIRT,Material.ANDESITE,Material.DIORITE,Material.GRANITE};
		boolean canCivilize=true;
		for(int i=0;i<100;i++){
			if(!canCivilize)break;
			l.add(0, 1, 0);
			Block bl=l.getBlock();
			for(Material m:mats){
				if(bl.getType().equals(m)){
					canCivilize=false;
					break;
				}
			}
		}
		if(canCivilize){
			if(pi.placeHandler==null){
				pi.placeHandler=b.getLocation();
			}else{
				if(pi.placeHandler.distance(b.getLocation())<=13){
					pi.placedAfter++;
					if(pi.placedAfter>=10){
						main.ters.addPoint(pi.placeHandler);
						main.ters.addPoint(b.getLocation());
						pi.placeHandler=null;
						pi.placedAfter=0;
					}
				}else{
					pi.placeHandler=b.getLocation();
					pi.placedAfter=0;
				}
			}
		}else if(pi.placeHandler!=null){
			pi.placeHandler=null;
			pi.placedAfter=0;
		}
		if(hitem!=null&&hitem.getType().equals(Material.FLINT_AND_STEEL)){
			if(!pi.timers.containsKey("noFlint")){
				pi.timers.put("noFlint", 12);
				main.instance.addWaiting(p.getName(), new Waiting(100, "setFire", b.getLocation().add(0.5, 0.5, 0.5), 2.5, "Вы отошли от огня.", "noFlint"), true);
			}
			e.setCancelled(true);
		}
		if(!e.isCancelled())World2.placeb(b.getLocation());
	}
	
	@EventHandler
	public void spawn(EntitySpawnEvent e){
		if(!(e.getEntity() instanceof LivingEntity))return;
		LivingEntity en=(LivingEntity) e.getEntity();
		if(en instanceof Monster){
			if(!en.getType().equals(EntityType.ZOMBIE_VILLAGER)&&!en.getType().equals(EntityType.PILLAGER))e.setCancelled(true);
			if(main.ters.isInPoints(en.getLocation()))return;
			if(e.getEntityType().equals(EntityType.ZOMBIE)){
				if(countEntities(e.getLocation(), 50, EntityType.ZOMBIE_VILLAGER)<=5&&!main.ters.isInMine(e.getLocation()))
				if(r.nextDouble()<=0.3)spawnSavage(e.getLocation());
			}
			/*if(e.getEntityType().equals(EntityType.SKELETON)){
				e.setCancelled(true);
				if(countEntities(e.getLocation(), 50, EntityType.PILLAGER)<=5&&!main.ters.isInMine(e.getLocation()))
				if(r.nextDouble()<=0.3)spawnRogue(e.getLocation());
			}*/
		}
		else if(en.getType().equals(EntityType.PHANTOM)){
			e.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void burn(EntityCombustEvent e){
		e.setCancelled(true);
	}*/
	
	int countEntities(Location l, double range, EntityType type){
		int ret=0;
		for(Entity en:l.getWorld().getEntities()){
			if(en.getType().equals(type)&&l.distance(en.getLocation())<=range){
				ret++;
			}
		}
		return ret;
	}
	
	void spawnSavage(Location loc){
		if(!main.ents.canSpawn(loc, 50, 15))return;
		ZombieVillager en=(ZombieVillager) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE_VILLAGER);
		en.setCustomName(ChatColor.GOLD+"Дикарь");
		en.getEquipment().clear();
		en.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
		en.setAdult();
		en.getEquipment().setHelmet(new ItemStack(Material.ACACIA_BUTTON));
		en.getEquipment().setHelmetDropChance(0f);
		if(r.nextDouble()<=0.3){
			en.getEquipment().setItemInMainHand(ItemUtil.create(Material.WOODEN_SWORD, 1, ChatColor.GOLD+"Дубина дикаря", null, null, 0));
		}
	}
	void spawnWolf(Location loc){
		Wolf en=(Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
		en.setCustomName(ChatColor.RED+"Волк");
		en.setAngry(true);
	}
	void spawnRogue(Location loc){
		Pillager en=(Pillager) loc.getWorld().spawnEntity(loc, EntityType.PILLAGER);
		en.setPatrolLeader(false);
		en.setCustomName(ChatColor.RED+"Разбойник");
		if(r.nextBoolean())en.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
	}
	
	@EventHandler
	public void tntPreExplode(ExplosionPrimeEvent e){
		Location l=e.getEntity().getLocation();
		double rd=r.nextDouble();
		if(rd<=0.075){//
			l.getWorld().playSound(l, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
			l.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 100, 0.3, 0.3, 0.3, 0);
			TextUtil.sdebug("tntCancel");
			e.setCancelled(true);
			return;
		}else if(rd<=0.085){
			e.setRadius(10);
			e.setFire(true);
			TextUtil.sdebug("tntLarge");
			return;
		}
		if(l.getY()>=60){
			if(rd<0.5){
				e.setRadius(3);
				TextUtil.sdebug("tntMiddle");
			}
		}
	}
	
	HashMap<Location,Integer> bdamages=new HashMap<>();
	
	
	
	@EventHandler
	public void interact(PlayerInteractEvent e){
		Player p=e.getPlayer();
		if(e.getHand()!=null){
			if(e.getHand().equals(EquipmentSlot.OFF_HAND)){
				ItemStack hitem=p.getInventory().getItemInOffHand();
				if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					if(hitem.getType().toString().contains("_AXE")&&e.getClickedBlock().getType().toString().contains("_LOG")){
						e.setCancelled(true);
						main.instance.addWaiting(p.getName(), new Waiting(20, "StrippingLog", e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), 2.5, "Не могу обтесать дерево так далеко", null), true);
						TextUtil.mes(p, "&6Kingdom", "Обтёсывать дерево можно только в крафте.");
					}
				}
				return;
			}
		}
		if(!e.getAction().toString().contains("CLICK"))return;
		PlayerInfo pi=plist.get(p.getName());
		ItemStack hitem=p.getInventory().getItemInMainHand();
		if(e.getAction().toString().contains("RIGHT_CLICK")){
			Item it=ItemLooking(p);
			if(it!=null){
				if(hitem==null||hitem.getType().equals(Material.AIR)){
					p.getInventory().setItemInMainHand(it.getItemStack());
					it.remove();
				}else if(InventoryUtil.canStack(it.getItemStack(), hitem, true)){
					hitem.setAmount(hitem.getAmount()+it.getItemStack().getAmount());
					it.remove();
				}
				e.setCancelled(true);
				return;
			}
			if(hitem.getType()==Material.STICK){
				HRManager.stickCrafting.open(p);
			}
		}
		if(hitem!=null&&!hitem.getType().equals(Material.AIR)){
			if(main.schs.clickteract(e)){
				e.setCancelled(true);
				return;
			}
		}
		Block b=e.getClickedBlock();
		//p.sendMessage(""+hitem.getType());
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(hitem.getType().toString().contains("_AXE")&&e.getClickedBlock().getType().toString().contains("_LOG")){
				e.setCancelled(true);
				TextUtil.mes(p, "&6Kingdom", "Обтёсывать дерево можно только в крафте.");
				return;
			}
		}
		main.packs.interact(e);
		if(b!=null){
			pi.lastClickedBlock=b;
			if(b.getState() instanceof InventoryHolder){
				Location bl=b.getLocation().add(0.5,0.5,0.5);
				if(bl.distance(p.getEyeLocation().subtract(0, 0.5, 0))>2.5){
					e.setCancelled(true);
					return;
				}
			}
		}
		if(GepUtil.loreContains(hitem, ChatColor.RED+"ЗАГОТОВКА.")){
			e.setCancelled(true);
			if(b!=null){
				if(b.getType().toString().contains("ANVIL")&&!pi.fastTimers.containsKey("forning")){
					if(!GepUtil.loreContains(hitem, ChatColor.GOLD+"Требуется выковать.")){
						TextUtil.mes(p, "&6Kingdom", "Для начала предмет следует выплавить.");
						return;
					}
					pi.fastTimers.put("forning", 20);
					p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.5f, 0.75f+r.nextFloat());
					return;
				}
				else if(b.getType().equals(Material.BLAST_FURNACE)){
					if(!GepUtil.loreContains(hitem, ChatColor.RED+"Требуется выплавить.")){
						TextUtil.mes(p, "&6Kingdom", "Предмет не нужно выплавлять.");
						return;
					}
					Location loc=b.getLocation().add(0.5, 0.5, 0.5);
					if(blockUsage.contains(loc)){
						TextUtil.mes(p, "&6Kingdom", "Эта плавильня занята. Найдте другую или ждите.");
						return;
					}
					blockUsage.add(loc);
					ItemStack item=hitem.clone();
					hitem.setAmount(0);
					p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2, 0.75f+r.nextFloat());
					p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 1+r.nextFloat());
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, r.nextFloat());
					loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 50, 0.3, 0.3, 0.3, 0.01);
					new BukkitRunnable(){
						@Override
						public void run() {
							ItemMeta meta=item.getItemMeta();
							List<String> lore=new ArrayList<>();
							for(String st:meta.getLore()){
								if(st.contains(ChatColor.RED+"Требуется выплавить."))lore.add(ChatColor.GOLD+"Требуется выковать.");
								else lore.add(st);
							}
							meta.setLore(lore);
							item.setItemMeta(meta);
							Item it=null;
							if(p==null||!p.isOnline()||p.getLocation().distance(loc)>5){
								it=loc.getWorld().dropItem(loc, item);
							}
							else{
								it=loc.getWorld().dropItem(p.getLocation(), item);
								it.setPickupDelay(0);
							}
							loc.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 1, 0.75f+r.nextFloat());
							loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 50, 0.3, 0.3, 0.3, 0.01);
							blockUsage.remove(loc);
						}
					}.runTaskLater(main.instance, 100);
					//pi.fastTimers.put("smelting", 50);
					return;
				}
			}
			TextUtil.mes(p, "&6Kingdom", "Это заготовка. Ей нельзя бить, копать, одевать, &cИЗ НЕЁ НУЖНО СДЕЛАТЬ ПРЕДМЕТ!");
			return;
		}
		/*if(e.getAction().toString().contains("LEFT_CLICK")){
			if(pi.getTmp("beam")!=null){
				PrepareBeam pb=(PrepareBeam) pi.getTmp("beam");
				if(hitem.getAmount()>=pb.height){
					hitem.setAmount(hitem.getAmount()-pb.height);
				}
				pb.drop();
				pi.remTmp("beam");
			}
		}*/
		if(b!=null){
			pi.lastClickedBlock=b;
			pi.lastBlockFace=e.getBlockFace();
			Location bl=b.getLocation().add(0.5,0.5,0.5);
			if(e.getAction()==Action.LEFT_CLICK_BLOCK){
				if(hitem.getType().toString().contains("PICKAXE")){
					e.setCancelled(true);
					if(p.getEyeLocation().distance(bl)>2.25){
						TextUtil.actionBar(p, "&fСлишком &cдалеко&f!");
						return;
					}
					Vector dir=GeomUtil.direction(p.getEyeLocation(), bl);
					int y=(int) (dir.getY()*90);
					//p.sendMessage("y="+y);
					if(y<-45)y=-1;
					else y=0;
					List<Location> ret=new ArrayList<>();
					for(int dx=-1;dx<2;dx+=2){
						for(int dy=1+y;dy<3+y;dy++){
							for(int dz=-1;dz<2;dz+=2){
								Location l=p.getLocation().add(dx*0.5, dy*0.5, dz*0.5);
								p.spawnParticle(Particle.VILLAGER_HAPPY, l, 1, 0, 0, 0, 0);
								Material bm=l.getBlock().getType();
								if(bm.isSolid()){
									ret.add(l);
								}
							}
						}
					}
					if(ret.size()>0){
						TextUtil.actionBar(p, "&fВы &cне можете &6замахнуться&f!");
						for(Location l:ret){
							p.spawnParticle(Particle.BARRIER, l, 1, 0, 0, 0, 0.0125);
						}
						return;
					}
					int add=1;
					if(pi.rel.good>0&&r.nextFloat()<=pi.rel.good*0.01)add++;
					GepUtil.HashMapReplacer(bdamages, b.getLocation(), add, false, false);
					if(bdamages.get(b.getLocation())>=3){
						bdamages.remove(b.getLocation());
						//BlockFace face=pi.lastBlockFace;
						//Location dropLoc=b.getLocation().add(0.5, 0.2, 0.5).add(face.getDirection().normalize());
						for(ItemStack item:b.getDrops(hitem)){
							bl.getWorld().dropItemNaturally(bl, item);
							//it.setVelocity(GepUtil.throwTo(dropLoc, p.getEyeLocation().add(r.nextDouble()-0.5, r.nextDouble()-0.5, r.nextDouble()-0.5), 0.1+r.nextDouble()*0.1));
						}
						b.setType(Material.AIR);
						World2.breakb(b.getLocation());
						/*
						Block down=b.getLocation().add(0, -1, 0).getBlock();
						if(down.getType()!=Material.AIR){
							Block up=b.getLocation().add(0, 1, 0).getBlock();
							if(up.getType().equals(Material.AIR)){
								List<Block> canDown=new ArrayList<>();
								canDown.add(b.getLocation().add(1, 1, 0).getBlock());
								canDown.add(b.getLocation().add(-1, 1, 0).getBlock());
								canDown.add(b.getLocation().add(0, 1, 1).getBlock());
								canDown.add(b.getLocation().add(0, 1, -1).getBlock());
								for(Block blo:new ArrayList<>(canDown)){
									if(blo.getType().equals(Material.AIR))canDown.remove(blo);
								}
								if(canDown.size()>0){
									Block blo=canDown.get(r.nextInt(canDown.size()));
									b.setType(blo.getType());
									blo.setType(Material.AIR);
								}else{
									b.setType(Material.AIR);
									World2.breakb(b.getLocation());
								}
							}else{
								Block upper=b.getLocation().add(0, 2, 0).getBlock();
								b.setType(up.getType());
								up.setType(upper.getType());
								upper.setType(Material.AIR);
							}
						}else{
							b.setType(Material.AIR);
							World2.breakb(b.getLocation());
						}*/
						b.getWorld().playSound(bl, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2, 0);
						
					}else{
						b.getWorld().playSound(bl, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2, 2);
					}
				}/*else if(hitem.getType().toString().contains("_AXE")){
					Location l=bl.clone();
					int length=0;
					for(;length<10;length++){
						l.add(0, 1, 0);
						if(l.getBlock().getType()!=b.getType()){
							break;
						}
						l.getBlock().setType(Material.AIR);
					}
					new Beam(bl, length, b.getType(), p.getEyeLocation().getDirection()).fall();
				}*/
			}
			if(b.getType().toString().contains("BED")){
				World world=Bukkit.getWorld("world");
				int time=(int) world.getTime()%24000;
				if(time>12000&&!pi.bools.contains("slept")){
					pi.setbool("slept", true);
					TextUtil.mes(p, "&6Kingdom", "Вы легли спать.");
				}
			}else if(b.getType().toString().contains("_DOOR")){
				Door door=(Door) b.getBlockData();
				if(door.getHalf().equals(Half.BOTTOM)){
					bl.add(0, 1, 0);
				}
				if(doors.containsKey(bl)){
					String code=doors.get(bl);
					if(hitem!=null&&hitem.getType().equals(Material.IRON_NUGGET)&&GepUtil.loreContains(hitem, code)){
						if(p.isSneaking()){
							e.setCancelled(true);
							Inventory inv=Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE+"Настройки замка");
							inv.setItem(13, ItemUtil.create(Material.IRON_INGOT, 1, ChatColor.AQUA+"Снять замок", null, null, 0));
							p.openInventory(inv);
							return;
						}
					}else{
						Inventory inv=Bukkit.createInventory(null, 27, ChatColor.RED+"Эта дверь заперта.");
						inv.setItem(11, ItemUtil.create(Material.IRON_NUGGET, 1, ChatColor.AQUA+"Взломать", null, null, 0));
						inv.setItem(15, ItemUtil.create(Material.IRON_AXE, 1, ChatColor.RED+"Выломать", null, null, 0));
						p.openInventory(inv);
						e.setCancelled(true);
						return;
					}
				}
				else if(hitem!=null&&hitem.getType().equals(Material.IRON_INGOT)&&GepUtil.loreContains(hitem, ChatColor.BLACK+"Key_id=")){
					doors.put(bl,ChatColor.BLACK+"Key_id="+GepUtil.intFromLore(hitem, ChatColor.BLACK+"Key_id="));
					TextUtil.mes(p, "&6Kingdom", "Вы заперли дверь. Key_id="+doors.get(bl));
					hitem.setAmount(hitem.getAmount()-1);
					e.setCancelled(true);
					p.getWorld().playSound(bl, Sound.BLOCK_IRON_DOOR_CLOSE, 1, 2);
				}
			}
		}
	}
	
	@EventHandler
	public void enInteract(PlayerInteractEntityEvent e){
		if(e.getHand()!=null){
			if(e.getHand().equals(EquipmentSlot.OFF_HAND)){
				return;
			}
		}
		if(e.getRightClicked() instanceof LivingEntity){
			Player p=e.getPlayer();
			PlayerInfo pi=plist.get(p.getName());
			LivingEntity en=(LivingEntity) e.getRightClicked();
			if(!en.hasAI()){
				if(pi.handBody==null){
					pi.handBody=en.getUniqueId();
				}else{
					pi.handBody=null;
					RoomInfo room=new RoomInfo();
					room.resetRoom(en.getLocation().add(0,0.75,0).getBlock().getLocation());
					if(room.isCreated){
						Slaves.createNewPrisoner(en.getLocation().getBlock().getLocation().add(0.5, 0.1, 0.5), p.getName());
						en.remove();
					}
				}
			}
			SlaveInfo sinf=Slaves.findByMobId(en.getUniqueId());
			if(sinf!=null){
				ProfPrisoner pris=sinf.asPrisoner();
				if(pris!=null){
					ItemStack hitem=p.getInventory().getItemInMainHand();
					int food=Food.getFoodLevel(hitem.getType());
					if(food>0){//Если предмет съедобен (кормёшка)
						if(pris.food>=19.5){
							p.getWorld().playSound(en.getLocation(),Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 2);
							e.setCancelled(true);
							return;
						}
						pris.food+=food;
						hitem.setAmount(hitem.getAmount()-1);
						p.getWorld().playSound(en.getLocation(),Sound.ENTITY_PLAYER_BURP, 1, 2);
						e.setCancelled(true);
						return;
					}else{
						if(pris.recruit>=100){
							String name=sinf.randName();
							sinf.name=name;
							en.setCustomName(name);
							ProfSlave ps=new ProfSlave();
							ps.mobId=en.getUniqueId();
							sinf.live=ps;
						}else{
							TextUtil.mes(p, "&6Рекрутинг", "&eПрогресс рекрутирования: &b"+TextUtil.cylDouble(pris.recruit, "#0.0")+"%");
						}
					}
				}else if(sinf.live instanceof ProfSlave){
					if(p.isSneaking()){
						pi.setOrRemoveTmp("dragging", en.getUniqueId());
						return;
					}
					pi.invS=sinf;
					if(sinf.live instanceof ProfLiveRunner){
						Invs.open(p, InvEvents.Runner);
						return;
					}
					sinf.GUI(p);
				}
			}
		}
	}
	
	public Item ItemLooking(Player p){
		Location loc=p.getEyeLocation();
		Vector vec = loc.toVector();
		Vector v1 = loc.getDirection().normalize().multiply(0.3);
		List<Item> its=new ArrayList<>();
		for(Entity en:p.getWorld().getEntities()){
			if(en.getType().equals(EntityType.DROPPED_ITEM)){
				its.add((Item) en);
			}
		}
		for(int i=0;i<13;i++){
			vec.add(v1);
			for(Item it:new ArrayList<>(its)){
				if(it.getLocation().add(0, 0.25, 0).distance(vec.toLocation(p.getWorld()))<=0.5){
					return it;
				}
			}
		}
		return null;
	}
	
	@EventHandler
	public void bucket(PlayerBucketFillEvent e){
		if(e.getItemStack().getType().equals(Material.LAVA_BUCKET)){
			e.setCancelled(true);
			Player p=e.getPlayer();
			p.damage(3);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, 1);
		}
	}
	
	public static HashMap<UUID, Integer> bodies = new HashMap<>();
	
	@EventHandler
	public void combust(EntityCombustEvent e){
		if(e.getEventName().equals("EntityCombustEvent"))e.setCancelled(true);
	}
	
	@EventHandler
	public void die(EntityDeathEvent e){
		if(e.getEntityType().equals(EntityType.SHEEP)){
			LivingEntity sheep=(LivingEntity) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.SHEEP);
			sheep.setInvulnerable(true);
			sheep.setAI(false);
			sheep.setGravity(false);
			sheep.setSilent(true);
			Location tp=e.getEntity().getLocation();
			tp.setPitch(0);
			sheep.teleport(tp);
			bodies.put(sheep.getUniqueId(), 5);
			e.getEntity().remove();
			//Можно сделать GUI с выбором: разделать аккуратно (требуется нож/топор) или по-быстрому (топор/нож).
			e.getDrops().clear();
		}else if(e.getEntityType().equals(EntityType.ZOMBIE_VILLAGER)){
			for(ItemStack item:new ArrayList<>(e.getDrops())){
				if(item.getType().equals(Material.ROTTEN_FLESH))e.getDrops().remove(item);
			}
		}
	}
	
	@EventHandler
	public void hurt(EntityDamageByEntityEvent e){
		Player p=null;
		Player damager=null;
		ItemStack weapon=null;
		if(e.getEntity() instanceof Player)p=(Player) e.getEntity();
		if(e.getDamager() instanceof Player){
			damager=(Player) e.getDamager();
			//damager.sendMessage("prehurt");
			weapon=damager.getInventory().getItemInMainHand();
			double r=1.3;
			if(weapon!=null&&!weapon.getType().equals(Material.AIR)){
				r=1.8;
				if(Items.isSimilar(weapon, Items.stick_knife))r=2;
				else if(weapon.getType().toString().contains("SWORD"))r=3;
				else if(weapon.getType().toString().contains("_AXE"))r=2.7;
			}
			r+=0.15;//hitbox-fix
			Location hands=damager.getEyeLocation().subtract(0, 0.5, 0);
			Location nearest=e.getEntity().getLocation();
			double nd=hands.distance(nearest);
			for(int i=0;i<10;i++){
				nearest.add(0,0.2,0);
				if(nearest.distance(hands)<nd){
					nd=nearest.distance(hands);
				}
			}
			if(nd>r){
				e.setCancelled(true);
				return;
			}
			double dam=e.getDamage();
			PlayerInfo pi=plist.get(damager.getName());
			dam*=1+pi.rel.good*0.01;
			if(Items.isSimilar(weapon, Items.stick_knife)){
				e.setCancelled(true);
				LivingEntity t = (LivingEntity) e.getEntity();
				t.damage(dam);
				Damageable itemdmg = (Damageable) weapon.getItemMeta();
		        int damage = itemdmg.getDamage()+2;
		        if (damage >= weapon.getType().getMaxDurability()) {
		            weapon.setAmount(0);
		            damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		        }else{
			        itemdmg.setDamage(damage);
			        weapon.setItemMeta((ItemMeta) itemdmg);
		        }
			}
			e.setDamage(dam);
		}
		//if(damager!=null)damager.sendMessage("hurt");
		if(p!=null){
			if(damager!=null){
				if(main.timer<=3600){
					e.setCancelled(true);
				}
				PlayerInfo pi=plist.get(p.getName());
				PlayerInfo pli=plist.get(damager.getName());
				if(!pi.timers.containsKey("PvP")){
					TextUtil.Title(p, "&c&lPvP&6!", "&6Не ливать!", 5, 10, 5);
				}
				if(!pli.timers.containsKey("PvP")){
					TextUtil.Title(damager, "&c&lPvP&6!", "&6Не ливать!", 5, 10, 5);
				}
				GepUtil.HashMapReplacer(pi.timers, "PvP", 30, false, true);
				GepUtil.HashMapReplacer(pli.timers, "PvP", 30, false, true);
			}
		}else{
			if(e.getEntity() instanceof LivingEntity){
				LivingEntity en=(LivingEntity) e.getEntity();
				if(en.getType().equals(EntityType.ZOMBIE_VILLAGER)){
					if(damager!=null){
						//damager.sendMessage("zvil");
						if(en.isInvulnerable()){
							//damager.sendMessage("invul");
							en.teleport(en.getLocation().add(0, 1.5, 0));
							en.setHealth(0);
							return;
						}
					}
					if(en.getHealth()-e.getFinalDamage()<=0){//entity-death.
						if(r.nextDouble()<=0.3){//TODO tmp-chance
							e.setCancelled(true);
							en.setInvulnerable(true);
							en.setAI(false);
							en.setGravity(false);
							en.setSilent(true);
							Location tp=e.getEntity().getLocation();
							tp.setPitch(0);
							en.teleport(tp);
							bodies.put(en.getUniqueId(), 5);
							return;
						}
					}
				}
				if(en instanceof Animals){
					if(en.getHealth()-e.getFinalDamage()<=0){//entity-death.
						HuntingManager.die(en);
						e.setCancelled(true);
					}
				}
			}
		}
		if(damager!=null){
			PlayerInfo pli=plist.get(damager.getName());
			if(pli.bools.contains("jailed")){
				e.setCancelled(true);
				return;
			}
			if(weapon!=null){
				if(GepUtil.loreContains(weapon, ChatColor.RED+"ЗАГОТОВКА.")){
					e.setCancelled(true);
					damager.sendTitle(ChatColor.RED+"ВНИМАНИЕ!", ChatColor.GOLD+"Вы не можете бить заготовкой!", 10, 20, 20);
					return;
				}
			}
			for(DeadAnimal da:HuntingManager.bodies){
				if(da.id==e.getEntity().getUniqueId()){
					for(ItemStack is:da.pt.drops.keySet()){
						if(main.r.nextFloat()<=da.pt.drops.get(is)){
							GepUtil.give(damager, Items.create(is, is.getAmount()));
						}
					}
					da.parts--;
					if(da.parts<=0){
						e.getEntity().remove();
					}
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void dm(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p=(Player) e.getEntity();
			PlayerInfo pi=plist.get(p.getName());
			if(pi.fastTimers.containsKey("nopvp")){
				e.setCancelled(true);
				return;
			}
			if(p.getHealth()-e.getFinalDamage()<6){
				float pit=(float) (1-(p.getHealth()-e.getFinalDamage())/12.0);
				if(pit>0)p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5f, pit);
			}
			if(p.getHealth()-e.getFinalDamage()<=0){
				String lore="";
				if(e.getCause().equals(DamageCause.FALL)){
					lore+="Похоже, он &eразбился&f.";
				}else if(e instanceof EntityDamageByEntityEvent){
					EntityDamageByEntityEvent enev=(EntityDamageByEntityEvent) e;
					TextUtil.sdebug("&8entityTypeOfDeath| "+p.getDisplayName()+"&8: |"+enev.getDamager().getType());
					if(enev.getDamager() instanceof Player){
						lore+="По всей видимости, его убил &cчеловек&f.";
						Player damer=(Player) enev.getDamager();
						ItemStack hitem=damer.getInventory().getItemInMainHand();
						for(ItemStack item:damer.getInventory().getArmorContents()){
							if(item!=null&&!item.getType().equals(Material.AIR)){
								addBlood(item);
							}
						}
						if(hitem!=null&&!hitem.getType().equals(Material.AIR)){
							addBlood(hitem);
							lore+=" На теле видны &bследы оружия&f.";
							if(hitem.getType().toString().contains("SWORD")){
								lore+=" Похоже, это был &6меч&f.";
							}else if(hitem.getType().toString().contains("_AXE")){
								lore+=" Похоже, это был &6топор&f.";
							}else if(hitem.getType().isBlock()){
								lore+=" Похоже, это был &6какой-то блок&f.";
							}else{
								lore+=" |Сложно понять, чем именно он был убит...";
							}
						}else{
							lore+=" На теле видны &bследы избиения&f.";
						}
					}else{
						Entity damer=enev.getDamager();
						EntityType type=damer.getType();
						if(type.toString().contains("TNT")){
							lore+="Его тело покрыто следами &cпороха и песка&f...";
						}else if(type.equals(EntityType.CREEPER)){
							lore+="Его тело покрыто следами &cпороха&f...";
						}else if(type.toString().contains("ARROW")){
							lore+="Из него торчит смертоносная &cстрела&f.";
						}
						else{
							if(damer instanceof Monster)lore+="По всей видимости, его убил &cмонстр&f.";
							else if(damer instanceof Animals)lore+="По всей видимости, его убило &cживотное&f.";
							else{
								lore+="|Сложно понять, какое существо его убило&f.";
							}
						}
					}
				}else if(e.getCause().toString().contains("FIRE")){
					lore+="Его тело &cобуглено&f.";
				}else{
					lore+="|Обстоятельства его смерти загадочны&f.";
					TextUtil.sdebug("&8deathCauseOf| "+p.getDisplayName()+"&8: |"+e.getCause());
				}
				if(e.getFinalDamage()<4){
					lore+=" В конце жизни он испытал &eнебольшую&f боль.";
				}else if(e.getFinalDamage()<10){
					lore+=" В конце жизни он испытал &6сильную&f боль.";
				}else{
					lore+=" В конце жизни он ощутил &cколоссальную&f боль.";
				}
				if(p.getFoodLevel()<7){
					lore+=" Он выглядет крайне истощённым, будто днями не ел.";
				}
				TextUtil.globMessage("&cСмерть", p.getDisplayName()+" &cпогиб. &f"+lore, Sound.BLOCK_BELL_USE, 3, 0.5f, null, null, 0, 0, 0);
				e.setCancelled(true);
				die(p);
			}
		}else{
			for(DeadAnimal da:HuntingManager.bodies){
				if(da.id==e.getEntity().getUniqueId()){
					e.setCancelled(true);
				}
			}
		}
	}
	
	void addBlood(ItemStack item){
		if(GepUtil.loreContains(item, ChatColor.RED+"Следы крови"))return;
		ItemMeta meta=item.getItemMeta();
		List<String> ilore=meta.getLore();
		if(ilore==null)ilore=new ArrayList<>();
		ilore.add(ChatColor.RED+"Следы крови");
		meta.setLore(ilore);
		item.setItemMeta(meta);
	}
	
	Location toDoorHead(Location l){
		Block b=l.getBlock();
		Door door=(Door) b.getBlockData();
		if(door.getHalf().equals(Half.BOTTOM)){
			l.add(0, 1, 0);
		}
		return l;
	}
	
	@EventHandler
	public void click(InventoryClickEvent e){
		if(Invs.event(e))return;
		Player p=(Player) e.getWhoClicked();
		PlayerInfo pi=plist.get(p.getName());
		if(e.getInventory()!=null&&e.getClickedInventory()!=null&&e.getView().getTitle()!=null){
			if(main.prisons.Interaction(e)){
				e.setCancelled(true);
				return;
			}
			if(e.getView().getTitle().contains(ChatColor.YELLOW+"Инвентарь игрока")){
				e.setCancelled(true);
			}
			else if(e.getView().getTitle().contains(ChatColor.RED+"Эта дверь заперта.")){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
					ItemStack item = e.getCurrentItem();
					if(item.getType().equals(Material.IRON_AXE)){
						if(r.nextDouble()<=0.2){
							p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 3, 0);
							Location door=toDoorHead(pi.lastClickedBlock.getLocation().add(0.5, 0.5, 0.5));
							door.getBlock().setType(Material.AIR);
							doors.remove(door);
						}else{
							p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 3, 0.8f);
						}
						p.damage(2);
					}else if(item.getType().equals(Material.IRON_NUGGET)){
						TextUtil.mes(p, "&6Kingdom", "В доработке...");
					}
					p.closeInventory();
				}
			}else if(e.getView().getTitle().contains(ChatColor.DARK_BLUE+"Настройки замка")){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
					ItemStack item = e.getCurrentItem();
					if(item.getType().equals(Material.IRON_INGOT)){
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
						Location door=toDoorHead(pi.lastClickedBlock.getLocation().add(0.5, 0.5, 0.5));
						String keyid=doors.get(door);
						doors.remove(door);
						ItemStack lock=ItemUtil.create(Material.IRON_INGOT, 1, ChatColor.AQUA+"Замок", new String[]{
								ChatColor.GREEN+"Выковано.",
								keyid
						}, null, 0);
						p.getInventory().addItem(lock);
					}
					p.closeInventory();
				}
			}
		}
	}
	
	@EventHandler
	public void close(InventoryCloseEvent e){
		if(e.getView().getTitle().equals(ChatColor.GOLD+"Инвентарь раба")){
			main.prisons.closed(e);
		}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent e){
		String mes=e.getMessage();
		Player p=e.getPlayer();
		PlayerInfo pi=plist.get(p.getName());
		double r=15;
		ChatColor col=ChatColor.GRAY;
		if(mes.charAt(0)=='!'){
			mes=mes.substring(1, mes.length());
			r=70;
			if(main.isKing(p))col=ChatColor.YELLOW;
			else col=ChatColor.WHITE;
		}else{
			if(main.isKing(p))col=ChatColor.GOLD;
		}
		mes=pi.chatName(pi)+": "+col+(mes.charAt(0)+"").toUpperCase()+mes.substring(1, mes.length());
		say(p.getEyeLocation(),mes,r);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void eat(PlayerItemConsumeEvent e){
		Player p=e.getPlayer();
		if(main.ters.isInPoints(p.getLocation())&&!p.getLocation().subtract(0, 0.5, 0).getBlock().getType().toString().contains("STAIRS")&&!p.getLocation().add(0, 1, 0).getBlock().getType().toString().contains("STAIRS")){
			e.setCancelled(true);
			TextUtil.mes(p, "&6Kingdom", "Ну что ты, как животное? Присядь и поешь, как человек!");
		}
		if(p.isInsideVehicle()){
			Entity en=p.getVehicle();
			if(en.getType().equals(EntityType.BOAT)||en.getType().equals(EntityType.MINECART)){
				e.setCancelled(false);
			}
		}
	}
	String[] ban={"STONE_BRICK","[CHEST","[WOODEN_PICKAXE","GOLDEN_","DIAMOND_","TNT"};
	@EventHandler
	public void preCraft(PrepareItemCraftEvent e){
		if(e.getInventory()==null)return;
		ItemStack item=e.getInventory().getResult();
		if(item==null)return;
		
		if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
			ItemMeta meta=item.getItemMeta();
			String name=meta.getDisplayName();
			if(name.contains("Замок")){
				ItemStack[] matrix = e.getInventory().getMatrix();
				ItemStack left=matrix[3];
				ItemStack right=matrix[5];
				if(GepUtil.loreContains(left, ChatColor.BLACK+"Key_id=")){
					if(right.getType().equals(Material.IRON_NUGGET)){
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.RED+"ЗАГОТОВКА.");
						lore.add(ChatColor.RED+"Требуется выплавить.");
						lore.add(ChatColor.BLACK+"Key_id="+GepUtil.intFromLore(left, ChatColor.BLACK+"Key_id="));
						meta.setLore(lore);
						meta.setDisplayName(ChatColor.AQUA+"Ключ");
						item.setItemMeta(meta);
						item.setType(Material.IRON_NUGGET);
						e.getInventory().setResult(item);
						return;
					}
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RED+"ЗАГОТОВКА.");
					lore.add(ChatColor.RED+"Требуется выплавить.");
					lore.add(ChatColor.BLACK+"Key_id="+GepUtil.intFromLore(left, ChatColor.BLACK+"Key_id="));
					meta.setLore(lore);
					item.setItemMeta(meta);
					e.getInventory().setResult(item);
				}else{
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RED+"ЗАГОТОВКА.");
					lore.add(ChatColor.RED+"Требуется выплавить.");
					lore.add(ChatColor.BLACK+"Key_id="+r.nextInt(1000000000));
					meta.setLore(lore);
					item.setItemMeta(meta);
					e.getInventory().setResult(item);
				}
			}else if(name.contains("Ключ")){
				ItemStack[] matrix = e.getInventory().getMatrix();
				ItemStack left=matrix[3];
				if(GepUtil.loreContains(left, ChatColor.BLACK+"Key_id=")){
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RED+"ЗАГОТОВКА.");
					lore.add(ChatColor.RED+"Требуется выплавить.");
					lore.add(ChatColor.BLACK+"Key_id="+GepUtil.intFromLore(left, ChatColor.BLACK+"Key_id="));
					meta.setLore(lore);
					item.setItemMeta(meta);
					e.getInventory().setResult(item);
				}else{
					e.getInventory().setResult(null);
				}
			}
			return;
		}
		if(item.getType().toString().contains("CHAINMAIL")){
			for(ItemStack ing:e.getInventory().getMatrix()){
				if(ing!=null&&ing.getType().equals(Material.IRON_NUGGET)){
					if(!GepUtil.loreContains(ing, ChatColor.GREEN+"Выковано.")){
						e.getInventory().setResult(null);
						break;
					}
				}
			}
			return;
		}
		if(item.getType().toString().contains("IRON")||item.getType().toString().contains("DIAMOND")){
			List<Material> ignores=new ArrayList<>();
			ignores.add(Material.IRON_INGOT);
			ignores.add(Material.IRON_NUGGET);
			if(ignores.contains(item.getType()))return;
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.RED+"ЗАГОТОВКА.");
			lore.add(ChatColor.RED+"Требуется выплавить.");
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
			e.getInventory().setResult(item);
		}else if(CraftBAN.contains(item.getType())){
			e.getInventory().setResult(null);
		}
		if(main.packs.containsMat(item.getType(), ban)){
			e.getInventory().setResult(null);
		}
	}
	
	
	
	@EventHandler
	public void craft(CraftItemEvent e){
		if(e.getInventory()==null)return;
		ItemStack item=e.getInventory().getResult();
		if(item==null)return;
		if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
			if(e.getClick().toString().contains("SHIFT")){
				e.setCancelled(true);
				return;
			}
			ItemMeta meta=item.getItemMeta();
			String name=meta.getDisplayName();
			if(name.contains("Замок")){
				ItemStack[] matrix = e.getInventory().getMatrix();
				ItemStack left=matrix[3];
				if(GepUtil.loreContains(left, ChatColor.BLACK+"Key_id=")){
					left.setAmount(left.getAmount()+1);
					return;
				}
				ItemStack keys=ItemUtil.create(Material.IRON_NUGGET, 1, ChatColor.AQUA+"Ключ", new String[]{
						ChatColor.RED+"ЗАГОТОВКА.",
						ChatColor.RED+"Требуется выплавить.",
						ChatColor.BLACK+"Key_id="+(GepUtil.intFromLore(item, ChatColor.BLACK+"Key_id="))
				}, null, 0);
				e.getWhoClicked().getInventory().addItem(keys);
			}else if(name.contains("Ключ")){
				ItemStack[] matrix = e.getInventory().getMatrix();
				ItemStack left=matrix[3];
				if(GepUtil.loreContains(left, ChatColor.BLACK+"Key_id=")){
					left.setAmount(left.getAmount()+1);
					return;
				}else{
					e.getInventory().setResult(null);
				}
			}
			return;
		}
	}
	
	@EventHandler
	public void armor(ArmorEquipEvent e){
		Player p=e.getPlayer();
		PlayerInfo pi=plist.get(p.getName());
		if(pi.fastTimers.containsKey("noArmEquip")){
			e.setCancelled(true);
			return;
		}
		ItemStack item=e.getNewArmorPiece();
		if(item!=null&&!item.getType().equals(Material.AIR)){
			if(GepUtil.loreContains(item, ChatColor.RED+"ЗАГОТОВКА.")){
				e.setCancelled(true);
				if(!e.getMethod().toString().contains("HOTBAR"))
					TextUtil.mes(p, "&6Kingdom", "Это даже не броня. Это лишь заготовка для того, чтобы выковать броню!");
			}
		}
		if(!e.isCancelled()){
			pi.fastTimers.put("noArmEquip", 20);
			new BukkitRunnable(){
				@Override
				public void run() {
					reCountSpeed(p);
				}
				
			}.runTaskLater(main.instance,1);
		}
	}
	
	void reCountSpeed(Player p){
		float speed=0.2f;//default speed
		for(int i=0;i<4;i++){
			ItemStack item=p.getInventory().getArmorContents()[i];
			if(item!=null){
				float daf=0;
				int coef=3;
				if(i==1||i==2)coef=8;
				else coef=4;
				if(item.getType().toString().contains("DIAMOND")||item.getType().toString().contains("IRON")){
					daf=0.05f;
				}else if(item.getType().toString().contains("CHAINMAIL")){
					daf=0.03f;
				}
				speed-=daf*(1.0/23*coef);
			}
		}
		p.setWalkSpeed(speed);
	}
	
	@EventHandler
	public void grow(BlockGrowEvent e){
		if(main.farms.grow(e)){
			e.setCancelled(true);
			return;
		}
		int m=(main.timer/7200)%4;
		double rd=r.nextDouble();
		if((m==1||m==3)&&rd<=0.5){
			e.setCancelled(true);
		}else if(m==2){
			Block b=e.getBlock();
			b.setType(Material.AIR);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void fade(BlockFadeEvent e){
		if(e.getBlock().getType().equals(Material.FARMLAND)){
			e.setCancelled(true);
		}
	}
	
	public static void say(Location from, String mes, double r){
		if(main.instance.mesType==0){
			GepUtil.globMessage(mes);
			return;
		}
		double space=0.995;
		for(Player pl:Bukkit.getOnlinePlayers()){
			Location point1=from.clone();
			Location point2=pl.getEyeLocation();
			World world =point1.getWorld();
			if(point1.distance(point2)<=r){
				if(main.instance.mesType==1){
					pl.sendMessage(mes);
					continue;
				}
			    Vector p1 = point1.toVector();
			    Vector p2 = point2.toVector();
			    Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
			    double length = 0;
			    int steps=0;
			    for (; length < r; p1.add(vector)) {
			    	steps++;
			    	if(p1.toLocation(world).distance(pl.getEyeLocation())<=1){
			    		pl.sendMessage(mes);
			    		break;
			    	}
			    	r--;
			    	Block b=p1.toLocation(world).getBlock();
			    	int influe=50;
			    	TextUtil.sdebug("type="+b.getType().toString());
			    	for(String st:chatInfluence.keySet()){
			    		if(b.getType().toString().contains(st)){
			    			influe=chatInfluence.get(st);
			    			break;
			    		}
			    	}
			    	if(influe>0)TextUtil.sdebug("-"+influe+"%, steps="+steps);
			        r*=(100-influe)*0.01;
			        length += space;
			    }
			}
		}
	}
	
	public void die(Player p){
		die(p, true);
	}
	
	public void die(Player p, boolean kick){
		if(main.instance.deathType==1){
			PlayerInfo pi=plist.get(p.getName());
			pi.realTimers.put("died", new Date().getTime());
			pi.kingdom=0;
			pi.pref=null;
			pi.food=10;
			p.setHealth(20);
			if(pi.timers.containsKey("PvP"))pi.timers.remove("PvP");
		}
		int onl=Bukkit.getOnlinePlayers().size()-1;
		for(ItemStack item:p.getInventory().getArmorContents()){
			if(item!=null&&!item.getType().equals(Material.AIR)){
				addBlood(item);
			}
		}
		for(ItemStack item:p.getInventory().getContents()){
			if(item!=null&&!item.getType().equals(Material.AIR)){
				p.getWorld().dropItemNaturally(p.getLocation(), item.clone());
			}
		}
		p.getInventory().clear();
		if(kick)new BukkitRunnable(){
			@Override
			public void run() {
				p.kickPlayer(TextUtil.string("&4&lВЫ УМЕРЛИ.\n&6Время вашего возрождения зависит от населения.\nСейчас онлайн &f"+onl+"&e, ждать примерно &6"+onl*2+" мин&e."));
			}
		}.runTaskLater(main.instance, 1);
	}
	@EventHandler
	public void regen(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player){
			if(e.getRegainReason().equals(RegainReason.SATIATED)){
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void hunger(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player){
			Player p=(Player) e.getEntity();
			PlayerInfo pi=plist.get(p.getName());
			int change=e.getFoodLevel()-p.getFoodLevel();
			if(change>0){
				pi.food+=change*0.5;
			}
			e.setFoodLevel((int) pi.food);
		}
	}
	
	public List<PlayerInfo> getAllPIs(){
		List<PlayerInfo> ret=new ArrayList<>();
		File fol=new File(main.instance.getDataFolder()+"/players");
		if(fol!=null)
		for(File f:new File(main.instance.getDataFolder()+"/players").listFiles()){
			ret.add(new PlayerInfo(f));
		}
		return ret;
	}
}
