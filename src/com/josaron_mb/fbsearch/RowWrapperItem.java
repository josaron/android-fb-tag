package com.josaron_mb.fbsearch;

public class RowWrapperItem {
	
	private static final int TYPE_PHOTO_ROW = 0;
    private static final int TYPE_POST_ROW = 1;
	
	private int type;
	private Photo photo1;
	private Photo photo2;
	//private Post post;
	
	public RowWrapperItem(Photo photo1, Photo photo2) {
		type = TYPE_PHOTO_ROW;
		this.photo1 = photo1;
		this.photo2 = photo2;
	}
	
	//public WrapperItem(Post post) {}

	public int getType() {
		return type;
	}
	
	public Photo getPhoto1() {
		return photo1;
	}
	
	public Photo getPhoto2() {
		return photo2;
	}
}
