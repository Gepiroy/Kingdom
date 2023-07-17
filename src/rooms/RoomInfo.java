package rooms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.scheduler.BukkitRunnable;

import Kingdom.main;
import UtilsKingdom.TextUtil;

public class RoomInfo {
	
	public int size=1;
	
	List<Location> doors = new ArrayList<>();
	
	public boolean isCreated=false;
	
	public void updateRoom(Location loc){
		new BukkitRunnable() {
			@Override
			public void run() {
				resetRoom(loc);
			}
		}.runTaskAsynchronously(main.instance);
	}
	
	public void resetRoom(Location loc){
		doors.clear();
		List<RBl> bls=new ArrayList<>();
		bls.add(new RBl(loc));
		int am=1;
		for(;am<1000;){
			for(RBl bl:new ArrayList<>(bls)){
				if(bl.create){
					am+=blad(bl.l.clone().add(1, 0, 0),bls);
					am+=blad(bl.l.clone().add(0, 1, 0),bls);
					am+=blad(bl.l.clone().add(0, 0, 1),bls);
					am+=blad(bl.l.clone().add(-1, 0, 0),bls);
					am+=blad(bl.l.clone().add(0, -1, 0),bls);
					am+=blad(bl.l.clone().add(0, 0, -1),bls);
					bl.create=false;
				}
			}
			for(RBl bl:new ArrayList<>(bls)){
				if(!bl.create){
					if(bl.check){
						bl.check=false;
					}else{
						bls.remove(bl);
					}
				}
			}
			if(bls.size()==0)break;
		}
		size=am;
		if(am>1000){
			TextUtil.debug("&cmore than 1000 blocks! &f(&e"+am+"&f)");
		}else{
			if(doors.size()>0)isCreated=true;
			TextUtil.debug("&aless than 1000 blocks! &f(&e"+am+"&f)");
		}
		TextUtil.debug("&bdoors-am: &e"+doors.size());
	}
	
	String[] igns={"_BED","_STAIRS"};
	
	int blad(Location l, List<RBl> bls){
		Block b=l.getBlock();
		if(!b.getType().equals(Material.AIR)){
			if(Tag.DOORS.isTagged(b.getType())){
				Door door = (Door) b.getBlockData();
				if(door.getHalf().equals(Half.BOTTOM)){
					doors.add(l);
				}
			}
			return 0;
		}
		if(!already(l,bls)){
			bls.add(new RBl(l));
			return 1;
		}
		return 0;
	}
	
	boolean already(Location l, List<RBl> bls){
		for(RBl bl:bls){
			if(bl.l.equals(l))return true;
		}
		return false;
	}
	
	boolean containsMat(Material mat, String[] mats){
		for(String st:mats){
			char c=st.charAt(0);
			if(c=='['&&mat.toString().equals(st.substring(1)))return true;
			else if(mat.toString().contains(st))return true;
		}
		return false;
	}
}
