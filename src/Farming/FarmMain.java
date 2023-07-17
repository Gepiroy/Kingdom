package Farming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockGrowEvent;

import Kingdom.main;
import UtilsKingdom.NMSUtil;
import objKingdom.Conf;
import rooms.RBl;

public class FarmMain {
	
	Random r=new Random();
	public HashMap<Location,WheatInfo> wheats=new HashMap<>();
	
	public FarmMain(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/farms.yml");
		for(String st:conf.getKeys("wheats")){
			wheats.put(conf.getLoc("wheats."+st+".loc"), new WheatInfo(conf,"wheats."+st+".info"));
		}
	}
	
	public void save(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/farms.yml");
		conf.set("wheats", null);
		int i=0;
		for(Location l:wheats.keySet()){
			conf.setLoc("wheats."+i+".loc",l);
			wheats.get(l).save(conf, "wheats."+i+".info");
			i++;
		}
		conf.save();
	}
	
	int rate=0;
	public void tick(){
		rate++;
		if(rate%20==0){//1 sec
			int m=(main.timer/7200)%4;//номер сезона (2 - зима)=>1,3=x0.3.
			for(Location loc:new ArrayList<>(wheats.keySet())){
				WheatInfo winf=wheats.get(loc);
				if(winf!=null){//can be removed by previous iteraction
					if(!loc.getBlock().getType().equals(Material.WHEAT)){
						wheats.remove(loc);
						continue;
					}
					if(m==0&&r.nextDouble()<=0.8)winf.grow++;
					else if((m==1||m==3)&&r.nextDouble()<=0.26)winf.grow++;
					if(winf.stage()>0&&winf.done<winf.stage()){
						powerUpWheat(loc, winf);
					}
				}
			}
		}
	}
	
	void powerUpWheat(Location l, WheatInfo winf){
		List<RBl> retBls=new ArrayList<>();
		List<RBl> bls=new ArrayList<>();
		bls.add(new RBl(l));
		retBls.add(new RBl(l));
		for(;retBls.size()<1000;){
			for(RBl bl:new ArrayList<>(bls)){
				if(bl.create){
					{
						RBl b=blad(bl.l.clone().add(1, 0, 0),bls);
						if(b!=null)retBls.add(b);
					}{
						RBl b=blad(bl.l.clone().add(0, 0, 1),bls);
						if(b!=null)retBls.add(b);
					}{
						RBl b=blad(bl.l.clone().add(-1, 0, 0),bls);
						if(b!=null)retBls.add(b);
					}{
						RBl b=blad(bl.l.clone().add(0, 0, -1),bls);
						if(b!=null)retBls.add(b);
					}
					bl.create=false;
				}
			}
			for(RBl bl:new ArrayList<>(bls)){
				if(!bl.create){
					if(bl.check){
						bl.check=false;
					}else{
						bls.remove(bl);
					}
				}
			}
			if(bls.size()==0)break;
		}
		boolean done=true;
		for(RBl bl:retBls){
			Block b=bl.l.getBlock();
			int stage=NMSUtil.getGrowStage(b);
		    if (stage+1>=winf.stage()) {
		    	done=false;
		    	if(r.nextBoolean()){
		    		NMSUtil.setGrowStage(b,winf.stage());
		    	}
		        if(wheats.containsKey(b.getLocation())&&!b.getLocation().equals(l)){
		        	wheats.remove(b.getLocation());
		        }
		    }
		}
		if(done)winf.done=winf.stage();
	}
	RBl blad(Location l, List<RBl> bls){
		Block b=l.getBlock();
		if(!b.getType().equals(Material.WHEAT)){
			return null;
		}
		if(!already(l,bls)){
			bls.add(new RBl(l));
			return new RBl(l);
		}
		return null;
	}
	boolean already(Location l, List<RBl> bls){
		for(RBl bl:bls){
			if(bl.l.equals(l))return true;
		}
		return false;
	}
	
	public boolean grow(BlockGrowEvent e){
		//int m=(main.timer/7200)%4;//номер сезона (2 - зима)
		Block b=e.getBlock();
		if(b.getType().equals(Material.WHEAT)){
			if(!wheats.containsKey(b.getLocation())){
				wheats.put(b.getLocation(), new WheatInfo());
			}
			return true;
		}
		return false;
	}
	
	
}
