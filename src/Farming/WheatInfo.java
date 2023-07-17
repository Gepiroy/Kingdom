package Farming;

import objKingdom.Conf;

public class WheatInfo {
	public int grow=0;
	public int done=0;
	
	public WheatInfo(){}
	
	public WheatInfo(Conf conf, String st){
		grow=conf.getInt(st+".grow");
		done=conf.getInt(st+".done");
	}
	
	public WheatInfo(int grow, int done){
		this.grow=grow;
		this.done=done;
	}
	
	public int stage(){
		int ret=grow/600;//4200 сек на вырост.
		if(ret>7)ret=7;
		return ret;
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".grow", grow);
		conf.set(st+".done", done);
	}
}
