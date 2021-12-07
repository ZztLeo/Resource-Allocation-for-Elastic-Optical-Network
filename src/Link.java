//定义链路属性，包括源节点（from）,目的节点（to）,链路上slot总数（numSlots）,slot的占用情况（slots[]）；

import java.io.Serializable;
import java.util.*;

public class Link implements Serializable {

	private static final long serialVersionUID = 1L;
	private int from;
	private int to;
	private int distance;
	private boolean[] slots;
	
	public Link (int from, int to, int numSlots, int distance){
		this.from = from;
		this.to = to;
		this.distance = distance;
		
		//未被占用，默认false；
		slots = new boolean[numSlots];
		for (int i = 0; i<numSlots; i++){
			slots[i] = false;
		}
	}
	
	public Link (Link another){
		this.from = another.getFrom();
		this.to = another.getTo();
		this.distance = another.getDistance();
		this.slots = another.getSlots();
	}
	
	public int getFrom(){
		return from;
	}
	
	public int getTo(){
		return to;
	}
	
	public int getDistance(){
		return distance;
	}
	
	public boolean[] getSlots(){
		boolean[] slotsCopy = new boolean[slots.length];
		System.arraycopy(slots, 0, slotsCopy, 0, slots.length);
		return slotsCopy;
	}
	
	public void reset(){
		for (int i=0; i<slots.length; i++){
			slots[i] = false;
			}
	}
	
	public void setSlots(boolean[] slots){
		System.arraycopy(slots, 0, this.slots, 0, slots.length);
	}	
	
	public void mask(boolean[] slots) {
		for (int i=0; i<slots.length; i++) {
			this.slots[i] |= slots[i];
		}
	}
	
	public void unmask(boolean[] slots) {
		for (int i=0; i<slots.length; i++) {
			this.slots[i] &= (!slots[i]);
		}
	}
	
	public static void main(String[] args){
		Link a = new Link(1,14,10,1);
		Link b = new Link(a);
		List<Link> linkList = new ArrayList<Link>();
		linkList.add(a);
		linkList.add(b);
		
	}
}