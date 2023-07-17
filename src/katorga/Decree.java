package katorga;

public class Decree {
	public String name;
	public int am=1;
	public String current=null;
	public Decree(String name){
		this.name=name;
	}
	public Decree(String name, String current){
		this.name=name;
		this.current=current;
	}
}
