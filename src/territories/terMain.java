package territories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.GepUtil;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;
import objKingdom.PlayerInfo;

public class terMain {
	public List<TerPoint> points=new ArrayList<>();
	
	public terMain(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/points.yml");
		for(String st:conf.getKeys("Points")){
			points.add(new TerPoint(conf, "Points."+st));
		}
	}
	
	public boolean isInPoints(Location l){
		TerPoint t2=new TerPoint(l);
		for(TerPoint tp:points){
			if(tp.isInMe(t2))return true;
		}
		return false;
	}
	public boolean isInPoints(TerPoint t2){
		for(TerPoint tp:points){
			if(tp.isInMe(t2))return true;
		}
		return false;
	}
	
	public TerPoint nearestPoint(Location l){
		double nd=10000;
		TerPoint ret=null;
		for(TerPoint tp:points){
			Location cp=tp.getCenter(l.getWorld());
			double dist=cp.distance(l);
			if(dist<nd){
				nd=dist;
				ret=tp;
			}
		}
		return ret;
	}
	
	int rate=0;
	String[] mines={"[STONE","[DIRT","[ANDESITE","[DIORITE","[GRANITE","[GRAVEL","_ORE"};
	public void tick(){//20/sec
		rate++;
		if(rate%4==0){//5/sec
			for(Player p:Bukkit.getOnlinePlayers()){
				drawWalls(p);
				if(isInMine(p.getEyeLocation())){
					Location l=p.getEyeLocation();
					if((main.packs.containsMat(l.clone().add(1, 0, 0).getBlock().getType(), mines)&&
						main.packs.containsMat(l.clone().subtract(1, 0, 0).getBlock().getType(), mines))||(
						main.packs.containsMat(l.clone().add(0, 0, 1).getBlock().getType(), mines)&&
						main.packs.containsMat(l.clone().subtract(0, 0, 1).getBlock().getType(), mines))){
						PlayerInfo pi=Events.plist.get(p.getName());
						if(!pi.timers.containsKey("MineAlarm")){
							TextUtil.Title(p, "&6Вам неудобно копать!", "&2Туннель слишком узкий...", 20, 20, 20);
							TextUtil.mes(p, "&6Kingdom", "Вам &6неудобно &fкопать в столь &cузком &fпроходе. Вы можете |смириться &f, или копать тоннель &bпо-шире&f.");
						}
						GepUtil.HashMapReplacer(pi.timers, "MineAlarm", 2, false, true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 8, 0));
					}
				}
			}
		}
	}
	
	Material[] mats={Material.STONE,Material.DIRT,Material.ANDESITE,Material.DIORITE,Material.GRANITE};
	
	public boolean isInMine(Location loc){
		Location l=loc.clone();
		for(int i=0;i<100;i++){
			l.add(0, 1, 0);
			Block bl=l.getBlock();
			for(Material m:mats){
				if(bl.getType().equals(m)){
					return true;
				}
			}
		}
		return false;
	}
	
	private int maxDist=24;
	private int step=3;
	
	public void addPoint(Location l){
		//if(isInPoints(l))return; Закомментил т. к. иногда просто нужно расширяться
		World w=l.getWorld();
		TerPoint point=new TerPoint(l);
		//Дальше будем заполнять пропуски, чтобы не было диких зон внутри города.
		List<TerPoint> nearestPoints=new ArrayList<>();
		Location loc=point.getCenter(w);
		for(TerPoint tp:points){
			if(tp.getCenter(w).distance(loc)<=maxDist)nearestPoints.add(tp);
		}
		for(TerPoint np:nearestPoints){
			double dist=loc.distance(np.getCenter(w));
			Vector vec=loc.toVector();
			Vector dir=np.getCenter(w).toVector().subtract(vec).normalize().multiply(step);
			for(int walked=0;walked<=dist;walked+=step){
				vec.add(dir);
				Location vl=vec.toLocation(w);
				w.spawnParticle(Particle.BARRIER, vl, 1, 0, 0, 0, 0);
				if(!isInPoints(vl)){
					points.add(new TerPoint(vl));
				}
			}
		}
		//Ну и обводка крайнего блока
		Location tmpL=l.clone();
		
		for(int i=0;i<8;i++){
			if(i==0||i==6||i==7)tmpL.add(2, 0, 0);
			else if(i==2||i==3)tmpL.add(-2, 0, 0);
			else if(i==4||i==5)tmpL.add(0, 0, -2);
			else tmpL.add(0, 0, 2);
			if(!point.isInMe(tmpL)&&!isInPoints(tmpL)){//Первая проверка для оптимизации
				points.add(new TerPoint(tmpL));
			}
		}
		if(!isInPoints(l))points.add(point);//Эта точка могла добавиться на пред. этапе
	}
	
	public void drawWalls(Player p){
		World w=p.getWorld();
		for(TerPoint tp:points){
			Location loc=tp.getCenterFromSky(w);
			if(p.getLocation().distance(loc)>20){
				continue;
			}
			if(!isInPoints(new TerPoint(tp.x+1, tp.z))){
				p.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(4, 2, 0), 10, 0, 1, 1.65, 0);
			}if(!isInPoints(new TerPoint(tp.x-1, tp.z))){
				p.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(-4, 2, 0), 10, 0, 1, 1.65, 0);
			}if(!isInPoints(new TerPoint(tp.x, tp.z+1))){
				p.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0, 2, 4), 10, 1.65, 1, 0, 0);
			}if(!isInPoints(new TerPoint(tp.x, tp.z-1))){
				p.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0, 2, -4), 10, 1.65, 1, 0, 0);
			}
		}
	}
	
	public void save(boolean reset){
		Conf conf=new Conf(main.instance.getDataFolder()+"/points.yml");
		if(reset)conf.set("Points", null);
		int i=0;
		for(TerPoint tp:points){
			tp.save(conf, "Points."+i);
			i++;
		}
		conf.save();
	}
	
	String[] ignoreSky={"[AIR","LEAVES"};
	public Location FromSky(Location l){
		Location ret=l.clone();
		if(main.packs.containsMat(ret.getBlock().getType(),ignoreSky)){
			while(ret.getBlockY()>0){
				ret.subtract(0, 1, 0);
				if(!main.packs.containsMat(ret.getBlock().getType(),ignoreSky)){
					ret.add(0, 1, 0);
					break;
				}
			}
		}else{
			while(ret.getBlockY()<256){
				ret.add(0, 1, 0);
				if(main.packs.containsMat(ret.getBlock().getType(),ignoreSky)){
					break;
				}
			}
		}
		if(ret.clone().subtract(0,2,0).getBlock().getType().equals(Material.GRASS_BLOCK))ret.subtract(0, 1, 0);//Если там была трава
		else if(ret.clone().subtract(0,3,0).getBlock().getType().equals(Material.GRASS_BLOCK))ret.subtract(0, 2, 0);//Высокий цветок
		return ret;
	}
}
