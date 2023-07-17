package cmds;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Kingdom.Events;
import UtilsKingdom.GepUtil;
import UtilsKingdom.TextUtil;
import objKingdom.PlayerInfo;

public class jail implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=(Player) sender;
		PlayerInfo pi=Events.plist.get(p.getName());
		if(args.length==0){
			TextUtil.mes(p, "&6Kingdom", "������ �� �������� ��� ������� �������.");
			p.sendMessage(TextUtil.string("&a/jail |<���> &f- ������� �������� ���� � �����. &d�������&f: �� ������ ���� ����� ������ � ���, ���������� � �������� ����."));
			p.sendMessage(TextUtil.string("&a/jail |<���> &einventory &f- ������� ��������� ���������� ��������, ����� �������� � ���� ����."));
			p.sendMessage(TextUtil.string("&a/jail |<���> &estand &f- ��������� ��������. |(�� �� ������ ������ ������, �� ������ ���� � ����������������� � �����.)"));
			p.sendMessage(TextUtil.string("&a/jail |<���> &efree &f- ���������� ��������. |(&e���������!|)"));
		}else{
			Player pl=Bukkit.getPlayer(args[0]);
			if(pl==null){
				TextUtil.mes(p, "&6Kingdom", "��� �� ����� ������ �������� � ����� ������.");
				return true;
			}
			if(pl.getLocation().distance(p.getLocation())>2){
				TextUtil.mes(p, "&6Kingdom", "������� ������.");
				return true;
			}
			PlayerInfo pli=Events.plist.get(args[0]);
			if(args.length==1){
				boolean sneak=pl.isSneaking();
				boolean head=pl.getLocation().getPitch()>=80;
				if(!sneak||!head){
					TextUtil.mes(p, "&6Kingdom", "&6"+pl.getName()+" &f������ "+GepUtil.boolCol(sneak)+"���������� &f� "+GepUtil.boolCol(head)+"�������� � ���&f.");
					return true;
				}
				pli.setbool("jailed", true);
				pli.jailTo=p.getName();
				TextUtil.Title(pl, "&c�� �������.", "|������ �� �� ������ &c"+p.getName()+"|.", 20, 30, 20);
				TextUtil.mes(p, "&6Kingdom", "�� ������� &6"+pl.getName());
				pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
			}else{
				if(!pli.bools.contains("jailed")||pi.bools.contains("jailed")){
					TextUtil.mes(p, "&6Kingdom", "�� �� ������ ������� �����, �. �. �� �������, ���� �� ������ "+pl.getName()+".");
					return true;
				}
				if(args[1].equals("free")){
					pli.bools.remove("jailed");
					TextUtil.Title(pl, "&a�� ��������!", "&b��� �������� &2"+p.getName()+"&b.", 20, 30, 20);
					TextUtil.mes(p, "&6Kingdom", "�� ���������� &6"+pl.getName());
					pli.jailTo=null;
					pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
				}
				else if(args[1].equals("stand")){
					TextUtil.Title(pl, "&6�� ��������.", "&e�� ������ ��, ����� ������� ������.", 20, 50, 20);
					TextUtil.mes(p, "&6Kingdom", "�� �������� &6"+pl.getName());
					pli.jailTo=null;
					pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
				}else if(args[1].equals("inventory")){
					p.openInventory(pl.getInventory());
				}
			}
		}
		return true;
	}
}
