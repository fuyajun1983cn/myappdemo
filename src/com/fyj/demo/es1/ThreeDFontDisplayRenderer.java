package com.fyj.demo.es1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class ThreeDFontDisplayRenderer implements Renderer {

	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		GL10JNILib.draw();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GL10JNILib.resize();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GL10JNILib.init();
	}

}
