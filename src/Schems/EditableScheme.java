package Schems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import objKingdom.Conf;

public class EditableScheme implements Scheme{
	public List<sLine> lines=new ArrayList<>();
	public List<xz> blocks=new ArrayList<>();
	public int wallsHeight=2;
	
	public xz p1;
	
	float size=0.25f;
	Location l;
	public String placer;
	
	public EditableScheme(Location l, String placer) {
		this.l=l;
		this.placer=placer;
	}

	@Override
	public void show() {
		List<xz> drawed=new ArrayList<>();
		for(xz bl:blocks){
			boolean[] ban={false,false,false,false};
			for(xz b:drawed){
				if(b.isHere(bl.x+1, bl.z))ban[1]=true;
				if(b.isHere(bl.x, bl.z+1))ban[2]=true;
				if(b.isHere(bl.x-1, bl.z))ban[3]=true;
				if(b.isHere(bl.x, bl.z-1))ban[0]=true;
			}
			//bl.drawSquare(l.clone().add(bl.x*size, wallsHeight*size, bl.z*size), 2, 0.333f*size, Particle.FIREWORKS_SPARK, ban);
			//GeomUtil.drawWall(xzToLoc(bl, false), size, wallsHeight, 255, 255, 255, size);
			bl.drawSquare(xzToLoc(bl, false), 2, size/2, 255, 255, 255, size, ban);
			
			drawed.add(bl);
		}
	}
	
	public Location xzToLoc(xz xz, boolean center){
		Location ret=l.clone().add(xz.x*size, 0, xz.z*size);
		if(center)ret.add(0.5*size, 0.5*size, 0.5*size);
		return ret;
	}
	
	public xz xzFromLoc(Location loc){
		return new xz(
				(int)Math.floor((loc.getX()-l.getX())/size),
				(int)Math.floor((loc.getZ()-l.getZ())/size)
				);
	}
	
	public xz xzFromVec(Vector loc){
		return new xz(
				(int)Math.floor((loc.getX()-l.getX())/size),
				(int)Math.floor((loc.getZ()-l.getZ())/size)
				);
	}
	
	public List<xz> betweenPoints(Location p1, Location p2){
		List<xz> ret=new ArrayList<>();
	    double distance = p1.distance(p2);
	    Vector v1 = p1.toVector();
	    Vector v2 = p2.toVector();
	    float step=size*0.5f;
	    Vector vector = v2.clone().subtract(v1).normalize().multiply(step);
	    float length = 0;
	    for (; length <= distance; v1.add(vector)) {
	    	xz toAdd=xzFromVec(v1);
	    	boolean add=true;
	    	for(xz xz:ret){
	    		if(xz.isHere(toAdd)){
	    			add=false;
	    			break;
	    		}
	    	}
	    	if(add)ret.add(toAdd);
	        length += step;
	    }
	    return ret;
	}
	
	public boolean alreadyExists(xz xz){
		for(xz b:blocks)if(b.isHere(xz))return true;
		return false;
	}
	
	public EditableScheme(Conf conf, String st, String placer, Location l){
		this.l=l;
		this.placer=placer;
		for(String s:conf.getKeys(st+".walls")){
			blocks.add(new xz(conf.getInt(st+".walls."+s+".x"),conf.getInt(st+".walls."+s+".z")));
		}
	}
	
	public void save(Conf conf, String st){
		int i=0;
		for(xz b:blocks){
			conf.set(st+".walls."+i+".x", b.x);
			conf.set(st+".walls."+i+".z", b.z);
			i++;
		}
	}
}
