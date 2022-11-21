package util;

import pstmproj.Agent;

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
	public Value get(Key key) {
		Node curr = head;
		while(curr.next != null) {
			curr = curr.next;
			if(curr.key.equals(key))return curr.value;	
		}
		return null;
	}
	public void put(Key key, Value value) {
		//符号表中已有该key值
		Node n = head;
		while(n.next != null) {
			n = n.next;
			if(n.key.equals(key)) {
				//Agent a1 = n.value[0];
				Agent[] pair = (Agent[])value;
				Agent[] pair2 = (Agent[])n.value;
				Agent[] pairs = {pair[0], pair[1], pair2[1]};
				n.value = (Value)pairs;
				return;
			}
		}
		//符号表中不存在该key值
		Node oldfirst = head.next;
		Node newfirst = new Node(key,value,oldfirst);
		head.next = newfirst;
		N++;
	}
	public void delete(Key key) {
		Node n = head;
		while(n.next != null) {
			if(n.next.key.equals(key)) {
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
}
