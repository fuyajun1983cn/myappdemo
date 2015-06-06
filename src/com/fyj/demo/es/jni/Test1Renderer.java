package com.fyj.demo.es.jni;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;

public class Test1Renderer implements Renderer {

	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		draw();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		resize(width, height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		init(GLImage.testImage1);
	}

	static {
		System.loadLibrary("texture_test");
	}

	private native void init(Bitmap bitmap);

	private native void resize(int w, int h);

	private native void draw();
}
