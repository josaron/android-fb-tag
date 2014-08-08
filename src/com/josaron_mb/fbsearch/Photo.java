package com.josaron_mb.fbsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.graphics.Bitmap;

public class Photo {

	private String url;
	private Bitmap bitmap = null;
	private String id;
	
	public Photo(String id, String url) {
		this.id = id;
		this.url = url;
	}
	
	public String getId() {
		return id;
	}
	
	public String getURL() {
		return url;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
