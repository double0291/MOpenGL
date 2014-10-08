package com.doublechen.mopengl.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.doublechen.mopengl.view.Cube;

public class CubeGLRender implements GLSurfaceView.Renderer {
	private boolean mTranslucentBackground;
	private Cube mCube;
	private float mTransY;

	 private float mAngle;

	public CubeGLRender(boolean useTranslucentBackground) {
		this.mTranslucentBackground = useTranslucentBackground;
		mCube = new Cube();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) { // 15
		// Set the background frame color
		gl.glDisable(GL10.GL_DITHER); // 16
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 17

		if (mTranslucentBackground) // 18
			gl.glClearColor(0, 0, 0, 0);
		else
			gl.glClearColor(1, 1, 1, 1);

		gl.glEnable(GL10.GL_CULL_FACE); // 19
		gl.glShadeModel(GL10.GL_SMOOTH); // 20
		gl.glEnable(GL10.GL_DEPTH_TEST); // 21
		// 只绘制正面
//		gl.glCullFace(GL10.GL_FRONT);
	}

	public void onDrawFrame(GL10 gl) { // 4
		// Redraw background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // 5
		gl.glClearColor(0.0f, 0.5f, 0.5f, 1.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW); // 6
		// 将当前的用户坐标系的原点移到屏幕中心，类似于一个复位操作
		gl.glLoadIdentity(); // 7
		
		// 沿着 X, Y 和 Z 轴移动
		gl.glTranslatef(0.0f, (float) Math.sin(mTransY) / 2, -7.0f); // 8
		
		// 变换角度
		gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(mAngle, 1.0f, 0.0f, 0.0f);
		
		/* 一系列的图形操作，最后的先执行，所以把形状的变换放到最后，
		 * 如果放到前面，会之旋转的过程中变换形状，而不是一开始变换 */
		// 变换形状
		gl.glScalef(1, 2, 1);
		

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // 9
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		mCube.draw(gl); // 10

		mTransY += .075f;
		mAngle += .4f;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) { // 11
		gl.glViewport(0, 0, width, height); // 12

//		float ratio = (float) width / height;
		
		float aspectRatio;
		float zNear = .1f;
		float zFar = 1000;
		// 加大这个值，可以将视角挪远
		float fieldOfView = 300.0f / 57.3f;
		float size;
		
		gl.glEnable(GL10.GL_NORMALIZE);
		aspectRatio=(float)width/(float)height; 
		gl.glMatrixMode(GL10.GL_PROJECTION);
		size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
		gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio, zNear, zFar);
		
		
//		gl.glMatrixMode(GL10.GL_PROJECTION); // 13
//		gl.glLoadIdentity();
//		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10); // 14
	}

}
