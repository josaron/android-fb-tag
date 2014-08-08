package com.josaron_mb.fbsearch;

import android.support.v7.app.ActionBarActivity;
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
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

import com.josaron_mb.fbsearch.R;
import com.josaron_mb.fbsearch.R.id;
import com.josaron_mb.fbsearch.R.layout;


public class ItemAdapter extends BaseAdapter {

	private static final int TYPE_PHOTO_ROW = 0;
    private static final int TYPE_POST_ROW = 1;
    private static final int TYPE_MAX_COUNT = TYPE_POST_ROW + 1;

    Context context;
    ArrayList<RowWrapperItem> items;
    private static LayoutInflater inflater = null;

    public ItemAdapter(Context context, ArrayList<RowWrapperItem> items) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
    	RowWrapperItem item = items.get(position);
        if (item.getType() == 0) {
        	return TYPE_PHOTO_ROW;
        }
        else {
        	return TYPE_POST_ROW;
        }
        //items.get(position).getType();
    }
    
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
        	if (type == TYPE_POST_ROW) {
        		view = inflater.inflate(R.layout.post_row, null);
        		TextView text = (TextView) view.findViewById(R.id.text);
                //text.setText(items.get(position));
        	}
        	else if (type == TYPE_PHOTO_ROW) {
        		view = inflater.inflate(R.layout.photo_row, null);
        		ImageButton leftPhoto = (ImageButton) view.findViewById(R.id.left_photo);
        		ImageButton rightPhoto = (ImageButton) view.findViewById(R.id.right_photo);
        		
        		RowWrapperItem item = (RowWrapperItem) getItem(position);
        		Bitmap b1 = item.getPhoto1().getBitmap();
        		Bitmap b2 = item.getPhoto2().getBitmap();
        		
        		if (b1 != null) leftPhoto.setImageBitmap(b1);
        		if (b2 != null) rightPhoto.setImageBitmap(b2);
        	}
        }

        return view;
    }
}