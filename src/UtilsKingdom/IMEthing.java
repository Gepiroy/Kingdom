package UtilsKingdom;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class IMEthing {
	public ItemStack item;
	public int slot;
	public Inventory inv;
	public IMEthing(ItemStack item, int slot, Inventory inv){
		this.item=item;
		this.slot=slot;
		this.inv=inv;
	}
}
