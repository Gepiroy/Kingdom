package Struct;

import org.bukkit.Location;

public class AbsLoc {
	public int x,y,z;
	
	public AbsLoc(int x, int y, int z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public AbsLoc(Location l){
		this.x=l.getBlockX();
		this.y=l.getBlockY();
		this.z=l.getBlockZ();
	}
	
	public boolean equal(int x, int y, int z){
		return (this.x==x&&this.y==y&&this.z==z);
	}
}
