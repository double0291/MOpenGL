package com.doublechen.mopengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.doublechen.mopengl.gl.MyGLSurfaceView;

public class OpenGLActivity extends Activity {
	private GLSurfaceView mGLView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get data
		int position = this.getIntent().getIntExtra("position", -1);
		if (position < 0)
			return;

		// Create a GLSurfaceView instance and set it as the ContentView for
		// this Activity
		mGLView = new MyGLSurfaceView(this, position);
		setContentView(mGLView);
	}
}
