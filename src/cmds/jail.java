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
			TextUtil.mes(p, "&6Kingdom", "Помощь по командам для лишения свободы.");
			p.sendMessage(TextUtil.string("&a/jail |<Имя> &f- связать человеку руки и вести. &dУсловия&f: он должен быть очень близко к вам, пригнуться и смотреть вниз."));
			p.sendMessage(TextUtil.string("&a/jail |<Имя> &einventory &f- открыть инвентарь связанного человека, чтобы отобрать у него вещи."));
			p.sendMessage(TextUtil.string("&a/jail |<Имя> &estand &f- развязать человека. |(Он не сможет ничего ломать, но сможет бить и взаимодействовать с миром.)"));
			p.sendMessage(TextUtil.string("&a/jail |<Имя> &efree &f- освободить человека. |(&eПОЛНОСТЬЮ!|)"));
		}else{
			Player pl=Bukkit.getPlayer(args[0]);
			if(pl==null){
				TextUtil.mes(p, "&6Kingdom", "Нет на земле живого человека с таким именем.");
				return true;
			}
			if(pl.getLocation().distance(p.getLocation())>2){
				TextUtil.mes(p, "&6Kingdom", "Слишком далеко.");
				return true;
			}
			PlayerInfo pli=Events.plist.get(args[0]);
			if(args.length==1){
				boolean sneak=pl.isSneaking();
				boolean head=pl.getLocation().getPitch()>=80;
				if(!sneak||!head){
					TextUtil.mes(p, "&6Kingdom", "&6"+pl.getName()+" &fдолжен "+GepUtil.boolCol(sneak)+"пригнуться &fи "+GepUtil.boolCol(head)+"смотреть в пол&f.");
					return true;
				}
				pli.setbool("jailed", true);
				pli.jailTo=p.getName();
				TextUtil.Title(pl, "&cВы связаны.", "|Теперь вы во власти &c"+p.getName()+"|.", 20, 30, 20);
				TextUtil.mes(p, "&6Kingdom", "Вы связали &6"+pl.getName());
				pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
			}else{
				if(!pli.bools.contains("jailed")||pi.bools.contains("jailed")){
					TextUtil.mes(p, "&6Kingdom", "Вы не можете сделать этого, т. к. вы связаны, либо не связан "+pl.getName()+".");
					return true;
				}
				if(args[1].equals("free")){
					pli.bools.remove("jailed");
					TextUtil.Title(pl, "&aВы свободны!", "&bВас развязал &2"+p.getName()+"&b.", 20, 30, 20);
					TextUtil.mes(p, "&6Kingdom", "Вы освободили &6"+pl.getName());
					pli.jailTo=null;
					pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
				}
				else if(args[1].equals("stand")){
					TextUtil.Title(pl, "&6Вы отвязаны.", "&eВы можете всё, кроме ломания блоков.", 20, 50, 20);
					TextUtil.mes(p, "&6Kingdom", "Вы отвязали &6"+pl.getName());
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
