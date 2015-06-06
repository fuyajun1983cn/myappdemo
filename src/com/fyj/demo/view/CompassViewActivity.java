package com.fyj.demo.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.fyj.demo.R;

public class CompassViewActivity extends Activity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.compassview);
		CompassView cv = (CompassView) this.findViewById(R.id.compassView);
		cv.setBearing(45);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		return super.dispatchKeyEvent(event);
	}

}
