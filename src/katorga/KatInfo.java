package katorga;

import java.util.Random;
import java.util.UUID;

import objKingdom.Conf;
import objKingdom.Role;

public class KatInfo implements Comparable<KatInfo>{
	public Role role=Role.SLAVE;
	public boolean isHere=true;
	public UUID workPlace=null;
	public UUID home=null;
	public float health=100;
	public float hate=0;
	public float gold=0;
	
	public float food=10;
	
	
	public KatInfo(){
		porog=15;//r.nextInt(8)+8;
	}
	
	final int porog;
	
	public void sec(){
		food-=0.01+food*0.01*0.005;//2000 sec to death (~33.3 mins)
		hate+=0.02*(porog-food)*(1.0/porog);
	}
	
	Random r=new Random();
	
	public boolean damage(float d){
		health-=d;
		double chanceToDeath=0.3-health*0.01;
		if(r.nextDouble()<=chanceToDeath){
			return true;
		}
		if(health<=0)return true;
		return false;
	}
	
	public void heal(float h){
		health+=h;
		if(health>100)health=100;
	}
	
	public void hate(float add){
		hate+=add;
		if(hate<0)hate=0;
	}
	
	public KatInfo(Conf conf, String st){
		role=Role.valueOf(conf.getString(st+".role"));
		isHere=conf.conf.getBoolean(st+".isHere");
		health=(float) conf.getDouble(st+".health");
		home=conf.getUUID(st+".home");
		hate=(float) conf.getDouble(st+".hate");
		gold=(float) conf.getDouble(st+".gold");
		food=(float) conf.getDouble(st+".food", 10);
		porog=15;
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".role", role.toString());
		conf.set(st+".isHere", isHere);
		conf.set(st+".health", health);
		conf.set(st+".hate", hate);
		conf.set(st+".gold", gold);
		conf.set(st+".food", food);
		if(home!=null)conf.set(st+".home", home.toString());
	}

	@Override
	public int compareTo(KatInfo t) {
		return role.importance-t.role.importance;
	}
	
}
