package objKingdom;

import org.bukkit.Material;

public enum Food{
	APPLE(Material.APPLE, 4),
	MUSHROOM_STEM(Material.MUSHROOM_STEM, 4),
	BREAD(Material.BREAD, 5),
	PORKCHOP(Material.PORKCHOP, 3),
	COOKED_PORKCHOP(Material.COOKED_PORKCHOP, 8),
	COD(Material.COD, 2),
	SALMON(Material.SALMON, 2),
	TROPICAL_FISH(Material.TROPICAL_FISH, 2),
	COOKED_COD(Material.COOKED_COD, 5),
	COOKED_SALMON(Material.COOKED_SALMON, 4),
	CAKE(Material.CAKE, 20),
	COOKIE(Material.COOKIE, 2),
	MELON_SLICE(Material.MELON_SLICE, 1),
	DRIED_KELP(Material.DRIED_KELP, 1),
	BEEF(Material.BEEF, 3),
	COOKED_BEEF(Material.COOKED_BEEF, 8),
	CHICKEN(Material.CHICKEN, 2),
	COOKED_CHICKEN(Material.COOKED_CHICKEN, 6),
	ROTTEN_FLESH(Material.ROTTEN_FLESH, 2),
	SPIDER_EYE(Material.SPIDER_EYE, 1),
	CARROT(Material.CARROT, 3),
	POTATO(Material.POTATO, 1),
	BAKED_POTATO(Material.BAKED_POTATO, 5),
	PUMPKIN_PIE(Material.PUMPKIN_PIE, 8),
	RABBIT(Material.RABBIT, 3),
	COOKED_RABBIT(Material.COOKED_RABBIT, 6),
	RABBIT_STEW(Material.RABBIT_STEW, 10),
	MUTTON(Material.MUTTON, 2),
	COOKED_MUTTON(Material.COOKED_MUTTON, 6),
	BEETROOT(Material.BEETROOT, 1),
	BEETROOT_SOUP(Material.BEETROOT_SOUP, 6),
	SWEET_BERRIES(Material.SWEET_BERRIES, 2),
	HONEY_BOTTLE(Material.HONEY_BOTTLE, 6);
	
	public final Material mat;
	public final int food;
	
	private Food(Material mat, int food){
		this.mat=mat;
		this.food=food;
	}
	
	public static int getFoodLevel(Material mat){
    	for(Food f:values()){
    		if(f.mat==mat)return f.food;
    	}
    	return -1;
    }
	
	public static Food getFood(Material mat){
    	for(Food f:values()){
    		if(f.mat==mat)return f;
    	}
    	return null;
    }
}
