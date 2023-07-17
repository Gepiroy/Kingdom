package Hunting;

public class DeadAnimalPreset {
	public final int minParts, maxParts;
	public final PartsType pt;
	
	public DeadAnimalPreset(int min, int max, PartsType pt){
		minParts=min;
		maxParts=max;
		this.pt=pt;
	}
}
