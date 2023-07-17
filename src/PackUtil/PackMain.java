package PackUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Kingdom.Events;
import Kingdom.main;
import UtilsKingdom.ItemMoveEvent;
import UtilsKingdom.ItemUtil;
import UtilsKingdom.TextUtil;
import objKingdom.Conf;
import objKingdom.PlayerInfo;

public class PackMain implements Listener{
	public HashMap<UUID,Pack> packs = new HashMap<>();
	public HashMap<String,Pack> types = new HashMap<>();//Все типы паков
	
	public PackMain(){
		types.put("seeds", new Pack(new String[]{"SEEDS","SAPLING","_TULIP","_DAISY","ALLIUM","_BLUET","PEONY","LILAC","LILY","[CORNFLOWER","POPPY","DANDELION","[SUGAR_CANE","[SWEET_BERRIES"},128,ChatColor.GOLD+"Растения",null));
		types.put("mines", new Pack(new String[]{"[COBBLESTONE","COAL","_ORE","[DIAMOND","[STONE","[DIORITE","[ANDESITE","[GRANITE","LAPIS"},64,ChatColor.GOLD+"Из шахты",null));
		types.put("logs", new Pack(new String[]{"_LOG","[STICK","_PLANKS"},64,ChatColor.GOLD+"Деревянное",null));
		//types.put("flowers", new Pack(new String[]{"_TULIP","_DAISY","ALLIUM","_BLUET","PEONY","LILAC","LILY","FLOWER","POPPY"},27,ChatColor.GOLD+"Цветы"));
	}
	
	public UUID createPackItem(ItemStack item, String type, boolean setName){
		ItemMeta meta=item.getItemMeta();
		List<String> lore=new ArrayList<>();
		if(meta.hasLore())lore=meta.getLore();
		UUID id=UUID.randomUUID();
		lore.add(ChatColor.BLACK+"PID="+id);
		meta.setLore(lore);
		Pack p=types.get(type);
		if(setName)meta.setDisplayName(p.displayName);
		item.setItemMeta(meta);
		packs.put(id, new Pack(p.mats, p.maxAm, p.displayName, id));
		return id;
	}
	public ItemStack createNewPackItem(ItemStack in, String type, boolean setName){
		ItemStack item=in.clone();
		ItemMeta meta=item.getItemMeta();
		List<String> lore=new ArrayList<>();
		if(meta.hasLore())lore=meta.getLore();
		UUID id=UUID.randomUUID();
		lore.add(ChatColor.BLACK+"PID="+id);
		meta.setLore(lore);
		Pack p=types.get(type);
		if(setName)meta.setDisplayName(p.displayName);
		item.setItemMeta(meta);
		packs.put(id, new Pack(p.mats, p.maxAm, p.displayName, id));
		return item;
	}
	
	public UUID getUUIDfromItem(ItemStack item){
		if(item==null)return null;
		ItemMeta meta=item.getItemMeta();
		List<String> lore=meta.getLore();
		if(lore==null)return null;
		for(String st:lore){
			if(st.contains(ChatColor.BLACK+"PID="))return UUID.fromString(st.substring(6));
		}
		return null;
	}
	
	String[] tools={"_PICK","_AXE","_HOE","_SHOVEL","SWORD","[BOW","CROSSBOW","FISHING_ROD","SHEARS","TRIDENT"};
	
	int freeSlots(Player p, int[] is){
		int ret=0;
		for(int i:is){
			ItemStack item=p.getInventory().getItem(i);
			if(item==null||item.getType().equals(Material.AIR))ret++;
		}
		return ret;
	}
	boolean remItem=true;
	@EventHandler
	public void pickUp2(EntityPickupItemEvent e){
		e.setCancelled(true);
		if(e.isCancelled()){
			return;
		}
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		Player p = (Player) e.getEntity();
		ItemStack item=e.getItem().getItemStack();
		if(containsMat(item.getType(), tools)){
			if(freeSlots(p,new int[]{0,1})==0){
				e.setCancelled(true);
			}
			return;
		}
		ItemMeta meta=item.getItemMeta();
		if(meta!=null){//Мы не сохраняем "сложные" предметы.
			if(meta.hasDisplayName())return;
			if(meta.hasEnchants())return;
		}
		//p.sendMessage("pickuped "+item.getType());
		    //ОТСЮДА КОД
		for(String key:types.keySet()){
			//Ищем, к какому паку вообще поднятый предмет принадлежит.
			if(types.get(key).containsMat(item.getType())){//ЕСТЬ ПРОБИТИЕ!
				TextUtil.debug("type(key)="+key);
				ItemStack is=e.getItem().getItemStack();
				remItem=true;
				ItemStack up=up(p, key, item);
				if(remItem){
					if(up==null){
						if(e.getRemaining()==0)e.getItem().remove();
						else{
							is.setAmount(e.getRemaining());
							TextUtil.debug("is-AM="+is.getAmount()+", type="+is.getType());
							e.getItem().setItemStack(is);
						}
					}else{
						is.setAmount(up.getAmount());
						e.getItem().setItemStack(is);
					}
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0);
				}else{
					return;
				}
			}
		}
		
		/*
		 * Распределялка инвентаря. В целом инв должен выглядеть так:
		 * |A| |A|           |
		 * |A| |A| |O|       |
		 * |-----------------|
		 * |#|#|B|#|#|#|B|B|#| <B: рюкзак
		 * |F|P|#|#|#|#|B|B|#| <F: слот для мешочка трав
		 * |#|#|#|#|#|#|#|#|#| \P: Пояс (инструменты)
		 * |T|T|~|~|I|I|I|I|I| <I: Прочие предметы
		 * Для инструментов выделено 2 слота. Ещё аж 5 для предметов, так их ещё на 4 увеличить можно!
		 *
		 * 
		 *
		 *
		 *
		 *
		 */
		
		/*
		 * На русском коде напишу код!!!!!
		 * ИгрокПоднялПредмет(){
		 *   К какому паку этот предмет относится?
		 *     ОбрабатываемОстаток() == null?{
		 *       Удаляем дроп.
		 *     }else{
		 *       Меняем количество дропнтого предмета для посл. переобработки.
		 *     }
		 * }
		 * Теперь обобщалки.
		 * 
		 * ОбрабатываемОстаток(Остаток){
		 *   НайтиПредметИзПака()!=null{
		 *     Добиваем до стака.
		 *     Остался необработ. остаток?{
		 *       ОбрабатываемОстаток()
		 *     }
		 *   }else{
		 *     return СуёмВПак()
		 *   }
		 * }
		 * 
		 * НайтиПредметИзПака(возвращать фулл стак?){
		 *   return предметИзПака, в котором не фулл стак ещё набран. (if возвращать фулл стак)
		 * }
		 * 
		 * СуёмВПак(Пак,Остаток){
		 *   Пак = НайтиПак()
		 *   Пак=null?{
		 *     ПредметДляПака = НайтиПредметИзПака(true)
		 *     Херачим этот предмет в предмет пака
		 *     Пак=пак из полученного предмета.
		 *   }
		 *   Суём в Пак остаток.
		 *   return Что-то не влезло в пак?
		 * }
		 * 
		 * НайтиПак(){
		 *   return Предмет пака из инвентаря. (ну или null)
		 * }
		 * 
		 */
	}
	
	public void SelfMadeAddItem(Player p, ItemStack item, int[] avals){
		for(int i:avals){
			ItemStack sitem=p.getInventory().getItem(i);
			sitem.clone();//TODO
		}
	}
	boolean canStack(ItemStack item, ItemStack target){
		if(target==null||item==null)return false;
		if(!item.getType().equals(target.getType()))return false;
		if(target.getAmount()==target.getMaxStackSize())return false;
		if(item.hasItemMeta()&&target.hasItemMeta()){
			if(item.getItemMeta().equals(target.getItemMeta()))return false;
		}else if(item.hasItemMeta()||target.hasItemMeta())return false;
		return true;
	}
	
	ItemStack up(Player p, String type, ItemStack puped){
		TextUtil.debug("up started.");
		ItemStack item=findSameItem(p, puped.getType());//Найти НЕ-полный стак
		if(item!=null){//Не добавлять в пак, игрок использует сам этот предмет!
			TextUtil.debug("same item!=null.");
			//int added=item.getType().getMaxStackSize()-item.getAmount();
			//if(puped.getAmount()<added)added=puped.getAmount();
			//item.setAmount(item.getAmount()+added);
			//puped.setAmount(puped.getAmount()-added);
			//if(puped.getAmount()>0){
			//	TextUtil.debug("that's not all. Repeat!");
			//	return up(p,type,puped);//TODO
			//}
			remItem=false;
			TextUtil.debug("that's all. End.");
			return null;
		}
		ItemStack pit=findPack(p, type, true);
		UUID id=null;
		if(pit!=null)id=getUUIDfromItem(pit);//Ищем пак со своб. местом
		Pack pack=null;
		if(id==null){//У игрока нет своб. пака
			TextUtil.debug("player haven't free pack.");
			if(findItemFromPack(p, type, true)==null){
				TextUtil.debug("player haven't ANYTHING! Do nothing.");
				remItem=false;
				return null;
			}
			pit=findOtherItem(p, puped.getType(), type);
			if(pit!=null){//Есть с чем объединять!
				TextUtil.debug("found item to merge!");
				ItemStack tmp=pit.clone();
				id=createPackItem(pit, type, true);
				pack=packs.get(id);
				pack.addItem(tmp);
			}else{
				pit=ItemUtil.create(puped.getType(), puped.getAmount(), types.get(type).displayName, null, null, 0);
				id=createPackItem(pit, type, false);
				pack=packs.get(id);
				HashMap<Integer,ItemStack> add=p.getInventory().addItem(pit);
				if(add.size()>0){//Не получилось добавить пак
					TextUtil.debug("fuck this player.");
					return puped;//Ну нахер тогда.
				}
			}
			TextUtil.debug("new pack created.");
		}else pack=packs.get(id);
		pack.addItem(puped);
		pack.resetDisplay(pit);
		TextUtil.debug("item added to the pack.");
		return null;
	}
	
	ItemStack findSameItem(Player p, Material mat){
		for(int i=0;i<36;i++){
			ItemStack it=p.getInventory().getItem(i);
			if(it!=null&&it.getType().equals(mat)&&isSimpleItem(it)){
				if(it.getAmount()==it.getType().getMaxStackSize())continue;
				return it;
			}
		}
		return null;
	}
	
	ItemStack findOtherItem(Player p, Material mat, String type){
		Pack pack=types.get(type);
		for(int i=0;i<36;i++){
			ItemStack it=p.getInventory().getItem(i);
			if(it!=null&&!it.getType().equals(Material.AIR)&&!it.getType().equals(mat)&&isSimpleItem(it)){
				if(pack.containsMat(it.getType())){
					if(it.getAmount()>=pack.maxAm)continue;
					return it;
				}
			}
		}
		return null;
	}
	
	ItemStack findItemFromPack(Player p, String type, boolean ignoreFull){
		Pack pack=types.get(type);
		for(int i=0;i<36;i++){
			ItemStack it=p.getInventory().getItem(i);
			if(it!=null&&!it.getType().equals(Material.AIR)&&isSimpleItem(it)){
				if(pack.containsMat(it.getType())){
					if(ignoreFull&&it.getAmount()==it.getType().getMaxStackSize())continue;
					return it;
				}
			}
		}
		return null;
	}
	
	boolean isSimpleItem(ItemStack item){
		ItemMeta meta=item.getItemMeta();//Мы не сохраняем "сложные" предметы...
		if(meta!=null&&(meta.hasDisplayName()||meta.hasEnchants()))return false;
		return true;
	}
	
	ItemStack findPack(Player p, String type, boolean ignoreFull){
		ItemStack item=null;
		for(int i=0;i<36;i++){
			ItemStack it=p.getInventory().getItem(i);
			if(it!=null&&!it.getType().equals(Material.AIR)){
				if(it.getItemMeta().hasDisplayName()&&it.getItemMeta().getDisplayName().equals(types.get(type).displayName)){
					if(ignoreFull){
						Pack pack=packs.get(getUUIDfromItem(it));
						if(pack.totalAm()>=pack.maxAm)continue;
					}
					item=it;
					break;
				}
			}
		}
		return item;
	}
	
	
	public void interact(PlayerInteractEvent e){
		Player p=e.getPlayer();
		ItemStack hitem=p.getInventory().getItemInMainHand();
		if(typeFromItem(hitem)!=null){
			Pack pack=packs.get(getUUIDfromItem(hitem));
			e.setCancelled(true);
			pack.openGUI(p);
		}
	}
	
	@EventHandler
	public void inv2(InventoryDragEvent e){
		ItemStack item=e.getOldCursor();
		if(item.getAmount()==1||item.hasItemMeta())e.setCancelled(true);
		//TextUtil.mes(e.getWhoClicked(), "&6Kingdom", "");
	}
	
	@EventHandler
	public void inv(InventoryClickEvent e){
		Player p=(Player) e.getWhoClicked();
		PlayerInfo pi=Events.plist.get(p.getName());
		if(e.getClickedInventory()!=null){
			if(e.getView().getTitle()!=null){
				for(String key:types.keySet()){
					Pack typedPack=types.get(key);
					if(typedPack.displayName.equals(e.getView().getTitle())){
						e.setCancelled(true);
						if(e.getClickedInventory().equals(e.getView().getTopInventory())){
							if(e.getCurrentItem()!=null){
								Pack pack=packs.get(pi.openedPack);
								if(pack==null)return;
								p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0);
								int rem=e.getCurrentItem().getAmount();
								HashMap<Integer, ItemStack> hm=p.getInventory().addItem(e.getCurrentItem());
								for(ItemStack item:hm.values()){
									rem-=item.getAmount();
								}
								boolean empty=pack.changeMat(e.getCurrentItem().getType(), -rem);
								if(empty){
									ItemStack it=p.getInventory().getItemInMainHand();
									it.setAmount(0);
									packs.remove(pack.id);
									p.closeInventory();
									return;
								}else if(pack.stacksAm()==1){
									p.getInventory().setItemInMainHand(pack.items.get(0).toItems()[0]);
									packs.remove(pack.id);
									p.closeInventory();
									return;
								}
								pack.openGUI(p);
							}
						}
						return;
					}
				}
			}
			//p.sendMessage(e.getClick().toString()+": "+e.getCurrentItem().toString()+" hotbar="+e.getHotbarButton()+" action="+e.getAction());
			ItemMoveEvent move=new ItemMoveEvent(e);
			if(move.handled){
				if(move.from!=null&&move.from.item!=null&&move.from.item.getType().equals(Material.BARRIER)&&!p.getGameMode().equals(GameMode.CREATIVE)){
					e.setCancelled(true);
					return;
				}
				/*if(move.to!=null){
					if(containsMat(move.from.item.getType(), tools)&&move.to.inv==p.getInventory()){//Кирку переместили в наш инвентарь
						if(move.to.slot>1||e.isShiftClick()){//Перемещают куда-то не туда!
							e.setCancelled(true);
							if(freeSlots(p,new int[]{0,1})==0){//Перемещать некуда.
								TextUtil.mes(p, "&6Kingdom", "Кончилось место для инструментов.");
							}else{
								
							}
						}
					}
				}*/
			}else{
				e.setCancelled(true);
				TextUtil.mes(p, "&cKingdom", "Ошибка обработки клика... Попробуйте переместить предмет по-другому.");
			}
		}
	}
	
	@EventHandler
	public void swap(PlayerSwapHandItemsEvent e){
		ItemStack item=e.getOffHandItem();//Будем запрещать перемещать В левую руку
		String type=typeFromItem(item);
		if(type!=null){
			e.setCancelled(true);
		}
	}
	ItemStack itemPackOfPlayer(Player p, Material defMat, String type){
		ItemStack item=null;
		for(int i=0;i<36;i++){
			ItemStack it=p.getInventory().getItem(i);
			if(it!=null&&!it.getType().equals(Material.AIR)){
				if(it.getItemMeta().hasDisplayName()&&it.getItemMeta().getDisplayName().equals(types.get(type).displayName)){
					item=it;
					break;
				}
			}
		}
		if(item==null){
			item=ItemUtil.create(defMat, 1, types.get(type).displayName, null, null, 0);
			createPackItem(item, type, false);
			p.getInventory().addItem(item);
		}
		return item;
	}
	public String typeFromItem(ItemStack item){
		if(item==null||!item.hasItemMeta())return null;
		return typeFromName(item.getItemMeta().getDisplayName());
	}
	public String typeFromName(String name){
		if(name==null)return null;
		for(String st:types.keySet()){
			Pack pack=types.get(st);
			if(pack.displayName.equals(name)){
				return st;
			}
		}
		return null;
	}
	
	public void savePacks(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/packs/packs.yml");
		for(UUID id:packs.keySet()){
			String st="Packs."+id.toString();
			Pack pack=packs.get(id);
			if(pack.items.size()!=0)pack.save(conf, st);
		}
		conf.save();
	}
	public void loadPacks(){
		Conf conf=new Conf(main.instance.getDataFolder()+"/packs/packs.yml");
		for(String key:conf.getKeys("Packs")){
			UUID id=UUID.fromString(key);
			packs.put(id, new Pack(conf, "Packs."+key, UUID.fromString(key)));
		}
		for(UUID id:packs.keySet()){
			String st="Packs."+id;
			Pack pack=packs.get(id);
			pack.save(conf, st);
		}
		conf.save();
	}
	public boolean containsMat(Material mat, String[] mats){
		for(String st:mats){
			char c=st.charAt(0);
			if(c=='['&&mat.toString().equals(st.substring(1)))return true;
			else if(mat.toString().contains(st))return true;
		}
		return false;
	}
}
