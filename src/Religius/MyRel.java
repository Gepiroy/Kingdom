package Religius;

import org.bukkit.Material;

public class MyRel {
	public Religion rel;
	public double good=0;
	
	public MyRel(){
		rel=new Religion("Моналит");
		rel.food.put(Material.PUMPKIN_PIE, 10);
		rel.food.put(Material.SWEET_BERRIES, 1);
		rel.food.put(Material.POTATO, -1);
		rel.food.put(Material.BAKED_POTATO, -3);
	}
	
	public void sec(){
		if(rel!=null){
			good-=0.005;
		}
	}
}
