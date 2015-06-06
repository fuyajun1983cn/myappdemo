package com.fyj.demo.es1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class TestRenderer1 implements Renderer {

	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT
				| GL10.GL_STENCIL_BUFFER_BIT);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, w, h);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		gl.glEnable(GL10.GL_STENCIL_TEST);
		gl.glEnable(GL10.GL_DEPTH_TEST);

	}

}
