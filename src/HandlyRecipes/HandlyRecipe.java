package HandlyRecipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import UtilsKingdom.GepUtil;
import UtilsKingdom.TextUtil;

public class HandlyRecipe {
	public List<ItemStack> needs = new ArrayList<>();
	private final ItemStack result;
	
	public HandlyRecipe(ItemStack result){
		this.result=result;
	}
	
	public HandlyRecipe add(ItemStack item){
		needs.add(item);
		return this;
	}
	
	public ItemStack getResult(){
		return result.clone();
	}
	
	public void tryToCraft(Player p){
		for(ItemStack need:needs){
			if(!hasItem(p, need)){
				TextUtil.mes(p, "ох ебаа", "Недостаточно материалов.");
				return;
			}
		}
		craft(p);
	}
	
	public void craft(Player p){
		for(ItemStack need:needs){
			takeItem(p, need);
		}
		GepUtil.give(p, result.clone());
	}
	
	public ItemStack displ(){
		ItemStack ret = result.clone();
		ItemMeta meta = ret.getItemMeta();
		List<String> lore = meta.getLore();
		if(lore==null)lore=new ArrayList<>();
		lore.add(TextUtil.string("&6-&eРецепт&6-"));
		for(ItemStack need:needs){
			if(need.hasItemMeta()&&need.getItemMeta().hasDisplayName())lore.add(TextUtil.string(" &f- "+need.getItemMeta().getDisplayName()+" &7x"+need.getAmount()));
			else lore.add(TextUtil.string(" &f- "+need.getType()+" &7x"+need.getAmount()));
		}
		meta.setLore(lore);
		ret.setItemMeta(meta);
		return ret;
	}
	
	public boolean hasItem(Player p, ItemStack check){
		int am=0;
		for(ItemStack item:p.getInventory().getContents()){
			if(item!=null&&item.isSimilar(check)){
				am+=item.getAmount();
				if(am>=check.getAmount())return true;
			}
		}
		return false;
	}
	
	public void takeItem(Player p, ItemStack check){
		int am=0;
		for(ItemStack item:p.getInventory().getContents()){
			if(item!=null&&item.isSimilar(check)){
				am+=item.getAmount();
				item.setAmount(Math.max(0, am-check.getAmount()));
				if(am>=check.getAmount())return;
			}
		}
	}
}
