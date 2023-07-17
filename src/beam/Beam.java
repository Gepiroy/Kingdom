package beam;

import java.util.Date;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.block.impl.CraftRotatable;
import org.bukkit.util.Vector;

import UtilsKingdom.TextUtil;

public class Beam {
	public final int length;
	public final Material mat;
	public final Location loc;
	public final Vector dir;
	
	public Beam(Location loc, int length, Material mat, Vector dir){
		this.loc=loc;
		this.length=length;
		this.mat=mat;
		double x=dir.getX(), z=dir.getZ();
		if(Math.abs(x)>Math.abs(z)){
			if(x>0)this.dir=new Vector(1, 0, 0);
			else this.dir=new Vector(-1, 0, 0);
		}else{
			if(z>0)this.dir=new Vector(0, 0, 1);
			else this.dir=new Vector(0, 0, -1);
		}
	}
	
	public void fall(){
		long started=new Date().getTime();
		Vector lv = new Vector(0,1,0);
		Vector rot = new Vector(dir.getZ(), 0, -dir.getX());
		double angle = Math.PI/(length*4);
		for(int a=0;a<Math.PI/(angle*2);a++){
			lv.rotateAroundAxis(rot, angle);
			//TextUtil.debug("rot"+a);
			//Vector v=lv.clone();
			Location l=loc.clone();
			for(int i=1;i<=length;i++){
				//v.add(lv);
				l.add(lv);
				l.getWorld().spawnParticle(Particle.CLOUD, l, 0, lv.getX(), (lv.getY()-1), lv.getZ(), 0.2);
				//TextUtil.debug("l.x,z="+l.getBlockX()+","+l.getBlockZ());
				//TextUtil.debug("loc.x,z="+loc.getBlockX()+","+loc.getBlockZ());
				if(loc.getBlockX()==l.getBlockX()&&loc.getBlockZ()==l.getBlockZ())continue;
				Material bm=l.getBlock().getType();
				if(bm.toString().contains("LEAVES"))continue;
				if(bm.getHardness()!=0){
					TextUtil.debug("FUCK mat="+bm);
					lv.rotateAroundAxis(rot, -angle);
					build(lv);
					return;
				}
			}
		}
		TextUtil.debug("log counting taked "+(new Date().getTime()-started)+" ms.");
		build(lv);
	}
	BlockFace face(Vector v){
		double x=v.getX(), y=v.getY(), z=v.getZ();
		if(y>x&&y>z)return BlockFace.UP;
		if(y<=x&&y<=z)return BlockFace.DOWN;
		if(Math.abs(x)>Math.abs(z)){
			if(x>0)return BlockFace.EAST;
			return BlockFace.WEST;
		}
		if(Math.abs(x)<=Math.abs(z)){
			if(z>0)return BlockFace.NORTH;
			return BlockFace.SOUTH;
		}
		return BlockFace.UP;
	}
	
	Axis toAx(Vector v){
		double x=Math.abs(v.getX()), y=Math.abs(v.getY()), z=Math.abs(v.getZ());
		if(y>x&&y>z)return Axis.Y;
		if(x>z)return Axis.X;
		return Axis.Z;
	}
	
	public void build(Vector lv){
		lv.normalize();
		//TextUtil.debug("building... lv="+lv);
		long started=new Date().getTime();
		Location l=loc.clone();
		for(int i=0;i<=length;i++){
			l.getBlock().setType(mat);
			CraftRotatable cr=(CraftRotatable) mat.createBlockData();
			cr.setAxis(toAx(lv));
			l.getBlock().setBlockData(cr);
			l.add(lv);
		}
		TextUtil.debug("log building taked "+(new Date().getTime()-started)+" ms.");
	}
}