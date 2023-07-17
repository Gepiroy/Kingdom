package katorga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import objKingdom.Role;
import rooms.SlaveInfo;

public class Organizing {
	public HashMap<SlaveInfo,Role> roles=new HashMap<>();
	public List<SlaveInfo> members=new ArrayList<>();
	public final Katorga target;
	public final boolean first;
	
	public Organizing(Katorga target, boolean first){
		this.target=target;
		this.first=first;
	}
	
	public Role getRole(SlaveInfo sinf){
		Role role=Role.SLAVE;
		if(roles.containsKey(sinf))role=roles.get(sinf);
		return role;
	}
	
	public void toggleMember(SlaveInfo sinf){
		if(members.contains(sinf))members.remove(sinf);
		else members.add(sinf);
	}
	
	public void toggleRole(SlaveInfo sinf){
		Role role=Role.SLAVE;
		if(roles.containsKey(sinf))role=roles.get(sinf);
		if(role==Role.SLAVE){
			role=Role.GUARD;
		}else if(role==Role.GUARD){
			role=Role.RUNNER;
		}else if(role==Role.RUNNER){
			role=Role.WARDEN;
		}else{
			role=Role.SLAVE;
		}
		if(roles.containsKey(sinf))roles.replace(sinf, role);
		else roles.put(sinf, role);
	}
}
