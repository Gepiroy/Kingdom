package rooms;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import objKingdom.Conf;

public class ProfSlave extends LiveSlave{
	//��� ����� ���, �. �. ������� � ���� �������, � �� � ���������.
	//����� ����, ProfSlave ����� ����� ����������� � ��������� ������ �������� � � �!
	
	//UUID mobId;
	//boolean moveToOwner;
	
	@Override
	public String sec() {
		if(mobId==null&&!loaded){
			return "IdIsNull";
		}
		LivingEntity en=(LivingEntity) Bukkit.getEntity(mobId);
		if(en==null){
			return "EntityIsNull";
		}
		return null;
	}

	@Override
	public void save(Conf conf, String st) {
		super.save(conf, st);
		
	}

	@Override
	public void load(Conf conf, String st) {
		super.load(conf, st);
	}
	
}
