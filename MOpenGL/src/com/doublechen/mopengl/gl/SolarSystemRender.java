package com.doublechen.mopengl.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.doublechen.mopengl.view.Planet;

public class SolarSystemRender implements GLSurfaceView.Renderer {
	private boolean mTranslucentBackground;

	Planet mPlanet;

	private float mTransY;
	private float mAngle;

	public SolarSystemRender(boolean useTranslucentBackground) {
		this.mTranslucentBackground = useTranslucentBackground;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		gl.glDisable(GL10.GL_DITHER); // 16
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 17

		if (mTranslucentBackground) // 18
			gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
		else
			gl.glClearColor(1, 1, 1, 1);

		gl.glEnable(GL10.GL_CULL_FACE); // 19
		gl.glShadeModel(GL10.GL_SMOOTH); // 20
		gl.glEnable(GL10.GL_DEPTH_TEST); // 21

		initGeometry(gl);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -4.0f);
		gl.glRotatef(mAngle, 1, 0, 0);
		gl.glRotatef(mAngle, 0, 1, 0);
		mPlanet.draw(gl);
		mTransY += .075f;
		mAngle += .4;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height); // 12

		float aspectRatio;
		float zNear = .1f;
		float zFar = 1000;
		float fieldOfView = 300.0f / 57.3f;
		float size;

		gl.glEnable(GL10.GL_NORMALIZE);
		aspectRatio = (float) width / (float) height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		size = zNear * (float) (Math.tan((double) (fieldOfView / 2.0f)));
		gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio, zNear, zFar);
	}

	private void initGeometry(GL10 g0) {
		// 更改stacks和slices的值会让球体更细腻，更改squash的值会让球变形
		// 还可以采用特殊的lighting和shading工具或者textures来让球更细腻
		// 这种会在后面展开
		mPlanet = new Planet(20, 20, 1.0f, 1.0f);
	}

}
