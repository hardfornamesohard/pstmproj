package util;

public class MaxPriorityQueue <T extends Comparable<T>> {
	private T []item;
	private int N;
	public MaxPriorityQueue(int capacity) {
		item = (T[]) new Comparable[capacity + 1];
		N = 0;
	}
	public int size() {
		return N;
	}
	public boolean isEmpty() {
		return N == 0;
	}
	private void exchange(int i, int j) {
		T tmp = item[i];
		item[i] = item[j];
		item[j] = tmp;
	}
	private boolean less(int i, int j) {
		return item[i].compareTo(item[j]) < 0;
	}
	public T delMax() {
		T max = item[1];
		exchange(1, N);
		item[N] = null;
		N--;
		sink(1);
		return max;
		
	}
	private void sink(int k) {
		while(2*k <= N) {
			int max = 2*k;
			if(2*k+1 <= N) max = less(2*k, 2*k+1)? 2*k+1:max;
			if(less(k, max)) exchange(k, max);
			else break;
			k = max;
		}
		
	}
	public void insert(T t) {
		item[++N] = t;
		swim(N);
		
	}

	private void swim(int k) {
		while(k > 1) {
			if(less(k/2, k)) exchange(k, k/2);
			k/=2;
		}
	}
}
