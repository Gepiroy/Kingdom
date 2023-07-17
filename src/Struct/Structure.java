package Struct;

import org.bukkit.inventory.ItemStack;

public class Structure {
	public final String id;
	public final ItemStack item;
	public int dscos, dssin, dsy, dcos, dsin, dy;
	
	public Structure(String id, ItemStack item){
		this.id=id;
		this.item=item;
	}
	
	public ItemStack genItem(int am){
		ItemStack ret=item.clone();
		ret.setAmount(am);
		return ret;
	}
}
