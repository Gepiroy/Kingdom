package Struct;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

public class AS {
	public double dx, dy, dz;
	public ArmorStand stand;
	public List<Material> avaliableItemsToPut = new ArrayList<>();
	public EulerAngle armpos;
	
	public void spawn(Location l){
		stand = (ArmorStand) l.getWorld().spawnEntity(l.add(dx, dy, dz), EntityType.ARMOR_STAND);
		stand.setMarker(true);
		stand.setRightArmPose(armpos);
	}
}
