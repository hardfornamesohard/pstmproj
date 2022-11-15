package util;

public class Bubble{
	public static void sort(Comparable []a) {
		for(int x = a.length - 1; x  > 0; x-- ) {
			for(int y = 0; y < x; y++ ) {
				if(greater(a[y], a[y+1]))exchange(a, y, y + 1);
			}
		}
		
	}
	private static boolean greater(Comparable v, Comparable w) {
		return v.compareTo(w) > 0;
	}
	private static void exchange(Comparable []a, int x, int y) {
		Comparable t = a[x];
		a[x] = a[y];
		a[y] = t;
	}

}
