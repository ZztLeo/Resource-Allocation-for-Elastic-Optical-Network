import java.io.*;
import java.util.*;



public class Network implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int numSlots, numNodes, numLinks;
	private DoubleHash<Integer, Integer, Link> index;
	private DoubleHash<Integer, Integer, Link> reverseIndex;
	private static DoubleHash<Integer, Integer, ArrayList<Path>> candidatePaths;
	private HashMap<Request, Connection> ongoingConnections;
	private HashMap<Link, Collection<Connection>> linkIndexedConnections;
	public HashMap<Integer, Node> nodeIndex;
	public int networkCapacity = 0;
	private int countNumber = 0;
	public int intervalNumber = 0;

	public Network(String fileName, int numSlots){
		
		this.numSlots = numSlots;			
		index = new DoubleHash<Integer, Integer, Link>();
		reverseIndex = new DoubleHash<Integer, Integer, Link>();
		
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = inputStream.readLine()) != null){
				String[] edge = line.split("\\s+");
				int from = Integer.parseInt(edge[0]);
				int to = Integer.parseInt(edge[1]);
				int distance = Integer.parseInt(edge[2]);
				
				Link newLink = new Link(from, to, this.numSlots, distance);
				index.put(from, to, newLink);
				reverseIndex.put(to, from, newLink);
			}
		} catch(Exception e){
			System.out.println(e);
		}

		numNodes = index.keySet().size();
		numLinks = index.size();
		
		nodeIndex = new HashMap<Integer, Node>();
		for(int i = 0; i < numNodes; i++){
			int nodeID = i+1;
			Node newNode = new Node(nodeID);
			nodeIndex.put(nodeID, newNode);
			nodeIndex.get(nodeID).nodeDegree = (int) (getOutLink(nodeID).size()+getInLink(nodeID).size())/2;
//			System.out.println("the degree of node " + nodeID + " is " + nodeIndex.get(nodeID).nodeDegree);
			
		}
		
		candidatePaths = new DoubleHash<Integer, Integer, ArrayList<Path>>();
		
		try{
			if(fileName == "nsf.txt"){
				inputStream = new BufferedReader(new FileReader("nsf_k_paths.txt"));
			}

			if(fileName == "usnet.txt"){
				inputStream = new BufferedReader(new FileReader("usnet_k_paths.txt"));
			}
			
			if(fileName == "cost239.txt"){
				inputStream = new BufferedReader(new FileReader("cost239_k_paths.txt"));
			}
						
			String line;
			while((line = inputStream.readLine()) != null){
				String[] token = line.split("\\s+");
				int numHops = token.length;
				int[] path = new int[numHops];
				for(int i = 0; i < numHops; i++){
					path[i] = Integer.parseInt(token[i]);
				}
				
				if(!candidatePaths.containsKey(path[0], path[numHops-1])){
					candidatePaths.put(path[0], path[numHops-1], new ArrayList<Path>());
				}
				candidatePaths.get(path[0], path[numHops-1]).add(new Path(this, path));
			}
		} catch (Exception e){
			System.out.println(e);
		}
		
		ongoingConnections = new HashMap<Request, Connection>();
		linkIndexedConnections = new HashMap<Link, Collection<Connection>>();
		
	}
	
	public void setAllFixedNodes(){
		for(int i = 1; i <= numNodes; i++){
			nodeIndex.get(i).setFixedNode();
		}
	}
	
	public void setFlxeNodes(int[] flexNodes){
		for(int i = 0; i < flexNodes.length; i++){
			nodeIndex.get(flexNodes[i]).setFlexNode();
		}
	}
	
	public void setAllFlexNodes(){
		for(int i = 1; i <= numNodes; i++){
			nodeIndex.get(i).setFlexNode();
		}
	}
	
	public int getNumNodes(){
		return numNodes;
	}
	
	public int getNumLinks(){
		return numLinks;
	}
	
	public int getCountNumber(){
		return countNumber;
	}
	
	public Link link(int from, int to){
		return index.get(from, to);
	}
	
	public Link reverseLink(int to, int from){
		return reverseIndex.get(to, from);
	}

	public Collection<Link> getOutLink(int from){
		return index.get(from).values();
	}

	public Collection<Link> getInLink(int to){
		return reverseIndex.get(to).values();
	}

	public void reset(){
		for(Link link : index.getValues()){
			link.reset();
		}
		ongoingConnections.clear();
		linkIndexedConnections.clear();
		
		for(int i = 1; i <= numNodes; i++){
				nodeIndex.get(i).reset();
		}
	}
	
	public int getNumConnections(){
		return ongoingConnections.size();
	}
		
	private boolean[] assignSpectrum(int start, int bandwidth){
		boolean[] spectrumAssignment = new boolean[numSlots];
		for(int i = 0; i < numSlots; i++){
			if((i >= start) && (i < start+bandwidth)){
				spectrumAssignment[i] = true;
			}
			else{
				spectrumAssignment[i] = false;
			}
		}
		return spectrumAssignment;
	}
	
	public boolean provisionSuperChannel(Request request, SimulationStats stats){
		int start = numSlots;
		int minStart = numSlots;
		Path route = null;
		for(Path path : candidatePaths.get(request.source, request.destination).subList(0,8)){
			//假如路径上所有点都是灵活节点；
			if(path.goThroughFixedNode() == false){
				boolean[] commonSlots = path.getCommonSlots();
				start = Algorithms.firstFit_AllFlexNodes(commonSlots, request.bandwidth);
			}			
			if(start < minStart){
				minStart = start;
				route = path;
			}
		}
		
		if(minStart < numSlots){
			Connection connection = new Connection(request, route, assignSpectrum(minStart, request.bandwidth));
			deploy(connection, stats);
			//将路径上每个节点的carried traffic增加；
			if(connection.getRequest().bandwidth == 6){
				for(Node node : connection.route.getNodeList()){
					node.nodeCarriedTraffic += 200; 
				}
			}
			else{
				for(Node node : connection.route.getNodeList()){
					node.nodeCarriedTraffic += 400; 
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean singlePath_provision(Request request, SimulationStats stats){
		int minStart = numSlots;
		Path route = null;
		Node sourceNode = nodeIndex.get(request.source);
		for(Path path : candidatePaths.get(request.source, request.destination).subList(0,5)){
			boolean[] commonSlots = path.getCommonSlots();
			int start = numSlots;
			//假如路径上所有点都是灵活节点；
			if((sourceNode.getIsFixedNode() == false) && (path.goThroughFixedNode() == false)){
				start = Algorithms.firstFit_AllFlexNodes(commonSlots, request.bandwidth);
			}
			//源节点是灵活节点，但经过固定节点；
			if((sourceNode.getIsFixedNode() == false) && (path.goThroughFixedNode() == true)){
				start = Algorithms.firstFit_FromFlexGoThroughFixed(commonSlots, request.bandwidth);
			}
			//源节点是固定节点；
			if(sourceNode.getIsFixedNode() == true){
				int bandwidth = 4;
				start = Algorithms.firstFit_FromFixed(commonSlots, bandwidth);
			}
			
			if(start < minStart){
				minStart = start;
				route = path;
			}
		}
		
		if(minStart < numSlots){
			if(sourceNode.getIsFixedNode() == true){
				Connection connection = new Connection(request, route, assignSpectrum(minStart, 4));
				deploy(connection, stats);
				//将路径上每个节点的carried traffic增加；
				if(connection.getRequest().bandwidth == 2){
					for(Node node : connection.route.getNodeList()){
						node.nodeCarriedTraffic += 40; 
					}
				}
				else{
					for(Node node : connection.route.getNodeList()){
						node.nodeCarriedTraffic += 100; 
					}
				}
			}
			else{
				Connection connection = new Connection(request, route, assignSpectrum(minStart, request.bandwidth));
				deploy(connection, stats);
				//将路径上每个节点的carried traffic增加；
				if(connection.getRequest().bandwidth == 2){
					for(Node node : connection.route.getNodeList()){
						node.nodeCarriedTraffic += 40; 
					}
				}
				else{
					for(Node node : connection.route.getNodeList()){
						node.nodeCarriedTraffic += 100; 
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean provision(EventTrace trace, Event event, SimulationStats stats){
		boolean provisionSuceess = false;
		//建立单通道；
		if(event.request.bandwidth <= 4){
			provisionSuceess = singlePath_provision(event.request, stats);
		}
		//建立多通道；
		else{
			//假如源节点是灵活节点；
			Node sourceNode = nodeIndex.get(event.request.source); 
			if(sourceNode.getIsFixedNode() == false){
				//首先尝试超通道；
				if(provisionSuperChannel(event.request, stats)){
					return true;
				}
				//如果超通道不行，则建立多通道；
				else{
					//例如，带宽为6则分成2个通道；带宽为10则分为4个通道；每个通道为100G，占用3个Slots;
					int bandwidth = event.request.bandwidth;
					int splitNum = 0;
					if(bandwidth == 6) {splitNum = 2;}
					if(bandwidth == 10) {splitNum = 4;}
//					System.out.println("totoal bandwidth is " + bandwidth + ", try to build " + splitNum + " paths!");
					HashMap<Integer, Request> splitRequest = new HashMap<Integer, Request>();
					int beenSplitProvision = 0;
					for(int pathNum = 1; pathNum <= splitNum; pathNum++){
						Request splitedRequest = new Request(event.request);
						splitedRequest.setBandwidth(3);
						splitedRequest.splitNum = pathNum;
						splitRequest.put(pathNum, splitedRequest);
						provisionSuceess = singlePath_provision(splitRequest.get(pathNum), stats);
						if(provisionSuceess){
							beenSplitProvision++;
							trace.add(new Event(EventType.DEPART,
									event.getTime() + trace.duringTime,
									splitRequest.get(pathNum)));
							continue;
						}
						else{
							for(int i = 1; i <= beenSplitProvision; i++){
								
								//将路径上每个节点的carried traffic减少；
								for(Node node : ongoingConnections.get(splitRequest.get(i)).route.getNodeList()){
									node.nodeCarriedTraffic -= 100;
								}
								release(splitRequest.get(i));
							}
//							System.out.println("However, failed on 第 " + (beenSplitProvision+1) + " 个分通道!!!!!!");
							break;
						}
					}
				}
			}
			
			//假如源节点是固定节点；
			else{
				//例如，带宽为6则分成2个通道；带宽为10则分为4个通道；每个通道为100G，占用4个Slots;
				int bandwidth = event.request.bandwidth;
				int splitNum = 0;
				if(bandwidth == 6) {splitNum = 2;}
				if(bandwidth == 10) {splitNum = 4;}
//				System.out.println("totoal bandwidth is " + bandwidth + ", try to build " + splitNum + " paths!");
				HashMap<Integer, Request> splitRequest = new HashMap<Integer, Request>();
				int beenSplitProvision = 0;
				for(int pathNum = 1; pathNum <= splitNum; pathNum++){
					Request splitedRequest = new Request(event.request);
					splitedRequest.setBandwidth(4);
					splitedRequest.splitNum = pathNum;
					splitRequest.put(pathNum, splitedRequest);
					provisionSuceess = singlePath_provision(splitRequest.get(pathNum), stats);
					if(provisionSuceess){
						beenSplitProvision++;
						trace.add(new Event(EventType.DEPART,
								event.getTime() + trace.duringTime,
								splitRequest.get(pathNum)));
						continue;
					}
					else{
						for(int i = 1; i <= beenSplitProvision; i++){
							//将路径上每个节点的carried traffic减少；
							for(Node node : ongoingConnections.get(splitRequest.get(i)).route.getNodeList()){
								node.nodeCarriedTraffic -= 100;
							}
							release(splitRequest.get(i));
						}
//						System.out.println("However, failed on 第 " + (beenSplitProvision+1) + " 个分通道!!!");
						break;
					}
				}
			}
		}
		return provisionSuceess;
	}
	
	public void deploy(Connection connection, SimulationStats stats){
		for(Link link : connection.route){
			link.mask(connection.spectrumAssignment);
			if(!linkIndexedConnections.containsKey(link)){
				linkIndexedConnections.put(link, new HashSet<Connection>());
				linkIndexedConnections.get(link).add(connection);
			}
		}
			
		ongoingConnections.put(connection.getRequest(), connection);
		
		//计算网络的容量；
		if((countNumber < 1) && (stats.getBandwidthBR() > 0.0001)){
			intervalNumber ++;
			if(intervalNumber > 0){
				countNumber++;
				intervalNumber = 0;
				for(Request request : ongoingConnections.keySet()){
					if(request.bandwidth == 2){
						networkCapacity += 40;
					}
					if(request.bandwidth == 3){
						networkCapacity += 100;
					}
					if(request.bandwidth == 4){
						networkCapacity += 100;
					}
					if(request.bandwidth == 6){
						networkCapacity += 200;
					}
					if(request.bandwidth == 10){
						networkCapacity += 400;
					}
				}	
			}
		}

		//打印部署的业务；
//		System.out.print("部署业务: ");
//		connection.getRequest().printRequest(connection.getRequest());
//		connection.route.printPath(connection.route);
/*		
		//打印分配的频谱；
		System.out.print("分配的频谱为   ");
		for(int i = 0; i < numSlots; i++){
			if (connection.spectrumAssignment[i] == true){
				System.out.print(i + " ");
			}
		}
		System.out.println();
		System.out.println("************************************************");
*/
	}

	public Connection release(Request request){
		if(!ongoingConnections.containsKey(request)){
			return null;
		}
		Connection connection = ongoingConnections.get(request);
		for(Link link : connection.route){
			link.unmask(connection.spectrumAssignment);
			linkIndexedConnections.get(link).remove(connection);
		}
		ongoingConnections.remove(request);
//		System.out.print("release: ");
//		request.printRequest(request);
		return connection;
	}
	
	public static void main(String[] args){
		Network network = new Network("usnet.txt", 10);
		network.getNumNodes();
		network.getNumLinks();
		int node = network.nodeIndex.get(4).getNodeID();
		boolean isFixedNode = network.nodeIndex.get(14).getIsFixedNode();
		System.out.println(node);
		System.out.println(isFixedNode);
	}
			
}