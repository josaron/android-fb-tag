package com.josaron_mb.fbsearch;

import java.util.HashMap;
import java.util.HashSet;

public class WrapperItem implements Comparable<WrapperItem> {

	private static final int TYPE_PHOTO = 3;
    private static final int TYPE_POST = 4;
    
    private int type;
    private Photo photo;
    //private Post post;
    
	private String id;
	private HashSet<String> tags;
	private HashMap<String, String> matches; // <id, name>
	
	public WrapperItem(String id, Photo photo) {
		type = TYPE_PHOTO;
		this.id = id;
		this.photo = photo;
	}
	
	//public WrapperItem(Post post) {}
	
	public int getType() {
		return type;
	}
	
	public Photo getPhoto() {
		return photo;
	}
	
	public void setMatches(HashMap<String, String> matches) {// <id, name>
		this.matches = matches;
	}
	
	/**
	 * Comparator for sorting photos in the priority queue.
	 */
	@Override
    public int compareTo(WrapperItem otherItem) {
		int currentsMatches = this.matches.size();
		int othersMatches = otherItem.matches.size();
        return (othersMatches - currentsMatches);
    }
}
