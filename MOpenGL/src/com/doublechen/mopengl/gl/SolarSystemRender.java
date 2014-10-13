package com.doublechen.mopengl.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.doublechen.mopengl.utils.BufferUtil;
import com.doublechen.mopengl.view.Planet;

public class SolarSystemRender implements GLSurfaceView.Renderer {
	// 代表光源0，最多8个，0-7
	public static final int SS_SUNLIGHT = GL10.GL_LIGHT0;

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
		// 下面这句话没用诶
		// gl.glCullFace(GL10.GL_BACK);
		gl.glShadeModel(GL10.GL_SMOOTH); // 20
		gl.glEnable(GL10.GL_DEPTH_TEST); // 21
		// 下面这句话加上后就不显示球来
		// gl.glDepthMask(false);

		initGeometry(gl);
		initLighting(gl);
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

	private void initLighting(GL10 gl) {
		// 环境光参数，RGBA
		// float[] ambient = { 0.3f, 0.7f, 0.5f, 1.0f };
		// 漫射光参数，RGBA
		float[] diffuse = { 0.0f, 1.0f, 0.0f, 1.0f }; // 1
		// X, Y, Z, and???
		// 遗留问题，这边的Y轴参数反了，不晓得为什么
		float[] pos = { 0f, 5.0f, -3.0f, 1f }; // 2

		float[] white = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] red = { 1.0f, 0.0f, 0.0f, 1.0f };
		float[] green = { 0.0f, 1.0f, 0.0f, 1.0f };
		float[] blue = { 0.0f, 0.0f, 1.0f, 1.0f };
		float[] cyan = { 0.0f, 1.0f, 1.0f, 1.0f }; // 青色
		float[] yellow = { 1.0f, 1.0f, 0.0f, 1.0f };
		float[] magenta = { 1.0f, 0.0f, 1.0f, 1.0f }; // 洋红
		float[] halfcyan = { 0.0f, 0.5f, 0.5f, 1.0f };

		// fv means float vector
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, BufferUtil.makeFloatBuffer(pos)); // 3
		// 光照颜色
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(green));// 4
		// 当前材质颜色，
		// 显示的颜色是光照颜色和材质颜色RGB互相与的结果，
		// 如果光照为红，材质为绿，会显示成一个黑球
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(yellow));
		// gl.glLightfv(SS_SUNLIGHT, GL10.GL_AMBIENT,
		// BufferUtil.makeFloatBuffer(ambient));
		// 如果使用GL_SMOOTH会使表面的光很柔和
		gl.glShadeModel(GL10.GL_SMOOTH);// 5
		// 启动光源总开关
		gl.glEnable(GL10.GL_LIGHTING);// 6
		// 允许光源1
		gl.glEnable(SS_SUNLIGHT);// 7

		gl.glLoadIdentity();
	}

}
