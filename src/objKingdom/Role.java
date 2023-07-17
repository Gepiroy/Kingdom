package objKingdom;

public enum Role {
	SLAVE("&6���", 10),
	GUARD("&9��������", 20),
	RUNNER("&b�����", 30),
	WARDEN("&c���������", 40);
	
	private Role(String display, int importance){
		this.display=display;
		this.importance=importance;
	}
	
	public final String display;
	public final int importance;
}
