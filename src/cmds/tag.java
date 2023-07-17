package cmds;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;
import objKingdom.PlayerInfo;

public class tag implements CommandExecutor{
	
	public Conf conf;
	
	public tag(){
		conf=new Conf(main.instance.getDataFolder()+"/tags.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p=(Player) sender;
			PlayerInfo pi=Events.plist.get(p.getName());
			boolean isKing=main.isKing(p);
			if(args.length==0){
				TextUtil.mes(p, "&6Kingdom", "Изменение званий игрока:");
				p.sendMessage(TextUtil.string("&a/tag &6set &e<Имя> <Звание> &f- установить человеку звание."));
				if(isKing)p.sendMessage(TextUtil.string("  Вы - &6король&f, и можете создавать &bсвои звания&f. Так же вы можете изменить &bсвоё&f звание."));
				else p.sendMessage(TextUtil.string("  Вы можете выдавать только те звания, которые в вашей власти (список в конце)."));
				p.sendMessage(TextUtil.string("&a/tag &6clear &f- отказаться от своего звания."));
				p.sendMessage(TextUtil.string("&a/tag &6clear &e<Имя> &f- отозвать человеку звание."));
				if(isKing)p.sendMessage(TextUtil.string("  Вы - &6король&f, и можете отзывать &bлюбые звания&f."));
				else p.sendMessage(TextUtil.string("  Вы можете отзывать только те звания, которые в вашей власти (список в конце)."));
				if(isKing){
					p.sendMessage(TextUtil.string(" Вы - &6король&f, и вы можете &bнастраивать &fзвания:"));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<Звание1> &6add &e<Звание2> &f- дать людям со &3званием1 &fвозможность выдавать и отзывать &eзвание2&f другим людям, не спрашивая у вас."));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<Звание1> &6remove &e<Звание2> &f- забрать у людей со &3званием1 &fвозможность выдавать и отзывать &eзвание2&f другим людям, не спрашивая у вас."));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<Звание> &6list &f- просмотр списка званий, которые контроллируют люди с &3этим званием&f."));
				}else{
					if(pi.pref==null){
						TextUtil.mes(p, "&6Kingdom", "У вас нет звания, как и полномочий.");
						return true;
					}
					List<String> zvs=tagsOfTag(pi.kingdom, pi.pref);
					if(zvs.size()>0){
						TextUtil.mes(p, "&6Kingdom", "Список полномочий звания "+pi.pref+" &f:");
						for(String st:zvs){
							p.sendMessage("- "+st);
						}
					}else{
						TextUtil.mes(p, "&6Kingdom", "У вашего звания нет полномочий.");
					}
				}
			}else{
				if(args[0].equalsIgnoreCase("set")&&args.length>=3){
					Player pl=Bukkit.getPlayer(args[1]);
					if(pl==null||p.getGameMode().equals(GameMode.SPECTATOR)){
						TextUtil.mes(p, "&6Kingdom", "Нет на земле живого человека с таким именем.");
						return true;
					}
					if(p.getLocation().distance(pl.getLocation())>20&&!isKing){
						TextUtil.mes(p, "&6Kingdom", "Вы слишком далеко.");
						return true;
					}
					if(isKing||canInflue(p, pl, args[2])){
						TextUtil.mes(p, "&6Kingdom", "|"+pl.getName()+" теперь &f"+args[2]+"|.");
						setTag(pl, pi.kingdom, args[2]);
					}
				}
				else if(args[0].equalsIgnoreCase("edit")&&args.length>=3&&isKing){
					// /tag edit &dКардинал add/remove/list &eПоследователь
					if(args.length==3){
						for(String st:tagsOfTag(pi.kingdom, args[1])){
							p.sendMessage("- "+st);
						}
						return true;
					}
					List<String> tags=tagsOfTag(pi.kingdom, args[1]);
					if(args[2].equals("add"))tags.add(args[3]);
					else if(args[2].equals("remove"))tags.remove(args[3]);
					conf.conf.set(pi.kingdom+"."+args[1]+".tags",tags);
					conf.save();
				}else if(args[0].equalsIgnoreCase("clear")){
					if(args.length==1){
						if(pi.pref!=null){
							pi.pref=null;
							pi.kingdom=0;
							TextUtil.mes(p, "&6Kingdom", "Вы стали |никем&f.");
							p.sendTitle(ChatColor.RED+"Обнуление", ChatColor.GRAY+"Вы отказались от своего звания.", 10, 20, 20);
							pi.updateListName(p);
						}else{
							TextUtil.mes(p, "&6Kingdom", "У вас и так нет звания.");
						}
					}
				}
			}
		}
		return true;
	}
	String setTag(Player p, int kingdom, String tag){
		PlayerInfo pi=Events.plist.get(p.getName());
		pi.kingdom=kingdom;
		if(tag.length()>13)tag=tag.substring(0, 13);
		pi.pref=tag;
		p.sendTitle(ChatColor.AQUA+"Ваше звание изменено!", "Теперь вы "+tag, 20, 50, 20);
		p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);
		pi.updateListName(p);
		return tag;
	}
	boolean canInflue(Player p, Player pl, String tag){
		PlayerInfo pi=Events.plist.get(p.getName());
		PlayerInfo pli=Events.plist.get(pl.getName());
		if(pi.pref==null){
			TextUtil.mes(p, "&6Kingdom", "У вас нет звания, как и полномочий.");
			return false;
		}
		if(pli.pref!=null&&!tagsOfTag(pi.kingdom,pli.pref).contains(tag)){
			TextUtil.mes(p, "&6Kingdom", "Звание этого игрока неподвластно вам.");
			return false;
		}
		if(!tagsOfTag(pi.kingdom,pi.pref).contains(tag)){
			TextUtil.mes(p, "&6Kingdom", "У вас нет полномочий выдавать/забирать это звание.");
			return false;
		}
		return true;
	}
	public List<String> tagsOfTag(int kingdom, String tag){
		return conf.getStringList(kingdom+"."+tag+".tags");
	}
}
