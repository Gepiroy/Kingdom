package rooms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombieVillager;

public class EntityPrisoner extends EntityZombieVillager{
	
    public EntityPrisoner(World world) {
    	super(EntityTypes.ZOMBIE_VILLAGER, ((CraftWorld) world).getHandle());
    	
    	
    }
    
    @Override
    public void initPathfinder() { // This method will apply some custom pathfinders to our pig
	    //super.initPathfinder(); // This will apply all default pathfinders to the pig
    	
	    /*
	     * this.targetSelector - Communicates what the pig's target to walk to will be.
	     *
	     * .a(0, pathfinder) - The pig's ai.
	     *
	     * 0, - priority
	     * new PathfinderGoalNearestAttackableTarget<EntityHuman> - Tells the pig to target the nearest attackable target - <EntityHuman>
	     * sets the type of mob to select.
	     *
	     * (this, - the pig
	     * EntityHuman.class, - what to target
	     * true) - this part is weird, but it tells the pig weather to target its own kind. We really don't need to worry about this, but
	     * if we used EntityCreature as the target this would stop the pig from attacking itself.
	     */
	    //this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
	   
	        /*
	     * this.goalSelector - Communicates what the pig's goal to perform will be.
	     *
	     * PathfinderGoalLeapAtTarget( - will make the pig leap at the player similar to a wolf attacking(this will not to damage)
	     * this, - the pig
	     * 1.0f) - the height of the jump(Please experiment with this to get a height you want)
	     */
	    //this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 1.0f));
   
    }
    
    public ZombieVillager spawn(Location loc) {
        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.world.addEntity(this, SpawnReason.CUSTOM);
        return (ZombieVillager) this.getBukkitEntity();
    }

    // TODO add custom stuffs to make it custom
}
