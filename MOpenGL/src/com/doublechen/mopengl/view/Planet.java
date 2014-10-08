package com.doublechen.mopengl.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Planet {
	FloatBuffer mVertexData;
	FloatBuffer mNormalData;
	FloatBuffer mColorData;

	float mScale;
	float mSquash; // 扁的程度
	float mRadius; // glScalef()方法会消耗CPU，所以直接绘制的时候来定制大小
	int mStacks, mSlices;

	public Planet(int stacks, int slices, float radius, float squash) {
		this.mStacks = stacks; // 1
		this.mSlices = slices;
		this.mRadius = radius;
		this.mSquash = squash;

		init(mStacks, mSlices, radius, squash, "dummy");
	}

	private void init(int stacks, int slices, float radius, float squash, String textureFile) {
		float[] vertexData;
		float[] colorData; // 2
		float colorIncrement = 0f;

		float blue = 0f;
		float red = 1.0f;
		// int numVertices = 0;
		int vIndex = 0; // vertex index
		int cIndex = 0; // color index

		mScale = radius;
		mSquash = squash;

		// 从上到下的颜色渐变
		colorIncrement = 1.0f / (float) stacks; // 3

		{
			mStacks = stacks;
			mSlices = slices;
			// vertices
			vertexData = new float[3 * ((mSlices * 2 + 2) * mStacks)]; // 4
			// color data
			colorData = new float[(4 * (mSlices * 2 + 2) * mStacks)]; // 5

			// 命名的时候，phi代表latitude，theta代表longitude
			int phiIdx, thetaIdx;
			// latitude
			// 从下到上
			for (phiIdx = 0; phiIdx < mStacks; phiIdx++) { // 6
				// starts at -90 degrees (-1.57 radians) goes up to
				// +90 degrees (or +1.57 radians)
				// the first circle
				float phi0 = (float) Math.PI * ((float) (phiIdx + 0) * (1.0f / (float) (mStacks)) - 0.5f); // 7
				// second one
				float phi1 = (float) Math.PI * ((float) (phiIdx + 1) * (1.0f / (float) (mStacks)) - 0.5f); // 8

				// 预处理以减少CPU消耗
				float cosPhi0 = (float) Math.cos(phi0); // 9
				float sinPhi0 = (float) Math.sin(phi0);
				float cosPhi1 = (float) Math.cos(phi1);
				float sinPhi1 = (float) Math.sin(phi1);

				float cosTheta, sinTheta;

				// longitude
				// 0到360
				for (thetaIdx = 0; thetaIdx < mSlices; thetaIdx++) { // 10
					// increment along the longitude circle each 'slice'
					float theta = (float) (-2.0f * (float) Math.PI * ((float) thetaIdx) * (1.0 / (float) (mSlices - 1)));
					cosTheta = (float) Math.cos(theta);
					sinTheta = (float) Math.sin(theta);

					// we are generating a vertical pair of points, such
					// as the first point of stack 0 and the first point
					// of stack 1 above it. This is how TRANGLE_STRIPS work,
					// taking a set of 4 vertices and essentially drawing two
					// triangles
					// at a time. The first is v0-v1-v2 and the next is
					// v2-v1-v3. Etc.

					// get x-y-z for the first vertex of stack
					vertexData[vIndex + 0] = mScale * cosPhi0 * cosTheta; // 11
					vertexData[vIndex + 1] = mScale * (sinPhi0 * mSquash);
					vertexData[vIndex + 2] = mScale * (cosPhi0 * sinTheta);

					vertexData[vIndex + 3] = mScale * cosPhi1 * cosTheta;
					vertexData[vIndex + 4] = mScale * (sinPhi1 * mSquash);
					vertexData[vIndex + 5] = mScale * (cosPhi1 * sinTheta);

					colorData[cIndex + 0] = (float) red; // 12
					colorData[cIndex + 1] = (float) 0f;
					colorData[cIndex + 2] = (float) blue;
					colorData[cIndex + 4] = (float) red;
					colorData[cIndex + 5] = (float) 0f;
					colorData[cIndex + 6] = (float) blue;
					colorData[cIndex + 3] = (float) 1.0;
					colorData[cIndex + 7] = (float) 1.0;

					cIndex += 2 * 4; // 13
					vIndex += 2 * 3; // 14
				}

				blue += colorIncrement; // 15
				red -= colorIncrement;

				// create a degenerate triangle to connect stacks and maintain
				// winding order
				vertexData[vIndex + 0] = vertexData[vIndex + 3] = vertexData[vIndex - 3]; // 16
				vertexData[vIndex + 1] = vertexData[vIndex + 4] = vertexData[vIndex - 2];
				vertexData[vIndex + 2] = vertexData[vIndex + 5] = vertexData[vIndex - 1];
			}
		}
		mVertexData = makeFloatBuffer(vertexData); // 17
		mColorData = makeFloatBuffer(colorData);
	}

	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CW); // 1
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexData); // 2
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorData);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		// GL_POINTS打点
//		gl.glDrawArrays(GL10.GL_POINTS, 0, (mSlices + 1) * 2 * (mStacks - 1) + 2); // 3
		// GL_LINE_STRIP可以只显示骨架
//		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, (mSlices + 1) * 2 * (mStacks - 1) + 2); // 3
		// GL_TRIANGLE_STRIP可以理解为铺砖
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (mSlices + 1) * 2 * (mStacks - 1) + 2); // 3
	}
}
