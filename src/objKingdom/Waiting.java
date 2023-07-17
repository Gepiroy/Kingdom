package objKingdom;

import org.bukkit.Location;

public class Waiting {
	public int timer=100;
	public String type;
	public Location loc;
	public double dist=0;
	public String outMes;
	public String Ptimer;
	
	public Waiting(int timer, String type, Location loc, double dist, String outMes, String Ptimer){
		this.timer=timer;
		this.type=type;
		this.loc=loc;
		this.dist=dist;
		this.outMes=outMes;
		this.Ptimer=Ptimer;
	}
}
