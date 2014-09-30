package com.doublechen.mopengl.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
	// (x, y)
	private float vertices[] = { // 2
			-1.0f, -1.0f, // left bottom
			1.0f, -1.0f,  // right bottom
			-1.0f, 1.0f,  // left top
			1.0f, 1.0f    // right top
	};

	byte maxColor = (byte) 255;

	// R G B A
	byte colors[] = { // 3
			maxColor, maxColor, 0, maxColor,
			0, maxColor, maxColor, maxColor,
			0, 0, 0, maxColor,
			maxColor, 0, maxColor, maxColor
	};

	private byte[] indices = { 0, 3, 1, 0, 2, 3 }; // 4

	private FloatBuffer mFVertexBuffer;
	private ByteBuffer mColorBuffer;
	private ByteBuffer mIndexBuffer;

	public Square() {
		// 注意allocate和allocateDirect
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4); // 5
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();
		mFVertexBuffer.put(vertices);
		mFVertexBuffer.position(0);

		mColorBuffer = ByteBuffer.allocateDirect(colors.length);
		mColorBuffer.put(colors);
		mColorBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl) { // 6
		// clockwise
		// 绘制图形的方向
		gl.glFrontFace(GL10.GL_CW); // 7

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mFVertexBuffer); // 8
		gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer); // 9
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer); // 10

		// counterclockwise, default
		gl.glFrontFace(GL10.GL_CCW); // 11
	}
}
