package Schems;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import UtilsKingdom.GeomUtil;

public class xz {
	
	public int x;
	public int z;
	
	public xz(int x, int z){
		this.x=x;
		this.z=z;
	}
	
	public xz(xz xz){
		this.x=xz.x;
		this.z=xz.z;
	}
	
	//ban - z-, x+, z+, x-.
	public void drawSquare(Location loc, int steps, float oneStep, Particle p, boolean[] ban){
		Vector v=loc.toVector();
		for(int i=0;i<steps;i++){
			v.add(new Vector(oneStep, 0, 0));
			if(i==steps-1&&ban[1])break;
			if(!ban[0])GeomUtil.drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(0, 0, oneStep));
			if(i==steps-1&&ban[2])break;
			if(!ban[1])GeomUtil.drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(-oneStep, 0, 0));
			if(i==steps-1&&ban[3])break;
			if(!ban[2])GeomUtil.drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(0, 0, -oneStep));
			if(i==steps-1&&ban[0])break;
			if(!ban[3])GeomUtil.drawDot(v.toLocation(loc.getWorld()), p);
		}
	}
	
	public void drawSquare(Location loc, int steps, float oneStep, int r, int g, int b, float csize, boolean[] ban){
		if(ban==null)ban=new boolean[]{false,false,false,false};
		Vector v=loc.toVector();
		for(int i=0;i<steps;i++){
			v.add(new Vector(oneStep, 0, 0));
			if(i==steps-1&&ban[1])break;
			if(!ban[0])GeomUtil.drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(0, 0, oneStep));
			if(i==steps-1&&ban[2])break;
			if(!ban[1])GeomUtil.drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(-oneStep, 0, 0));
			if(i==steps-1&&ban[3])break;
			if(!ban[2])GeomUtil.drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<steps;i++){
			v.add(new Vector(0, 0, -oneStep));
			if(i==steps-1&&ban[0])break;
			if(!ban[3])GeomUtil.drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
	}
	
	public boolean isHere(int x, int z){
		return (this.x==x&&this.z==z);
	}
	
	public boolean isHere(xz xz){
		return (this.x==xz.x&&this.z==xz.z);
	}
	
	public double dist(xz xz){
		return Math.hypot(x-xz.x, z-xz.z);
	}
	
	public void subtract(xz xz){
		this.x-=xz.x;
		this.z-=xz.z;
	}
	
	public void multiply(float m){
		this.x*=m;
		this.z*=m;
	}
	
	public void normalize(){
		double m=Math.sqrt(x*x+z*z);
		x/=m;
		z/=m;
	}
}
