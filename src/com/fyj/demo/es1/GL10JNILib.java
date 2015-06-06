package com.fyj.demo.es1;

public class GL10JNILib {

	static {
		System.loadLibrary("threedfonttest");
	}

	public static native void init();

	public static native void resize();

	public static native void draw();
}
