

public class Algorithms{

	public static int firstFit_AllFlexNodes(boolean[] commonSlots, int bandwidth){
		int numSlots = commonSlots.length;
		int freeSlots = 0;
		for(int i = 0; i < numSlots; i++){
			if(commonSlots[i] == true){
				if(freeSlots == 0){ 
					continue;
				}
				freeSlots = 0;
				continue;
			}
			freeSlots++;
			if(freeSlots >= bandwidth){
				return i-bandwidth+1;
			}
		}
		if(freeSlots >= bandwidth){ 
			return numSlots-freeSlots;
		}
		return numSlots;
	}
	
	public static int firstFit_FromFlexGoThroughFixed(boolean[] commonSlots, int bandwidth){
		int numSlots = commonSlots.length;
		int freeSlots = 0;
		for(int i = 0; i < numSlots; i++){
			if(commonSlots[i] == true){
				if(freeSlots == 0){ 
					continue;
				}
				freeSlots = 0;
				continue;
			}
			freeSlots++;
			if(i%4 == 0){
				freeSlots = 1;
			}
			if(freeSlots >= bandwidth){
				return i-bandwidth+1;
			}
		}
		if(freeSlots >= bandwidth){ 
			return numSlots-freeSlots;
		}
		return numSlots;
	}
			
	public static int firstFit_FromFixed(boolean[] commonSlots, int bandwidth){
		int numSlots = commonSlots.length;
		int freeSlots = 0;
		for(int i = 0; i < numSlots; i++){
			if(commonSlots[i] == true){
				if(freeSlots == 0){ 
					continue;
				}
				freeSlots = 0;
				continue;
			}
			if(i%4 == 0 || freeSlots > 0){
				freeSlots++;
				if(freeSlots >= bandwidth){
					return i-bandwidth+1;
				}
			}
			else continue;
		}
		if(freeSlots >= bandwidth){ 
			return numSlots-freeSlots;
		}
		return numSlots;
	}
		
	public static void main(String[] args){
		boolean[] slotsUsage = {false, false, false, true, true, false, false, false, false, false, false, false, false};
		int start = Algorithms.firstFit_FromFixed(slotsUsage, 4);
		System.out.println(start);
	}	
}