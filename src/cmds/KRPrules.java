package cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import Kingdom.main;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import invsUtil.Inv;
import invsUtil.Invs;
import objKingdom.Conf;
import objKingdom.Rule;
import objKingdom.Rules;

public class KRPrules implements CommandExecutor{
	
	static List<Rules> rules = new ArrayList<>();
	public KRPrules(){
		reload();
	}
	
	void reload(){
		rules.clear();
		Conf conf=new Conf(main.instance.getDataFolder()+"/rules.yml");
		if(!conf.file.exists()){
			conf.set("a.0.slot", 0);
			conf.set("a.0.mat", "BOOK");
			conf.set("a.0.name", "Test rules");
			conf.set("a.0.rules.0.name", "Test rule");
			conf.set("a.0.rules.0.mat", "PAPER");
			conf.set("a.0.rules.0.lore", "This rule only need to tell admins how to work with rules setting. No less, no more.");
			conf.save();
		}
		for(String st:conf.getKeys("a")){
			rules.add(new Rules(conf, "a."+st));
		}
	}
	
	public static final Inv invRules=new Inv("Правила"){
		@Override
		public void displItems(Inventory inv) {
			for(Rules rs:rules){
				List<String> lore = new ArrayList<>();
				lore.add("&e"+rs.rules.size()+" &fправил:");
				for(Rule r:rs.rules){
					lore.add("&8 - "+r.name);
				}
				lore.add("&aКлик &fдля подробностей.");
				inv.setItem(rs.slot, ItemUtil.create(rs.mat, 1, rs.name, lore, null, 0));
			}
		}
		@Override
		public void click(InventoryClickEvent e) {
			for(Rules r:rules){
				if(e.getSlot()==r.slot){
					pi.setTmp("RulesPage", r);
					Invs.open(p, invRule);
					return;
				}
			}
		}
	};
	
	public static final Inv invRule=new Inv("Правило"){
		@Override
		public void displItems(Inventory inv) {
			int i=0;
			Rules rs = (Rules) pi.getTmp("RulesPage");
			for(Rule r:rs.rules){
				inv.setItem(i, ItemUtil.create(r.mat, 1, r.name, TextUtil.split(r.lore, 40), null, 0));
				i++;
			}
		}
		@Override
		public void click(InventoryClickEvent e) {
			
		}
	};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=(Player) sender;
		if(args.length==0){
			Invs.open(p, invRules);
		}else if(p.isOp()){
			reload();
		}
		return true;
	}
}
