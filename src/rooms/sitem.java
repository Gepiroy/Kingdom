package rooms;

import org.bukkit.Material;

import objKingdom.Conf;

public class sitem {
	public Material mat;
	public int am;
	
	public sitem(Material mat, int am){
		this.mat=mat;
		this.am=am;
	}
	
	public sitem(Conf conf, String st){
		mat=Material.getMaterial(conf.getString(st+".mat"));
		am=conf.getInt(st+".am");
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".mat", mat.toString());
		conf.set(st+".am", am);
	}
}
