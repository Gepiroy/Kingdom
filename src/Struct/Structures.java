package Struct;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;

public class Structures {
	
	private Structures(){}
	
	public void init(){
		list.add(new Structure("BodyCut", ItemUtil.create(Material.LEATHER, 1, "&6����������� ����", "��������� ����������� ����.", null, 0)));
		list.add(new Structure("GrassSleep", ItemUtil.create(Material.GREEN_BED, 1, "&7����� ��� ��� � �����", "��������� ����� �� �����.", null, 0)));
		
	}
	
	public static List<Structure> list = new ArrayList<>();
	
	public static List<Built> builts = new ArrayList<>();
	
	public static Structure strucItem(ItemStack item){
		for(Structure s:list){
			if(ItemUtil.isItemsEqual(item, s.item)){
				return s;
			}
		}
		return null;
	}
}
