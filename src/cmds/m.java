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
			TextUtil.mes(p, "&6Kingdom", "&a/m &e<Имя> <Сообщение> &f- отправить письмо птицей лично этому человеку.");
		}else{
			Player pl=Bukkit.getPlayer(args[0]);
			if(pl==null){
				TextUtil.mes(p, "&6Kingdom", "Нет на земле живого человека с таким именем.");
				return true;
			}
			String mes=args[1];
			for(int i=2;i<args.length;i++){
				mes+=" "+args[i];
			}
			pl.sendMessage(TextUtil.string("&bПисьмо &fот &e"+p.getDisplayName()+"&f: ")+mes);
			p.sendMessage(TextUtil.string("&bПисьмо &fдля &e"+pl.getDisplayName()+"&f: ")+mes);
		}
		return true;
	}
	
}
