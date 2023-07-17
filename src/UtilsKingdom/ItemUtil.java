package UtilsKingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class ItemUtil {
	
	/**
	 * Create ItemStack
	 * @param material - Material
	 * @param amount - Amount
	 * @param DisplayName - DisplayName (if null, ignore)
	 * @param lore - Lore (as ArrayList<> or String[])
	 * @param ench - Enchantment/Enchantment[]/HashMap(Enchantment, Integer)/List(Enchantment). Null will be ignored.
	 * @param lvl - Level of the enchantment. int/int[]/List(Integer). If ench is HashMap, this arg will be ignored.
	 * @param lvl must be for all enchantments in equal order.
	 * @return ItemStack that was created.
	 */
	public static ItemStack create(Material material, int amount, String DisplayName, Object lore, Object ench, Object lvl){
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', DisplayName));
		}
		if(lore!=null)meta.setLore(lore(lore));
		if(ench != null){
			addEnch(meta, ench, lvl);
		}
		item.setItemMeta(meta);
		return item;
	}
	/**
	 * Create a unbreakable tool or armor.
	 * In minigames tools and armor are often unbreakable. So if you want to use breakable tools, just use create(...);.
	 * @param material - Material
	 * @param DisplayName - DisplayName
	 * @param lore - Lore (as ArrayList<> or String[])
	 * @param ench - Enchantment/Enchantment[]/HashMap(Enchantment, Integer)/List(Enchantment). Null will be ignored.
	 * @param lvl - Level of the enchantment. int/int[]/List(Integer). If ench is HashMap, this arg will be ignored.
	 * @param lvl must be for all enchantments in equal order.
	 * @return Tool that was created.
	 */
	public static ItemStack createTool(Material material, String DisplayName, Object lore, Object ench, Object lvl) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		if(lore!=null)meta.setLore(lore(lore));
		meta.setUnbreakable(true);
		if(ench != null){
			addEnch(meta, ench, lvl);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Create a custom potion.
	 * @param material - Material (POTION/SPLASH_POTION/LINHERING_POTION)
	 * @param amount - Amount (stack potions)
	 * @param DisplayName - DisplayName -_-
	 * @param lore - Lore (as ArrayList<> or String[])
	 * @param color - The color that will set to potion's texture. Null will set default color. You can use Color.fromRGB(r, g, b).
	 * @param pe - Array with your custom potion effects.
	 * @param hide - Hide potion effects?
	 * @return Potion that was created.
	 */
	public static ItemStack createPotion(Material material, int amount, String DisplayName, Object lore, Color color, PotionEffect[] pe, boolean hide) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(DisplayName != null){
			meta.setDisplayName(DisplayName);
		}
		if(lore!=null)meta.setLore(lore(lore));
		item.setItemMeta(meta);
		PotionMeta pmeta = (PotionMeta) item.getItemMeta();
		if(color!=null)pmeta.setColor(color);
		if(hide)pmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		if(pe!=null){
			for(PotionEffect pef:pe){
				pmeta.addCustomEffect(pef, false);
			}
		}
		item.setItemMeta(pmeta);
		return item;
	}
	
	/**
	 * Create a colored leather armor.
	 * @param mat - Material (LEATHER_BOOTS/LEGGINGS/CHESTPLATE/HELMET)
	 * @param DisplayName - DisplayName -_-
	 * @param lore - Lore (as ArrayList<> or String[])
	 * @param color - The color that will set to this clotches. Null will set default color. You can use Color.fromRGB(r, g, b).
	 * @return Your colored armor that was created.
	 */
	public static ItemStack createArmorColored(Material mat, String DisplayName, Object lore, Color color) {
		ItemStack item = create(mat,1,DisplayName,lore,null,null);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if(color!=null)meta.setColor(color);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}
	
	
	
	//Creating has ended. Now some helpful things...
	
	
	
	@SuppressWarnings("unchecked")
	public static List<String> lore(Object lore){//null always checked.
		List<String> setLore=new ArrayList<>();
		if(lore instanceof String){
			setLore.add((String) lore);
		}else if(lore instanceof String[]){
			String[] tmp=(String[]) lore;
			for(int i=0;i<tmp.length;i++){
				setLore.add(tmp[i]);
			}
		}else if(lore instanceof List)setLore=(List<String>) lore;
		int i=0;
		for(String st:new ArrayList<>(setLore)){
			setLore.set(i, TextUtil.string(st));
			i++;
		}
		return setLore;
	}
	
	@SuppressWarnings("unchecked")
	static void addEnch(ItemMeta meta, Object ench, Object lvl){//null always checked.
		List<Enchantment> enchs=new ArrayList<>();
		List<Integer> ints=new ArrayList<>();
	 //--------------------------------------------
		if(lvl instanceof int[]){
			for(int i:(int[])lvl){
				ints.add(i);
			}
		}else if(lvl instanceof List){
			ints=(List<Integer>) lvl;
		}
	 //--------------------------------------------
		if(ench instanceof Enchantment){
			meta.addEnchant((Enchantment)ench, (int)lvl, true);
			return;
		}else if(ench instanceof Enchantment[]){
			Enchantment[] tmp=(Enchantment[]) ench;
			for(int i=0;i<tmp.length;i++){
				enchs.add(tmp[i]);
			}
		}else if(ench instanceof HashMap){
			HashMap<Enchantment,Integer> tmp=(HashMap<Enchantment, Integer>) ench;
			for(Enchantment en:tmp.keySet()){
				meta.addEnchant(en, tmp.get(en), true);
			}
			return;
		}else if(ench instanceof List){
			enchs=(List<Enchantment>) ench;
		}
	 //--------------------------------------------
		for(int i=0;i<enchs.size();i++){
			meta.addEnchant(enchs.get(i), ints.get(i), true);
		}
	}
	
	//Next - working with ItemStacks.
	
	
	/**
	 * Find last double in item's lore's line that was selected by search text.
	 * @param item - Item where we'll search.
	 * @param str - The text that must be in a target line.
	 * @return The last founded double or 0.
	 */
	public static double DoubleFromLore(ItemStack item, String str){
		if(!item.hasItemMeta())return 0;
		if(!item.getItemMeta().hasLore())return 0;
		for(String st:item.getItemMeta().getLore()){
			if(st.contains(str)){
				String working=ChatColor.stripColor(st);//remove color codes.
				return TextUtil.lastDoubleFromString(working);
			}
		}
		return 0;
	}
	
	/**
	 * Check if lore in item contains text.
	 * @param item - Item where we'll search.
	 * @param str - text to search
	 * @return Is there our text?
	 */
	public static boolean loreContains(ItemStack item, String str){
		if(item==null)return false;
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasLore())return false;
		for(String st:item.getItemMeta().getLore()){
			if(st.contains(TextUtil.string(str)))return true;
		}
		return false;
	}
	/**
	 * Fast check item's name without simple issues like "item is null", "item haven't meta" etc.
	 * @param item - Item to check.
	 * @param name - Name to comparsion.
	 * @return Is this item have same name?
	 */
	public static boolean itemName(ItemStack item, String name) {
		if(item==null)return false;
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasDisplayName())return false;
		if(item.getItemMeta().getDisplayName().equals(name))return true;
		return false;
	}
	/**
	 * Check if we can stack an item with the target.
	 * @param item - Our item.
	 * @param target - Where we want to stack our item.
	 * @param mustAddAll - Should the item be added without a trace?
	 * @return Can we stack item with target?
	 */
	public static boolean canStack(ItemStack item, ItemStack target, boolean mustAddAll){
		if(target==null||item==null)return false;
		if(!item.getType().equals(target.getType()))return false;
		if(target.getAmount()==target.getMaxStackSize())return false;
		if(item.hasItemMeta()&&target.hasItemMeta()){
			if(!item.getItemMeta().equals(target.getItemMeta()))return false;
		}else if(item.hasItemMeta()||target.hasItemMeta())return false;
		if(mustAddAll&&item.getAmount()+target.getAmount()>item.getType().getMaxStackSize())return false;
		return true;
	}
	/**
	 * Fast check I tired to объяснять.
	 * @param item -_-
	 * @param mat if null, ignore.
	 * @param name if null, ignore.
	 * @param inLore if null, ignore.
	 * @return
	 */
	public static boolean isOurItem(ItemStack item, Material mat, String name, String inLore){
		if(item==null)return false;
		if(mat!=null&&!item.getType().equals(mat))return false;
		if(!item.hasItemMeta())return false;
		if(!item.getItemMeta().hasDisplayName())return false;
		if(inLore!=null&&!loreContains(item, inLore))return false;
		if(name!=null&&!item.getItemMeta().getDisplayName().equals(name))return false;
		return true;
	}
	/**
	 * Are items equal? (Not including amount)
	 * @param item - First item.
	 * @param other - Second item.
	 * @return Is they are equal?
	 */
	public static boolean isItemsEqual(ItemStack item, ItemStack other){
		if(item==null||other==null)return false;//Если оба null - смысл проверки?
		if(!item.getType().equals(other.getType()))return false;
		if(item.hasItemMeta()&&other.hasItemMeta()){
			if(!item.getItemMeta().equals(other.getItemMeta()))return false;
		}else if(item.hasItemMeta()||other.hasItemMeta())return false;
		return true;
	}
	
	public static class BuildItem{
		private ItemStack item;
		private ItemMeta meta;
		
		private Object ench;
		private Object lvl;
		
		public BuildItem(Material mat){
			this.item=new ItemStack(mat);
			this.meta=item.getItemMeta();
		}
		
		public BuildItem am(int am){
			this.item.setAmount(am);
			return this;
		}public BuildItem name(String name){
			this.meta.setDisplayName(name);
			return this;
		}public BuildItem lore(Object lore){
			this.meta.setLore(ItemUtil.lore(lore));
			return this;
		}public BuildItem ench(Object ench){
			this.ench=ench;
			return this;
		}public BuildItem lvl(Object lvl){
			this.lvl=lvl;
			return this;
		}
		public ItemStack build(){
			if(ench != null)addEnch(meta, ench, lvl);
			item.setItemMeta(meta);
			return item;
		}
	}
}
