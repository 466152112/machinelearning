package bean;

import java.util.ArrayList;

public class OnePairOfUser {
	private String use1;
	private String use2;
	
	private ArrayList<String> use1followSet;
	private ArrayList<String> use2followSet;
	public String getUse1() {
		return use1;
	}
	public void setUse1(String use1) {
		this.use1 = use1;
	}
	public String getUse2() {
		return use2;
	}
	public void setUse2(String use2) {
		this.use2 = use2;
	}
	
	public ArrayList<String> getUse1followSet() {
		return use1followSet;
	}
	public void setUse1followSet(String[] aa) {
		this.use1followSet=new ArrayList<String>();
		for(int i=0,length=aa.length;i<length;i++){
			this.use1followSet.add( aa[i]);
		}
		
	}
	public ArrayList<String> getUse2followSet() {
		return use2followSet;
	}
	public void setUse2followSet(String[] aa) {
		this.use2followSet=new ArrayList<String>();
		for(int i=0,length=aa.length;i<length;i++){
			this.use2followSet.add( aa[i]);
		}
	}
	public int commonNeighborNumber(){
		int count=0;
		for (String key : use1followSet) {
			if (use2followSet.contains(key)) {
				count++;
			}
		}
		return count;
	}
	
}
