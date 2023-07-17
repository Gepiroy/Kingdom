package cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import UtilsKingdom.TextUtil;

public class m implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=(Player) sender;
		if(args.length<2){
			TextUtil.mes(p, "&6Kingdom", "&a/m &e<���> <���������> &f- ��������� ������ ������ ����� ����� ��������.");
		}else{
			Player pl=Bukkit.getPlayer(args[0]);
			if(pl==null){
				TextUtil.mes(p, "&6Kingdom", "��� �� ����� ������ �������� � ����� ������.");
				return true;
			}
			String mes=args[1];
			for(int i=2;i<args.length;i++){
				mes+=" "+args[i];
			}
			pl.sendMessage(TextUtil.string("&b������ &f�� &e"+p.getDisplayName()+"&f: ")+mes);
			p.sendMessage(TextUtil.string("&b������ &f��� &e"+pl.getDisplayName()+"&f: ")+mes);
		}
		return true;
	}
	
}
