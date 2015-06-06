package com.fyj.demo;

import android.app.Application;
import android.util.Log;

public class MyAppDemoApplication extends Application {
	private static final String TAG = "MyAppDemoApplication";

	@Override
	public void onCreate() {
		Log.i(TAG, "My App Demo is running...");
	}
}
