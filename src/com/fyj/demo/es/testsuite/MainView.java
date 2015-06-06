package com.fyj.demo.es.testsuite;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder = null;
	private RenderThread renderThread = null;
	private Context context = null;

	public MainView(Context context) {
		super(context);

		this.context = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		setRenderer(new TRenderer(context));
	}

	public void setRenderer(Renderer renderer) {
		renderThread = new RenderThread(renderer, context);
		renderThread.setSurfaceHolder(surfaceHolder);
		renderThread.start();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		TLogger.Info("GLBSurfaceView::surfaceCreated");
		renderThread.surfaceCreated();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		TLogger.Info("surfaceDestroyed");
		renderThread.surfaceDestroyed();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		TLogger.Info("surfaceChanged");
		renderThread.onWindowResize(w, h);
	}

	/**
	 * Inform the view that the activity is paused.
	 */
	public void onPause() {
		renderThread.onPause();
	}

	/**
	 * Inform the view that the activity is resumed.
	 */
	public void onResume() {
		renderThread.onResume();
	}

	/**
	 * Inform the view that the window focus has changed.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		renderThread.onWindowFocusChanged(hasFocus);
	}

	/**
	 * Queue an "event" to be run on the GL rendering thread.
	 * 
	 * @param r
	 *            the runnable to be run on the GL rendering thread.
	 */
	public void queueEvent(Runnable r) {
		renderThread.queueEvent(r);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		renderThread.requestExitAndWait();
	}

}
