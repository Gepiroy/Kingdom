package Raids;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import Kingdom.main;
import UtilsKingdom.NMSUtil;
import UtilsKingdom.TextUtil;
import World2D.Loc2;
import World2D.Vec2;
import World2D.World2;

public class Raider{
	public final UUID id;
	public Task task;
	public Creature en;
	
	public Raider(UUID id){
		this.id=id;
		en=(Creature) Bukkit.getEntity(id);
	}
	
	void steal(){
		List<Loc2> nears = World2.findNearLocs(new Loc2(en.getLocation()), 20);
		if(nears.size()==0){
			TextUtil.debug("nears=null");
			return;
		}
		List<Block> changes=new ArrayList<>();
		for(Loc2 l:nears){
			Block change = l.FirstChangeOnGround();
			if(change!=null)changes.add(change);
		}
		if(changes.size()==0)return;
		Block change = changes.get(main.r.nextInt(changes.size()));
		if(change==null){
			TextUtil.debug("change==null");
			return;
		}
		task=new TaskStealBlock(change);
		TextUtil.debug("task steal!");
	}
	
	public void sec(){
		//TextUtil.debug("sec of raider...");
		updateTask();
	}
	
	void updateTask(){
		if(task!=null){
			//TextUtil.debug("task!=null");
			if(task.update(en)){
				TextUtil.debug("task=null.");
				task=null;
			}
		}
		else{
			TextUtil.debug("steal()");
			task=new TaskFear();
			//steal();
		}
	}
	class Task{
		Location goTo;
		LivingEntity target;
		
		void setGoTo(Location goTo){
			this.goTo=goTo;
		}
		
		public boolean update(Creature en){
			if(target!=null)en.setTarget(target);
			else{
				if(goTo!=null){
					NMSUtil.move(en, goTo);
				}
			}
			return false;
		}
	}

	class TaskStealBlock extends Task{
		Block toSteal;
		public TaskStealBlock(Block toSteal){
			this.toSteal=toSteal;
			goTo=toSteal.getLocation().add(0.5, 0.5, 0.5);
		}
		
		@Override public boolean update(Creature en){
			super.update(en);
			double dist=en.getLocation().distance(goTo);
			//TextUtil.debug("dist="+dist);
			if(dist<=2){
				goTo=null;
				toSteal.breakNaturally();
				return true;
			}
			return false;
		}
	}

	class TaskFear extends Task{
		public TaskFear(){
			resetTo();
		}
		
		void resetTo(){
			Vec2 whereRun=new Vec2(0, 0);
			for(Player p:Bukkit.getOnlinePlayers()){
				double dist=p.getLocation().distance(en.getLocation());
				TextUtil.debug("dist="+dist);
				if(dist<40)whereRun.add(en.getLocation().getX()-p.getLocation().getX(), en.getLocation().getZ()-p.getLocation().getZ());
			}
			TextUtil.debug("whereRun=="+whereRun);
			whereRun.normalize();
			whereRun.x*=200;
			whereRun.z*=200;
			whereRun.x+=en.getLocation().getBlockX();
			whereRun.z+=en.getLocation().getBlockZ();
			goTo=new Loc2((int)whereRun.x, (int)whereRun.z).toLocFromSky();
		}
		
		@Override public boolean update(Creature en){
			super.update(en);
			if(goTo.distance(en.getLocation())<=2)return true;
			return false;
		}
	}
}