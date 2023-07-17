package World2D;

import org.bukkit.Location;

public class Vec2 {
	public double x, z;
	
	public Vec2(double x, double z){
		this.x=x;
		this.z=z;
	}
	public Vec2(Location l){
		this.x=l.getX();
		this.z=l.getZ();
	}
	
	public Vec2 add(double x, double z){
		this.x+=x;
		this.z+=z;
		return this;
	}
	
	public double dist(double x, double z){
		return Math.hypot(this.x-x, this.z-z);
	}
	
	public void normalize(){
		double coef=Math.sqrt(x*x + z*z);
		x/=coef;
		z/=coef;
	}
	
	public double toGrad(Loc2 center){
		return Math.atan2(x-center.x, z-center.z);
	}
	
	@Override
	public String toString(){
		return "Loc2(x="+x+";z="+z+")";
	}
}
