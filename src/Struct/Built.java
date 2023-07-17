package Struct;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

public class Built {
	public int rotation = 0;
	/*\Z
	 * 
	 *    0 X 0
	 *  3       1
	 *  X   Y   X
	 *  3       1
	 *    2 X 2 
	 * ---------------> X
	 */
	public Structure str;
	public Location loc;
	
	public Location toMyLoc(Location l){
		Location ret = l.clone().subtract(loc);
		Location re = ret.clone();
		if(rotation==1){
			ret.setX(-re.getZ());
			ret.setZ(re.getX());
		}else if(rotation==2){
			ret.setX(-re.getX());
			ret.setZ(-re.getZ());
		}else if(rotation==3){
			ret.setX(re.getZ());
			ret.setZ(-re.getX());
		}
		return ret;
	}
	
	public void clicked(PlayerInteractEvent e){
		
	}
}
