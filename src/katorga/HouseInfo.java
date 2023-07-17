package katorga;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;

public class HouseInfo extends BuildingInfo{
	public int comfort=10;
	public int health=2;
	public int slots=5;
	public double neighbourCoef=1.0;//Снижение влияния соседей.
	public List<UUID> members=new ArrayList<>();
	
	public static class Builder extends BuildingInfo.Builder<Builder>{
		private int comfort=10;
		private int health=2;
		private int slots=5;
		private double neighbourCoef=1.0;//Снижение влияния соседей.
		
		public Builder(String name, Material displ){
			super(name, displ);
		}
		public Builder comfort(int comfort){
			this.comfort=comfort;
			return this;
		}
		public Builder health(int health){
			this.health=health;
			return this;
		}
		public Builder slots(int slots){
			this.slots=slots;
			return this;
		}
		public Builder neighbourCoef(int neighbourCoef){
			this.neighbourCoef=neighbourCoef;
			return this;
		}
		@Override
		public HouseInfo build(){
			return new HouseInfo(this);
		}
		@Override
		protected Builder self() {
			return this;
		}
	}
	
	private HouseInfo(Builder b){
		super(b);
		comfort = b.comfort;
		health = b.health;
		slots = b.slots;
		neighbourCoef = b.neighbourCoef;
	}

	@Override
	public ItemStack displayItem(){
		List<String> lore=new ArrayList<>(this.lore);
		lore.add("&2Вместительность: &e"+slots);
		lore.add("&2Комфорт: &e"+comfort);
		lore.add("&2Здоровье: &e"+health);
		lore.add("&2Влияние соседей: &ex"+TextUtil.cylDouble(neighbourCoef,"#0.00"));
		lore.add("&6Требуется рабочей силы: &e"+toReady);
		return ItemUtil.create(displMat, 1, "&3"+name, lore, null, 0);
	}
}
