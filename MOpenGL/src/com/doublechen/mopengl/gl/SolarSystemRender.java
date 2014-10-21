package com.doublechen.mopengl.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.doublechen.mopengl.utils.BufferUtil;
import com.doublechen.mopengl.view.Planet;

public class SolarSystemRender implements GLSurfaceView.Renderer {
	public static final int X_VALUE = 0;
	public static final int Y_VALUE = 1;
	public static final int Z_VALUE = 2;

	Planet mEarth, mSun;
	float[] mEyePosition = { 0.0f, 0.0f, 0.0f };

	static float angle = 0.0f;

	// 代表光源0，最多8个，0-7
	public static final int SS_SUNLIGHT = GL10.GL_LIGHT0;
	public static final int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
	public static final int SS_FILLLIGHT2 = GL10.GL_LIGHT2;

	private boolean mTranslucentBackground;

	public SolarSystemRender(boolean useTranslucentBackground) {
		this.mTranslucentBackground = useTranslucentBackground;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		/*
		 * 对于可用颜色较少的系统，可以以牺牲分辨率为代价，通过颜色值的抖动来增加可用颜色数量。抖动操作是和硬件相关的，
		 * OpenGL允许程序员所做的操作就只有打开或关闭抖动操作
		 * 。实际上，若机器的分辨率已经相当高，激活抖动操作根本就没有任何意义。要激活或取消抖动
		 * ，可以用glEnable(GL_DITHER)和glDisable(GL_DITHER)函数。默认情况下，抖动是激活的。
		 */
		gl.glDisable(GL10.GL_DITHER); // 16
		/*
		 * 是指定颜色和纹理坐标的插值质量. GL_FASTEST为使用速度最快的模式. GL_NICEST为使用质量最好的模式.
		 * 还有一个GL_DONT_CARE为由驱动设备来决定.
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 17

		/* 设置屏幕背景色 */
		if (mTranslucentBackground) // 18
			gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
		else
			gl.glClearColor(1, 1, 1, 1);

		/*
		 * OpenGL ES
		 * 使用也只能使用三角形来定义一个面(Face)，为了获取绘制的高性能，一般情况不会同时绘制面的前面和后面，只绘制面的“前面
		 * ”。虽然“前面”“后面”的定义可以应人而易，但一般为所有的“前面”定义统一的顶点顺序(顺时针或是逆时针方向）。
		 * 只绘制“前面”的过程称为”Culling”。
		 */
		/* 打开背面剪裁，打开忽略"后面"设置 */
		gl.glEnable(GL10.GL_CULL_FACE); // 19
		/* 明确指明"忽略"哪个面的代码如下 */
		// gl.glCullFace(GL10.GL_BACK);
		/* 着色模型为平滑着色 */
		gl.glShadeModel(GL10.GL_SMOOTH); // 20
		/*
		 * 启用深度检测
		 * 用来开启更新深度缓冲区的功能，也就是，如果通过比较后深度值发生变化了，会进行更新深度缓冲区的操作。启动它，OpenGL就可以跟踪再Z轴上的像素
		 * ，这样，它只会再那个像素前方没有东西时，才会绘画这个像素。
		 * 
		 * 在做绘画3D时，这个功能最好启动，视觉效果比较真实。
		 */
		gl.glEnable(GL10.GL_DEPTH_TEST); // 21
		// 纹理贴图
		// ???置为false后就不显示球
		// gl.glDepthMask(false);

		initGeometry(gl);
		initLighting(gl);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		float paleYellow[] = { 1.0f, 1.0f, 0.3f, 1.0f }; // 1
		float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float cyan[] = { 0.0f, 1.0f, 1.0f, 1.0f };
		float black[] = { 0.0f, 0.0f, 0.0f, 0.0f }; // 2

		// 轨道
		float orbitalIncrement = 1.25f; // 3
		float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glPushMatrix(); // 4

		gl.glTranslatef(-mEyePosition[X_VALUE], -mEyePosition[Y_VALUE], -mEyePosition[Z_VALUE]); // 5

		// 太阳是发光的，这边设置太阳的光源
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, BufferUtil.makeFloatBuffer(sunPos)); // 6
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(white));

		gl.glPushMatrix(); // 7
		// 让地球绕着太阳转
		angle += orbitalIncrement; // 8
		gl.glRotatef(angle, 0.0f, 1.0f, 0.0f); // 9
		executePlanet(mEarth, gl); // 10
		gl.glPopMatrix(); // 11

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, BufferUtil.makeFloatBuffer(paleYellow)); // 12
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(black)); // 13
		executePlanet(mSun, gl); // 14

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, BufferUtil.makeFloatBuffer(black)); // 15
		gl.glPopMatrix(); // 16
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height); // 12

		float aspectRatio;
		float zNear = .1f;
		float zFar = 1000;
		float fieldOfView = 3000.0f / 57.3f;
		float size;

		gl.glEnable(GL10.GL_NORMALIZE);
		aspectRatio = (float) width / (float) height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		size = zNear * (float) (Math.tan((double) (fieldOfView / 2.0f)));
		/*
		 * 后两个参数分别是近平面和远平面距离摄像机的距离
		 */
		gl.glFrustumf(size, -size, -size / aspectRatio, size / aspectRatio, zNear, zFar);
	}

	private void initGeometry(GL10 g0) {
		mEyePosition[X_VALUE] = 0.0f;
		mEyePosition[Y_VALUE] = 0.0f;
		mEyePosition[Z_VALUE] = 0.0f;

		mEarth = new Planet(50, 50, .3f, 1.0f);
		mEarth.setPosition(0.0f, 0.0f, -2.0f);

		mSun = new Planet(50, 50, 1.0f, 1.0f);
		mSun.setPosition(0.0f, 0.0f, 0.0f);
	}

	private void initLighting(GL10 gl) {
		float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] posFill1 = { -15.0f, 15.0f, 0.0f, 1.0f };
		float[] posFill2 = { -10.0f, -4.0f, 1.0f, 1.0f };
		float[] white = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] dimblue = { 0.0f, 0.0f, .2f, 1.0f };
		float[] cyan = { 0.0f, 1.0f, 1.0f, 1.0f };
		float[] yellow = { 1.0f, 1.0f, 0.0f, 1.0f };
		float[] magenta = { 1.0f, 0.0f, 1.0f, 1.0f };
		float[] dimmagenta = { .75f, 0.0f, .25f, 1.0f };
		float[] dimcyan = { 0.0f, .5f, .5f, 1.0f };

		// Lights go here
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, BufferUtil.makeFloatBuffer(sunPos));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(white));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(yellow));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, BufferUtil.makeFloatBuffer(posFill1));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(dimblue));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(dimcyan));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, BufferUtil.makeFloatBuffer(posFill2));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(dimmagenta));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(dimblue));

		// Materials go here.
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, BufferUtil.makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, BufferUtil.makeFloatBuffer(white));
		gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION, .001f);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(SS_SUNLIGHT);
		gl.glEnable(SS_FILLLIGHT1);
		gl.glEnable(SS_FILLLIGHT2);
	}

	private void executePlanet(Planet mPlanet, GL10 gl) {
		float posX, posY, posZ;
		posX = mPlanet.mPos[0]; // 17
		posY = mPlanet.mPos[1];
		posZ = mPlanet.mPos[2];

		gl.glPushMatrix();
		gl.glTranslatef(posX, posY, posZ); // 18
		mPlanet.draw(gl);
		gl.glPopMatrix();
	}
}
