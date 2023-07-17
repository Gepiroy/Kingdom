package katorga;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import UtilsKingdom.ItemUtil;

public abstract class BuildingInfo {
	public int costPerDay=0;//Скок чего требует, потом разработаю.
	public int toReady=100;
	public String name="Хата Женякаса";
	public Material displMat;
	public List<String> lore=null;
	
	abstract static class Builder<T extends Builder<T>> implements Cloneable{
		private final String name;
		private final Material displMat;
		
		private int costPerDay=0;
		private int toReady=100;
		private List<String> lore=new ArrayList<>();
		
		public Builder(String name, Material displMat){
			this.name=name;
			this.displMat=displMat;
		}
		public T costPerDay(int costPerDay){
			this.costPerDay=costPerDay;
			return self();
		}
		public T toReady(int toReady){
			this.toReady=toReady;
			return self();
		}
		public T lore(Object lore){
			this.lore=ItemUtil.lore(lore);
			return self();
		}
		abstract BuildingInfo build();
		
		protected abstract T self();
	}
	
	BuildingInfo(Builder<?> b){
		name=b.name;
		displMat=b.displMat;
		costPerDay=b.costPerDay;
		toReady=b.toReady;
		lore=b.lore;
	}
	public abstract ItemStack displayItem();
}