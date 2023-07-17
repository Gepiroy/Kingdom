package UtilsKingdom;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.block.impl.CraftCrops;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_16_R3.EntityCreature;

public final class NMSUtil {

    private NMSUtil(){}
    
    public static void move(Entity en, Location move){
    	EntityCreature cr=((EntityCreature) ((CraftEntity) en).getHandle());
    	cr.getNavigation().a(move.getX(), move.getY(), move.getZ(), 1.0);
    }
    
    public static int getGrowStage(Block b){
		CraftCrops crop = (CraftCrops) b.getState().getBlockData();
		return crop.getAge();
    }
    public static void setGrowStage(Block b, int stage){
		CraftCrops crop = (CraftCrops) b.getState().getBlockData();
        crop.setAge(stage);
        b.setBlockData(crop);
    }
}
