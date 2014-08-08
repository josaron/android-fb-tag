package com.josaron_mb.fbsearch;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class AsyncParam {

	private Context context;
	private HashMap<String, String> photos; // <id, url>
	
	public AsyncParam(Context context, HashMap<String, String> photos) {
		this.context = context;
		this.photos = photos;
	}

	public Context getContext() {
		return context;
	}

	public HashMap<String, String> getPhotos() {
		return photos;
	}
}
