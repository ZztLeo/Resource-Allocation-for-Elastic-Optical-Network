
public class Event implements Comparable<Event>{
	
	private EventType type;
	private Double time;
	public Request request;
	
	public Event(EventType type, Double time, Request request){
		this.type = type;
		this.time = time;
		this.request = request;
	}
	
	public EventType getType(){
		return type;
	}
	
	public Double getTime(){
		return time;
	}
	
	public Request getRequest(){
		return request;
	}
	
	public int compareTo(Event e){
		return this.time.compareTo(e.getTime());
	}
	
}

enum EventType{
	ARRIVE, DEPART
}