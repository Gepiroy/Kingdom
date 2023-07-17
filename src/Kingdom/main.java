package Kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import Farming.FarmMain;
import HandlyRecipes.HRManager;
import Hunting.HuntingManager;
import PackUtil.PackMain;
import Raids.RaidMain;
import Schems.sMain;
import cmds.tag;
import objKingdom.Conf;
import objKingdom.PlayerInfo;
import objKingdom.Waiting;
import rooms.PrisonMain;
import territories.TerPoint;
import territories.terMain;
import UtilsKingdom.GeomUtil;
import UtilsKingdom.GepUtil;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import World2D.World2;
import beam.PrepareBeam;

public class main extends JavaPlugin{
	public static main instance;
	public static int timer=0;
	public static Random r=new Random();
	public static tag tag;
	public Conf glob;
	public int deathType=1;//0-момент, 1-минута/онлайн, 2-hardcore.
	public int mesType=1;//0-весь мир, дефолт, 1-тупо радиус, 2-стены.
	public static Events events=new Events();
	public static PackMain packs;
	public static terMain ters;
	public static EntityMain ents;
	public HashMap<String,Waiting> waitings=new HashMap<>();
	public static sMain schs;
	public static PrisonMain prisons;
	public static FarmMain farms;
	
	public void onEnable(){
		instance = this;
		packs=new PackMain();
		ters=new terMain();
		packs.loadPacks();
		ents=new EntityMain();
		schs=new sMain();
		prisons=new PrisonMain();
		prisons.load();
		farms=new FarmMain();
		HuntingManager.init();
		new PlayerInfo();
		tag=new tag();
		new GepUtil();
		new ItemUtil();
		new TextUtil();
		World2.load();
		HRManager.enable();
		Bukkit.getPluginCommand("tag").setExecutor(tag);
		Bukkit.getPluginCommand("king").setExecutor(new cmds.king());
		Bukkit.getPluginCommand("show").setExecutor(new cmds.show());
		Bukkit.getPluginCommand("m").setExecutor(new cmds.m());
		Bukkit.getPluginCommand("jail").setExecutor(new cmds.jail());
		Bukkit.getPluginCommand("kdedit").setExecutor(new cmds.kdedit());
		Bukkit.getPluginCommand("labor").setExecutor(new cmds.labor());
		Bukkit.getPluginCommand("krprules").setExecutor(new cmds.KRPrules());
		Bukkit.getPluginManager().registerEvents(events, this);
		Bukkit.getPluginManager().registerEvents(packs, this);
		Bukkit.getWorld("world").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		events.enable();
		for(Player p:Bukkit.getOnlinePlayers()){
			Events.doJoin(p);
		}
		glob=new Conf(getDataFolder()+"/global.yml");
		loadGlob();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			int secRate=0;
			public void run(){
				secRate++;
				schs.tick();
				prisons.tick();
				farms.tick();
				if(secRate>=20){
					secRate=0;
					for(Player p:Bukkit.getOnlinePlayers()){
						PlayerInfo pi=Events.plist.get(p.getName());
						if(!pi.timers.containsKey("sciGUI")){
							pi.timers.put("sciGUI", 5);
						}
						for(String st:new ArrayList<>(pi.timers.keySet())){
							if(GepUtil.HashMapReplacer(pi.timers, st, -1, true, false)){
								if(st.equals("ghost")){
									p.setGameMode(GameMode.SURVIVAL);
									p.teleport(kingdomSpawn(1));
								}
								if(st.equals("PvP")){
									TextUtil.Title(p, "&c&lPvP &aoff!", "&bМожно ливать.", 10, 15, 10);
								}
							}
						}
						if(pi.timers.containsKey("ghost")){
							p.sendTitle(TextUtil.string("|Вы - призрак."), TextUtil.string("|Вы появитесь в новом обличии через &f"+pi.timers.get("ghost")+"| сек."), 0, 25, 10);
						}
						ItemStack hitem=p.getInventory().getItemInMainHand();
						if(GepUtil.loreContains(hitem, ChatColor.RED+"Следы крови")){
							p.getWorld().playSound(p.getLocation(), Sound.AMBIENT_UNDERWATER_EXIT, 1, 1);
							if(r.nextDouble()<=0.1){
								ItemMeta meta=hitem.getItemMeta();
								List<String> lore=new ArrayList<>();
								for(String st:meta.getLore()){
									if(st.contains(ChatColor.RED+"Следы крови"))continue;
									lore.add(st);
								}
								meta.setLore(lore);
								hitem.setItemMeta(meta);
								TextUtil.mes(p, "&6Kingdom", "Кровь отмыта.");
							}
						}
						if(p.getGameMode().equals(GameMode.SURVIVAL))pi.food-=0.006;
						if(p.getFoodLevel()!=(int)pi.food)p.setFoodLevel((int) pi.food);
						if(timer%10==0)pi.updateListName(p);
					}
					if(timer%720==0){//1 day (12 mins)
						int dayNum=timer/720;
						if(dayNum%10==0){//1 mounth (10 days (120 mins (2 hours)))
							int mounthNum=dayNum/10;
							if(mounthNum%4==0){
								TextUtil.globMessage("&6Kingdom", "Наступает &aЛЕТО&f! Самое лучшее время для фермерства!", Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 0, "&aЛето &fнаступило!", "Лучшее время для фермерства!", 30, 30, 50);
								//Bukkit.getWorld("world").setGameRule(GameRule.RANDOM_TICK_SPEED, 2);
							}else if(mounthNum%4==1){
								TextUtil.globMessage("&6Kingdom", "Наступает &6ОСЕНЬ&f! Скорость роста упала.", Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 0, "&6Осень &fнаступила!", "Фермерство ослабевает...", 30, 30, 50);
								//Bukkit.getWorld("world").setGameRule(GameRule.RANDOM_TICK_SPEED, 1);
							}else if(mounthNum%4==2){
								TextUtil.globMessage("&6Kingdom", "Наступает &bЗИМА&f! Ничего не растёт.", Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 0, "&aЗима &fнаступила!", "Ничего не растёт!", 30, 30, 50);
								//Bukkit.getWorld("world").setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
							}else if(mounthNum%4==3){
								TextUtil.globMessage("&6Kingdom", "Наступает &aВЕСНА&f! Пора позаботиться о фермах.", Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 0, "&aВесна &fнаступила!", "Хоть небольшая скорость роста...", 30, 30, 50);
								//Bukkit.getWorld("world").setGameRule(GameRule.RANDOM_TICK_SPEED, 1);
							}
						}
						saveGlob();
					}
					timer++;
					RaidMain.sec();
				}
				ters.tick();
				
				for(Player p:Bukkit.getOnlinePlayers()){
					PlayerInfo pi=Events.plist.get(p.getName());
					if(pi.jailTo!=null){
						Player jailer=Bukkit.getPlayer(pi.jailTo);
						if(jailer==null){
							pi.jailTo=null;
							TextUtil.mes(p, "&6Kingdom", "Связавший вас человек пропал. Вы можете убежать, но вам нужно развязать руки. Это сможет сделать любой свободный человек. |/jail "+p.getName()+" free");
						}else{
							if(p.getLocation().distance(jailer.getLocation())>4){
								p.setVelocity(GepUtil.throwTo(p.getLocation(), jailer.getLocation().add(0, 0.1, 0), 0.4));
							}
						}
					}
					ItemStack chest=p.getInventory().getChestplate();
					if(chest!=null&&(chest.getType().equals(Material.IRON_CHESTPLATE)||chest.getType().equals(Material.DIAMOND_CHESTPLATE))){
						for(Player pl:Bukkit.getOnlinePlayers()){
							double dist=pl.getLocation().distance(p.getLocation());
							if(dist<=1&&dist>0){
								pl.setVelocity(GepUtil.throwTo(p.getLocation(), pl.getLocation(), 0.2));
							}
						}
					}
					for(String st:new ArrayList<>(pi.fastTimers.keySet())){
						if(GepUtil.HashMapReplacer(pi.fastTimers, st, -1, true, false)){
							if(st.equals("forning")){
								if(r.nextDouble()<0.25){
									ItemStack hitem=p.getInventory().getItemInMainHand();
									if(GepUtil.loreContains(hitem, ChatColor.GOLD+"Требуется выковать.")){
										ItemMeta meta=hitem.getItemMeta();
										List<String> lore=new ArrayList<>();
										for(String s:meta.getLore()){
											if(s.contains(ChatColor.GOLD+"Требуется выковать."))continue;
											else if(s.contains(ChatColor.RED+"ЗАГОТОВКА."))lore.add(ChatColor.GREEN+"Выковано.");
											else lore.add(s);
										}
										meta.setLore(lore);
										hitem.setItemMeta(meta);
										p.sendTitle(ChatColor.GREEN+"Выковано!", "", 10, 20, 20);
									}else{
										TextUtil.mes(p, "&6Kingdom", "В вашей руке некуемый предмет.");
									}
								}else{
									p.sendTitle(ChatColor.GRAY+"Ещё подковать...", "", 10, 20, 20);
								}
							}
						}
					}
					/*if(!pi.fastTimers.containsKey("ghostPunch")){
						if(pi.timers.containsKey("ghost")&&!p.getLocation().getBlock().getType().equals(Material.AIR)){
							Vector v=p.getVelocity();
							v.setX(v.getX()*-30);
							v.setY(v.getY()+0.1);
							p.setVelocity(v);
							pi.fastTimers.put("ghostPunch", 2);
						}
					}*/
					if(pi.food>=10)pi.regen++;
					if(pi.food>=18)pi.regen++;
					if(p.getLocation().getBlock().getType().toString().contains("BED")){
						pi.regen++;
					}
					if(pi.regen>=60){
						pi.regen-=60;
						if(pi.timers.containsKey("hurted")||p.isDead())continue;
						double maxhp=p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
						if(p.getHealth()<maxhp){
							double hp=p.getHealth();
							hp+=1;
							if(hp>maxhp)hp=maxhp;
							p.setHealth(hp);
							pi.food-=0.05;
						}
					}
				}
				if(secRate%2==0){//Мини-оптимизаша :3
					World world=Bukkit.getWorld("world");
					int time=(int) world.getTime()%24000;
					if(time>12000)world.setTime(time+8);
					else world.setTime(time+1);
					if(time==11000){
						TextUtil.globMessage("&6Kingdom", "&2Темнеет&f... Надо подумать о &bсне&f...");
					}
					if(time==12000){
						TextUtil.globMessage("&6Kingdom", "&2Ночь&f. &bПоспите&f, чтобы избежать &cбессонницы&f.");
						saveEveryDay();
					}
					if(time==30){
						for(Player p:Bukkit.getOnlinePlayers()){
							PlayerInfo pi=Events.plist.get(p.getName());
							if(pi.bools.contains("slept")){
								TextUtil.mes(p, "&6Kingdom", "Этой ночью вы поспали и не получили усталости.");
								pi.bools.remove("slept");
							}else{
								p.sendTitle(ChatColor.BLUE+"Этой ночью вы не спали.", ChatColor.GRAY+"Сегодня вы чувствуете усталость.", 20, 40, 60);
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 2400, 0));
							}
						}
					}
					//----------------------------------------------//
					for(String st:new ArrayList<>(waitings.keySet())){
						Player p=Bukkit.getPlayer(st);
						if(p==null){//END by leave
							waitings.remove(st);
							continue;
						}
						PlayerInfo pi=Events.plist.get(p.getName());
						Waiting w=waitings.get(st);
						if(w.dist>0){
							double dist=p.getEyeLocation().distance(w.loc);
							if(dist>w.dist){//END by dist
								if(w.outMes!=null)TextUtil.mes(p, "&6Kingdom", w.outMes);
								remWaiting(st, w.Ptimer, pi);
								continue;
							}
						}
						w.timer--;
						//TICK-fails
						if(w.type.equals("setFire")||w.type.equals("StrippingLog")){
							if(!w.loc.getBlock().getType().equals(Material.AIR)){
								remWaiting(st, w.Ptimer, pi);
								continue;
							}
						}
						if(w.timer<=0){//END by timer
							if(w.type.equals("setFire")){
								p.getWorld().playSound(w.loc, Sound.ITEM_FLINTANDSTEEL_USE, 2, 0);
								if(w.loc.getBlock().getType().equals(Material.AIR))w.loc.getBlock().setType(Material.FIRE);
							}else if(w.type.equals("StrippingLog")){
								p.getWorld().playSound(w.loc, Sound.ENTITY_SHEEP_SHEAR, 2, 0);
								Material mat=Material.getMaterial("STRIPPED_"+w.loc.getBlock().getType());
								if(mat!=null)w.loc.getBlock().setType(mat);
							}
							remWaiting(st, w.Ptimer, pi);
							continue;
						}//TICK
						if(w.type.equals("setFire")){
							if(r.nextDouble()<=0.2){
								if(r.nextDouble()<0.1){
									p.getWorld().playSound(w.loc, Sound.ITEM_FLINTANDSTEEL_USE, 2, 0);
									w.loc.getBlock().setType(Material.FIRE);
									remWaiting(st, w.Ptimer, pi);
								}else{
									p.getWorld().playSound(w.loc, Sound.ITEM_FLINTANDSTEEL_USE, 2, 2);
									w.loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, w.loc, r.nextInt(3)+1, 0.1, 0.1, 0.1, 0.125);
								}
							}
						}else if(w.type.equals("StrippingLog")){
							if(w.timer%10==0){
								p.getWorld().playSound(w.loc, Sound.ENTITY_SILVERFISH_AMBIENT, 1.5f, 2);
								BlockData bd = w.loc.getBlock().getType().createBlockData();
								p.getWorld().spawnParticle(Particle.BLOCK_CRACK, w.loc, r.nextInt(3)+1, 0.25, 0.25, 0.25, 0.125, bd);
							}
						}
					}
				}
				if(secRate%2==0){
					HuntingManager.tick();
					for(Player p:Bukkit.getOnlinePlayers()){
						/*Item it=events.ItemLooking(p);
						if(it!=null){
							ItemStack hitem=p.getInventory().getItemInMainHand();
							if(hitem.getType().equals(Material.AIR)||InventoryUtil.canStack(it.getItemStack(), hitem, true)){
								if(it!=null){
									p.spawnParticle(Particle.CRIT_MAGIC, it.getLocation().add(0,0.25,0), 1, 0, 0, 0, 0);
								}
							}
						}*/
						PlayerInfo pi=Events.plist.get(p.getName());
						if(pi.getTmp("beam")!=null){
							PrepareBeam pb=(PrepareBeam) pi.getTmp("beam");
							pb.update();
						}
						if(pi.getTmp("dragging")!=null){
							UUID id=(UUID) pi.getTmp("dragging");
							Entity en=Bukkit.getEntity(id);
							if(en==null){
								pi.remTmp("dragging");
							}else{
								Location mustBe=en.getLocation();
								double step=0.1;
								Location to=p.getLocation().add(0, 0.2, 0);
								Vector dir=GeomUtil.direction(to, mustBe).normalize().multiply(step);
								double dist=mustBe.distance(to)-2;
								if(dist<=0)continue;
								double power=dist;
								for(int i=0;i<dist/step;i++){
									Material mat=mustBe.getBlock().getType();
									if(mat.getHardness()!=0)power-=mat.getHardness();
									if(power<=0)break;
									mustBe.add(dir);
									p.spawnParticle(Particle.VILLAGER_HAPPY, mustBe, 0, 0, 0, 0, 0);
								}
								en.teleport(mustBe);
							}
						}
					}
				}
			}
		}, 1, 1);
		
		addRecipe(ItemUtil.create(Material.IRON_INGOT, 1, ChatColor.AQUA+"Замок", null, null, 0),
				new Material[]{null,null,null,
						Material.IRON_INGOT,null,Material.IRON_NUGGET,
						null,null,null});
		addRecipe(ItemUtil.create(Material.IRON_INGOT, 1, ChatColor.AQUA+"Замок", null, null, 0),
				new Material[]{null,null,null,
						Material.IRON_INGOT,null,Material.IRON_INGOT,
						null,null,null});
		addRecipe(ItemUtil.create(Material.IRON_NUGGET, 1, ChatColor.AQUA+"Ключ", null, null, 0),
				new Material[]{null,null,null,
						Material.IRON_NUGGET,null,Material.IRON_NUGGET,
						null,null,null});
		addRecipe(ItemUtil.create(Material.IRON_NUGGET, 1, ChatColor.AQUA+"Кольца кольчуги", new String[]{ChatColor.RED+"ЗАГОТОВКА.",ChatColor.RED+"Требуется выплавить."}, null, 0),
				new Material[]{Material.IRON_NUGGET,Material.IRON_NUGGET,Material.IRON_NUGGET,
						Material.IRON_NUGGET,null,Material.IRON_NUGGET,
						Material.IRON_NUGGET,Material.IRON_NUGGET,Material.IRON_NUGGET});
		{
			ShapedRecipe rc = new ShapedRecipe(new NamespacedKey(instance, UUID.randomUUID().toString()), new ItemStack(Material.CHAINMAIL_HELMET));
	        rc.shape("111", "101");
	        rc.setIngredient('1', Material.IRON_NUGGET);
	        Bukkit.getServer().addRecipe(rc);
		}{
			ShapedRecipe rc = new ShapedRecipe(new NamespacedKey(instance, UUID.randomUUID().toString()), new ItemStack(Material.CHAINMAIL_CHESTPLATE));
	        rc.shape("101", "111", "111");
	        rc.setIngredient('1', Material.IRON_NUGGET);
	        Bukkit.getServer().addRecipe(rc);
		}{
			ShapedRecipe rc = new ShapedRecipe(new NamespacedKey(instance, UUID.randomUUID().toString()), new ItemStack(Material.CHAINMAIL_LEGGINGS));
	        rc.shape("111", "101", "101");
	        rc.setIngredient('1', Material.IRON_NUGGET);
	        Bukkit.getServer().addRecipe(rc);
		}{
			ShapedRecipe rc = new ShapedRecipe(new NamespacedKey(instance, UUID.randomUUID().toString()), new ItemStack(Material.CHAINMAIL_BOOTS));
	        rc.shape("101", "101");
	        rc.setIngredient('1', Material.IRON_NUGGET);
	        Bukkit.getServer().addRecipe(rc);
		}
	}
	
	public boolean addWaiting(String st, Waiting w, boolean replace){
		if(waitings.containsKey(st)){
			if(replace){
				waitings.replace(st, w);
			}
			else{
				return false;
			}
		}else waitings.put(st, w);
		return true;
	}
	
	public void remWaiting(String st, String timer, PlayerInfo pi){
		waitings.remove(st);
		if(timer!=null&&pi!=null&&pi.timers.containsKey(timer))pi.timers.remove(timer);
	}
	
	void addRecipe(ItemStack stack, Material[] istack) {
        ShapedRecipe rc = new ShapedRecipe(new NamespacedKey(instance, UUID.randomUUID().toString()), stack);
        rc.shape("012", "345", "678");
        for (int i = 0; i < 9; i++) {
            if (istack[i] != null && istack[i] != Material.AIR)
                rc.setIngredient(String.valueOf(i).toCharArray()[0], istack[i]);
        }
        Bukkit.getServer().addRecipe(rc);
    }
	public void saveGlob(){
		glob.conf.set("timer", timer);
		glob.conf.set("Doors", null);
		int i=0;
		for(Location l:events.doors.keySet()){
			glob.setLoc("Doors."+i+".loc", l);
			glob.conf.set("Doors."+i+".key", events.doors.get(l));
			i++;
		}
		glob.save();
	}
	public void loadGlob(){
		timer=glob.conf.getInt("timer");
		for(String st:glob.getKeys("Doors")){
			events.doors.put(glob.getLoc("Doors."+st+".loc"), glob.conf.getString("Doors."+st+".key"));
		}
	}
	public void onDisable(){
		for(Player p:Bukkit.getOnlinePlayers()){
			Events.doLeave(p);
		}
		saveGlob();
		packs.savePacks();
		ters.save(false);
		prisons.disable();
		farms.save();
		World2.save();
	}
	void saveEveryDay(){
		new BukkitRunnable() {
			@Override
			public void run() {
				packs.savePacks();
				ters.save(false);
			}
		}.runTaskAsynchronously(this);
	}
	public static boolean isKing(Player p){
		return GepUtil.isFullyItem(p.getInventory().getHelmet(), ChatColor.GOLD+"Королевская корона", Material.GOLDEN_HELMET);
	}
	public Location kingdomSpawn(int num){
		return Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.5, 0.5);
	}
	
	public Location findLocToSpawn(World w, Player p){
		if(ters.points.size()==0){
			for(int i=0;i<10;i++){
				Location loc=Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.5, 0.5);
				loc.add(r.nextInt(512)-256, 0, r.nextInt(512)-256);
				loc=ters.FromSky(loc);
				if(isPointReadyToSpawnPlayer(loc, p))return loc;
			}
			return null;
		}
		for(TerPoint tp:ters.points){
			Location loc=tp.getCenter(w);
			int dx=r.nextInt(101)-50;
			if(dx>0)dx+=75;
			else dx-=75;
			int dz=r.nextInt(101)-50;
			if(dz>0)dz+=100;
			else dz-=100;
			loc.add(dx, 0, dz);
			loc=ters.FromSky(loc);
			if(isPointReadyToSpawnPlayer(loc, p))return loc;
		}
		return null;
	}
	boolean isPointReadyToSpawnPlayer(Location l, Player p){
		TextUtil.debug("&ex="+l.getBlockX()+", y="+l.getBlockY()+", z="+l.getBlockZ());
		if(!l.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.GRASS_BLOCK)){
			TextUtil.debug("&cItsNotGrassBlock, its &e"+l.clone().subtract(0, 1, 0).getBlock().getType());
			return false;
		}
		
		TerPoint tp=ters.nearestPoint(l);
		double tpdist=tp.getCenter(l.getWorld()).distance(l);
		if(tp!=null&&tpdist>75&&tpdist<150){
			TextUtil.debug("&cDistToNearestCenter="+(int)tp.getCenter(l.getWorld()).distance(l));
			return false;
		}
		Player np=nearestPlayer(l, new String[]{p.getName()});
		double npdist=np.getLocation().distance(l);
		if(np!=null&&npdist>75&&npdist<150){
			TextUtil.debug("&cDistToNearestPlayer="+(int)np.getLocation().distance(l));
			return false;
		}
		
		return true;
	}
	public Player nearestPlayer(Location l, String[] ignores){
		double nd=10000;
		Player ret=null;
		for(Player p:Bukkit.getOnlinePlayers()){
			if(ignores!=null)for(String n:ignores)if(p.getName().equals(n))continue;//fast-check
			double dist=p.getLocation().distance(l);
			if(dist<nd){
				nd=dist;
				ret=p;
			}
		}
		return ret;
	}
}
