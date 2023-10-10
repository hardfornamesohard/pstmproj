package pstmproj;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SymbolTable<Key, Value> {

	private Node head;
	private int N;
	public SymbolTable() {
		head = new Node(null, null, null) ;
		N = 0;
	}
	public int size() {
		return this.N;
	}
	public boolean isEmpty() {
		return this.N == 0;
	}
	public Agent[] get(Key key) {
		int count = 0;
		Node curr = head;
		while(curr.next != null) {
			curr = curr.next;
			if(curr.key.equals(key))count++;	
		}
		if(count == 0) return null;
		Agent[] values = new Agent[count];
		Node get = head;
		int index = 0;
		while(get.next != null) {
			get = get.next;
			if(get.key.equals(key)) {
				values[index] = (Agent)get.value;
				index++;
			}
		}
		return values;
	}
	public void put(Key key, Value value) {
		//���ű������и�keyֵ,�����и�value��������
		Node n = head;
		while(n.next!=null) {
			n = n.next;
			if(n.key == key && n.value == value) return;
		}
		//���ű��в����ڸ�key-value��
		Node oldfirst = head.next;
		Node newfirst = new Node(key,value,oldfirst);
		head.next = newfirst;
		N++;
	}
	public void delete(Key key, Value value) {
		Node n = head;
		while(n.next != null) {
			if(n.next.key.equals(key) && n.next.value.equals(value)) {
				n.next = n.next.next;
				N--;
				return;
			}
			n = n.next;
		}
	}
	private class Node{
		public Key key;
		public Value value;
		public Node next;
		public Node(Key key, Value value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
	}
	public Set<Key> keys(){
		Set<Key> keys = new HashSet<>();
		if(head == null) return null;
		for (int i = 0; i < N; i++) {
			keys.add(head.key);
			head = head.next;
		}
		Iterator<Key> iterator = keys.iterator();
		while (iterator.hasNext()){
			System.out.println(iterator.next());
		}
		return keys;
	}
}
