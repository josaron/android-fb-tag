package com.josaron_mb.fbsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

public class DataHolder extends Application {
	private static final String TAG = "DataHolder";
	
	// Basic user info
	private Bitmap userProfilePic;
	private String userName;
	
	ArrayList<WrapperItem> matchingItems = new ArrayList<WrapperItem>();
	HashMap<String, WrapperItem> storedItems = new HashMap<String, WrapperItem>(); // <id, photo>
	
	
	public WrapperItem getItemById(String id) {
		return storedItems.get(id);
	}
	
	public void storeItem(String id, String url, HashMap<String, String> matches) {
		// matches = <id, name>
		if (!storedItems.containsKey(id)) {
			saveNewItem(id, url);
		}
		updateItem(id, matches); // reset the matches list
		addMatchingItem(id);
	}

	private void saveNewItem(String id, String url) {
		boolean isPhoto = true;
		if (isPhoto) {
			Photo photo = new Photo(id, url);
			WrapperItem item = new WrapperItem(id, photo);
			storedItems.put(id, item);
		}
	}
	private void updateItem(String id, HashMap<String, String> matches) {
		WrapperItem item = storedItems.get(id);
		item.setMatches(matches);
	}
	private void addMatchingItem(String id) {
		WrapperItem item = storedItems.get(id);
		matchingItems.add(item);
	}
	
	// Returns <id, photo>
	public HashMap<String, WrapperItem> getStoredItems() {
		return storedItems;
	}
	public ArrayList<WrapperItem> getMatchingItems() {
		return matchingItems;
	}
	
	/*public Bitmap getUserProfilePic() {
	return userProfilePic;
}
public void setUserProfilePic(Bitmap userProfilePic) {
	this.userProfilePic = userProfilePic;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}*/
}
