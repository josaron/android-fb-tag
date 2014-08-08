package com.josaron_mb.fbsearch;

import java.util.Comparator;

public class WrapperItemComparator implements Comparator<WrapperItem> {
	
	@Override
	public int compare(WrapperItem item1, WrapperItem item2) {
		return item1.compareTo(item2);
	}
}
