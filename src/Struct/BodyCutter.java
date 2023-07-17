package Struct;

import org.bukkit.event.player.PlayerInteractEvent;

public class BodyCutter extends Built{
	
	
	
	public BodyCutter(){
		
	}
	
	@Override
	public void clicked(PlayerInteractEvent e){
		AbsLoc where = new AbsLoc(e.getClickedBlock().getLocation());
		if(where.equal(1, 0, 0)){//knife-slot
			
		}
	}
}
