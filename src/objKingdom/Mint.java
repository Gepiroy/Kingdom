package objKingdom;

public class Mint {
	private int i=0;
	
	private final int max;
	private final int min;
	
	public Mint(int min, int max){
		this.min=min;
		this.max=max;
	}
	
	public Mint(int min, int max, int set){
		this.min=min;
		this.max=max;
		i=set;
	}
	
	public int add(int add){
		i+=add;
		if(i>max)i=max;
		else if(i<min)i=min;
		return i;
	}
	public int addRec(int add){
		i+=add;
		if(i>max)i-=max-min+1;
		else if(i<min)i+=max-min+1;
		return i;
	}
	
	public int get(){
		return i;
	}
	
	@Override
	public String toString(){
		return i+"";
	}
}
