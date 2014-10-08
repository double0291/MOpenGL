package com.doublechen.mopengl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private String[] mActivities = { "Square", "Cube", "Planet" };

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mListView = new ListView(this);
		mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list_item, getData()));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				Intent i = new Intent();
				i.putExtra("position", position);
				i.setClass(MainActivity.this, OpenGLActivity.class);
				startActivity(i);
			}
		});

		setContentView(mListView);
	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>(mActivities.length);

		for (String a : mActivities) {
			data.add(a);
		}

		return data;
	}
}
