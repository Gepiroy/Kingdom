package Religius;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import UtilsKingdom.ItemUtil;
import invsUtil.Inv;

public class Religion {
	public final String name;
	public HashMap<Material, Integer> food = new HashMap<>();
	
	public Religion(String name){
		this.name=name;
	}
	
	public void GUI(){
		new Inv("Религия") {
			@Override
			public void displItems(Inventory inv) {
				int i=0;
				for(Material mat:food.keySet()){
					inv.setItem(i, ItemUtil.create(mat, 1, null, new String[]{
							"&fВера: &b"+food.get(mat),
					}, null, 0));
					i++;
				}
			}
			
			@Override
			public void click(InventoryClickEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
	}
}
