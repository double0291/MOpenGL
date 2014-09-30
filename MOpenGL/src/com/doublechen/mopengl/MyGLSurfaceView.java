package com.doublechen.mopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	public MyGLSurfaceView(Context context) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(new MyGLRender(true));
	}

}
