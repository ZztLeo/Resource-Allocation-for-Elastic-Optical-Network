import java.util.*;
import java.lang.Math;

public class EventTrace extends PriorityQueue<Event>{

	private static final long serialVersionUID = 1L;
	private Random randomStream;
	private static int randomSeed = 88888888;
	private static double serviceRate =1;
	private static int simulatedLength;
	private double arrivalRate;
	private int[][] bandwidthProfile;
	private int numNodes;
	public double duringTime;
	
	public static void setSimulatedLength(int length){
		simulatedLength = length;
	}
	
	public int getSimulatedLength(){
		return simulatedLength;
	}
	
	public EventTrace(Double trafficLoad, int[][] bandwidthProfile, int numNodes){
		arrivalRate = trafficLoad;
		this.bandwidthProfile = bandwidthProfile;
		this.numNodes = numNodes;
		randomStream = new Random(randomSeed);
		this.add(new Event(EventType.ARRIVE,
				nextExp(arrivalRate),
				new Request(1, this.numNodes, this.bandwidthProfile, randomStream)));
	}
	
	public double nextExp(double rate){
		return (-1/rate)*Math.log(randomStream.nextDouble());
	}
	
	public Event nextEvent(){
		Event currentEvent = poll();
		
		if(currentEvent == null) 
			return null;
		
		if(currentEvent.getType() == EventType.DEPART) 
			return currentEvent;
		
		duringTime = nextExp(serviceRate);
		this.add(new Event(EventType.DEPART,
				currentEvent.getTime() + duringTime,
				currentEvent.request));
		
		if(currentEvent.request.id < simulatedLength){
			this.add(new Event(EventType.ARRIVE,
					currentEvent.getTime() + nextExp(arrivalRate),
					new Request(currentEvent.request.id+1, numNodes, bandwidthProfile, randomStream)));
		}
		
		return currentEvent;		
	}
	
	public double getServiceRate(){
		return serviceRate;
	}
	
	public static void main(String[] args){
		int[][] bandwidthProfile = {{1},{1},{1},{4, 10, 24}};
		EventTrace trace = new EventTrace(2.0, bandwidthProfile, 14);
		Event currentEvent;
		while((currentEvent = trace.nextEvent()) != null){
			System.out.println(currentEvent.request.id + " " +
			currentEvent.getType() + " " + 
			currentEvent.getTime() + " " +
			currentEvent.request.source + " " +
			currentEvent.request.destination + " " +
			currentEvent.request.bandwidth);
		}
	}
	
}