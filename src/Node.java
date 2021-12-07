
public class Node implements Comparable<Node> {

	private int nodeID;
	private boolean isFixedNode;
	public Integer nodeDegree;
	public Integer node400G = 0;
	public Integer node200G = 0;
	public Integer node100G = 0;
	public Integer node40G = 0;
	public Integer nodeGeneratedTraffic = 0;
	public Integer nodeCarriedTraffic = 0;
	
	
	
	public int compareTo(Node node){
//		return this.nodeDegree.compareTo(node.nodeDegree);
//		return this.node40G.compareTo(node.node40G);
//		return this.node400G.compareTo(node.node400G);
//		return this.nodeGeneratedTraffic.compareTo(node.nodeGeneratedTraffic);
		return this.nodeCarriedTraffic.compareTo(node.nodeCarriedTraffic);
	}
		
	public Node(int nodeID){
		this.nodeID = nodeID;
		isFixedNode = false;
	}
	
	public void reset(){
		node400G = 0;
		node200G = 0;
		node100G = 0;
		node40G = 0;
		nodeGeneratedTraffic = 0;
		nodeCarriedTraffic = 0;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public boolean getIsFixedNode(){
		return isFixedNode;
	}
	
	public void setFixedNode(){
		isFixedNode = true;
	}
	
	public void setFlexNode(){
		isFixedNode = false;
	}
	
	public static void main(String[] args){
		Node node = new Node(1);
		System.out.println("the node ID is " + node.getNodeID());
		System.out.println("the node is a fixed node? " + node.getIsFixedNode());
		node.setFixedNode();
		System.out.println("the node is a fixed node? " + node.getIsFixedNode());
	}

}