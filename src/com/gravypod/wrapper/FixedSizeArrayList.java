package com.gravypod.wrapper;

import java.util.ArrayList;

public class FixedSizeArrayList<T> extends ArrayList<T> {
	
	private static final long serialVersionUID = 3278041448599317104L;
	
	private final int maxIndex;
	public FixedSizeArrayList(int maxSize) {
	
		super(maxSize);
		this.maxIndex = maxSize - 1;
	}
	
	@Override
	public boolean add(T t) {
	
		if (size() > (maxIndex + 1)) {
			remove(0);
		}
		return super.add(t);
	}
	
	public T set(int index, T element) {
		
		if (index > maxIndex) {
			throw new IndexOutOfBoundsException("Used an index outside of the FixedArrayList");
		}
		return super.set(index, element);
	}
	
}
