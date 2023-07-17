package katorga;

import Kingdom.main;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;

public class Buildings {
	
	private Buildings(){}
	
	public static Building loadBuilding(Katorga kat, Conf conf, String st){
		String name=conf.getString(st+".name","Хата Женякаса");
		BuildingInfo inf=findBuildingInfoByName(name);
		if(inf==null){
			TextUtil.debug("Невозможно загрузить постройку: name="+name+", st="+st);
			return null;
		}
		if(inf instanceof HouseInfo){
			return new House(kat, conf, st);
		}else if(inf instanceof FishingInfo){
			return new Fishing(kat, conf, st);
		}else{
			return new Building(kat, conf, st);
		}
	}
	
	public static Building createBuilding(BuildingInfo inf, Katorga kat){
		if(inf==null)return null;
		if(inf instanceof HouseInfo){
			return new House((HouseInfo) inf, kat);
		}else if(inf instanceof FishingInfo){
			return new Fishing((FishingInfo) inf, kat);
		}else{
			TextUtil.debug("&6Creating &cbuilding&6 by info "+inf);
			return new Building(inf, kat);
		}
	}
	
	public static BuildingInfo findBuildingInfoByName(String name){
		if(name==null)return null;
		for(BuildingInfo i:main.prisons.buildings){
			if(i.name.equals(name))return i;
		}
		return null;
	}
}
