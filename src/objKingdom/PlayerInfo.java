package objKingdom;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import Kingdom.Events;
import Kingdom.main;
import Religius.MyRel;
import UtilsKingdom.GepUtil;
import UtilsKingdom.TextUtil;
import katorga.Organizing;
import rooms.SlaveInfo;

public class PlayerInfo {
	public HashMap<String,Integer> timers=new HashMap<>();
	public HashMap<String,Integer> fastTimers=new HashMap<>();
	public List<String> bools=new ArrayList<>();
	public HashMap<String,Long> realTimers = new HashMap<>();
    public HashMap<String,Integer> waits = new HashMap<>();
    public String pname;
    public String pref;
    public int kingdom=0;
    public int regen=0;
    public double food=10;
    public String jailTo=null;
    public Block lastClickedBlock;
    public BlockFace lastBlockFace;
    public String ip=null;
    public UUID openedPack;
    public Location placeHandler=null;
    public int placedAfter=0;
    public UUID handBody=null;
    public SlaveInfo invS=null;
    public Organizing orgLabor=null;
    public MyRel rel=new MyRel();
    private HashMap<String, Object> tmps = new HashMap<>();
    /*
     * Skills.
     * Тип уровень и опыт.
     * И опыт со временем пропадает, а расскажут нам об этом, когда мы захотим заюзать наши скиллы...
     */
    public PlayerInfo(){
    	
    }
    public PlayerInfo(File file){
    	FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
    	pname=conf.getString("pname");
    	load(conf);
    }
    public PlayerInfo(String p){
    	pname=p;
    	File file = new File(main.instance.getDataFolder()+"/players/"+p+".yml");
    	FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
    	load(conf);
    }
    void load(FileConfiguration conf){
    	if(conf.contains("timers"))for(String st:conf.getConfigurationSection("timers").getKeys(false)){
			timers.put(st, conf.getInt("timers."+st));
		}
		if(conf.contains("fastTimers"))for(String st:conf.getConfigurationSection("fastTimers").getKeys(false)){
			fastTimers.put(st, conf.getInt("fastTimers."+st));
		}
		if(conf.contains("waits"))for(String st:conf.getConfigurationSection("waits").getKeys(false)){
			waits.put(st, conf.getInt("waits."+st));
		}
		if(conf.contains("bools"))bools=conf.getStringList("bools");
		if(conf.contains("realTimers"))for(String st:conf.getConfigurationSection("realTimers").getKeys(false)){
			realTimers.put(st,conf.getLong("realTimers."+st));
		}
		if(conf.contains("pref"))pref=conf.getString("pref");
		if(conf.contains("kingdom"))kingdom=conf.getInt("kingdom");
		if(conf.contains("food"))food=conf.getDouble("food");
    }
	public void save(){
		File file = new File(main.instance.getDataFolder()+"/players/"+pname+".yml");
    	FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.set("timers",null);
		for(String st:timers.keySet()){
			conf.set("timers."+st,timers.get(st));
		}
		conf.set("fastTimers",null);
		for(String st:fastTimers.keySet()){
			conf.set("fastTimers."+st,fastTimers.get(st));
		}
		conf.set("waits",null);
		for(String st:waits.keySet()){
			conf.set("waits."+st,waits.get(st));
		}
		conf.set("bools",bools);
		conf.set("realTimers",null);
		for(String st:realTimers.keySet()){
			conf.set("realTimers."+st,realTimers.get(st));
		}
		conf.set("pref", pref);
		conf.set("kingdom", kingdom);
		conf.set("food", food);
		conf.set("ip", ip);
		conf.set(pname, pname);
		GepUtil.saveCfg(conf, file);
	}
    public void setbool(String bool, boolean set){
    	if(set)if(!bools.contains(bool))bools.add(bool);
    	else if(bools.contains(bool))bools.remove(bool);
    }
    public void changeBool(String bool){
    	setbool(bool, !bools.contains(bool));
    }
    public int getWait(String key){
    	if(!waits.containsKey(key))return 0;
    	else return waits.get(key);
    }
    public void changeWait(String key, int change, boolean zero){
    	GepUtil.HashMapReplacer(waits, key, change, zero, false);
    }
    public String chatName(PlayerInfo otherPI){
    	String st="|";
    	if(pref!=null){
    		st=ChatColor.AQUA+""+kingdom+" &f"+pref+"| ";
    	}
    	return TextUtil.string(st)+pname;
    }
    public void updateListName(Player p){
		for(Player pl:Bukkit.getOnlinePlayers()){
			PlayerInfo pi=Events.plist.get(pl.getName());
			if(pi==null){
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+pl.getName()+" nopi");
				continue;
			}
			Scoreboard s=pl.getScoreboard();
			Team t = null;
			String prefix=ChatColor.GRAY+"";
	    	if(pref!=null){
	    		prefix=TextUtil.string(pref+"| ");
	    	}
			if(s.getTeam(prefix) == null){
				t = s.registerNewTeam(prefix);
			}
			else t = s.getTeam(prefix);
			t.setPrefix(prefix);
			t.addEntry(p.getName());
		}
		for(Player pl:Bukkit.getOnlinePlayers()){
			Scoreboard s=p.getScoreboard();
			PlayerInfo pi=Events.plist.get(pl.getName());
			if(pi==null){
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+pl.getName()+" nopi");
				continue;
			}
			Team t = null;
			String prefix=ChatColor.GRAY+"";
	    	if(pi.pref!=null){
	    		prefix=TextUtil.string(pi.pref+"| ");
	    	}
	    	if(s.getTeam(prefix) == null){
				t = s.registerNewTeam(prefix);
			}
			else t = s.getTeam(prefix);
			t.setPrefix(prefix);
			t.addEntry(pl.getName());
		}
    }
    public Object getTmp(String key){
    	if(tmps.containsKey(key))return tmps.get(key);
    	return null;
    }
    public void setTmp(String key, Object t){
    	if(tmps.containsKey(key))tmps.replace(key, t);
    	else tmps.put(key, t);
    }
    public boolean hasTmp(String key){
    	return tmps.containsKey(key);
    }
    public void setOrRemoveTmp(String key, Object t){
    	if(tmps.containsKey(key))tmps.remove(key);
    	else tmps.put(key, t);
    }
    public void remTmp(String key){
    	if(tmps.containsKey(key))tmps.remove(key);
    }
}
