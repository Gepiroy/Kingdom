package PackUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import objKingdom.Conf;

public class PackItem {
	public Material mat;
	public int am;
	
	public PackItem(ItemStack item){
		mat=item.getType();
		am=item.getAmount();
	}
	public PackItem(Conf conf, String where){
		mat=Material.getMaterial(conf.getString(where+".mat"));
		am=conf.getInt(where+".am");
	}
	
	public void save(Conf conf, String where){
		conf.set(where+".mat", mat.toString());
		conf.set(where+".am", am);
	}
	
	public ItemStack[] toItems(){
		int stack=mat.getMaxStackSize();
		ItemStack[] ret = new ItemStack[(am-1)/stack+1];
		int tam=am;
		for(int i=0;i<ret.length;i++){
			if(tam>=stack)ret[i]=new ItemStack(mat,stack);
			else ret[i]=new ItemStack(mat,tam);
			tam-=stack;
		}
		return ret;
	}
}
