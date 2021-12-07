public class Connection{
	
	public Path route;
	private Request request;
	
	public boolean[] spectrumAssignment;
	private int start;
	private int end;
	
	public Connection(Request request, Path route, boolean[] spectrumAssignment){
		this.request = request;
		this.route = route;
		this.spectrumAssignment = spectrumAssignment;
		
		for(int i = 0; i < spectrumAssignment.length; i++){
			if (spectrumAssignment[i] == true){
				start = i;
				break;
			}
		}
		
		for(int i = start; i < spectrumAssignment.length; i++){
			if (spectrumAssignment[i] == false){
				end = i-1;
				return;
			}
		}
		
		end = spectrumAssignment.length-1;
	}
	
	public Connection(Connection another){
		this.route = another.route;
		this.request = another.getRequest();
		
		this.spectrumAssignment = another.copySpectrumAssignment();
		this.start = another.getStart();
		this.end = another.getEnd();
	}
	
	public Request getRequest(){
		return request;	
	}
	
	public boolean[] copySpectrumAssignment(){
		boolean[] copy = new boolean[spectrumAssignment.length];
		System.arraycopy(spectrumAssignment, 0, copy, 0, spectrumAssignment.length);
		return copy;
	}
	
	public void setStart(int start){
		this.start = start;
	}
	
	public int getStart(){
		return start;
	}
	
	public void setEnd(int end){
		this.end = end;
	}
	
	public int getEnd(){
		return end;
	}
		
}