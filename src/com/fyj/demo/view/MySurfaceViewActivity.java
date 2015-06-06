package com.fyj.demo.view;

import android.app.Activity;
import android.os.Bundle;

public class MySurfaceViewActivity extends Activity {

	private MySurfaceView view = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = new MySurfaceView(this);
		setContentView(view);
	}

}
