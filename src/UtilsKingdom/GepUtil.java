package UtilsKingdom;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GepUtil {
	public static boolean HashMapReplacer(HashMap<String,Integer> hm, String key, int val, boolean zero, boolean set){
		if(hm.containsKey(key)){
			if(set)hm.replace(key, val);
			else hm.replace(key, hm.get(key)+val);
		}
		else{
			hm.put(key, val);
		}
		if(zero){
			if(hm.get(key)<=0){
				hm.remove(key);
				return true;
			}
		}
		return false;
	}
	public static boolean HashMapReplacer(HashMap<String,Double> hm, String key, double val, boolean zero, boolean set){
		if(hm.containsKey(key)){
			if(set)hm.replace(key, val);
			else hm.replace(key, hm.get(key)+val);
		}
		else{
			hm.put(key, val);
		}
		if(zero){
			if(hm.get(key)<=0){
				hm.remove(key);
				return true;
			}
		}
		return false;
	}
	public static boolean HashMapReplacer(HashMap<Location,Integer> hm, Location key, int val, boolean zero, boolean set){
		if(hm.containsKey(key)){
			if(set)hm.replace(key, val);
			else hm.replace(key, hm.get(key)+val);
		}
		else{
			hm.put(key, val);
		}
		if(zero){
			if(hm.get(key)<=0){
				hm.remove(key);
				return true;
			}
		}
		return false;
	}
	public static String chancesByCoef(String[] sts, int[] coefs){
		int coef=0;
		for(int d:coefs){
			coef+=d;
		}
		int r = new Random().nextInt(coef);
		int ch = 0;
		for(int i=0;i<sts.length;i++){
			if(r>=ch&&r<=ch+coefs[i]){
				return sts[i];
			}
			ch+=coefs[i];
		}
		return ""+r;
	}
	public static String chancesByCoef(HashMap<String, Integer> sts){
		int coef=0;
		for(int d:sts.values()){
			coef+=d;
		}
		int r = new Random().nextInt(coef);
		int ch = 0;
		for(String st:sts.keySet()){
			if(r>=ch&&r<ch+sts.get(st)){
				return st;
			}
			ch+=sts.get(st);
		}
		return ""+r;
	}
	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
	public static int intFromString(String st){
		String rets = "";
		int ret = 0;
		boolean negative=false;
		boolean ignore=false;
		for(int i=0;i<st.length();i++){
			if(!ignore&&isNumeric(st.charAt(i)+"")){
				rets = rets+st.charAt(i)+"";
			}
			if((st.charAt(i)+"").equals("-"))negative=true;
			if((st.charAt(i)+"").equals("§")||(st.charAt(i)+"").equals("&"))ignore=true;
			else ignore=false;
		}
		ret = Integer.parseInt(rets);
		if(negative)ret=-ret;
		return ret;
	}
	public static boolean loreContains(ItemStack item,String str){
		if(item==null)return false;
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasLore())return false;
		for(String st:item.getItemMeta().getLore()){
			if(st.contains(str)){
				return true;
			}
		}
		return false;
	}
	public static boolean haveItem(Player p, Material mat, int am){
		if(countItem(p, mat)>=am)return true;
		return false;
	}
	public static int countItem(Player p, Material mat){
		int am=0;
		for(ItemStack item:p.getInventory()){
			if(item!=null&&item.getType().equals(mat)){
				am+=item.getAmount();
			}
		}
		return am;
	}
	public static void sellItems(Player p, Material mat, String name, int am){
		for(ItemStack item:p.getInventory()){
			if(item!=null&&item.getType().equals(mat)){
				if(name!=null){
					if(!item.hasItemMeta()||item.getItemMeta().getDisplayName().contains(name))continue;
				}
				if(item.getAmount()<=am){
					am-=item.getAmount();
					item.setAmount(0);
				}
				else{
					item.setAmount(item.getAmount()-am);
					return;
				}
			}
		}
	}
	public static int intFromLore(ItemStack item,String str){
		String rets = "";
		int ret = 0;
		boolean negative=false;
		if(!item.hasItemMeta())return 0;
		if(!item.getItemMeta().hasLore())return 0;
		for(String st:item.getItemMeta().getLore()){
			if(st.contains(str)){
				boolean ignore=false;
				for(int i=0;i<st.length();i++){
					if(!ignore&&isNumeric(st.charAt(i)+"")){
						rets = rets+st.charAt(i)+"";
					}
					if((st.charAt(i)+"").equals("-"))negative=true;
					if((st.charAt(i)+"").equals("§")||(st.charAt(i)+"").equals("&"))ignore=true;
					else ignore=false;
				}
			}
		}
		if(!rets.equals(""))ret = Integer.parseInt(rets);
		if(negative)ret=-ret;
		return ret;
	}
	public static void globMessage(String mes, Sound sound, float vol, float speed, String title, String subtitle, int spawn, int hold, int remove){
		for(Player p:Bukkit.getOnlinePlayers()){
			if(mes!=null)p.sendMessage(mes);
			if(sound!=null){
				p.playSound(p.getLocation(), sound, vol, speed);
			}
			if(title!=null||subtitle!=null) {
				p.sendTitle(title, subtitle, spawn, hold, remove);
			}
		}
	}
	public static ChatColor boolCol(boolean arg){
		if(arg)return ChatColor.GREEN;
		else return ChatColor.RED;
	}
	public static ChatColor boolCol(ChatColor Tcolor, ChatColor Fcolor, boolean arg){
		if(arg)return Tcolor;
		else return Fcolor;
	}
	public static void debug(String message, String whoCaused, String type){
		String prefix = ChatColor.GRAY+"[DEBUG";
		if(whoCaused!=null)prefix+="(from "+ChatColor.YELLOW+whoCaused+ChatColor.GRAY+")";
		prefix+="]";
		if(type.equals("error"))prefix+=ChatColor.RED;
		if(type.equals("info"))prefix+=ChatColor.AQUA;
		if(Bukkit.getPlayer("Gepiroy")!=null){
			Bukkit.getPlayer("Gepiroy").sendMessage(prefix+message);
		}
		Bukkit.getConsoleSender().sendMessage(prefix+message);
	}
	public static boolean chance(int ch){
		return new Random().nextInt(100)+1<=ch;
	}
	public static boolean chance(double ch){
		return new Random().nextDouble()<=ch;
	}
	public static String chances(String[] sts, double[] chs){
		double r = new Random().nextInt(100)+new Random().nextDouble();
		double ch = 0.000;
		for(int i=0;i<sts.length;i++){
			if(r>ch&&r<=ch+chs[i]){
				return sts[i];
			}
			ch+=chs[i];
		}
		return "";
	}
	public static boolean itemName(ItemStack item, String name) {
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasDisplayName())return false;
		if(item.getItemMeta().getDisplayName().equals(name))return true;
		return false;
	}
	public static boolean isFullyItem(ItemStack item, String name, Material mat){
		if(item==null)return false;
		if(mat!=null&&!item.getType().equals(mat))return false;
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasDisplayName())return false;
		if(!item.getItemMeta().hasLore())return false;
		if(name!=null&&!item.getItemMeta().getDisplayName().equals(name))return false;
		return true;
	}
	public static ArrayList<String> stringToArrayList(String st){
		ArrayList<String> ret = new ArrayList<>();
		String toadd = "";
		for(int i=0;i<st.length();i++){
			String c = st.charAt(i)+"";
			if(!c.equals(";")){
				toadd=toadd+c;
			}
			else{
				ret.add(toadd);
				toadd="";
			}
		}
		return ret;
	}
	public static String ArrayListToString(ArrayList<String> ara){
		String ret = "";
		for(String st:ara){
			ret = ret+st+";";
		}
		return ret;
	}
	public static void saveCfg(FileConfiguration conf, File file) {
	    try {
	        conf.save(file);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public static void saveLocToConf(FileConfiguration conf, String st, Location loc){
		if(loc==null){
			conf.set(st, null);
			return;
		}
		conf.set(st+".world",loc.getWorld().getName());
		conf.set(st+".x",loc.getX());
		conf.set(st+".y",loc.getY());
		conf.set(st+".z",loc.getZ());
	}
	public static Location getLocFromConf(FileConfiguration conf, String st){
		if(!conf.contains(st)){
			debug("no loc "+st+" in config!",null,"error");
			return null;
		}
		return new Location(Bukkit.getWorld(conf.getString(st+".world")),conf.getDouble(st+".x"),conf.getDouble(st+".y"),conf.getDouble(st+".z"));
	}
	public static String toTime(int i){
		return i/60+":"+i%60;
	}
	public static String CylDouble(double d, String cyl){
		return new DecimalFormat(cyl).format(d).replaceAll(",", ".");
	}
	public static void globMessage(String mes) {
		globMessage(mes, null, 0, 0, null, null, 0, 0, 0);
	}
	public static String maxFromHM(HashMap<String,Integer> hm){
		String ret="";
		int max=Integer.MIN_VALUE;
		for(String st:hm.keySet()){
			if(hm.get(st)>max){
				max=hm.get(st);
				ret=st;
			}
		}
		return ret;
	}
	public static String intToRoman(int number){
        String romanValue = "";
        int N = number;
        int numbers[]  = {1, 4, 5, 9, 10, 50, 100, 500, 1000 };
        String letters[]  = { "I", "IV", "V", "IX", "X", "L", "C", "D", "M"};
        while ( N > 0 ){
        for (int i = 0; i < numbers.length; i++){
        if ( N < numbers[i] ){
        N -= numbers[i-1];
        romanValue += letters[i-1];
        break;
        }
        }
        }
        return romanValue;
    }
	public static int letterToNumber(String letter){
        if(letter.equals("I") )
            return 1;
        else if(letter.equals("II"))
            return 2;
        else if(letter.equals("III"))
            return 3;
        else if(letter.equals("IV"))
            return 4;
        else if(letter.equals("V"))
            return 5;
        else if(letter.equals("IX"))
            return 9;
        else if(letter.equals("X"))
            return 10;
        else if(letter.equals("L"))
            return 50;
        else if(letter.equals("C"))
            return 100;
        else if(letter.equals("D"))
            return 500;
        else if(letter.equals("M"))
            return 1000;
        else return -1;
    }
	public static Vector throwTo(Location from, Location to, double power){
	    Vector p1 = from.toVector();
	    Vector p2 = to.toVector();
	    Vector vector = p2.clone().subtract(p1).normalize().multiply(power);
		return vector;
	}
	public static void give(Player p, ItemStack item){
		HashMap<Integer, ItemStack> drop = p.getInventory().addItem(item);
		for(Integer key:drop.keySet()){
			Item dropped = p.getWorld().dropItem(p.getLocation(), drop.get(key));
			dropped.setPickupDelay(0);
		}
	}
	public static Player nearestPlayer(Location l, double md){
		Player ret=null;
		double n=md;
		for(Player p:Bukkit.getOnlinePlayers()){
			if(p.getLocation().distance(l)<n){
				ret=p;
				n=p.getLocation().distance(l);
			}
		}
		return ret;
	}
}