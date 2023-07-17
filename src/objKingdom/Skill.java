package objKingdom;

import org.bukkit.entity.Player;

public class Skill {
	public int lvl=0;
	public int exp=0;
	public int[] need;
	public String upMessage;
	public String downMessage;
	public int alarmLevel=0;
	public Skill(int[] need, String upMessage, String downMessage){
		this.need=need;
		this.upMessage=upMessage;
		this.downMessage=downMessage;
	}
	
	public void downGrade(){
		if(exp>0)exp--;
	}
	
	public void changeExp(int am){
		exp+=am;
		if(exp<0){
			if(lvl>0){
				
			}else exp=0;
		}
	}
	
	public void checkMes(Player p){
		
	}
}
