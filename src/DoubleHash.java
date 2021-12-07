import java.util.*;

public class DoubleHash<K1, K2, V> extends HashMap<K1, HashMap<K2, V>>{
	
	private static final long serialVersionUID = 1L;

	public boolean containsKey(K1 key1, K2 key2){
		if(this.containsKey(key1)){
			if(this.get(key1).containsKey(key2)){
				return true;
			}
		}
		return false;
	}
	
	public V get(K1 key1, K2 key2){
		if(containsKey(key1, key2)){
			return super.get(key1).get(key2);
		}
		return null;
	}
	
	public V put(K1 key1, K2 key2, V value){
		if(this.containsKey(key1)){
			return this.get(key1).put(key2, value);
	}
		this.put(key1, new HashMap<K2, V>());
		this.get(key1).put(key2, value);
		return null;
	}
	
	public Collection<V> getValues(){
		Collection<V> allValues = new ArrayList<V>();
		for (K1 key1 : this.keySet()){
			allValues.addAll(super.get(key1).values());
		}
		return allValues;
	}
	
	public int size(){
		int size = 0;
		for (K1 key1 : this.keySet()){
			size += this.get(key1).size();
		}
		return size;
	}
	
	public void print(){
		for (K1 key1 : this.keySet()){
			for (K2 key2 : this.get(key1).keySet()){
				System.out.println(this.get(key1, key2));
			}
		}
	}
	
	public static void main(String[] args){
		DoubleHash<Integer, Integer, String> index = new DoubleHash<Integer, Integer, String>();
		index.put(1, 14, "a");
		index.put(1, 7, "b");
		index.put(2, 12, "c");
		index.put(2, 6, "d");
		index.print();
		Collection<String> collection = new ArrayList<String>();
		collection = index.getValues();
		System.out.println(collection);
		
		DoubleHash<Integer, Integer, Link> x = new DoubleHash<Integer, Integer, Link>();
		Link link = new Link(1,14,10,1);
		x.put(1, 14, link);

	}
	
}