package objKingdom;

public enum Role {
	SLAVE("&6Раб", 10),
	GUARD("&9Охранник", 20),
	RUNNER("&bГонец", 30),
	WARDEN("&cНачальник", 40);
	
	private Role(String display, int importance){
		this.display=display;
		this.importance=importance;
	}
	
	public final String display;
	public final int importance;
}
