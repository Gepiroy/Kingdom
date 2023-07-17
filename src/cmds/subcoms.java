package cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class subcoms {
	public List<subcom> subs=new ArrayList<>();
	public String pref;
	public subcoms(){
		
	}
	
	public boolean writed(CommandSender sender, String arg, int args){
		subcom s=get(arg);
		if(s==null){
			sender.sendMessage("Такой команды нет.");
			return false;
		}
		if(args<s.minArgs()){
			s.mes(sender, pref, args);
			return false;
		}
		return true;
	}
	
	subcom get(String arg){
		for(subcom s:subs){
			if(s.arg.equals(arg))return s;
		}
		return null;
	}
}
