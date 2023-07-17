package territories;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import objKingdom.Conf;

public class TerPoint {
	public int x;//x Получанка (*8)
	public int z;//z Получанка (*8)
	public TerPoint(int x, int z){
		this.x=x;
		this.z=z;
	}
	public TerPoint(Location l){
		this.x=(int) Math.floor(l.getBlockX()/8.0);
		this.z=(int) Math.floor(l.getBlockZ()/8.0);
	}
	public boolean isInMe(TerPoint t2){
		return (t2.x==x&&t2.z==z);
	}
	public boolean isInMe(Location l){
		TerPoint t2=new TerPoint(l);
		return (t2.x==x&&t2.z==z);
	}
	public Location getCenterFromSky(World w){
		Location ret=new Location(w, x*8+4, 100.5, z*8+4);
		Block b=ret.getBlock();
		if(b.getType().equals(Material.AIR)){
			while(ret.getBlockY()>40){
				ret.subtract(0, 1, 0);
				if(!b.getType().equals(Material.AIR)){
					ret.add(0, 1, 0);
					break;
				}
			}
		}else{
			while(ret.getBlockY()<256){
				ret.add(0, 1, 0);
				if(b.getType().equals(Material.AIR)){
					break;
				}
			}
		}
		return ret;
	}
	public Location getCenter(World w){
		return new Location(w, x*8+4.5, 100, z*8+4.5);
	}
	public TerPoint(Conf conf, String where){
		x=conf.getInt(where+".x");
		z=conf.getInt(where+".z");
	}
	
	public void save(Conf conf, String where){
		conf.set(where+".x", x);
		conf.set(where+".z", z);
	}
}
