package com.doublechen.mopengl.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	public MyGLSurfaceView(Context context) {
		super(context);
	}

	public MyGLSurfaceView(Context context, int position) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		switch (position) {
		case 0:
			setRenderer(new SquareGLRender(true));
			break;
		case 1:
			setRenderer(new CubeGLRender(true));
			break;
		case 2:
			setRenderer(new SolarSystemRender());
			break;

		default:
			break;
		}

	}

}
