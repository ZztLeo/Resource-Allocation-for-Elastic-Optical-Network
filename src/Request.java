import java.io.Serializable;
import java.util.*;

public class Request implements Serializable{

	private static final long serialVersionUID = 1L;
	public int id;
	public int source;
	public int destination;
	public int bandwidth;
	public int bandwidthIndex;
	public int splitNum = 0;
	
	public Request(int id, int numNodes, int[][] bandwidthProfile, Random randomStream){
		this.id = id;
		//产生源节点和目的节点；
		int sourceRandomNumber, destinationRandomNumber;
		sourceRandomNumber = randomStream.nextInt(24);
		destinationRandomNumber = randomStream.nextInt(24);
		
		source = sourceRandomNumber + 1;
		destination = destinationRandomNumber + 1;
		
		while(source == destination){
			if(destination == 24){
				destination = 1;
			}
			else
				destination +=1;
		}
		
		//产生带宽；
		int profileIndex = 0;
		profileIndex = source % 3;
		int [] bandwidthSet = bandwidthProfile[profileIndex];
		bandwidthIndex = randomStream.nextInt(bandwidthSet.length);
		bandwidth = bandwidthSet[bandwidthIndex];

	}
	
	public Request(Request request){
		this.id = request.id;
		this.source = request.source;
		this.destination = request.destination;
		this.bandwidth = request.bandwidth;
		this.bandwidthIndex = request.bandwidthIndex;
	}
	
	public void setBandwidth(int bandwidth){
		this.bandwidth = bandwidth;
	}
	
	public void printRequest(Request request){
		System.out.println("ID: " + request.id + "  源: " + request.source +
				"  宿: " + request.destination + "  BW: " + request.bandwidth);
	}
	
	public static void main(String[] args){
		int randomSeed = 88888888;
		int id = 1;
		int numNodes = 3;
		int[][] bandwidthProfile = {{1,2},{3,4,5},{6,7,8,9,10}};
		Random randomStream = new Random(randomSeed);
		Request request = new Request(id, numNodes, bandwidthProfile, randomStream);
		System.out.println(request.id + " " + request.source + " " + request.destination + " " + request.bandwidth);
	}
}