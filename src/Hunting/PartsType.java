package Hunting;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

public class PartsType {
	
	public final HashMap<ItemStack, Float> drops = new HashMap<>();
	
	public PartsType(){
		
	}
	
	public PartsType add(ItemStack item, float chance){
		drops.put(item, chance);
		return this;
	}
}
