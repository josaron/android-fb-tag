package com.josaron_mb.fbsearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.josaron_mb.fbsearch.R;
import com.josaron_mb.fbsearch.R.id;
import com.josaron_mb.fbsearch.R.layout;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
//import android.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class MainFragment extends Fragment implements AsyncResponse {
	
	//
	// INSTANCE VARIABLES
	//
	
	private static final String TAG = "MainFragment";
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private UiLifecycleHelper uiHelper;
	private TextView textViewResults;
	private Button batchRequestButton;
	private Button addButton;
	private Button requestData;
	private ImageView profilePic;
	
	private static DataHolder dataHolder;
	private static JSONPhotoParser jsonPhotoParser;
	private Context context;
	private GraphObject graphObject;

	
	/**
	 * Constructor
	 */
	public MainFragment() {
	}
	
	
	//
	// OVERRIDEN PUBLIC METHODS
	//
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    context = getActivity();
		dataHolder = ((DataHolder) context.getApplicationContext());
		jsonPhotoParser = new JSONPhotoParser(context);
	    
	    uiHelper = new UiLifecycleHelper((Activity) context, callback);
	    uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		textViewResults = (TextView) view.findViewById(R.id.text_view_results);
		batchRequestButton = (Button) view.findViewById(R.id.batch_request);
		addButton = (Button) view.findViewById(R.id.add_button);
		profilePic = (ImageView) view.findViewById(R.id.photo1);
		requestData = (Button) view.findViewById(R.id.data_request);
		
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "user_photos"));
		
		/*addButton.setOnClickListener(new View.OnClickListener() {
			@Override
		    public void onClick(View v) {
			    //Toast.makeText(getActivity().getBaseContext(), "msg", Toast.LENGTH_LONG).show();
			    //openNewFragment();
			}
		});*/
		
		requestData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "test");
				HashSet<String> searchSet = constructSearchSet();
				requestPhotos();
				if (graphObject != null) {
					processGraphObject(searchSet); 
				}
				HashMap<String, WrapperItem> storedItems = dataHolder.getStoredItems();
				if (storedItems.size() > 0) {
					openNewFragment();
				}
			}
		});
		
		batchRequestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] user = {"me"};
				String edge = "/picture";
				String[] keys = {"redirect", "type"};
				String[] vals = {"false", "large"};
				//doBatchRequest(user, edge, keys, vals);
				//handleGraphObject();
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	//
	// PRIVATE METHODS
	//
	
	private void processGraphObject(HashSet<String> searchSet) {
		JSONArray itemList = getItemList(); // items are either posts or photos
		JSONObject item;
		for (int i = 0; i < itemList.length(); i++) {
			try {
				item = (JSONObject) itemList.get(i);
				HashMap<String, String> matches = findTagMatches(searchSet, item); // <id, name>
				if (matches.size() > 0) {
					saveItem(matches, item);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Error retrieving item from JSONArray - processObject()");
				e.printStackTrace();
			}
		}
	}
	
	private JSONArray getItemList() {
		boolean photoRequest = true;
		JSONArray itemList = null;
		if (photoRequest) {
			itemList = (JSONArray) graphObject.getProperty("data");
		}
		return itemList;
	}

	// Returns <id, name>
	private HashMap<String, String> findTagMatches(HashSet<String> searchSet, JSONObject item) {
		HashMap<String, String> matches = new HashMap<String, String>();	
		boolean photoMode = true;
		if (photoMode) {
			jsonPhotoParser.setPhoto(item);
			HashMap<String, String> allTags = jsonPhotoParser.getAllTags(); // <name, id>
			for (String person : searchSet) {
				if (allTags.containsKey(person)) {
					matches.put(allTags.get(person), person); //<id, name>
				}
			}
		}
		return matches;
	}
	
	private void saveItem(HashMap<String, String> matches, JSONObject item) { 
		// matches = <id, name>
		jsonPhotoParser.setPhoto(item);
		String id = jsonPhotoParser.getId();
		String url = jsonPhotoParser.getURL();

		dataHolder.storeItem(id, url, matches);
		// Adds field to photo....
			
	}
	
	private HashSet<String> constructSearchSet() {
		HashSet<String> searchSet = new HashSet<String>();
		searchSet.add("Shlomo Fischer");
		searchSet.add("Eli Freedman");
		searchSet.add("Rachel Sterman");
		searchSet.add("David Goldrich");
		return searchSet;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        batchRequestButton.setVisibility(View.VISIBLE);
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        batchRequestButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	private void requestPhotos() {
		String[] user = {"me"};
		String edge = "/photos";
		String[] keys = {};
		String[] vals = {};
		doBatchRequest(user, edge, keys, vals);
	}
	
	private void doBatchRequest(String[] ids, String edge, String[] keys, String[] vals) {
	    RequestBatch requestBatch = new RequestBatch();
	    for (String id : ids) {
	    	String graphPath = id + edge;
	    	Log.i(TAG, graphPath);
	    	Bundle params = new Bundle();
	    	for (int i = 0; i < keys.length; i++) {
	    		params.putString(keys[i], vals[i]);
	    	}
	        requestBatch.add(new Request(Session.getActiveSession(), 
	                graphPath, 
	                //params,  
	                null,
	                HttpMethod.GET, 
	                new Request.Callback() {
	            		public void onCompleted(Response response) {
	            			graphObject = response.getGraphObject();
	            		}
	        }));
	    }
	    requestBatch.executeAsync(); 
	}
	
	/**
	 * Called upon the completion of 
	 * the asynchronous task
	 */
	public void processFinish() {
		/*String name = dataHolder.getUserName();
		Bitmap bitmap = dataHolder.getUserProfilePic();
		
		bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
		profilePic.setImageBitmap(bitmap);*/
	}
	
	/**
	 * Call this method to open the next fragment
	 */
	private void openNewFragment() {
		/*Intent intent = new Intent(MainActivity.this, ResultActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
   		String message = "Hello world";
		intent.putExtra("msg", message);
   		startActivity(intent);*/
		
		String str = "Results";
		
		// Create new fragment and transaction
		ResultsFragment newFragment = new ResultsFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(((ViewGroup)getView().getParent()).getId(), newFragment, str);
		transaction.addToBackStack("Results");

		// Commit the transaction
		transaction.commit();
	}
	
	/*
	AsyncParam param = new AsyncParam(context, urlStrings);
	
	DownloadImageAsync asyncTask = new DownloadImageAsync();
	asyncTask.delegate = MainFragment.this;
	asyncTask.execute(param);*/
}