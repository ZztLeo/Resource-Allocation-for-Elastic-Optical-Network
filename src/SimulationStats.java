import java.util.*;
import java.text.DecimalFormat;

public class SimulationStats{
	private int[] bandwidthSet = {2, 3, 6, 10};
	private HashMap<Integer, Counter> counterTable;
	
	private class Counter{
		public int total = 0;
		public int blocked = 0;
		public double blockRatio = 0;
		
		public void reset(){
			total = 0;
			blocked = 0;
			blockRatio = 0;
		}
	}
	
	public SimulationStats(){
		counterTable = new HashMap<Integer, Counter>();
		for(int bandwidth : bandwidthSet){
			counterTable.put(bandwidth, new Counter());
		}
	}
	
	public void reset(){
		for(int bandwidth : bandwidthSet){
			counterTable.get(bandwidth).reset();
		}
	}
	
	public int increaseTotal(int bandwidth){
		counterTable.get(bandwidth).total++;
		return counterTable.get(bandwidth).total;
	}
	
	public int increaseBlocked(int bandwidth){
		counterTable.get(bandwidth).blocked++;
		return counterTable.get(bandwidth).blocked;
	}
	
	public int getTotal(int bandwidth){
		return counterTable.get(bandwidth).total;
	}
	
	public int getBlocked(int bandwidth){
		return counterTable.get(bandwidth).blocked;
	}
	
	public int getTotalBlocked(){
		int totalBlocked = 0;
		for(int bandwidth : bandwidthSet){
			totalBlocked += getBlocked(bandwidth);
		}
		return totalBlocked;
	}

	public int getTotalBandwidth(){
		int totalBandwidth = 0;
		for(int bandwidth : bandwidthSet){
			totalBandwidth += bandwidth*counterTable.get(bandwidth).total;
		}
		return totalBandwidth; 
	}
	
	public double getSeparateBR(int bandwidth){
		int totalBandwidth = getTotalBandwidth();
		int totalBlockedBandwidth = counterTable.get(bandwidth).blocked;
		counterTable.get(bandwidth).blockRatio = (double) (bandwidth * totalBlockedBandwidth) / totalBandwidth;
		return counterTable.get(bandwidth).blockRatio;
	}
	
	//总的带宽阻塞率；
	public double getBandwidthBR(){
		double totalBandwidth = 0;
		double blockedBandwidth = 0;
		for(int bandwidth : bandwidthSet){
			totalBandwidth += bandwidth*getTotal(bandwidth);
			blockedBandwidth += bandwidth*getBlocked(bandwidth);
		}
		if(totalBandwidth == 0){
			return 0;
		}
		else
			return blockedBandwidth/totalBandwidth;
	}
	
	public void printResult(){
		DecimalFormat df = new DecimalFormat("0.0000"); 
		for(int bandwidth: counterTable.keySet()){
				System.out.println(
//									"bandwidth = " + bandwidth 
//									+ "   total " + getTotal(bandwidth) 
//									+ "   blocked " + getBlocked(bandwidth) 
//									+ "  blockRatio is " + 
									df.format(getSeparateBR(bandwidth)));
		}
		double bandwidthBR = getBandwidthBR();
		System.out.println(
//							"All Bandwidth Blocking Ratio = " + 
							df.format(bandwidthBR));
	}
	
}