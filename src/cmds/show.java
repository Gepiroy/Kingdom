package cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import Kingdom.Events;
import UtilsKingdom.TextUtil;

public class show implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=(Player) sender;
		if(args.length==0){
			TextUtil.mes(p, "&6Kingdom", "&a/show &e<Имя> &f- показать свой инвентарь. |(Вещи забрать не смогут)");
		}else{
			Player pl=Bukkit.getPlayer(args[0]);
			if(pl==null){
				TextUtil.mes(p, "&6Kingdom", "Нет на земле живого человека с таким именем.");
				return true;
			}
			if(p.getLocation().distance(pl.getLocation())>3){
				TextUtil.mes(p, "&6Kingdom", "Ваши карманы не видны издалека. Может быть, стоит подойти по-ближе?");
				return true;
			}
			Inventory inv=Bukkit.createInventory(null, 45, ChatColor.YELLOW+"Инвентарь игрока "+ChatColor.WHITE+p.getName());
			inv.setItem(0, p.getInventory().getHelmet());
			inv.setItem(1, p.getInventory().getChestplate());
			inv.setItem(2, p.getInventory().getLeggings());
			inv.setItem(3, p.getInventory().getBoots());
			inv.setItem(7, p.getInventory().getItemInOffHand());
			for(int i=0;i<9;i++){
				inv.setItem(35+i, p.getInventory().getItem(i));//hotbar
			}
			for(int i=9;i<36;i++){
				inv.setItem(i, p.getInventory().getItem(i));//inv
			}
			pl.openInventory(inv);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_HORSE_ARMOR, 1, 0);
			Events.say(p.getEyeLocation(), ChatColor.DARK_GRAY+p.getName()+" показывает карманы "+pl.getName()+".", 10);
		}
		return true;
	}
	
}
