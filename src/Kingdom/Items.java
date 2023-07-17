package Kingdom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import UtilsKingdom.ItemUtil;

public class Items {
	
	public static final ItemStack kopalka = ItemUtil.create(Material.WOODEN_HOE, 1, "&6Палка-копалка", "&7Дешевейший способ стать фермером.", null, 0);
	public static final ItemStack stick_knife = ItemUtil.create(Material.WOODEN_SWORD, 1, "&6Палочный нож", new String[]{"&7Дешевейший способ разделки.", "&fБьёт недалеко и лишь 1 цель.", "&cНож."}, null, 0);
	public static final ItemStack stickfire = ItemUtil.create(Material.CAMPFIRE, 1, "&6Палочный костёр", new String[]{"&7Дешевейший способ обжарить мясо.", "&cОдноразовый."}, null, 0);
	
	public static ItemStack create(ItemStack item, int am){
		ItemStack ret=item.clone();
		ret.setAmount(am);
		return ret;
	}
	
	public static boolean isSimilar(ItemStack from, ItemStack to){
		if(from==null||to==null)return false;
		if(from.isSimilar(to))return true;
		if(from.getType()==to.getType()){
			if(from.getItemMeta() instanceof Damageable){
				ItemStack check = from.clone();
				Damageable cmeta = (Damageable) check.getItemMeta();
				cmeta.setDamage(((Damageable)to.getItemMeta()).getDamage());
				check.setItemMeta((ItemMeta) cmeta);
				return check.isSimilar(to);
			}
		}
		return false;
	}
}
