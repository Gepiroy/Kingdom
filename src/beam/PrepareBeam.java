package beam;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import UtilsKingdom.TextUtil;

public class PrepareBeam {
	public final Player p;
	public final Location start;
	
	public PrepareBeam(Player p, Location start){
		this.p=p;
		this.start=start;
	}
	
	public int height=0;
	
	public void update(){
		double rad=-p.getEyeLocation().getPitch()*(Math.PI/180);
		Location fix=p.getLocation();
		fix.setY(start.getY());
		double d=fix.distance(start);
		double h2 = p.getEyeLocation().getY()-start.getY();
		//if(rad<0)rad=0;
		if(rad==Math.PI/2)rad=Math.PI/2.01;
		double h1 = d*Math.sin(rad) / Math.sin(Math.PI/2+rad);
		height = (int) Math.ceil(h1 + h2);
		//if(height<0)height=0;
		//height++;
		if(height>10)height=10;
		if(height<1)height=1;
		Location l=start.clone();
		for(int i=0;i<height;i++){
			p.spawnParticle(Particle.CLOUD, l, 1, 0, 0, 0, 0);
			l.add(0, 1, 0);
			Material mat = l.getBlock().getType();
			if(mat.getHardness()!=0){
				TextUtil.Title(p, "", "&cМешает блок!", 0, 5, 10);
				return;
			}
		}
		TextUtil.Title(p, "", "&bДлина: &e"+height, 0, 5, 10);
	}
	
	public void drop(){
		new Beam(start, height-1, Material.OAK_LOG, p.getEyeLocation().getDirection()).fall();
	}
}
