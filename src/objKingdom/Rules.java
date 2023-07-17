package objKingdom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Rules {
	
	public final int slot;
	public final Material mat;
	public final String name;
	public final List<Rule> rules;
	
	public Rules(Conf conf, String st){
		slot = conf.getInt(st+".slot");
		mat = Material.getMaterial(conf.getString(st+".mat"));
		name=conf.getString(st+".name");
		List<Rule> rules = new ArrayList<>();
		for(String s:conf.getKeys(st+".rules")){
			rules.add(new Rule(conf,st+".rules."+s));
		}
		this.rules=rules;
	}
}
