package com.josaron_mb.fbsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.facebook.model.GraphObject;

public class JSONPhotoParser {
	
	private static final String TAG = "PhotoObjectParser";
	private DataHolder dataHolder;
	
	private JSONObject jsonPhoto;
	
	
	public JSONPhotoParser(Context context) {
		dataHolder = ((DataHolder) context.getApplicationContext());
	}
	
	
	public void setPhoto(JSONObject jsonPhoto) {
		this.jsonPhoto = jsonPhoto;
	}
	
	public String getId() {
		return jsonPhoto.optString("id");
	}
	
	/*public String getName() {
		
	}*/
	
	public String getURL() {
		return jsonPhoto.optString("source");
	}
	
	// return <name, id>
	public HashMap<String, String> getAllTags() {
		HashMap<String, String> tags = new HashMap<String, String>();
		try {
			JSONArray jsonTagList = jsonPhoto.getJSONObject("tags").getJSONArray("data");
			for (int i = 0; i < jsonTagList.length(); i++) {
				JSONObject taggedPerson = jsonTagList.getJSONObject(i);
				String name = taggedPerson.optString("name");
				String id = taggedPerson.optString("id");
				tags.put(name, id);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tags;
	}
}
