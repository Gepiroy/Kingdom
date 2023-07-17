package katorga;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;

public class InvHouse {
	public House house;
	public List<String> baseLore=new ArrayList<>();
	
	public InvHouse(House house, Object baseLore){
		this.house=house;
		this.baseLore=ItemUtil.lore(baseLore);
	}
	
	public ItemStack genItem(UUID kid){
		//List<String> lore=new ArrayList<>(baseLore);
		//lore.add("&2Вместительность: &e"+house.slots);
		//lore.add("&2Комфорт: &e"+house.comfort);
		//lore.add("&2Здоровье: &e"+house.health);
		//lore.add("&2Влияние соседей: &ex"+TextUtil.cylDouble(house.neighbourCoef,"#0.00"));
		//lore.add("&6Требуется рабочей силы: &e"+house.toReady);
		//Katorga kat=main.prisons.kats.get(kid);
		//lore.add("&6Уже есть: &e"+kat.findBuildingsByName(house.name).size());
		return null;// ItemUtil.create(house.displMat, 1, "&3"+house.name, lore, null, 0);
	}
}
