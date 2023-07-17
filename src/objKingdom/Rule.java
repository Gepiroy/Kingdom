package objKingdom;

import org.bukkit.Material;

public class Rule {
	public final String name;
	public final String lore;
	public final Material mat;
	
	public Rule(Conf conf, String st){
		name=conf.getString(st+".name");
		lore=conf.getString(st+".lore");
		mat=Material.getMaterial(conf.getString(st+".mat"));
	}
}
