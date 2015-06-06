package com.fyj.demo.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.fyj.demo.R;

public class MyPreferencesActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
