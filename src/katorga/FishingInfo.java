package katorga;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;

public class FishingInfo extends BuildingInfo{
	public int maxGuards=1;
	public int maxWorkers=4;
	
	public static class Builder extends BuildingInfo.Builder<Builder>{
		private int maxGuards=1;
		private int maxWorkers=4;
		
		public Builder(String name, Material displ){
			super(name, displ);
		}
		public Builder maxGuards(int maxGuards){
			this.maxGuards=maxGuards;
			return this;
		}
		public Builder maxWorkers(int maxWorkers){
			this.maxWorkers=maxWorkers;
			return this;
		}
		@Override
		public FishingInfo build(){
			return new FishingInfo(this);
		}
		@Override
		protected Builder self() {
			return this;
		}
	}
	
	private FishingInfo(Builder b){
		super(b);
		maxGuards=b.maxGuards;
		maxWorkers=b.maxWorkers;
	}

	@Override
	public ItemStack displayItem(){
		List<String> lore=new ArrayList<>(this.lore);
		lore.add("&2Макс. охранников: &e"+maxGuards);
		lore.add("&2Макс. рабочих: &e"+maxWorkers);
		lore.add("&6Требуется рабочей силы: &e"+toReady);
		return ItemUtil.create(displMat, 1, "&3"+name, lore, null, 0);
	}
}
