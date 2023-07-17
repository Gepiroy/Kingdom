package NameGenerator;

import java.util.Random;

import UtilsKingdom.TextUtil;

public class NameGenerator {
	static Liters[] lits=new Liters[]{
			new Liters(
					new char[][]{
						new char[]{'л'},//без щ
						new char[]{'м'},
						new char[]{'н'},
						new char[]{'р'},
						new char[]{'в','ф'},
						new char[]{'г','к','б','ч','п','т','д','ц','й'},
						new char[]{'ж','ш','щ','х'},
						new char[]{'з','с'}
						}, 1),
			new Liters(
					new char[][]{
						new char[]{'а','о','у','ы','э','и'},
						new char[]{'я','ю','е'}//без ё
						}, 1)
	};
	
	static Random r=new Random();
	
	public static String randName(){
		TextUtil.debug("gen new name...");
		int am=0;
		int prev=-1;
		char prevc='0';
		String name="";
		for(int i=0;i<r.nextInt(5)+3;i++){
			int type=r.nextInt(3);
			if(type==2)type=1;
			String deb="t="+type;
			if(type==prev){
				am++;
				if(am>lits[type].maxSame){
					type++;
					if(type==2)type=0;
					deb+="; nope, t="+type+".";
					am=0;
				}
				TextUtil.debug("after "+prevc+" am="+am);
			}
			TextUtil.debug(deb);
			prev=type;
			char toAdd=lits[type].getC(prevc, r);
			prevc=toAdd;
			if(i==0)toAdd=((toAdd+"").toUpperCase()).charAt(0);
			name+=toAdd;
		}
		if(prev==1){
			name+=lits[0].getC(prevc, r);
		}
		TextUtil.debug("name "+name+" generated!");
		return name;
	}
}
