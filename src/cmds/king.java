package cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.GepUtil;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import objKingdom.PlayerInfo;

public class king implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(/*sender instanceof Player&&*/!sender.getName().equals("BorzoyNoob")&&!sender.getName().equals("Gepiroy")){
			TextUtil.mes(sender, "&6Kingdom", "Новые короли появляются лишь по воле всевышнего!");
			return true;
		}
		Player p=Bukkit.getPlayer(args[0]);
		if(p==null){
			TextUtil.mes(sender, "&6Kingdom", "Player is not online.");
			return true;
		}
		PlayerInfo pi=Events.plist.get(p.getName());
		int crowns=1+main.instance.glob.conf.getInt("crowns");
		main.instance.glob.conf.set("crowns", crowns);
		main.instance.glob.save();
		ItemStack crown=ItemUtil.createTool(Material.GOLDEN_HELMET, ChatColor.GOLD+"Королевская корона", new String[]{
				TextUtil.string("Надетая корона наделяет вас"),
				TextUtil.string("властью &6короля&f королевства &b№"+(crowns)),
		}, Enchantment.PROTECTION_ENVIRONMENTAL, 10);
		TextUtil.globMessage("&6Kingdom", "&4"+args[0]+" &eстал &6королём &eкоролевства &b№"+crowns, Sound.ENTITY_WITHER_DEATH, 10, 0, "&6&lНОВЫЙ КОРОЛЬ!", "&4"+args[0]+"&e возглавил королевство &b№"+crowns, 50, 50, 50);
		for(Player pl:Bukkit.getOnlinePlayers()){
			double dist=pl.getLocation().distance(p.getLocation());
			if(dist<=15&&dist>0){
				TextUtil.sdebug("pl="+pl);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 10));
				pl.setVelocity(pl.getVelocity().add(GepUtil.throwTo(p.getLocation(), pl.getLocation().add(0, 0.2, 0), (15-dist)*0.2)));
			}
		}
		pi.kingdom=crowns;
		pi.pref="&6&lКОРОЛЬ";
		p.getInventory().setHelmet(crown);
		p.getWorld().strikeLightning(p.getLocation());
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 50, 1));
		pi.updateListName(p);
		return true;
	}
	
}
