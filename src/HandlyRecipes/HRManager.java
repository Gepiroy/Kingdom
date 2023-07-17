package HandlyRecipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Kingdom.Items;
import invsUtil.Inv;

public class HRManager {
	
	public static List<HandlyRecipe> stickRecipes = new ArrayList<>();
	public static final Inv stickCrafting = new Inv("&6Палка") {
		
		@Override
		public void displItems(Inventory inv) {
			int i=0;
			for(HandlyRecipe hr:HRManager.stickRecipes){
				inv.setItem(i, hr.displ());
				i++;
			}
		}
		
		@Override
		public void click(InventoryClickEvent e) {
			HandlyRecipe hr = HRManager.stickRecipes.get(e.getSlot());
			hr.tryToCraft(p);
		}
	};
	
	public static void enable(){
		stickRecipes.add(new HandlyRecipe(Items.kopalka)
				.add(new ItemStack(Material.STICK, 2)));
		stickRecipes.add(new HandlyRecipe(Items.stickfire)
				.add(new ItemStack(Material.STICK, 10)));
		stickRecipes.add(new HandlyRecipe(Items.stick_knife)
				.add(new ItemStack(Material.STICK, 3)));
	}
}
