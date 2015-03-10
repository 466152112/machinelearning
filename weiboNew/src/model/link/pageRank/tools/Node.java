package model.link.pageRank.tools;

public class Node {
	
	String nodeName;
	/*
	 * ï¿½ï¿½ï¿?
	 */
	private int indegree;
	/*
	 * ï¿½ï¿½ï¿½ï¿½
	 */
	private int outdegree;
	private double energy;
	
	public Node(String nodeName){
		this.nodeName=nodeName;
		indegree=0;
		outdegree=0;
		energy=0;
	}
	@Override
	public int hashCode(){
		return nodeName.hashCode();
	}

	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public int getIndegree() {
		return indegree;
	}
	public void setIndegree(int indegree) {
		this.indegree = indegree;
	}
	public int getOutdegree() {
		return outdegree;
	}
	public void setOutdegree(int outdegree) {
		this.outdegree = outdegree;
	}
	public double getEnergy() {
		return energy;
	}
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	
}
