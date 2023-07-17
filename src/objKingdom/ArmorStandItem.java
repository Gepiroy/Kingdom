package objKingdom;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import Kingdom.main;

public class ArmorStandItem {
	Random r=main.r;
	public ArmorStand as;
	public final Location baseLoc;
	boolean defPos=false;
	
	EulerAngle angle;
	
	public ArmorStandItem(Location l, boolean defPos){
		this.defPos=defPos;
		baseLoc=l;
		ArmorStand as = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		as.setArms(true);
		as.setBasePlate(false);
		as.setCollidable(false);
		as.setGravity(false);
		as.setMarker(true);
		as.setAI(false);
		as.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SHOVEL));
		as.setInvisible(true);
		angle=new EulerAngle(0, r.nextDouble()*Math.PI*2, 0);
		new BukkitRunnable(){
			int timer=0;
			@Override
			public void run() {
				timer++;
				if(timer>=20)as.remove();
				if(Bukkit.getEntity(as.getUniqueId())==null){
					this.cancel();
					return;
				}
				angle=new EulerAngle(angle.getX()+Math.PI*0.2, angle.getY()+Math.PI*0.2, 0);
				setItemPos(as, angle);
				as.setRightArmPose(angle);
			}
		}.runTaskTimer(main.instance, 1, 15);
	}
	
	public void setItemPos(ArmorStand as, EulerAngle angle){
		if(!defPos){
			Location l=baseLoc.clone();
			double x=0,y=0,z=0;
			x+=Math.sin(angle.getX());
			y+=-Math.cos(angle.getX());
			
			z+=Math.sin(angle.getY());
			x+=-Math.cos(angle.getY());
			
			z+=Math.sin(angle.getZ());
			y+=-Math.cos(angle.getZ());
			Vector dir=new Vector(x, y, z);
			dir.multiply(0.5);
			l.subtract(dir);
			as.teleport(l);
		}
		as.setRightArmPose(angle);
	}
}
