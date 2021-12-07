import java.util.*;

public class Path extends ArrayList<Link>{
	
	private static final long serialVersionUID = 1L;
	
	private Node pathSourceNode;
	private Node pathDestinationNode;
	private HashMap<Integer, Node> nodeList;

	public Path(){
		//none;
	}
	
	public Path(Network network, int[] path){
		nodeList = new HashMap<Integer, Node>();
		for(int i = 0; i < path.length-1; i++){
			this.add(network.link(path[i], path[i+1]));
			nodeList.put(path[i], network.nodeIndex.get(path[i]));
		}
		nodeList.put(path[path.length-1], network.nodeIndex.get(path[path.length-1]));
		pathSourceNode = nodeList.get(path[0]);
		pathDestinationNode = nodeList.get(path[path.length-1]);
	}
	
	public void printPath(Path path){
		System.out.print("the route is ");
		for(int i = 0; i < path.size(); i++){
			System.out.print(path.get(i).getFrom() + "--");
		}
		System.out.print(path.get(path.size()-1).getTo());
		System.out.println("");
	}

	public Node getPathSourceNode(){
		return pathSourceNode;
	}
	
	public Node getPathDestinationNode(){
		return pathDestinationNode;
	}
	
	public Collection<Node> getNodeList(){
		Collection<Node> nodeOnRoute = nodeList.values();
		return nodeOnRoute;
	}
	
	
	public boolean goThroughFixedNode(){
		boolean goThroughFixedNode = false;
		for(Node node : nodeList.values()){
			goThroughFixedNode |= node.getIsFixedNode();
		}
		return goThroughFixedNode;
	}
	
	public boolean goThroughAllFixedNode(){
		boolean goThroughAllFixedNode = true;
		for(Node node : nodeList.values()){
			goThroughAllFixedNode &= node.getIsFixedNode();
		}
		return goThroughAllFixedNode;
	}
	
	public boolean[] getCommonSlots(){
		boolean[] slots = this.get(0).getSlots();
		for (Link link : this){
			boolean[] curLinkSlots = link.getSlots();
			for( int i = 0; i < slots.length; i++){
				slots[i] |= curLinkSlots[i];
			}
		}
		return slots;		
	}
	
}