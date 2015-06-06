package com.fyj.demo.es.jni;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class Test1Activity extends Activity {
	private GLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/*
		 * new Thread(new Runnable() {
		 * 
		 * public void run() { // TODO Auto-generated method stub
		 * GLImage.loadImage(Test1Activity.this); }
		 * 
		 * }).start();
		 */
		/*
		 * StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		 * .detectDiskReads().detectDiskWrites().detectNetwork() // or //
		 * .detectAll() // for // all // detectable // problems
		 * .penaltyLog().build()); StrictMode.setVmPolicy(new
		 * StrictMode.VmPolicy.Builder()
		 * .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		 * .penaltyLog().penaltyDeath().build());
		 */

		GLImage.loadImage(this);

		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setRenderer(new Test1Renderer());

		setContentView(mGLSurfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}
}
