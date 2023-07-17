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
				TextUtil.mes(p, "&6Kingdom", "��������� ������ ������:");
				p.sendMessage(TextUtil.string("&a/tag &6set &e<���> <������> &f- ���������� �������� ������."));
				if(isKing)p.sendMessage(TextUtil.string("  �� - &6������&f, � ������ ��������� &b���� ������&f. ��� �� �� ������ �������� &b���&f ������."));
				else p.sendMessage(TextUtil.string("  �� ������ �������� ������ �� ������, ������� � ����� ������ (������ � �����)."));
				p.sendMessage(TextUtil.string("&a/tag &6clear &f- ���������� �� ������ ������."));
				p.sendMessage(TextUtil.string("&a/tag &6clear &e<���> &f- �������� �������� ������."));
				if(isKing)p.sendMessage(TextUtil.string("  �� - &6������&f, � ������ �������� &b����� ������&f."));
				else p.sendMessage(TextUtil.string("  �� ������ �������� ������ �� ������, ������� � ����� ������ (������ � �����)."));
				if(isKing){
					p.sendMessage(TextUtil.string(" �� - &6������&f, � �� ������ &b����������� &f������:"));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<������1> &6add &e<������2> &f- ���� ����� �� &3�������1 &f����������� �������� � �������� &e������2&f ������ �����, �� ��������� � ���."));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<������1> &6remove &e<������2> &f- ������� � ����� �� &3�������1 &f����������� �������� � �������� &e������2&f ������ �����, �� ��������� � ���."));
					p.sendMessage(TextUtil.string("&a/tag &bedit &3<������> &6list &f- �������� ������ ������, ������� ������������� ���� � &3���� �������&f."));
				}else{
					if(pi.pref==null){
						TextUtil.mes(p, "&6Kingdom", "� ��� ��� ������, ��� � ����������.");
						return true;
					}
					List<String> zvs=tagsOfTag(pi.kingdom, pi.pref);
					if(zvs.size()>0){
						TextUtil.mes(p, "&6Kingdom", "������ ���������� ������ "+pi.pref+" &f:");
						for(String st:zvs){
							p.sendMessage("- "+st);
						}
					}else{
						TextUtil.mes(p, "&6Kingdom", "� ������ ������ ��� ����������.");
					}
				}
			}else{
				if(args[0].equalsIgnoreCase("set")&&args.length>=3){
					Player pl=Bukkit.getPlayer(args[1]);
					if(pl==null||p.getGameMode().equals(GameMode.SPECTATOR)){
						TextUtil.mes(p, "&6Kingdom", "��� �� ����� ������ �������� � ����� ������.");
						return true;
					}
					if(p.getLocation().distance(pl.getLocation())>20&&!isKing){
						TextUtil.mes(p, "&6Kingdom", "�� ������� ������.");
						return true;
					}
					if(isKing||canInflue(p, pl, args[2])){
						TextUtil.mes(p, "&6Kingdom", "|"+pl.getName()+" ������ &f"+args[2]+"|.");
						setTag(pl, pi.kingdom, args[2]);
					}
				}
				else if(args[0].equalsIgnoreCase("edit")&&args.length>=3&&isKing){
					// /tag edit &d�������� add/remove/list &e�������������
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
							TextUtil.mes(p, "&6Kingdom", "�� ����� |�����&f.");
							p.sendTitle(ChatColor.RED+"���������", ChatColor.GRAY+"�� ���������� �� ������ ������.", 10, 20, 20);
							pi.updateListName(p);
						}else{
							TextUtil.mes(p, "&6Kingdom", "� ��� � ��� ��� ������.");
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
		p.sendTitle(ChatColor.AQUA+"���� ������ ��������!", "������ �� "+tag, 20, 50, 20);
		p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);
		pi.updateListName(p);
		return tag;
	}
	boolean canInflue(Player p, Player pl, String tag){
		PlayerInfo pi=Events.plist.get(p.getName());
		PlayerInfo pli=Events.plist.get(pl.getName());
		if(pi.pref==null){
			TextUtil.mes(p, "&6Kingdom", "� ��� ��� ������, ��� � ����������.");
			return false;
		}
		if(pli.pref!=null&&!tagsOfTag(pi.kingdom,pli.pref).contains(tag)){
			TextUtil.mes(p, "&6Kingdom", "������ ����� ������ ������������ ���.");
			return false;
		}
		if(!tagsOfTag(pi.kingdom,pi.pref).contains(tag)){
			TextUtil.mes(p, "&6Kingdom", "� ��� ��� ���������� ��������/�������� ��� ������.");
			return false;
		}
		return true;
	}
	public List<String> tagsOfTag(int kingdom, String tag){
		return conf.getStringList(kingdom+"."+tag+".tags");
	}
}
