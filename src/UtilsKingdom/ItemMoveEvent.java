package UtilsKingdom;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ItemMoveEvent {
	
	public IMEthing from=null;//Внутри могут быть null-ы!
	public IMEthing to=null;//Внутри могут быть null-ы!
	public boolean handled=true;//Если false, ItemMove ничего не распознал.
	
	
	public ItemMoveEvent(InventoryClickEvent e){//Все проверки на нуллы должны быть ДО вызова.
		String act=e.getAction().toString();
		Player p=null;
		if(e.getWhoClicked() instanceof Player)p=(Player) e.getWhoClicked();
		if(e.getAction().equals(InventoryAction.NOTHING));
		else if(act.contains("PICKUP")){
			from=new IMEthing(e.getCurrentItem(),e.getSlot(),e.getClickedInventory());
			to=new IMEthing(e.getCursor(),-1,null);
		}else if(act.contains("PLACE")){
			from=new IMEthing(e.getCursor(),-1,null);
			to=new IMEthing(e.getCurrentItem(),e.getSlot(),e.getClickedInventory());
		}else if(e.getAction().equals(InventoryAction.HOTBAR_SWAP)){
			from=new IMEthing(p.getInventory().getItem(e.getHotbarButton()),e.getHotbarButton(),p.getInventory());
			to=new IMEthing(e.getCurrentItem(),e.getSlot(), e.getClickedInventory());
		}else if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
			from=new IMEthing(e.getCursor(),-1,null);
			to=new IMEthing(e.getCurrentItem(),e.getSlot(),e.getClickedInventory());
		}else if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
			from=new IMEthing(e.getCurrentItem(),e.getSlot(),e.getClickedInventory());
			Inventory other=e.getView().getTopInventory();
			if(e.getClickedInventory()==other)other=e.getView().getBottomInventory();
			int bestSlot=InventoryUtil.getBestSlotToStack(other, e.getCurrentItem(), false, true);
			to=new IMEthing(other.getItem(bestSlot),bestSlot,other);
		}else{
			TextUtil.debug("&cUnhandled &eact&f: &b"+act);
			handled=false;
		}
	}
	
	public boolean isFullIMEthing(IMEthing I){
		if(I==null||I.slot==-1||I.inv==null||I.item==null)return false;
		return true;
	}
}
