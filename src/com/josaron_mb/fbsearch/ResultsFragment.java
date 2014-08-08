package com.josaron_mb.fbsearch;

import android.support.v7.app.ActionBarActivity;

import com.facebook.*;
import com.facebook.model.*;
import com.josaron_mb.fbsearch.R;
import com.josaron_mb.fbsearch.R.id;
import com.josaron_mb.fbsearch.R.layout;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ResultsFragment extends Fragment implements AsyncResponse {
	
	private static final String TAG = "ResultsFragment";
	private static final int TYPE_PHOTO_ROW = 0;
    private static final int TYPE_POST_ROW = 1;
	private static final int TYPE_PHOTO = 3;
    private static final int TYPE_POST = 4;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private UiLifecycleHelper uiHelper;
	private static DataHolder dataHolder;
	private static JSONPhotoParser jsonPhotoParser;
	private Context context;
	private ListView listView;
	
	private ArrayList<RowWrapperItem> rowItems;
	ArrayList<WrapperItem> matchingItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.i(TAG, "Result frag started");
	    context = getActivity();
		dataHolder = ((DataHolder) context.getApplicationContext());
		jsonPhotoParser = new JSONPhotoParser(context);
	    
	    uiHelper = new UiLifecycleHelper((Activity) context, callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		int xml = R.layout.fragment_results;
		View view = inflater.inflate(R.layout.fragment_results, container, false);
	
	    /*// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra("msg");*/
		
		matchingItems = dataHolder.getMatchingItems();
		if (matchingItems.size() > 0) {
			Collections.sort(matchingItems, new WrapperItemComparator());
			downloadImages(matchingItems);
			//ArrayList<RowWrapperItem> rowItems = convertToRows(matchingItems);
		}
		// create adapter list
		// putoutview
	
	    // Create a text view
	    TextView textView = new TextView(getActivity().getBaseContext());
	    textView.setTextSize(40);
	    textView.setText("Try again.");


	    ListView listView = (ListView) view.findViewById(R.id.list);
		
		/*boolean t = true;
	    if (t) {
	    	listView.setAdapter(new ItemAdapter(getActivity().getBaseContext(), rowItems));
	    }
		else {
			view = textView;
		}*/


		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                    long id) {
                //String msg = rowItems.get(position);
                //Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG).show();
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
	
	private ArrayList<RowWrapperItem> convertToRows(ArrayList<WrapperItem> sortedMatches) {
		ArrayList<WrapperItem> matchingItems = dataHolder.getMatchingItems();
		ArrayList<RowWrapperItem> rowItems = new ArrayList<RowWrapperItem>();
		if (matchingItems.size() > 0) {
			Collections.sort(matchingItems, new WrapperItemComparator());
			for (int i = 0; i < matchingItems.size(); i = i + 2) {
				/*Photo left = matchingItems.get(i).getPhoto();
				Photo right = null;
				if (i + 1 < matchingItems.size()) {
					right = matchingItems.get(i + 1).getPhoto();
				}
				RowWrapperItem photoRow = new RowWrapperItem(left, right);
				rowItems.add(photoRow);*/
				if (matchingItems.get(i).getType() == TYPE_PHOTO) {
					Photo left = matchingItems.get(i).getPhoto();
					Photo right = null;
					int j = i + 1;
					while (j < matchingItems.size() &&
							matchingItems.get(j).getType() != TYPE_PHOTO) {
						j++;
					}
					if (j < matchingItems.size() &&
							matchingItems.get(j).getType() == TYPE_PHOTO) {
						right = matchingItems.get(j).getPhoto();
					}
					RowWrapperItem photoRow = new RowWrapperItem(left, right);
					rowItems.add(photoRow);
					for (int k = i + 1; k < j; k++) {
						//rowItems.add(new RowWrapperItem(matchingItems.get(k).getPost());
					}
					i = j;
				}
				else {
					//rowItems.add(new RowWrapperItem(matchingItems.get(i).getPost());
				}
			}
		}
		return rowItems;
	}
	
	private void downloadImages(ArrayList<WrapperItem> matchingItems) {
		Log.i(TAG, "downloadImages()");
		if (matchingItems.size() > 0) {
			HashMap<String, String> toDownload = new HashMap<String, String>();
			for (int i = 0; i < matchingItems.size(); i++) {
				WrapperItem currentItem = matchingItems.get(i);
				if (currentItem.getType() == TYPE_PHOTO &&
						currentItem.getPhoto().getBitmap() != null) {
					Photo currentPhoto = currentItem.getPhoto();
					toDownload.put(currentPhoto.getId(), currentPhoto.getURL());
				}
				
			}
			
			AsyncParam param = new AsyncParam(context, toDownload);
			DownloadImageAsync asyncTask = new DownloadImageAsync();
			asyncTask.delegate = ResultsFragment.this;
			asyncTask.execute(param);
		}
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
		listView.setAdapter(new ItemAdapter(getActivity().getBaseContext(), rowItems));
	}
    
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        //batchRequestButton.setVisibility(View.VISIBLE);
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        //batchRequestButton.setVisibility(View.INVISIBLE);
	    }
	}

}