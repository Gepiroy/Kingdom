package UtilsKingdom;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Kingdom.main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {
	public static final String PP=ChatColor.LIGHT_PURPLE+"ОВ";
	public static String twoCols(String st, ChatColor c1, ChatColor c2, int iter, String addict){
		String RAID="";
		for(int i=0;i<st.length();i++){
			char c=st.charAt(i);
			RAID+=GepUtil.boolCol(c1, c2, (iter+i)%2==0)+addict+c;
		}
		return RAID;
	}
	public static String days(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" дней";
		if(m%10==1)return m+" день";
		if(m%10>=2&&m%10<=4)return m+" дня";
		return m+" ч.";
	}
	public static String hours(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" часов";
		if(m%10==1)return m+" час";
		if(m%10>=2&&m%10<=4)return m+" часа";
		return m+" ч.";
	}
	public static String minutes(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" минут";
		if(m%10==1)return m+" минута";
		if(m%10>=2&&m%10<=4)return m+" минуты";
		return m+" мин.";
	}
	public static String secundes(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" секунд";
		if(m%10==1)return m+" секунда";
		if(m%10>=2&&m%10<=4)return m+" секунды";
		return m+" сек.";
	}
	public static String minutesto(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" минут";
		if(m%10==1)return m+" минуту";
		if(m%10>=2&&m%10<=4)return m+" минуты";
		return m+" мин.";
	}
	public static String secundesto(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" секунд";
		if(m%10==1)return m+" секунду";
		if(m%10>=2&&m%10<=4)return m+" секунды";
		return m+" сек.";
	}
	public static String times(int sec){
		String ret="";
		if(sec>0)ret=secundes(sec%60);
		if(sec/60>0)ret=minutes(sec%3600/60)+" "+ret;
		if(sec/3600>0)ret=hours(sec%(3600*24)/3600)+" "+ret;
		if(sec/(3600*24)>0)ret=days(sec/(3600*24))+" "+ret;
		return ret;
	}
	public static String string(ChatColor defCol, String text){
		return string(defCol+"", text);
	}
	public static String string(String text){
		return string(ChatColor.GRAY+"", text);
	}
	public static void mes(CommandSender p, String pref, String mes){
		if(pref==null)p.sendMessage(string(mes));
		else p.sendMessage(string("["+pref+"|] &f"+mes));
	}
	public static void globMessage(String pref, String mes){
		GepUtil.globMessage(string("["+pref+"|] &f"+mes));
	}
	public static void globMessage(String prefix, String mes, Sound sound, float vol, float speed, String title, String subtitle, int spawn, int hold, int remove){
		String chat=string(ChatColor.GRAY, "["+prefix+"|] &f"+mes);
		String tit=null;
		if(title!=null)tit=string(title);
		String sub=null;
		if(subtitle!=null)sub=string(subtitle);
		for(Player p:Bukkit.getOnlinePlayers()){
			if(mes!=null)p.sendMessage(chat);
			if(sound!=null){
				p.playSound(p.getLocation(), sound, vol, speed);
			}
			if(title!=null||subtitle!=null) {
				p.sendTitle(tit, sub, spawn, hold, remove);
			}
		}
	}
	public static void persGlob(Player p, String prefix, String mes, Sound sound, float vol, float speed, String title, String subtitle, int spawn, int hold, int remove){
		String tit=null;
		if(title!=null)tit=string(title);
		String sub=null;
		if(subtitle!=null)sub=string(subtitle);
		if(mes!=null){
			String chat=string(ChatColor.GRAY, "["+prefix+"|] &f"+mes);
			p.sendMessage(chat);
		}
		if(sound!=null){
			p.playSound(p.getLocation(), sound, vol, speed);
		}
		if(title!=null||subtitle!=null) {
			p.sendTitle(tit, sub, spawn, hold, remove);
		}
	}
	public static void Title(Player p, String up, String down, int spawn, int hold, int out){
		p.sendTitle(string(up), string(down), spawn, hold, out);
	}
	public static void globTitle(String up, String down, int spawn, int hold, int out){
		for(Player p:Bukkit.getOnlinePlayers()){
			p.sendTitle(string(up), string(down), spawn, hold, out);
		}
	}
	public static String string(String defCol, String text){
		String ret="";
		for(int i=0;i<text.length();i++){
			char c=text.charAt(i);
			if((c=='|'))ret+=defCol;
			else if((c=='$'))ret+=ChatColor.DARK_GREEN+"$"+defCol;
			else if((c=='~')){
				if((text.charAt(i+1)+"").equals("A"))ret+=ChatColor.BLUE+"~A~"+defCol;
				else if((text.charAt(i+1)+"").equals("P"))ret+=ChatColor.LIGHT_PURPLE+"ОВ"+defCol;
				else{
					ret+=c;
					ret+=text.charAt(i+1);
				}
				i++;
			}else if((c=='&'))ret+="§";
			else ret+=c;
		}
		return ret;
	}
	public static String toDate(long time){
		return toDate(time, "yyyy-MM-dd HH:mm:ss");
	}
	public static String toDate(long time, String type){
		Date when=new Date(time);
		DateFormat TIMESTAMP = new SimpleDateFormat(type);
		return TIMESTAMP.format(when);
	}
	static boolean debug=true;
	public static void debug(String st){
		if(debug)Bukkit.getConsoleSender().sendMessage(string("deb from &6"+main.instance.getName()+"&f: "+st));
	}
	public static void sdebug(String st){
		Bukkit.getConsoleSender().sendMessage(string("deb from &6"+main.instance.getName()+"&f: "+st));
		for(Player p:Bukkit.getOnlinePlayers()){
			if(p.isOp())p.sendMessage(string("deb from &6"+main.instance.getName()+"&f: "+st));
		}
	}
	public double equal(String s1, String s2, boolean ignoreCase){
		int sovp=0;
		for(int i=s1.length();i>0;i--){
			if(!ignoreCase&&s1.charAt(i)==s2.charAt(i))sovp++;
			else if(ignoreCase&&(s1.charAt(i)+"").equalsIgnoreCase(s2.charAt(i)+""))sovp++;
		}
		return 1.0*s1.length()/sovp;
	}
	public static char boolc(char c1, char c2, boolean bool){
		if(bool)return c1;
		else return c2;
	}
	public static String cylDouble(double d, String cyl){
		return new DecimalFormat(cyl).format(d).replace(",", ".");
	}
	
	static List<Double> doublesFromString(String st){
		List<Double> ret=new ArrayList<>();
		Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
		Matcher matcher=pat.matcher(st);
		while (matcher.find()) {
			ret.add(Double.parseDouble(matcher.group()));
		}
		return ret;
	}
	
	static List<Integer> intsFromString(String st){
		List<Integer> ret=new ArrayList<>();
		Pattern pat=Pattern.compile("[-]?[0-9]+");
		Matcher matcher=pat.matcher(st);
		while (matcher.find()) {
			ret.add(Integer.parseInt(matcher.group()));
		}
		return ret;
	}
	
	//Helpful when need to get value from item or other lore.
	//9 workers worked last 8 nights and maked 98 papers! ret=98.
	public static int lastIntFromString(String st){
		List<Integer> pret = intsFromString(st);
		if(pret.size()==0)return 0;
		return pret.get(pret.size()-1);
	}
	public static double lastDoubleFromString(String st){
		List<Double> pret = doublesFromString(st);
		if(pret.size()==0)return 0;
		return pret.get(pret.size()-1);
	}
	public static List<String> multiLore(List<String> l1, List<String> l2){
		List<String> ret=new ArrayList<>();
		double maxPerLine=0;
		for(String st:l1){
			double syms=length(st);
			if(syms>maxPerLine)maxPerLine=syms;
		}
		int i=0;
		for(;i<l1.size();i++){
			String st=new String(l1.get(i));
			double l=length(st);
			while(l<=maxPerLine){
				st+=" ";
				l++;
			}
			if(i<l2.size())st+=l2.get(i);
			ret.add(st);
		}
		for(;i<l2.size();i++){
			String st="";
			int l=0;
			while(l<=maxPerLine){
				st+=" ";
				l++;
			}
			st+=l2.get(i);
			ret.add(st);
		}
		return ret;
	}
	static double length(String st){
		double syms=st.length();
		for(int i = 0; i < st.length(); i++) {
			char c=st.charAt(i);
		    if(c=='&') syms-=2;
		    else if(c=='('||c==')'||c==':'||c=='.'||c==','||c==';'||c=='|') syms-=0.5;
		}
		return syms;
	}
	public static String boolst(String st, String sf, boolean bool){
		if(bool)return st;
		return sf;
	}
	public static void actionBar(Player p, String st){
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.translateAlternateColorCodes('&', st)));
	}
	public static List<String> split(String st, int maxPerLine){
		List<String> ret = new ArrayList<>();
		String[] sts = st.split(" ");
		char lastColor='f';
		String tmp="";
		int line=0;
		for(String s:sts){
			for(int i=0;i<s.length();i++){
				if(s.charAt(i)=='&'&&s.length()<=i+2){
					lastColor=s.charAt(i+1);
					i++;
				}
			}
			if(line+s.length()>maxPerLine){
				line=0;
				ret.add(new String(tmp));
				tmp="&"+lastColor;
			}
			if(line!=0)tmp+=" ";
			line+=s.length();
			tmp+=s;
		}
		if(tmp.length()>0)ret.add(new String(tmp));
		return ret;
	}
}
