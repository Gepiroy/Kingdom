package cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Kingdom.main;
import UtilsKingdom.TextUtil;

public class kdedit implements CommandExecutor{

	subcoms subs;
	
	public kdedit(){
		subs=new subcoms();
		subs.pref="/kdedit &a";
		subs.subs.add(new subcom("ters", new String[]{"<reset>"},"�������� ��� ��������� ����������."));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//there are a lot of unhandled things, because this commands is just for me.
		if(!sender.isOp()){
			TextUtil.mes(sender, "&c����!", "&f��� ������� ��� &b���&c���&b��&e :)");
			return true;
		}
		//Player p=null;//���� ������ �����, ��� �����.
		//if(sender instanceof Player){
		//	p=(Player) sender;
		//}
		
		if(args.length>0){
			if(subs.writed(sender, args[0], args.length-1)){
				if(args[0].equals("ters")){
					if(args[1].equals("reset")){
						main.ters.points.clear();
						main.ters.save(true);
						TextUtil.mes(sender, "&6���������", "&f��������.");
					}
				}
			}
		}else{
			sender.sendMessage("������ ������:");
			for(subcom s:subs.subs){
				s.mes(sender, subs.pref);
			}
		}
		
		return true;
	}
	
}
