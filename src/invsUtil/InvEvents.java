package invsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import Kingdom.main;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import katorga.Building;
import katorga.BuildingInfo;
import katorga.Decree;
import katorga.KatInfo;
import katorga.Katorga;
import katorga.Organizing;
import katorga.Katorga.Tax;
import objKingdom.Role;
import rooms.HuntQuest;
import rooms.ProfLiveRunner;
import rooms.ProfSlave;
import rooms.SlaveInfo;
import rooms.Slaves;
import rooms.sitem;

public class InvEvents {
	
	public static List<Inv> invs = new ArrayList<>();
	
	public static final Inv Slave = new Inv(ChatColor.GOLD+"���") {
		@Override public void displItems(Inventory inv) {
			
			inv.setItem(1, ItemUtil.create(Material.CHEST, 1, ChatColor.GOLD+"���������", new String[]{
					ChatColor.AQUA+""+sinf.invAm()+ChatColor.WHITE+" ���������",
					ChatColor.GREEN+""+sinf.invFoodAm()+ChatColor.WHITE+" ��. ����"}, null, 0));
			inv.setItem(4, ItemUtil.create(Material.BOW, 1, ChatColor.GOLD+"�� �����", new String[]{"����)"}, null, 0));
			inv.setItem(7, ItemUtil.create(Material.BLUE_BED, 1, ChatColor.GOLD+"Sethome", new String[]{sinf.home.getBlockX()+";"+sinf.home.getBlockY()+";"+sinf.home.getBlockZ()+";"}, null, 0));
			inv.setItem(9, ItemUtil.create(Material.NAME_TAG, 1, ChatColor.GOLD+"����������� ���", new String[]{"&e"+sinf.name}, null, 0));
			inv.setItem(13, ItemUtil.create(Material.LEAD, 1, ChatColor.GOLD+"���� �� ����������", new String[]{sinf.live.moveToOwner+""}, null, 0));
			if(pi.orgLabor!=null){
				Role role=Role.SLAVE;
				if(pi.orgLabor.roles.containsKey(pi.invS))role=pi.orgLabor.roles.get(pi.invS);
				inv.setItem(23, ItemUtil.create(Material.IRON_PICKAXE, 1, ChatColor.GOLD+"�������� ���� �� �������", new String[]{"&f������� ����: &e"+role.display}, null, 0));
			}}
		@Override public void click(InventoryClickEvent e) {
			switch(e.getSlot()){
			case(1):{
				sinf.inventory(p);
			}break;
			case(4):{
				p.closeInventory();
				if(sinf.home==null){
					p.sendMessage("� ���� ��� ����. �� ������ �� �������.");
					return;
				}
				Entity en=Bukkit.getEntity(sinf.live.mobId);
				en.remove();
				main.prisons.quests.add(new HuntQuest(pi.invS));
				sinf.live=null;
				TextUtil.mes(p, "����", "&6"+sinf.name+" &f���� �� �����. ������ �� ������� � ����� ���� � �������. �� ��� ����... ��� ������... ��� ������)");
			}break;
			case 7:{
				Entity en=Bukkit.getEntity(sinf.live.mobId);
				sinf.home=en.getLocation().getBlock().getLocation().add(0.5, 0.1, 0.5);
				sinf.GUI(p);
			}break;
			case 9:
				sinf.name=sinf.randName();
				Bukkit.getEntity(sinf.live.mobId).setCustomName(ChatColor.GOLD+sinf.name);
				sinf.GUI(p);
				break;
			case 13:{
				//TextUtil.debug("sinf="+sinf);
				sinf.live.moveToOwner=!sinf.live.moveToOwner;
				if(sinf.live.moveToOwner)p.closeInventory();
				else sinf.GUI(p);
			}break;
			case 23:{
				pi.orgLabor.toggleRole(pi.invS);
				sinf.GUI(p);
			}break;
			}
		}
	};
	public static final Inv Building = new Inv(ChatColor.GOLD+"�������������") {
		@Override public void displItems(Inventory inv) {
			int i=0;
			for(BuildingInfo b:main.prisons.buildings){
				inv.setItem(i, b.displayItem());
				i++;
			}}
		@Override public void click(InventoryClickEvent e) {
			BuildingInfo binf=main.prisons.buildings.get(e.getSlot());
			ProfLiveRunner runner=(ProfLiveRunner) sinf.live;
			runner.addDecree(new Decree("build", binf.name));
		}
	};
	public static final Inv Runner=new Inv(ChatColor.GOLD+"�����") {
		@Override public void displItems(Inventory inv) {
			ProfLiveRunner runner = ((ProfLiveRunner)sinf.live);
			{
				List<String> lore=new ArrayList<>();
				if(runner.messages!=null&&runner.messages.size()>0){
					for(String st:runner.messages){
						lore.add(ChatColor.translateAlternateColorCodes('&', st));
					}
				}else{
					lore.add(ChatColor.GRAY+"��������� ���.");
				}
				inv.setItem(0, ItemUtil.create(Material.PAPER, 1, ChatColor.BLUE+"���������", lore, null, 0));
			}
			inv.setItem(1, ItemUtil.create(Material.CHEST, 1, ChatColor.GOLD+"����", new String[]{
					"&b"+sinf.invAm()+ChatColor.WHITE+" ���������",
					"&a"+sinf.invFoodAm()+ChatColor.WHITE+" ��. ����",
					"&f�������� �� "+TextUtil.boolst("&6�����", "&e��-�������", runner.dropToStorage),
					"&8(&b���&f ��� ����� ��������&8)"}, null, 0));
			Katorga kat=main.prisons.kats.get(runner.kid);
			{
				List<String> lore=new ArrayList<>();
				if(kat!=null){
					if(kat.members.size()>0){
						lore.add("&e����� �������:");
						for(Role role:Role.values()){
							String st=role.display+"&f: ";
							st+="&a"+new Katorga.Find(kat).role(role).find().size();
							st+="&8/&f"+new Katorga.Find(kat).role(role).isHere(true).find().size();
							lore.add(st);
						}
						for(SlaveInfo osinf:kat.members.keySet()){
							KatInfo kinf=kat.members.get(osinf);
							if(osinf!=null){
								if(kinf.isHere)lore.add("&6"+osinf.name+" &f("+kinf.role.display+"&f)");
								else lore.add("&8"+osinf.name+" &f("+kinf.role.display+"&f)");
							}
						}
					}else{
						lore.add("&c�� ������� ��� �����.");
					}
				}
				inv.setItem(3, ItemUtil.create(Material.CAMPFIRE, 1, ChatColor.BLUE+"���������� � �������", lore, null, 0));
			}{
				List<String> lore=new ArrayList<>();
				for(Decree d:runner.decrees){
					lore.add("&d"+d.name+"&e: &f"+d.am+" &6{&f"+d.current+"&6}");
				}
				inv.setItem(5, ItemUtil.create(Material.KNOWLEDGE_BOOK, 1, ChatColor.BLUE+"�����", lore, null, 0));
			}
			inv.setItem(7, ItemUtil.create(Material.GOLDEN_PICKAXE, 1, ChatColor.GOLD+"�������� �� ���������", new String[]{kat.digs[kat.digType].toString()}, null, 0));
			{
				List<String> lore=new ArrayList<>();
				if(kat!=null){
					if(kat.storage.storage.size()>0){
						lore.add("&e�����:");
						for(sitem sit:kat.storage.storage){
							lore.add("&8 - &f"+sit.mat+"&8x&f"+sit.am);
						}
					}else{
						lore.add("&8����� ����.");
					}
					lore.add("&8----------");
					lore.add("&e�����&f: &a"+kat.baseTax+"&2%");
					lore.add("&6��������� ���� �&f: &e"+kat.freeFood);
					lore.add("&6����� �������&f: &8"+kat.giveFoodMode);
				}
				inv.setItem(9, ItemUtil.create(Material.CHEST_MINECART, 1, ChatColor.BLUE+"��������� �������", lore, null, 0));
			}
			inv.setItem(10, ItemUtil.create(Material.OAK_DOOR, 1, "test", null, null, null));
			inv.setItem(25, ItemUtil.create(Material.OAK_DOOR, 1, ChatColor.GOLD+"������� �������", new String[]{"&11&22&33&44&55&66&77&88&99&00&aa&bb&cc&dd&ee&ff"}, null, 0));
			inv.setItem(26, ItemUtil.create(Material.IRON_DOOR, 1, ChatColor.GOLD+"������� �����", new String[]{"&11&22&33&44&55&66&77&88&99&00&aa&bb&cc&dd&ee&ff"}, null, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			ProfLiveRunner pr = (ProfLiveRunner) sinf.live;
			Katorga kat=main.prisons.kats.get(pr.kid);
			switch(e.getSlot()){
			case(0):{
				
			}break;
			case(1):{
				if(e.getClick().isRightClick()){
					pr.dropToStorage=!pr.dropToStorage;
					Invs.open(p, InvEvents.Runner);
				}
				else sinf.inventory(p);
			}break;
			case(3):{
				
			}break;
			case(5):{
				main.prisons.buildGUI(p);
			}break;
			case(7):{
				kat.digType++;
				if(kat.digType>=kat.digs.length)kat.digType=0;
				Invs.open(p, InvEvents.Runner);
			}break;
			case(9):{
				Invs.open(p, InvEvents.KatSettings);
			}break;
			case(10):{
				kat.GUI(p);
			}break;
			case(25):{
				kat.members.remove(sinf);
				ProfSlave ps=new ProfSlave();
				ps.mobId=sinf.live.mobId;
				sinf.live=ps;
				sinf.sayToOwner("&6"+sinf.name+" &f���� �������.");
				p.closeInventory();
			}break;
			case(26):{
				TextUtil.debug("kid="+pr.kid+";kat="+kat+";invId="+pi.invS);
				kat.addMember(pi.invS, null);
				for(Decree d:pr.decrees){
					if(d.name.equals("build")){
						for(int i=0;i<d.am;i++)kat.addBuilding(d.current);
					}
				}
				for(sitem sit:new ArrayList<>(sinf.inv)){
					if(pr.dropToStorage)kat.storage.add(sit);
					else kat.export.add(sit);
					sinf.inv.remove(sit);
				}
				pr.getEntity().remove();
				kat.backCaravan(sinf.id);
				sinf.live=null;
				kat.messages.add("&8����� &f"+sinf.name+" &8�������� �� �������.");
				sinf.sayToOwner("&6"+sinf.name+" &f��������� �������.");
				p.closeInventory();
			}break;
			}
		}
	};
	public static final Inv KatSettings = new Inv(ChatColor.BLUE+"��������� �������") {
		@Override public void displItems(Inventory inv) {
			ProfLiveRunner runner = ((ProfLiveRunner)sinf.live);
			Katorga kat=main.prisons.kats.get(runner.kid);
			{
				List<String> lore = new ArrayList<>();
				for(Material mat:kat.taxes.keySet()){
					Tax tax=kat.taxes.get(mat);
					lore.add("&8 - &f"+mat+"&8: &"+TextUtil.boolc('e', '6', tax.tax!=-1)+tax.tax());
				}
				lore.add("&8----------");
				lore.add("&e������� �����&f: &a"+kat.baseTax+"&2%");
				inv.setItem(1, ItemUtil.create(Material.CHEST, 1, "&a�&b�&c�&d�&e�&f�&a�&b�&c�&d�&e�&f�&a�&b�&c�", lore, null, 0));
			}
			inv.setItem(10, ItemUtil.create(Material.GOLD_INGOT, 1, ChatColor.GOLD+"������� �����", new String[]{
					"&6"+kat.baseTax+"&2%"
			}, null, 0));
			inv.setItem(16, ItemUtil.create(Material.MUTTON, 1, ChatColor.BLUE+"������� ������ ��� ���������� ���", new String[]{
					"&6"+kat.freeFood
			}, null, 0));
			inv.setItem(25, ItemUtil.create(Material.KNOWLEDGE_BOOK, 1, ChatColor.BLUE+"����� �������", new String[]{
					"&e"+kat.giveFoodMode
			}, null, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			ProfLiveRunner pr = (ProfLiveRunner) sinf.live;
			Katorga kat=main.prisons.kats.get(pr.kid);
			switch(e.getSlot()){
			case(1):{
				Invs.open(p, InvEvents.TaxSettings);
			}break;
			case(10):{
				if(e.getClick().isLeftClick()){
					kat.baseTax.add(1);
				}else{
					kat.baseTax.add(-1);
				}
				Invs.open(p, InvEvents.KatSettings);
			}break;
			case(16):{
				if(e.getClick().isLeftClick()){
					kat.freeFood++;
				}else{
					kat.freeFood--;
				}
				Invs.open(p, InvEvents.KatSettings);
			}break;
			case(25):{
				if(e.getClick().isLeftClick()){
					kat.giveFoodMode++;
				}else{
					kat.giveFoodMode++;
				}
				if(kat.giveFoodMode>2){
					kat.giveFoodMode-=3;
				}else if(kat.giveFoodMode<0){
					kat.giveFoodMode+=3;
				}
				Invs.open(p, InvEvents.KatSettings);
			}break;
			}
		}
	};
	public static final Inv TaxSettings = new Inv(ChatColor.GOLD+"��������� �������") {
		@Override public void displItems(Inventory inv) {
			ProfLiveRunner runner = ((ProfLiveRunner)sinf.live);
			Katorga kat=main.prisons.kats.get(runner.kid);
			int i=0;
			for(Material mat:kat.taxes.keySet()){
				Tax tax=kat.taxes.get(mat);
				inv.setItem(i, ItemUtil.create(mat, 1, null, new String[]{"&a�����&f: &"+TextUtil.boolc('e', '6', tax.tax!=-1)+tax.tax()+"&2%"}, null, 0));
				i++;
				if(i==27)break;
			}
			//inv.setItem(26, ItemUtil.create(Material.KNOWLEDGE_BOOK, 1, ChatColor.BLUE+"������� �����", new String[]{
			//		"&e"+kat.baseTax
			//}, null, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			ProfLiveRunner pr = (ProfLiveRunner) sinf.live;
			Katorga kat=main.prisons.kats.get(pr.kid);
			int i=0;
			for(Material mat:kat.taxes.keySet()){
				if(i==e.getSlot()){
					Tax tax=kat.taxes.get(mat);
					if(e.getClick().isKeyboardClick()){
						tax.tax=-1;
					}else if(e.getClick().isLeftClick()){
						tax.addTax(1);
					}else if(e.getClick().isRightClick()){
						tax.addTax(-1);
					}
					Invs.open(p, InvEvents.TaxSettings);
					return;
				}
				i++;
			}
		}
	};
	public static final Inv Buildings = new Inv(ChatColor.BLUE+"�������� �������") {
		@Override public void displItems(Inventory inv) {
			ProfLiveRunner pr = (ProfLiveRunner) sinf.live;
			Katorga kat=main.prisons.kats.get(pr.kid);
			int i=0;
			for(Building b:kat.buildings.values()){
				inv.setItem(i, b.displayItem());
				i++;
			}}
		@Override public void click(InventoryClickEvent e) {
			ProfLiveRunner pr = (ProfLiveRunner) sinf.live;
			Katorga kat=main.prisons.kats.get(pr.kid);
			int i=0;
			for(Building b:kat.buildings.values()){
				if(e.getSlot()==i){
					if(b.closed)b.open();
					else b.close();
					Invs.open(p, InvEvents.Buildings);
				}
				i++;
			}}
	};
	public static final Inv Labors = new Inv(ChatColor.RED+"���� �������") {
		@Override public void displItems(Inventory inv) {
			inv.setItem(26, ItemUtil.create(Material.IRON_DOOR, 1, "&6����� �������", new String[]{"&f���� 1 ���������, 1 ����� � 1 ����..."}, null, 0));
			int i=0;
			for(Katorga kat:main.prisons.kats.values()){
				if(kat.owner.equals(p.getName())){
					List<String> lore=new ArrayList<>();
					lore.add("&f�����: &e"+kat.members.size());
					lore.add("&f��������: &6"+kat.buildings.size());
					inv.setItem(i, ItemUtil.create(Material.WOODEN_PICKAXE, 1, "&6������� &f#&e"+kat.name, lore, null, 0));
					i++;
				}
			}}
		@Override public void click(InventoryClickEvent e) {
			if(e.getSlot()==26){
				UUID kid=UUID.randomUUID();
				Katorga kat=new Katorga(p.getName(), kid);
				pi.orgLabor=new Organizing(kat, true);
				Invs.open(p, InvEvents.LabCaravan);
			}else{
				int i=0;
				for(Katorga kat:main.prisons.kats.values()){
					if(kat.owner.equals(p.getName())){
						if(e.getSlot()==i){
							if(pi.orgLabor==null||pi.orgLabor.target!=kat)pi.orgLabor=new Organizing(kat,false);
							Invs.open(p, InvEvents.LabCaravan);
							return;
						}
						i++;
					}
				}
			}}
	};
	public static final Inv LabCaravan = new Inv(ChatColor.GOLD+"����� ���������") {
		@Override public void displItems(Inventory inv) {
			Organizing org=pi.orgLabor;
			List<SlaveInfo> slaves=new Slaves.Find().owner(p.getName()).only(ProfSlave.class).find();
			int i=0;
			for(SlaveInfo sinf:slaves){
				List<String> lore=new ArrayList<>();
				Role role=Role.SLAVE;
				if(org.roles.containsKey(sinf))role=org.roles.get(sinf);
				lore.add("&f����: "+role.display);
				Enchantment ench=null;
				if(org.members.contains(sinf)){
					ench=Enchantment.ARROW_DAMAGE;
					lore.add("&a������� � ������!");
				}else{
					lore.add("&8�� ������ � ������.");
				}
				inv.setItem(i, ItemUtil.create(Material.EGG, 1, "&6"+sinf.name, lore, ench, 1));
				i++;
				if(i==17)break;
			}
			inv.setItem(23, ItemUtil.create(Material.SADDLE, 1, "&b������� �����", new String[]{
					new Katorga.Find(org.target).role(Role.RUNNER).isHere(true).find().size()+" ���."
					}, null, 0));
			inv.setItem(26, ItemUtil.create(Material.IRON_DOOR, 1, "&6���������!", org.members.size()+" ���.", null, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			Organizing org=pi.orgLabor;
			Katorga kat=org.target;
			if(e.getSlot()==26){
				for(SlaveInfo sinf:org.members){
					Role role=org.getRole(sinf);
					kat.addMember(sinf, role);
					sinf.live.getEntity().remove();
					sinf.live=null;
				}
				if(!org.first)kat.messages.add("&8������� ����� ���������. (&f"+org.members.size()+" ���.&8)");
				else{
					kat.messages.add("&b��������� ������� ��������� �� ����� � ��������� �� ������.");
					kat.sendCaravan("started","&6% &e�������� � ��� �������� ��� ������ ���������!");
				}
				p.closeInventory();
				pi.orgLabor=null;
			}else if(e.getSlot()==23){
				org.target.sendCaravan(null,"&6% &f������ �� ������ �������!");
				p.closeInventory();
				pi.orgLabor=null;
			}else{
				List<SlaveInfo> slaves=new Slaves.Find().owner(p.getName()).only(ProfSlave.class).find();
				SlaveInfo sinf=slaves.get(e.getSlot());
				if(e.getClick().isLeftClick()){
					org.toggleMember(sinf);
				}else{
					org.toggleRole(sinf);
				}
				Invs.open(p, InvEvents.LabCaravan);
			}
		}
	};
}
