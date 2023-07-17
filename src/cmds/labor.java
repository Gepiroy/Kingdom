package cmds;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import Kingdom.main;
import Raids.Raid;
import Raids.RaidMain;
import Raids.Raider;
import UtilsKingdom.ItemUtil;
import invsUtil.InvEvents;
import invsUtil.Invs;
import objKingdom.ArmorStandItem;
import rooms.ProfPrisoner;
import rooms.SlaveInfo;
import rooms.Slaves;

public class labor implements CommandExecutor{
	
	Random r=main.r;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=(Player) sender;
		if(args.length==1){
			SlaveInfo sinf=Slaves.createNewPrisoner(p.getLocation().getBlock().getLocation().add(0.5, 0.1, 0.5), p.getName());
			ProfPrisoner pris=(ProfPrisoner) sinf.live;
			pris.recruit=100;
			return true;
		}
		if(args.length==2){
			Raid raid=new Raid(p.getLocation());
			for(int i=0;i<3;i++){
				ZombieVillager z=main.prisons.spawnNMS(p.getLocation());
				z.setCustomName(ChatColor.GOLD+"Дикарь");
				z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
				z.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(300);
				//z.setRemoveWhenFarAway(false); already
				if(main.r.nextDouble()<=0.3){
					z.getEquipment().setItemInMainHand(ItemUtil.create(Material.WOODEN_SWORD, 1, ChatColor.GOLD+"Дикарская дубина", null, null, 0));
				}
				Raider rer=new Raider(z.getUniqueId());
				raid.raiders.add(rer);
			}
			RaidMain.raids.add(raid);
			return true;
		}
		if(args.length==3){
			//Item item = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.STONE));
			//item.setGravity(false);
			//item.setPersistent(true);
			//item.setTicksLived(-999999);
			for(int i=0;i<10;i++){
				new ArmorStandItem(p.getLocation(), false);
			}
			for(int i=0;i<10;i++){
				new ArmorStandItem(p.getLocation().add(3, 1, 0), true);
			}
			return true;
		}
		Invs.open(p, InvEvents.Labors);
		return true;
	}
}
