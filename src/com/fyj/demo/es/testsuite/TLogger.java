package com.fyj.demo.es.testsuite;


import android.util.Log;

/**
 * 
 * ERROR: 1000 WARNING: 900 VERBOSE: 800 ALL: 0
 * 
 */
public class TLogger {
	public final static void Error(String message) {
		if (Consts.VERBOSE <= Consts.logError) {
			Log.e("MyAppDemo(com.fyj.demo.es.jni)", message);
		}
	}

	public final static void Warning(String message) {
		if (Consts.VERBOSE <= Consts.logWarning) {
			Log.w("MyAppDemo(com.fyj.demo.es.jni)", message);
		}
	}

	public final static void Info(String message) {
		if (Consts.VERBOSE <= Consts.logVerbose) {
			Log.v("MyAppDemo(com.fyj.demo.es.jni)", message);
		}
	}

	public final static void Exception(Throwable t) {
		t.printStackTrace();
	}

	public final static void Exception(Exception ex) {
		ex.printStackTrace();
	}
}
