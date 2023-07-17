package Hunting;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import Kingdom.main;

public class DeadAnimal {
	public final UUID id;
	public int parts;
	public PartsType pt;
	
	public DeadAnimal(LivingEntity en){
		this.id=en.getUniqueId();
		DeadAnimalPreset dap = HuntingManager.presets.get(EntityType.COW);
		if(HuntingManager.presets.containsKey(en.getType())){
			dap=HuntingManager.presets.get(en.getType());
		}
		parts=main.r.nextInt(dap.maxParts-dap.minParts+1)+dap.minParts;
		pt=dap.pt;
	}
}
