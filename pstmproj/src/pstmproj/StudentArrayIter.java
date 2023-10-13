package pstmproj;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StudentArrayIter<T> implements Iterator<T> {
    private T[] array;
    private int index;

    public StudentArrayIter(T[] array) {
        this.array = array;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T next() {
        if(!hasNext()) throw new NoSuchElementException();
        T element = array[index];
        index++;
        return element;
    }
}
