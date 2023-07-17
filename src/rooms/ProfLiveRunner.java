package rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import Kingdom.main;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import katorga.Decree;
import katorga.KatInfo;
import katorga.Katorga;
import objKingdom.Conf;

public class ProfLiveRunner extends ProfSlave{
	//Посол-раб, находящийся в мире игроков.
	
	public UUID kid;
	public List<String> messages=new ArrayList<>();
	
	public List<Decree> decrees = new ArrayList<>();
	
	public boolean dropToStorage=true;
	
	public ProfLiveRunner(){
		
	}
	
	@Override
	public void save(Conf conf, String st) {
		conf.set(st+".type", "ProfLiveRunner");
		conf.set(st+".id", mobId.toString());
		conf.set(st+".kid", kid.toString());
		conf.set(st+".messages", messages);
	}

	@Override
	public void load(Conf conf, String st) {
		super.load(conf, st);
		kid=conf.getUUID(st+".kid");
		messages=conf.getStringList(st+".messages");
	}
	
	public void GUIa(Player p, SlaveInfo sinf){
		Inventory inv=Bukkit.createInventory(null, 27, ChatColor.GOLD+"Гонец");
		{
			List<String> lore=new ArrayList<>();
			if(messages!=null&&messages.size()>0){
				for(String st:messages){
					lore.add(ChatColor.translateAlternateColorCodes('&', st));
				}
			}else{
				lore.add(ChatColor.GRAY+"Сообщений нет.");
			}
			inv.setItem(0, ItemUtil.create(Material.PAPER, 1, ChatColor.BLUE+"Сообщения", lore, null, 0));
		}
		inv.setItem(1, ItemUtil.create(Material.CHEST, 1, ChatColor.GOLD+"Груз", new String[]{
				ChatColor.AQUA+""+sinf.invAm()+ChatColor.WHITE+" предметов",
				ChatColor.GREEN+""+sinf.invFoodAm()+ChatColor.WHITE+" ед. пищи"}, null, 0));
		Katorga kat=main.prisons.kats.get(kid);
		{
			List<String> lore=new ArrayList<>();
			if(kat!=null){
				if(kat.members.size()>0){
					lore.add("&eЧлены каторги:");
					for(SlaveInfo osinf:kat.members.keySet()){
						KatInfo kinf=kat.members.get(osinf);
						if(osinf!=null){
							if(kinf.isHere)lore.add("&6"+osinf.name+" &f(&e"+kinf.role+"&f)");
							else lore.add("&8"+osinf.name+" &f(&8"+kinf.role+"&f)");
						}
					}
				}else{
					lore.add("&cНа каторге нет людей.");
				}
			}
			inv.setItem(3, ItemUtil.create(Material.CAMPFIRE, 1, ChatColor.BLUE+"Информация о каторге", lore, null, 0));
		}{
			List<String> lore=new ArrayList<>();
			for(Decree d:decrees){
				lore.add("&d"+d.name+"&e: &f"+d.am+" &6{&f"+d.current+"&6}");
			}
			inv.setItem(5, ItemUtil.create(Material.KNOWLEDGE_BOOK, 1, ChatColor.BLUE+"Указы", lore, null, 0));
		}
		inv.setItem(7, ItemUtil.create(Material.GOLDEN_PICKAXE, 1, ChatColor.GOLD+"Добывать по умолчанию", new String[]{kat.digs[kat.digType].toString()}, null, 0));
		inv.setItem(10, ItemUtil.create(Material.OAK_DOOR, 1, "test", null, null, null));
		inv.setItem(26, ItemUtil.create(Material.IRON_DOOR, 1, ChatColor.GOLD+"Сослать назад", new String[]{"&11&22&33&44&55&66&77&88&99&00&aa&bb&cc&dd&ee&ff"}, null, 0));
		p.openInventory(inv);
	}
	
	public void addDecree(Decree d){
		TextUtil.debug("Добавляю указ");
		for(Decree ed:decrees){
			if(ed.name.equals(d.name)){
				if( (d.current!=null) == (ed.current!=null) ){
					if(ed.current!=null){
						if(ed.current.equals(d.current)){
							ed.am++;
							TextUtil.debug("Добавлен к старому");
							return;
						}
					}else{
						TextUtil.debug("Добавлен к старому");
						ed.am++;
						return;
					}
				}
			}
		}
		TextUtil.debug("Добавлен новый");
		decrees.add(d);
	}
}
