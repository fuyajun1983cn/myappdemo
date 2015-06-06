package com.fyj.demo.es1;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class TestActivity1 extends Activity {
	private GLSurfaceView mainView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mainView = new GLSurfaceView(this);
		mainView.setRenderer(new TestRenderer1());
		setContentView(mainView);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mainView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mainView.onResume();
	}
}
