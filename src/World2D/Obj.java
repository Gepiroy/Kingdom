package World2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

import UtilsKingdom.GeomUtil;
import UtilsKingdom.TextUtil;

public class Obj {
	public List<Loc2> locs=new ArrayList<>();
	
	public Loc2 center;
	int diam;
	int xmax, xmin, zmax, zmin;
	
	public Obj(List<Loc2> locs){
		this.locs=locs;
		updateCenter();
	}
	
	public void updateCenter(){
		Loc2 f=locs.get(0);
		xmax=f.x; xmin=f.x; zmax=f.z; zmin=f.z;
		for(Loc2 l:locs){
			if(l.x>xmax)xmax=l.x;
			if(l.x<xmin)xmin=l.x;
			if(l.z>zmax)zmax=l.z;
			if(l.z<zmin)zmin=l.z;
		}
		center=new Loc2(xmin+(xmax-xmin)/2, zmin+(zmax-zmin)/2);
		diam=xmax-xmin;
		if(zmax-zmin>diam)diam=zmax-zmin;
	}
	
	public void showBox(){
		
	}
	
	Comparator<Loc2> comp=new Comparator<Loc2>() {
		@Override
		public int compare(Loc2 l1, Loc2 l2) {
			return l2.x-l1.x;
		}
	};
	Comparator<Loc2> radComp=new Comparator<Loc2>() {
		@Override
		public int compare(Loc2 l1, Loc2 l2) {
			return (int) (l1.toGrad(center)-l2.toGrad(center));
		}
	};
	
	boolean isHereByWalls(Loc2 l){
		boolean xp=false, xm=false, zp=false, zm=false;
		for(Loc2 l2:locs){
			if(l2.x==l.x){
				if(l2.z>=l.z)zp=true;
				if(l2.z<=l.z)zm=true;
			}
			if(l2.z==l.z){
				if(l2.x>=l.x)xp=true;
				if(l2.x<=l.x)xm=true;
			}
		}
		if(!xm){
			//int xmin=this.xmin;
			Collections.sort(locs,comp);
			int minz=l.z-zmin;
			//Loc2 nearest;
			for(Loc2 l2:locs){
				if(l2.x<l.x&&Math.abs(l.z-l2.z)<minz){
					minz=Math.abs(l.z-l2.z);
					//nearest=l2;
				}
			}
		}
		return xp&&xm&&zp&&zm;
	}
	
	void shootToCenter(){
		long startTime=new Date().getTime();
		//Collections.sort(locs, radComp);
		HashMap<Integer, List<Loc2>> ls=new HashMap<>();
		for(Loc2 l:locs){
			int g=(int) (l.toGrad(center)*(1.0/diam));
			if(!ls.containsKey(g)){
				ls.put(g, new ArrayList<>());
			}
			ls.get(g).add(l);
		}
		boolean unvoiding=false;
		int i=0;
		int max=diam;
		for(;i<max*2;i++){
			int r=i;
			if(r>=max)r-=max;
			if(ls.containsKey(r)){
				unvoiding=true;
			}else if(unvoiding){
				break;
			}
		}
		int galaxyStart=i-1;
		for(int a=1;a<max;a++){
			int rad=galaxyStart+a;
			if(rad>=max)rad-=max;
			if(ls.containsKey(rad)){
				Loc2 start=ls.get(galaxyStart).get(0);
				Loc2 end=ls.get(rad).get(0);
				TextUtil.debug("&bПроизводительность стрельбы в центр &e"+locs.size()+"&b блоков: &f"+(new Date().getTime()-startTime)+" мс.");
				GeomUtil.lineBetweenTwoPoints(new Location(World2.world, start.x+0.5, 70.5, start.z+0.5), new Location(World2.world, end.x+0.5, 70.5, end.z+0.5), 1, 0, 0, 255, 1);
				break;
			}
		}
	}
}
