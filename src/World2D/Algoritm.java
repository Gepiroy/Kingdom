package World2D;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import UtilsKingdom.TextUtil;

public class Algoritm {
	
	List<Loc2> list;
	List<Loc2> obj=new ArrayList<>();
	List<Loc2> nextmp=new ArrayList<>();
	
	public Algoritm(){
		list=new ArrayList<>(World2.blocks.keySet());
	}
	
	public void algoritm(){
		long start=new Date().getTime();
		int objs=0;
		TextUtil.debug("Starting alg from &b"+list.get(0));
		addNext(list.get(0));
		//World2.objs.clear();
		while(list.size()>0){
			List<Loc2> tmp=new ArrayList<>(nextmp);
			nextmp.clear();
			for(Loc2 t:tmp){
				for(Loc2 l:World2.findNearLocs(t, 3, new ArrayList<>(list))){
					addNext(l);
				}
			}
			obj.addAll(tmp);
			if(nextmp.size()==0){
				TextUtil.debug("Alg fin with &a"+obj.size()+"&f in one obj.");
				if(obj.size()>5){
					//World2.objs.add(new Obj(new ArrayList<>(obj)));
					objs++;
				}
				obj.clear();
				if(list.size()>0){
					TextUtil.debug("Continuing alg with &b"+list.get(0));
					addNext(list.get(0));
				}
			}
			if(list.size()==0){
				for(Loc2 l:nextmp){
					obj.add(l);
				}
				TextUtil.debug("The last object built. Size=&6"+obj.size());
				objs++;
			}
		}
		TextUtil.debug("Total objs: &e"+objs);
		TextUtil.debug("&bПроизводительность алгоритма: &f"+(new Date().getTime()-start)+" мс.");
	}
	void addNext(Loc2 l){
		nextmp.add(l);
		list.remove(l);
	}
}
