import java.util.PriorityQueue;

public class NodeSelection extends PriorityQueue<Node>{

	private static final long serialVersionUID = 1L;

	public Node nextNode(){
		Node currentNode = poll();
		if(currentNode == null) 
			return null;
		return currentNode;
	}
	
}
