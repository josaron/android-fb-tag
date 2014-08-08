package com.josaron_mb.fbsearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadImageAsync extends AsyncTask<AsyncParam, Void, HashMap<String, Bitmap>> {
	
	public AsyncResponse delegate = null;
	
	private static final String TAG = "DownloadImageAsync";
	private Context context;
    
	@Override
	protected void onPreExecute() {
		Log.i(TAG, "onPreExecute()");
	}
	
	@Override
	protected HashMap<String, Bitmap> doInBackground(AsyncParam... params) {
		Log.i(TAG, "doInBackground()");
		
		AsyncParam param = params[0];
		context = param.getContext();
		HashMap<String, String> photos = param.getPhotos();
		HashMap<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();
		
		for (String id : photos.keySet()) {
			URL url;
			String urlString = photos.get(id);
			try {
				url = new URL(urlString);
				Bitmap bitmap = getBitmapFromURL(url);
				bitmaps.put(id, bitmap);
			} catch (MalformedURLException e) {
				Log.e(TAG, "Error instantiating URL.");
				e.printStackTrace();
			}
		}
		return bitmaps; 
    }

	/*@Override
    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }*/

	/**
	 * Save the retrieved data to the 
	 * global DataHolder object.
	 */
	@Override
    protected void onPostExecute(HashMap<String, Bitmap> bitmaps) {
		DataHolder dataHolder = ((DataHolder) context.getApplicationContext());
		Log.d(TAG, "onPostExecute()");
		
		for (String id : bitmaps.keySet()) {
			Bitmap b = bitmaps.get(id);
			Photo p = dataHolder.getItemById(id).getPhoto();
			p.setBitmap(b);
		}
		
		delegate.processFinish();
    }
	
	private static Bitmap getBitmapFromURL(URL url) {
	    try {
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	    	Log.e(TAG, "Error getting bitmap.");
	        e.printStackTrace();
	        return null;
	    }
	}
}