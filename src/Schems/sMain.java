package Schems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import Kingdom.main;
import UtilsKingdom.GeomUtil;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;

public class sMain {
	
	public sMain(){
		
	}
	
	public List<EditableScheme> eSchems=new ArrayList<>();
	
	int timer=0;
	public void tick(){// 20/sec
		timer++;
		if(timer%1==0){// 20/sec
			for(EditableScheme s:eSchems){
				s.show();
			}
			for(Player p:Bukkit.getOnlinePlayers()){
				ItemStack hitem=p.getInventory().getItemInMainHand();
				if(hitem.getType().equals(Material.FEATHER)){
					xz look=Looking(p);
					if(look!=null){
						EditableScheme s=findPlayersScheme(p.getName());
						//GeomUtil.drawCube(s.l.clone().add(look.x*s.size, 0, look.z*s.size), s.size, 0, 0, 0, s.size);
						look.drawSquare(s.l.clone().add(look.x*s.size, 0.01, look.z*s.size), 2, s.size/2, 0, 0, 0, s.size/2, null);
						if(s.p1!=null){
							GeomUtil.lineBetweenTwoPoints(s.xzToLoc(s.p1, true), s.xzToLoc(look, true), 0.333f*s.size, 0, 0, 255, s.size/2);
							List<xz> betw=s.betweenPoints(s.xzToLoc(s.p1, true), s.xzToLoc(look, true));
							for(xz b:betw){
								b.drawSquare(s.xzToLoc(b, false), 2, s.size/2, 100, 100, 255, s.size, null);
								//GeomUtil.drawWall(s.xzToLoc(b, false), s.size, s.wallsHeight, 100, 100, 255, s.size/2);
							}
						}
					}
				}
			}
		}
	}
	
	public UUID getUUIDfromItem(ItemStack item, String search){
		if(item==null||!item.hasItemMeta())return null;
		ItemMeta meta=item.getItemMeta();
		List<String> lore=meta.getLore();
		if(lore==null)return null;
		for(String st:lore){
			if(st.contains(search))return UUID.fromString(st.substring(search.length()));
		}
		return null;
	}
	
	public boolean clickteract(PlayerInteractEvent e){//calls only при ПКМ с предметом в руках
		Player p=e.getPlayer();
		ItemStack hitem=p.getInventory().getItemInMainHand();
		if(hitem.getType().equals(Material.PAPER)&&e.getClickedBlock()!=null){
			EditableScheme s=findPlayersScheme(p.getName());
			if(s!=null){
				UUID id=getUUIDfromItem(hitem, ChatColor.BLACK+"SID=");
				if(id==null){
					id=UUID.randomUUID();
					ItemMeta meta=hitem.getItemMeta();
					List<String> lore=new ArrayList<>();
					lore.add(ChatColor.BLACK+"SID="+id);
					meta.setLore(lore);
					meta.setDisplayName(ChatColor.GOLD+"Чертёж");
					hitem.setItemMeta(meta);
				}
				Conf conf=new Conf(main.instance.getDataFolder()+"/schs/"+id+".yml");
				s.save(conf, "a");
				conf.saveAsync();
				eSchems.remove(s);
				TextUtil.mes(p, "", "Планирование окончено!");
			}else{
				UUID id=getUUIDfromItem(hitem, ChatColor.BLACK+"SID=");
				if(id==null){
					eSchems.add(new EditableScheme(e.getClickedBlock().getLocation().add(0, 1, 0),p.getName()));
				}else{
					eSchems.add(new EditableScheme(new Conf(main.instance.getDataFolder()+"/schs/"+id+".yml"),"a",p.getName(),e.getClickedBlock().getLocation().add(0, 1, 0)));
				}
				
				TextUtil.mes(p, "", "Планирование начато! Используйте перо для добавления стен и палку для обозначения фундамента.");
			}
			return true;
		}
		if(hitem.getType().equals(Material.FEATHER)){
			xz look=Looking(p);
			if(look!=null){
				EditableScheme s=findPlayersScheme(p.getName());
				if(e.getAction().toString().contains("RIGHT")){
					//if(!s.alreadyExists(look))s.blocks.add(look);
					if(s.p1==null)s.p1=look;
					else{
						List<xz> betw=s.betweenPoints(s.xzToLoc(s.p1, true), s.xzToLoc(look, true));
						for(xz b:betw){
							if(!s.alreadyExists(b))s.blocks.add(b);
						}
						s.p1=null;
					}
				}else{
					for(xz b:new ArrayList<>(s.blocks)){//already contains check
						if(b.isHere(look)){
							s.blocks.remove(b);
							return true;
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public xz Looking(Player p){
		EditableScheme s=findPlayersScheme(p.getName());
		if(s==null)return null;
		Location loc=p.getEyeLocation();
		Vector vec = loc.toVector();
		Vector v1 = loc.getDirection().normalize().multiply(0.1);
		for(int i=0;i<40;i++){
			vec.add(v1);
			if(Math.abs(vec.getY()-s.l.getY())<=0.051){
				return s.xzFromVec(vec);
			}
		}
		return null;
	}
	
	EditableScheme findPlayersScheme(String p){
		for(EditableScheme s:eSchems){
			if(s.placer.equals(p))return s;
		}
		return null;
	}
	
}
