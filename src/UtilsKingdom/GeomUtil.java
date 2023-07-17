package UtilsKingdom;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class GeomUtil {
	
	private GeomUtil(){}
	
	public static Vector direction(Location from, Location to){
		return from.toVector().subtract(to.toVector()).normalize();
	}
	
	public static void lineBetweenTwoPoints(Location p1, Location p2, float step, int r, int g, int b, float size){
	    World w = p1.getWorld();
	    double distance = p1.distance(p2);
	    Vector v1 = p1.toVector();
	    Vector v2 = p2.toVector();
	    Vector vector = v2.clone().subtract(v1).normalize().multiply(step);
	    float length = 0;
	    for (; length < distance; v1.add(vector)) {
	    	drawRedDot(v1.toLocation(w), r, g, b, size);
	        length += step;
	    }
	}
	
	public static Location LookingPreBlock(Player p, float back, float dist){
		Location loc=p.getEyeLocation();
		Vector vec = loc.toVector();
		Vector v1 = loc.getDirection().normalize().multiply(0.1);
		float walked=0;
		for(;walked<dist;walked+=0.1f){
			vec.add(v1);
			Material mat=vec.toLocation(p.getWorld()).getBlock().getType();
			if(!mat.equals(Material.AIR)){
				vec.subtract(v1.multiply(back*10));
				return vec.toLocation(p.getWorld());
			}
		}
		return vec.toLocation(p.getWorld());
	}
	
	public static void drawCube(Location loc, float size, Particle p){
		Vector[] v = {
				loc.toVector(),
				loc.clone().add(1*size, 0, 0).toVector(),
				loc.clone().add(0, 0, 1*size).toVector(),
				loc.clone().add(1*size, 0, 1*size).toVector()
		};
		for(int i=0;i<5;i++){
			v[0].add(new Vector(0.2*size, 0, 0));
			v[1].add(new Vector(0, 0, 0.2*size));
			v[2].add(new Vector(0, 0, -0.2*size));
			v[3].add(new Vector(-0.2*size, 0, 0));
			for(Vector vec:v){
				drawDot(vec.toLocation(loc.getWorld()), p);
			}
		}
		for(int i=0;i<5;i++){
			for(Vector vec:v){
				vec.add(new Vector(0, 0.2*size, 0));
				drawDot(vec.toLocation(loc.getWorld()), p);
			}
		}
		for(int i=0;i<5;i++){
			v[0].add(new Vector(-0.2*size, 0, 0));
			v[1].add(new Vector(0, 0, -0.2*size));
			v[2].add(new Vector(0, 0, 0.2*size));
			v[3].add(new Vector(0.2*size, 0, 0));
			for(Vector vec:v){
				drawDot(vec.toLocation(loc.getWorld()), p);
			}
		}
	}
	public static void drawCube(Location loc, float size, int r, int g, int b, float csize){
		Vector[] v = {
				loc.toVector(),
				loc.clone().add(1*size, 0, 0).toVector(),
				loc.clone().add(0, 0, 1*size).toVector(),
				loc.clone().add(1*size, 0, 1*size).toVector()
		};
		for(int i=0;i<5;i++){
			v[0].add(new Vector(0.2*size, 0, 0));
			v[1].add(new Vector(0, 0, 0.2*size));
			v[2].add(new Vector(0, 0, -0.2*size));
			v[3].add(new Vector(-0.2*size, 0, 0));
			for(Vector vec:v){
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
		for(int i=0;i<5;i++){
			for(Vector vec:v){
				vec.add(new Vector(0, 0.2*size, 0));
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
		for(int i=0;i<5;i++){
			v[0].add(new Vector(-0.2*size, 0, 0));
			v[1].add(new Vector(0, 0, -0.2*size));
			v[2].add(new Vector(0, 0, 0.2*size));
			v[3].add(new Vector(0.2*size, 0, 0));
			for(Vector vec:v){
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
	}
	
	public static void drawWall(Location loc, float size, int Y, int r, int g, int b, float csize){
		int steps=2;
		float step=1.0f/steps*size;
		Vector[] v = {
				loc.toVector(),
				loc.clone().add(1*size, 0, 0).toVector(),
				loc.clone().add(0, 0, 1*size).toVector(),
				loc.clone().add(1*size, 0, 1*size).toVector()
		};
		for(int i=0;i<steps;i++){
			v[0].add(new Vector(step, 0, 0));
			v[1].add(new Vector(0, 0, step));
			v[2].add(new Vector(0, 0, -step));
			v[3].add(new Vector(-step, 0, 0));
			for(Vector vec:v){
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
		for(int i=0;i<(steps)*Y;i++){
			for(Vector vec:v){
				vec.add(new Vector(0, step, 0));
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
		for(int i=0;i<steps;i++){
			v[0].add(new Vector(-step, 0, 0));
			v[1].add(new Vector(0, 0, -step));
			v[2].add(new Vector(0, 0, step));
			v[3].add(new Vector(step, 0, 0));
			for(Vector vec:v){
				drawRedDot(vec.toLocation(loc.getWorld()), r, g, b, csize);
			}
		}
	}
	
	public static void drawRect(Location loc, int dx, int dz, float oneStep, Particle p){
		Vector v=loc.toVector();
		for(int i=0;i<dx;i++){
			v.add(new Vector(oneStep, 0, 0));
			drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, oneStep));
			drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<dx;i++){
			v.add(new Vector(-oneStep, 0, 0));
			drawDot(v.toLocation(loc.getWorld()), p);
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, -oneStep));
			drawDot(v.toLocation(loc.getWorld()), p);
		}
	}
	
	public static void drawRect(Location loc, int dx, int dz, float oneStep, int r, int g, int b, float csize){
		Vector v=loc.toVector();
		for(int i=0;i<dx;i++){
			v.add(new Vector(oneStep, 0, 0));
			drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, oneStep));
			drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<dx;i++){
			v.add(new Vector(-oneStep, 0, 0));
			drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, -oneStep));
			drawRedDot(v.toLocation(loc.getWorld()), r, g, b, csize);
		}
	}
	
	public static void drawDot(Location l, Particle p){
		l.getWorld().spawnParticle(p, l, 0, 0, 0, 0);
	}
	
	public static void drawRedDot(Location l, int r, int g, int b, float size){
		DustOptions opt=new DustOptions(Color.fromRGB(r, g, b), size);
		l.getWorld().spawnParticle(Particle.REDSTONE, l, 0, 0, 0, 0, opt);
	}
	
}
