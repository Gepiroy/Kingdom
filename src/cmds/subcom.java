package cmds;

import org.bukkit.command.CommandSender;

import UtilsKingdom.TextUtil;

public class subcom {
	public String arg="setroom";//Сам первый аргумент
	public String tip="Установить [|игроку&f] кабинет.";//Подсказка
	public String[] args={"<Id>","[Player]"};//Цвета подбираются автоматически.
	
	public subcom(String arg, String[] args, String tip){
		this.arg=arg;
		this.args=args;
		this.tip=tip;
	}
	
	public void mes(CommandSender sender, String pref){
		String mes=pref+arg;
		for(String arg:args){
			mes+=" ";
			for(int i=0;i<arg.length();i++){
				char c=arg.charAt(i);
				if(c=='<')mes+="&6<&f";
				else if(c=='>')mes+="&6>";
				else if(c=='[')mes+="&f[|";
				else if(c==']')mes+="&f]";
				else mes+=c;
			}
		}
		mes+=" &f- "+tip;
		sender.sendMessage(TextUtil.string(mes));
	}
	
	public void mes(CommandSender sender, String pref, int aram){
		String mes=pref+arg;
		int num=0;
		for(String arg:args){
			mes+=" ";
			for(int i=0;i<arg.length();i++){
				char c=arg.charAt(i);
				if(c=='<')mes+="&"+TextUtil.boolc('a', 'c', num<aram)+"<&f";
				else if(c=='>')mes+="&"+TextUtil.boolc('a', 'c', num<aram)+">";
				else if(c=='[')mes+="&f[|";
				else if(c==']')mes+="&f]";
				else mes+=c;
			}
			num++;
		}
		sender.sendMessage(TextUtil.string(mes));
	}
	
	public int minArgs(){
		int ret=0;
		for(String arg:args){
			if(arg.charAt(0)=='<')ret++;
			else break;
		}
		return ret;
	}
}
