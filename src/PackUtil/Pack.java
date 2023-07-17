package PackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;
import objKingdom.PlayerInfo;

public class Pack{
	
	public String displayName;
	public String[] mats;
	public int maxAm=27;
	public List<PackItem> items=new ArrayList<>();
	public UUID id;
	
	public Pack(String[] mats, int maxAm, String displayName, UUID id){
		this.mats=mats;
		this.maxAm=maxAm;
		this.displayName=displayName;
		this.id=id;
	}
	
	public boolean canAddItem(ItemStack item){
		if(item==null||item.getType().equals(Material.AIR))return false;
		ItemMeta meta=item.getItemMeta();
		if(meta!=null){//Мы не сохраняем "сложные" предметы.
			if(meta.hasDisplayName())return false;
			if(meta.hasEnchants())return false;
		}
		if(totalAm()>=maxAm)return false;
		return containsMat(item.getType());
	}
	
	public boolean containsMat(Material mat){
		for(String st:mats){
			char c=st.charAt(0);
			if(c=='['&&mat.toString().equals(st.substring(1)))return true;
			else if(mat.toString().contains(st))return true;
		}
		return false;
	}
	
	public Material maxMat(){
		int max=0;
		Material ret=Material.BARRIER;
		for(PackItem pi:items){
			if(pi.am>max){
				max=pi.am;
				ret=pi.mat;
			}
		}
		return ret;
	}
	
	public void resetDisplay(ItemStack item){
		Material mat=maxMat();
		int am=totalAm();
		if(am>mat.getMaxStackSize())am=mat.getMaxStackSize();
		item.setAmount(am);
		item.setType(mat);
	}
	
	public int stacksAm(){
		int ret=0;
		for(PackItem pit:items){
			ret+=(pit.am-1)/pit.mat.getMaxStackSize()+1;
		}
		return ret;
	}
	
	public int totalAm(){
		int ret=0;
		for(PackItem pit:items){
			ret+=pit.am;
		}
		return ret;
	}
	
	public void openGUI(Player p){
		Inventory inv=Bukkit.createInventory(null, 27, displayName);
		int slot=0;
		for(PackItem pit:items){
			for(ItemStack it:pit.toItems()){
				inv.setItem(slot, it);
				slot++;
			}
		}
		PlayerInfo pi=Events.plist.get(p.getName());
		pi.openedPack=id;
		p.openInventory(inv);
	}
	
	public void addItem(ItemStack item){
		for(PackItem pit:items){
			if(pit.mat.equals(item.getType())){
				pit.am+=item.getAmount();
				return;
			}
		}
		items.add(new PackItem(item));
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".type", main.packs.typeFromName(displayName));
		int i=0;
		for(PackItem pit:items){
			pit.save(conf, st+".items."+i);
			i++;
		}
	}
	public Pack(Conf conf, String st, UUID id){
		Pack pack=main.packs.types.get(conf.getString(st+".type"));
		displayName=pack.displayName;
		mats=pack.mats;
		maxAm=pack.maxAm;
		this.id=id;
		for(String i:conf.getKeys(st+".items")){
			items.add(new PackItem(conf, st+".items."+i));
		}
	}
	/**
	 * 
	 * @param mat
	 * @param change
	 * @return Пустой ли теперь этот пак
	 */
	public boolean changeMat(Material mat, int change){
		for(PackItem pit:new ArrayList<>(items)){
			if(pit.mat.equals(mat)){
				pit.am+=change;
				if(pit.am<=0){
					items.remove(pit);
					if(pit.am<0)TextUtil.debug("&cAm of &6"+pit.mat.toString()+"&e=&c"+pit.am);
				}
				break;
			}
		}
		if(items.size()==0)return true;
		return false;
	}
}
