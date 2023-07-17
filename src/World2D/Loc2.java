package World2D;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import objKingdom.Conf;

public class Loc2 {
	public int x;
	public int z;
	
	public Loc2(int x, int z){
		this.x=x;
		this.z=z;
	}
	public Loc2(Location l){
		this.x=l.getBlockX();
		this.z=l.getBlockZ();
	}
	public Loc2(Conf conf, String st){
		x=conf.getInt(st+".x");
		z=conf.getInt(st+".z");
	}
	
	public Loc2 add(int x, int z){
		this.x+=x;
		this.z+=z;
		return this;
	}
	
	public double dist(int x, int z){
		return Math.hypot(this.x-x, this.z-z);
	}
	
	public int changes(boolean retIfOne){
		int ret=0;
		int y=130;
		Location l=new Location(World2.world, x, y, z);
		Location nl=new Location(World2.naturalWorld, x, y, z);
		for(;y>3;y--){
			l.subtract(0, 1, 0);
			nl.subtract(0, 1, 0);
			if(!l.getBlock().getType().isAir()){
				Material mat=l.getBlock().getType();
				if(mat.getHardness()<=0)continue;
				if(World2.ignoresNatural.contains(mat))continue;
				if(mat!=nl.getBlock().getType()){
					ret++;
					if(retIfOne)return 1;
				}
			}
		}
		return ret;
	}
	
	public List<Block> getChanges(){
		List<Block> ret=new ArrayList<>();
		int y=130;
		Location l=new Location(World2.world, x, y, z);
		Location nl=new Location(World2.naturalWorld, x, y, z);
		for(;y>3;y--){
			l.subtract(0, 1, 0);
			nl.subtract(0, 1, 0);
			if(!l.getBlock().getType().isAir()){
				Material mat=l.getBlock().getType();
				if(mat.getHardness()<=0&&mat!=Material.WHEAT)continue;
				if(World2.ignoresNatural.contains(mat))continue;
				if(mat!=nl.getBlock().getType()){
					ret.add(l.getBlock());
				}
			}
		}
		return ret;
	}
	
	public Block FirstChangeOnGround(){
		int y=130;
		Location l=new Location(World2.world, x, y, z);
		Location nl=new Location(World2.naturalWorld, x, y, z);
		for(;y>3;y--){
			l.subtract(0, 1, 0);
			nl.subtract(0, 1, 0);
			if(!l.getBlock().getType().isAir()){
				Material mat=l.getBlock().getType();
				if(mat.getHardness()<=0&&mat!=Material.WHEAT)continue;
				if(World2.ignoresNatural.contains(mat))return null;
				if(mat!=nl.getBlock().getType()){
					return l.getBlock();
				}
				return null;
			}
		}
		return null;
	}
	
	public Location toLocFromSky(){
		int y=130;
		Location l=new Location(World2.world, x, y, z);
		for(;y>3;y--){
			l.subtract(0, 1, 0);
			if(!l.getBlock().getType().isAir()){
				Material mat=l.getBlock().getType();
				if(mat.getHardness()<=0)continue;
				return new Location(World2.world, x, y, z);
			}
		}
		return null;
	}
	
	public void normalize(){
		double coef=Math.sqrt(x*x + z*z);
		x/=coef;
		z/=coef;
	}
	
	public double toGrad(Loc2 center){
		return Math.atan2(x-center.x, z-center.z);
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".x", x);
		conf.set(st+".z", z);
	}
	
	public void demonstrate(){
		Location l=new Location(World2.world, x+0.5, 70, z+0.5);
		l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 100, 0, 1, 0, 0);
	}
	
	@Override
	public String toString(){
		return "Loc2(x="+x+";z="+z+")";
	}
}
