
public class Simulation{
	
	private double trafficLoad;
	private static int[][] bandwidthProfile;
	
	public void setTrafficLoad(double trafficLoad){
		this.trafficLoad = trafficLoad;
	}
	
	public int[][] getBandwidthProfile() {
		return bandwidthProfile;
	}

	public void setBandwidthProfile(int[][] bandwidthProfile) {
		Simulation.bandwidthProfile = bandwidthProfile;
	}
	
	public double getTrafficLoad(){
		return trafficLoad;
	}
			
	public void startSimulate(Network network, int simulatedLength){
		EventTrace trace = new EventTrace(trafficLoad, getBandwidthProfile(), network.getNumNodes());
		EventTrace.setSimulatedLength(simulatedLength);
		SimulationStats stats = new SimulationStats();
		
		Event event;
		while((event = trace.nextEvent()) != null){
			if(event.getType() == EventType.ARRIVE){
				if(event.request.bandwidth == 10){
					network.nodeIndex.get(event.request.source).node400G += 1;
					network.nodeIndex.get(event.request.source).nodeGeneratedTraffic += 400;
				}

				if(event.request.bandwidth == 6){
					network.nodeIndex.get(event.request.source).node200G += 1;	
					network.nodeIndex.get(event.request.source).nodeGeneratedTraffic += 200;
				}
				if(event.request.bandwidth == 3){
					network.nodeIndex.get(event.request.source).node100G += 1;	
					network.nodeIndex.get(event.request.source).nodeGeneratedTraffic += 100;
				}

				if(event.request.bandwidth == 2){
					network.nodeIndex.get(event.request.source).node40G += 1;	
					network.nodeIndex.get(event.request.source).nodeGeneratedTraffic += 40;
				}
				stats.increaseTotal(event.request.bandwidth);
				boolean provisionSuceess = network.provision(trace, event, stats);
				
				//若频谱分配不成功，则阻塞；
				if(!provisionSuceess){
					stats.increaseBlocked(event.request.bandwidth);
				

//					System.out.println("￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥");
//					System.out.print("blocked --- ");
//					event.request.printRequest(event.request);
//					System.out.println("￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥");
//					System.out.println("************************************************");

				}
			}
			else if(event.getType() == EventType.DEPART){
				network.release(event.request);
			}
		}
		for(int nodeID : network.nodeIndex.keySet()){
			System.out.println("Node " + nodeID + " has 400G: " + network.nodeIndex.get(nodeID).node400G + ";"
					+ " has 200G: " + network.nodeIndex.get(nodeID).node200G + ";"
					+ " has 100G: " + network.nodeIndex.get(nodeID).node100G + ";"
					+ " has 40G: " + network.nodeIndex.get(nodeID).node40G + ";"
					+ " has Generated Traffic: " + network.nodeIndex.get(nodeID).nodeGeneratedTraffic
					+ " has Carried Traffic: " + network.nodeIndex.get(nodeID).nodeCarriedTraffic);
		}
		//输出总的网络承载容量；
		stats.printResult();
		int averageNetworkCapacity = 0;
		averageNetworkCapacity = (int) (((double)network.networkCapacity)/(network.getCountNumber()));
		System.out.println(
//							"The network capacity is " + 
							averageNetworkCapacity);		
	}
		
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		
		//改变拓扑，改变链路的slots容量;
		Network network = new Network("usnet.txt", 320);
		network.setAllFixedNodes();
		
		//设置灵活节点节点(默认全部是fixed节点)；
		int[] flexNodes = {17,7,12,16,10,6,11,9,18,22,15,8,14,13,3,2,20,5,21,23,19,24,4,1};
		network.setFlxeNodes(flexNodes);

//		network.setAllFlexNodes();

		Simulation simulation = new Simulation();
		
		//改变业务的带宽属性(只能取值2,3,6,10)；
		
		int[][] bandwidthProfileA = {{2,2,2,2,2,2,2,2,2,2,			3,3,3,3,3,3,		6,6,6,				10			},
									 {2,2,2,2,2,2,2,2,2,2,2,2,		3,3,3,				6,6,6,6,			10			},
									 {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
									  3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
									  6,6,6,6,6,6,				10,10,10,10,10,10,10,10	}
									};
		
		int[][] bandwidthProfileB = {{2,2,2,2,						3,3,3,3,3,3,3,3,3,3,	6,6,6,6,		10,10		},
									 {2,2,2,2,2,2,2,2,				3,3,3,3,				6,6,6,6,6,6,	10,10		},
									 {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
									  3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
									  6,6,6,6,6,6,6,6,6,6,6,6,6,6,			10,10,10,10,10,10,10,10,10,10,10,10,		}
									};
		
		int[][] bandwidthProfileC = {{3,3,3,3,3,3,3,3,				6,6,6,6,6,6,6,6,			10,10,10,10				},
									 {3,3,3,3,3,3,					6,6,6,6,6,6,6,6,6,6,6,		10,10,10				},
									 {3,3,3,3,3,3,3,3,3,3,			6,6,6,6,6,					10,10,10,10,10			}
									};
		
		int[][] bandwidthProfile = bandwidthProfileA;
		
		simulation.setBandwidthProfile(bandwidthProfile);
		
		//改变业务数目；
		int simulationLength = 500000;
		
		//改变业务量；
		for(double trafficLoad = 1300; trafficLoad <= 9999999; trafficLoad += 999999999){
			simulation.setTrafficLoad(trafficLoad);			
			System.out.println("trafficLoad = " + trafficLoad + ";");
			System.out.println();
			
			simulation.startSimulate(network, simulationLength);
		
			System.out.println("=============================================================================");			
			
			NodeSelection nodeSelection = new NodeSelection();
			for(int i = 1; i <= network.getNumNodes(); i++){
				nodeSelection.add(network.nodeIndex.get(i));
			}
			
			System.out.print("节点的排序为： ");
			Node node;
			while((node = nodeSelection.nextNode()) != null){
				System.out.print("," + node.getNodeID());
			}
			System.out.println();
			System.out.println();

			network.reset();
		}
		long elapsedTimeInSec = (System.currentTimeMillis()-startTime)/1000;
		System.out.println();
		System.out.println("It took " + elapsedTimeInSec/3600 + " hours and " +
		(elapsedTimeInSec%3600)/60 + " minutes and " +
		(elapsedTimeInSec%3600)%60 + " seconds!");
	}
	
}