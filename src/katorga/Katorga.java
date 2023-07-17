package katorga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import Kingdom.main;
import UtilsKingdom.TextUtil;
import invsUtil.InvEvents;
import objKingdom.Conf;
import objKingdom.Food;
import objKingdom.Mint;
import objKingdom.Role;
import rooms.SlaveInfo;
import rooms.Slaves;
import rooms.sitem;

public class Katorga {
	public final UUID kid;
	public String name="err";
	public String owner;
	public Storage export;
	public Storage storage;
	public HashMap<SlaveInfo,KatInfo> members=new HashMap<>();
	public List<String> messages=new ArrayList<>();
	
	public int freeFood=5;
	
	public Mint baseTax=new Mint(0, 100);
	public HashMap<Material, Tax> taxes=new HashMap<>();
	
	public HashMap<UUID,Building> buildings=new HashMap<>();
	public HashMap<String,UUID> causes=new HashMap<>();
	
	public List<Caravan> caravans=new ArrayList<>();
	
	public int digType=0;
	
	public Material[] digs = {Material.DIRT,Material.COBBLESTONE,Material.OAK_LOG};
	
	public int wallLvl=0;//0-отсутствие; 1-разметка; 2-заборы; 3-частокол; 4-стена... Мб ещё вышки при большом уровне.
	
	Random r=new Random();
	
	public Katorga(String owner, UUID kid){
		this.owner=owner;
		this.kid=kid;
		this.name=kid.toString().substring(0, 4);
		create();
	}
	
	public Katorga(Conf conf, String st){
		owner=conf.getString(st+".owner");
		kid=UUID.fromString(st.substring(2));
		name=conf.getString(st+".name",kid.toString().substring(0,4));
		storage=new Storage(conf, st+".storage");
		export=new Storage(conf, st+".export");
		for(String s:conf.getKeys(st+".members")){
			KatInfo kinf=new KatInfo(conf,st+".members."+s);
			SlaveInfo sinf=Slaves.findById(UUID.fromString(s));
			members.put(sinf, kinf);
		}
		messages=conf.getStringList(st+".messages");
		digType=conf.getInt(st+".digType");
		freeFood=conf.getInt(st+".freeFood", 5);
		baseTax.add(conf.getInt(st+".tax", 50));
		giveFoodMode=conf.getInt(st+".giveFoodMode", 0);
		for(String s:conf.getKeys(st+".caravans")){
			caravans.add(new Caravan(conf, st+".caravans."+s));
		}
		for(String s:conf.getKeys(st+".buildings")){
			Building b=Buildings.loadBuilding(this, conf, st+".buildings."+s);
			buildings.put(UUID.fromString(s),b);
		}
		for(String s:conf.getKeys(st+".taxes")){
			taxes.put(Material.valueOf(s),new Tax(conf, st+".taxes."+s));
		}
		create();
	}
	
	void create(){
		globalFear();
	}
	
	public void save(Conf conf, String st){
		conf.set(st+".owner", owner);
		conf.set(st+".name", name);
		int i=0;
		conf.set(st+".storage", null);
		storage.save(conf, st+".storage");
		export.save(conf, st+".export");
		conf.set(st+".members", null);
		for(SlaveInfo sinf:members.keySet()){
			KatInfo kinf=members.get(sinf);
			kinf.save(conf, st+".members."+sinf.id);
		}
		conf.set(st+".messages", messages);
		conf.set(st+".digType", digType);
		conf.set(st+".tax", baseTax.get());
		conf.set(st+".giveFoodMode", giveFoodMode);
		conf.set(st+".freeFood", freeFood);
		i=0;
		for(Caravan c:caravans){
			c.save(conf, st+".caravans."+i);
			i++;
		}
		for(UUID id:buildings.keySet()){
			Building b=buildings.get(id);
			b.save(conf, st+".buildings."+id);
		}
		for(Material mat:taxes.keySet()){
			taxes.get(mat).save(conf, st+".taxes."+mat);
		}
	}
	

	enum Regime{
		WORK,
		SLEEP
	}
	
	int power=0;
	int lastHour=-1;
	
	Regime regime=Regime.WORK;
	
	public void sec(){
		for(SlaveInfo sinf:new ArrayList<>(members.keySet())){
			KatInfo kinf=members.get(sinf);
			if(sinf==null){
				if(kinf.isHere)messages.add("&8Кто-то пропал, находясь на каторге.");
				else messages.add("&8Кто-то пропал, находясь вне каторги.");
				members.remove(sinf);
				continue;
			}
		}
		//Верхние строки обеспечивают безопасность.
		for(UUID hid:findBuildingsByName("Барак", true)){
			House h=(House) buildings.get(hid);
			h.updateMembers(members);
		}
		int hour=(int) Bukkit.getWorld("world").getTime()/1000+8;
		if(hour!=lastHour){
			if(hour!=-1)changeHour(hour);
			lastHour=hour;
		}
		//Любые изменения - дальше этой строки.
		if(hour>=24)hour-=24;
		for(SlaveInfo sinf:new ArrayList<>(members.keySet())){
			KatInfo kinf=members.get(sinf);
			kinf.sec();
			if(kinf.food<freeFood){
				giveFood(kinf);
			}
		}
		if(regime==Regime.WORK){
			List<SlaveInfo> slaves=new Find(this).isHere(true).role(Role.SLAVE).find();
			int addPower=0;
			hires();
			for(UUID bid:getUnfinishedBuildings()){
				for(SlaveInfo sinf:slaves){
					KatInfo kinf=members.get(sinf);
					kinf.workPlace=bid;
				}
				break;
			}
			for(SlaveInfo sinf:slaves){
				KatInfo kinf=members.get(sinf);
				if(kinf.workPlace==null){
					addPower++;
					kinf.hate+=0.01*baseTax.get()*0.01;
					if(r.nextDouble()<=0.003){
						hurt(sinf, 10+r.nextInt(15), "&6% &cпогиб&f, пока копал.");
						continue;
					}
				}else{
					if(buildings.containsKey(kinf.workPlace)){
						Building b=buildings.get(kinf.workPlace);
						if(b.toReady>0){
							b.toReady--;
							if(r.nextDouble()<=0.0025){
								hurt(sinf, 10+r.nextInt(15), "&6% &cпогиб&f на стройке здания &e"+b.info.name+"&f.");
							}
							if(b.toReady<=0){
								builtEvent(b);
							}
						}else{
							//do nothing -_-
						}
					}else{
						kinf.workPlace=null;
					}
				}
			}
			power+=addPower;
			int add=power/5;
			add(new sitem(digs[digType],add));
			power-=add*5;
		}
		for(Caravan c:new ArrayList<>(caravans)){
			if(c.sec()){
				if(!c.toOwner){
					addMember(c.sinf, null);
				}
				caravans.remove(c);
			}
		}
		
		if(export.am()>=64){
			sendCaravan(null, "&fПрибыл гонец &6% &fс грузом.");
		}
	}
	
	void hires() {
		for(Building b:buildings.values()){
			if(b instanceof Fishing){
				Fishing f=(Fishing) b;
				f.hire();
				f.sec();
			}
		}
	}

	void builtEvent(Building b){
		messages.add("&aЗавершено строительство здания &e"+b.info.name+"&f.");
		for(SlaveInfo sinf:new Find(this).workPlace(b.id).find()){
			KatInfo kinf=members.get(sinf);
			kinf.workPlace=null;
		}
		sendCaravan(null, "&fПрибыл гонец &6% &fс вестью о оконченной стройке.");
	}
	
	float globalFear=0;
	
	void globalFear(){
		globalFear=0;
		int guards=findPawnsByRole(Role.GUARD, true).size();
		int slaves=findPawnsByRole(Role.SLAVE, true).size();
		float GuardPerSlave = 1.0f*guards/slaves;
		globalFear+=GuardPerSlave;
		globalFear+=1;//Страх оказаться одним
	}
	
	void escape(SlaveInfo escaper){
		String mes="&6"+escaper.name+" &eпытается сбежать! ";
		List<SlaveInfo> nearGuard=new ArrayList<>();
		double chance=0.4;
		//if(regime==Regime.SLEEP)chance*=0.5;
		for(SlaveInfo sinf:findPawnsByRole(Role.GUARD, true)){
			if(r.nextDouble()<=chance){
				nearGuard.add(sinf);
			}
		}
		int size=nearGuard.size();
		if(size>0){
			mes+="&9";
			for(SlaveInfo sinf:nearGuard){
				mes+=sinf.name+"&f, &9";
			}
			mes=mes.substring(0,mes.length()-4);
			if(size>1){
				mes+=" &fгонятся за ним!";
			}else{
				mes+=" &fгонится за ним!";
			}
		}else{
			mes+="&cРядом нет охранников!";
		}
		messages.add(mes);
		chance=1.0/Math.pow(2, size);
		if(r.nextDouble()<=chance){
			messages.add("&6"+escaper.name+" &cсбежал!");
			members.remove(escaper);
			Slaves.regDeath(escaper);
		}else{
			if(!hurt(escaper, r.nextFloat()*50+20, "&6% &5погиб при задержании.")){
				messages.add("&6"+escaper.name+" &8отмудохан и пошёл работать.");
				KatInfo kinf=members.get(escaper);
				kinf.hate=0;
			}
		}
	}
	
	void changeHour(int hour){
		globalFear();
		rasprToHouses();
		if(hour>=23||hour<=7){
			regime=Regime.SLEEP;
		}else{
			regime=Regime.WORK;
		}
		if(regime==Regime.SLEEP){//Время сна
			List<SlaveInfo> slaves=new Find(this).isHere(true).role(Role.SLAVE).find();
			for(SlaveInfo sinf:slaves){
				KatInfo kinf=members.get(sinf);
				House h=(House) buildings.get(kinf.home);
				if(h!=null){
					kinf.heal(h.info().health*(kinf.food/10.0f));
					kinf.hate(h.info().comfort*0.1f);
				}
			}
		}
		for(SlaveInfo sinf:new Find(this).isHere(true).find()){
			KatInfo kinf=members.get(sinf);
			if(kinf.food<=0)hurt(sinf, 5, "&6% &cпогиб &8от голода.");
			if(kinf.hate>globalFear*50){
				double chance=0.1;
				chance*=(1-kinf.health*0.01);
				if(hour<23&&hour>7)chance*=0.33;
				if(r.nextDouble()<=chance)escape(sinf);
			}
		}
	}
	
	public int giveFoodMode=0;
	void giveFood(KatInfo kinf){
		List<Food> avals=storage.avaliableFood();
		if(avals.size()==0)return;
		Food f=null;
		if(giveFoodMode==0)f=avals.get(r.nextInt(avals.size()));
		else{
			if(giveFoodMode==1)Collections.sort(avals, bestFoodComp);
			else Collections.sort(avals, unbestFoodComp);
			f=avals.get(0);
		}
		storage.add(f.mat, -1);
		kinf.food+=f.food;
	}
	
	final Comparator<Food> bestFoodComp=new Comparator<Food>(){
	     @Override
	     public int compare(Food s1, Food s2){
	         return s1.food-s2.food;
	     }
	};
	final Comparator<Food> unbestFoodComp=new Comparator<Food>(){
	     @Override
	     public int compare(Food s1, Food s2){
	         return s2.food-s1.food;
	     }
	};
	
	public void sendCaravan(String cause, String hello){
		SlaveInfo sinf=getOneRunner();
		if(sinf!=null){
			if(cause!=null){
				if(causes.containsKey(cause))return;
				causes.put(cause, sinf.id);
			}
			Caravan c=new Caravan(sinf,kid);
			messages.add("&fСобран и отправлен караван &e"+sinf.name+" &8(cause="+cause+")");
			int i=0;
			int max=5;
			for(sitem sit:new ArrayList<>(export.storage)){
				int am=sit.am;
				int stack=sit.mat.getMaxStackSize();
				if(am>stack*(max-i))am=stack*(max-i);
				sinf.addToInv(new sitem(sit.mat, am));
				i+=(am-1)/stack+1;
				export.add(new sitem(sit.mat,-am));
				if(i>=max)break;
			}
			c.toOwner=true;
			c.timer=15;
			i=messages.size()-12;
			if(i<0)i=0;
			for(;i<messages.size();i++){
				c.messages.add(messages.get(i));
			}
			c.hello=hello;
			caravans.add(c);
			members.get(sinf).isHere=false;
		}
	}
	
	public void backCaravan(UUID id){
		for(String cause:new ArrayList<>(causes.keySet())){
			if(causes.get(cause).equals(id)){
				causes.remove(cause);
				break;
			}
		}
	}
	
	public SlaveInfo getOneRunner(){
		for(SlaveInfo sinf:members.keySet()){
			KatInfo kinf=members.get(sinf);
			if(kinf.role==Role.RUNNER&&kinf.isHere){
				return sinf;
			}
		}
		return null;
	}
	
	public void addMember(SlaveInfo sinf, Role role){
		KatInfo kit=members.get(sinf);
		if(kit!=null){
			kit.isHere=true;
			if(role!=null)kit.role=role;
			return;
		}
		KatInfo nkit=new KatInfo();
		if(role!=null)nkit.role=role;
		members.put(sinf,nkit);
	}
	
	public void addBuilding(String name){
		for(BuildingInfo b:main.prisons.buildings){
			if(b.name.equals(name)){//TODO useless code!
				UUID id=UUID.randomUUID();
				if(b instanceof HouseInfo){
					House h=(House) Buildings.createBuilding(b, this);
					h.id=id;
					buildings.put(id, h);
				}else if(b instanceof FishingInfo){
					Fishing f=(Fishing) Buildings.createBuilding(b, this);
					TextUtil.debug("&6Adding &afishing &6by name &e"+name);
					f.id=id;
					buildings.put(id, f);
				}else{
					TextUtil.debug("&6Adding &cbuilding &6by name &e"+name);
					Building f=Buildings.createBuilding(b, this);
					f.id=id;
					buildings.put(id, f);
				}
			}
		}
	}
	
	void rasprToHouses(){
		List<SlaveInfo> guys=new ArrayList<>();//Очередь на получение жилья
		for(SlaveInfo id:findPawnsByRole(Role.WARDEN))if(id!=null)guys.add(id);
		for(SlaveInfo id:findPawnsByRole(Role.RUNNER))if(id!=null)guys.add(id);
		for(SlaveInfo id:findPawnsByRole(Role.GUARD))if(id!=null)guys.add(id);
		for(SlaveInfo id:findPawnsByRole(Role.SLAVE))if(id!=null)guys.add(id);
		List<UUID> houses=new ArrayList<>();//Список домов по рейтингу комфорта
		List<UUID> fins=getFinishedHouses();
		while(fins.size()>0){
			UUID maxId=null;
			int maxC=-1;
			for(UUID id:fins){
				House h=(House) buildings.get(id);
				h.members.clear();
				if(h.info().comfort>maxC){
					maxC=h.info().comfort;
					maxId=id;
				}
			}
			houses.add(maxId);
			fins.remove(maxId);
			//maxC=-1;
			//maxId=null;
		}
		//TextUtil.debug("[raspr] &ehouses: &f"+houses);
		for(SlaveInfo sinf:guys){
			KatInfo kinf=members.get(sinf);
			kinf.home=null;
			for(UUID hid:new ArrayList<>(houses)){
				House h=(House) buildings.get(hid);
				if(h.isFull()){
					houses.remove(hid);
					//String deb="[raspr] &ehouse (";
					//deb+=shortId(hid)+") is full (";
					//for(UUID mid:h.members){
					//	deb+=shortId(mid)+";";
					//}
					//deb+="), rem it.";
					//TextUtil.debug(deb);
					continue;
				}
				kinf.home=hid;
				h.members.add(sinf.id);
				break;
			}
		}
	}
	
	String shortId(UUID id){
		return id.toString().substring(0, 4);
	}
	
	List<SlaveInfo> findPawnsByRole(Role role){
		return new Find(this).role(role).find();
	}
	
	List<SlaveInfo> findPawnsByRole(Role role, boolean isHere){
		return new Find(this).role(role).isHere(isHere).find();
	}
	
	List<SlaveInfo> findFreePawnsByRole(Role role, boolean isHere){
		return new Find(this).free(true).role(role).isHere(isHere).find();
	}
	
	public static class Find{
		private final Katorga kat;
		public Find(Katorga kat){
			this.kat=kat;
		}
		private boolean freed=false;
		private boolean free=false;
		private boolean isHered=false;
		private boolean isHere=false;
		private Role role=null;
		private UUID workPlace=null;
		private UUID home=null;
		
		public Find free(boolean free){
			freed=true;
			this.free=free;
			return this;
		}
		public Find role(Role role){
			this.role=role;
			return this;
		}
		public Find isHere(boolean isHere){
			isHered=true;
			this.isHere=isHere;
			return this;
		}
		public Find workPlace(UUID id){
			workPlace=id;
			return this;
		}
		public Find home(UUID id){
			home=id;
			return this;
		}
		public List<SlaveInfo> find(){
			List<SlaveInfo> guys=new ArrayList<>();
			for(SlaveInfo sinf:kat.members.keySet()){
				KatInfo kinf=kat.members.get(sinf);
				if(freed&&free!=(kinf.workPlace==null))continue;
				if(isHered&&isHere!=kinf.isHere)continue;
				if(role!=null&&role!=kinf.role)continue;
				if(workPlace!=null&&kinf.workPlace!=null&&!kinf.workPlace.equals(workPlace))continue;
				if(home!=null&&kinf.workPlace!=null&&!kinf.home.equals(home))continue;
				guys.add(sinf);
			}
			return guys;
		}
	}
	
	public List<UUID> findBuildingsByName(String name){
		List<UUID> ret=new ArrayList<>();
		for(UUID id:buildings.keySet()){
			Building b = buildings.get(id);
			if(b.info.name.equals(name))ret.add(id);
		}
		return ret;
	}
	
	public List<UUID> findBuildingsByName(String name, boolean isFinished){
		List<UUID> ret=new ArrayList<>();
		for(UUID id:buildings.keySet()){
			Building b = buildings.get(id);
			if(b.info.name.equals(name)&&(b.toReady<=0)==isFinished)ret.add(id);
		}
		return ret;
	}
	
	public List<UUID> getUnfinishedBuildings(){
		List<UUID> ret=new ArrayList<>();
		for(UUID id:buildings.keySet()){
			Building b = buildings.get(id);
			if(b.toReady>0)ret.add(id);
		}
		return ret;
	}
	
	public List<UUID> getFinishedHouses(){
		List<UUID> ret=new ArrayList<>();
		for(UUID id:buildings.keySet()){
			Building b = buildings.get(id);
			if(b.toReady<=0&&b instanceof House)ret.add(id);
		}
		return ret;
	}
	
	public List<UUID> getFinishedBuildings(){
		List<UUID> ret=new ArrayList<>();
		for(UUID id:buildings.keySet()){
			Building b = buildings.get(id);
			if(b.toReady<=0)ret.add(id);
		}
		return ret;
	}
	
	public boolean hurt(SlaveInfo sinf, float d, String mes){
		KatInfo kinf=members.get(sinf);
		if(kinf!=null){
			if(kinf.damage(d)){
				if(mes!=null)messages.add(mes.replace("%", sinf.name));
				members.remove(sinf);
				Slaves.regDeath(sinf);
				return true;
			}
		}
		return false;
	}
	
	public void GUI(Player p){
		invsUtil.Invs.open(p, InvEvents.Buildings);
	}
	
	public class Tax{
		public int tax;
		public int log;
		public Tax(Conf conf, String st){
			tax=conf.getInt(st+".tax", -1);
			log=conf.getInt(st+".log", 0);
		}
		public Tax(){
			tax=-1;
			log=0;
		}
		public void addTax(int c){
			if(tax==-1)tax=baseTax.get()+c;
			else tax+=c;
		}
		public int tax(){
			if(tax==-1)return baseTax.get();
			return tax;
		}
		public void save(Conf conf, String st){
			conf.set(st+".tax", tax);
			conf.set(st+".log", log);
		}
		public void add(sitem sit){
			boolean exportIsFull=export.am()>=64*9;
			if(!exportIsFull)log+=tax()*sit.am;
			int am=log/100;
			if(am>0){
				log-=100*am;
				export.add(sit.mat,am);
			}
			if(am<sit.am)storage.add(sit.mat,sit.am-am);
		}
	}
	
	public Tax taxFor(Material mat){
		if(taxes.containsKey(mat))return taxes.get(mat);
		Tax t=new Tax();
		taxes.put(mat, t);
		return t;
	}
	
	public void add(sitem sit){
		if(!taxes.containsKey(sit.mat)){
			taxes.put(sit.mat, new Tax());
		}
		taxes.get(sit.mat).add(sit);
	}
}
