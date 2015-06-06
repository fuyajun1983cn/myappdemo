package com.fyj.demo.es.testsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.SurfaceHolder;

/**
 * 
 * 
 * @author Yajun Fu
 */
public class RenderThread extends Thread {
	private static final Semaphore eglSemaphore = new Semaphore(1);
	private boolean isPaused = false;
	private boolean isContextLost = false;
	private boolean lostFocus = false;
	private boolean hasFocus = false;
	private boolean hasSurface = false;
	private int width = 0;
	private int height = 0;
	private GraphicsDevice graphicsDevice = null;
	private SurfaceHolder surfaceHolder = null;
	private ArrayList<Runnable> eventQueue = new ArrayList<Runnable>();
	private Context context = null;

	public RenderThread(Renderer renderer, Context ctx) {
		super();
		context = ctx;
		setName("RenderThread");
		graphicsDevice = GraphicsDevice.instance();
	}

	private void waitWhileSurfaceIsCreated() {
		try {
			while (!hasSurface) {
				synchronized (this) {
					wait(100);
				}
			}
		} catch (Exception ex) {
			TLogger.Error(ex.toString());
			ex.printStackTrace();
		}
	}

	private boolean handleEvents() {
		try {
			synchronized (this) {
				Runnable r;
				while ((r = getEvent()) != null) {
					r.run();
				}
				if (isPaused) {
					// GLBNativeFunctions.appSkipCurrentTest();
					lostFocus = true;
					return false;
				}
				if (needToWait()) {
					while (needToWait()) {
						wait();
					}
				}
			}
		} catch (InterruptedException e) {
			TLogger.Error(e.toString());
			e.printStackTrace();
		}

		return true;
	}

	private boolean updateAndRender(TestDescriptor currentTest,
			boolean isHighLevelTest) {
		/*
		 * if (GLBNativeFunctions.IsClearBufferNeeded()) {
		 * graphicsDevice.clear(GL10.GL_COLOR_BUFFER_BIT |
		 * GL10.GL_DEPTH_BUFFER_BIT); }
		 * 
		 * if (!GLBNativeFunctions.appAnimate()) { return false; } else {
		 * GLBNativeFunctions.appRender();
		 * 
		 * if (GLBNativeFunctions.IsSwapBufferNeeded())
		 * graphicsDevice.swapBuffers(); }
		 */
		return true;
	}

	private void drawLoadingScreen() {
		/*
		 * GLBNativeFunctions.appRenderLoadingScreen();
		 * graphicsDevice.swapBuffers();
		 * GLBNativeFunctions.appRenderLoadingScreen();
		 * graphicsDevice.swapBuffers();
		 */
	}

	private void drawRunningScreen() {
		/*
		 * GLBNativeFunctions.appRenderRunningScreen();
		 * graphicsDevice.swapBuffers();
		 * GLBNativeFunctions.appRenderRunningScreen();
		 * graphicsDevice.swapBuffers();
		 */
	}

	private boolean createGraphicsContext(TestDescriptor currentTest) {
		int selectedConfigId = -1;
		boolean isSurfaceCreated = false;

		LinkedList<Integer> configIdList = graphicsDevice
				.findValidEGLConfig(currentTest);
		Iterator<Integer> iterator = configIdList.iterator();

		while (!isSurfaceCreated && iterator.hasNext()) {
			selectedConfigId = iterator.next();

			if (selectedConfigId == -1) {
				graphicsDevice.clearActiveEGLConfig();

				return false;
			}

			EGLConfig currentEGLConfig = graphicsDevice
					.createEGLContext(selectedConfigId);
			waitWhileSurfaceIsCreated();
			isSurfaceCreated = graphicsDevice.createGLSurface(currentEGLConfig,
					surfaceHolder);
		}

		TLogger.Info("Selected EGLConfig Id: " + selectedConfigId);

		return isSurfaceCreated;
	}

	private void guardedRun() throws InterruptedException {
		/*
		 * graphicsDevice.init(); if
		 * (!createGraphicsContext(TestDescriptor.getStandardConfig())) {
		 * graphicsDevice.cleanUp();
		 * GLBNativeFunctions.reportEGLError(graphicsDevice.GetEGLError());
		 * return; }
		 * 
		 * GLBNativeFunctions.main(width, height,
		 * GLBenchmarkActivity.testsToRun);
		 * GLBNativeFunctions.createGLBEGLInfo();
		 * GlobalVariables.isTexturePreTestPassed = GLBNativeFunctions
		 * .isTexturePreTestPassed(); GLBenchmarkActivity.info = new GLBInfo();
		 * GLBenchmarkActivity.info.queryEGL(graphicsDevice.getEGl10(),
		 * graphicsDevice.getEglDisplay(), graphicsDevice.getActiveEGLConfig());
		 * 
		 * while (true) { graphicsDevice.cleanUp(); graphicsDevice.init();
		 * 
		 * TestDescriptor currentTest = GLBNativeFunctions.appSelectNextTest();
		 * 
		 * if (currentTest == null) { break; }
		 * 
		 * boolean isValidEGLConfigFound = createGraphicsContext(currentTest);
		 * if (isValidEGLConfigFound == false) { if (currentTest.getFsaa() != 0)
		 * GLBNativeFunctions
		 * .reportEGLError(GLBTestErrorType.GLB_TESTERROR_NOFSAA .getId()); else
		 * if (currentTest.isOffscreen()) GLBNativeFunctions
		 * .reportEGLError(GLBTestErrorType
		 * .GLB_TESTERROR_OFFSCREEN_NOT_SUPPORTED .getId()); else if
		 * (currentTest.getDepthBpp() > 16)
		 * GLBNativeFunctions.reportEGLError(12); else
		 * GLBNativeFunctions.reportEGLError(graphicsDevice .GetEGLError());
		 * 
		 * continue; }
		 * 
		 * if (hasSurface) { float brightness = currentTest.getBrightness();
		 * GLBenchmarkActivity.setBrightness((brightness) > 1.0f ? 1.0f :
		 * brightness); }
		 * 
		 * GLBNativeFunctions.appReset(); drawLoadingScreen();
		 * 
		 * Settings settings = new Settings(context); Point offscreenRes =
		 * settings.getOffscreenResolution();
		 * GLBNativeFunctions.SetCustomOffscreenViewport(offscreenRes.x,
		 * offscreenRes.y);
		 * 
		 * int errorCode = 0; if (lostFocus) {
		 * GLBNativeFunctions.appSkipCurrentTest();
		 * 
		 * } else { errorCode = GLBNativeFunctions.appInitCurrentTest(); }
		 * 
		 * if (errorCode == 1) { offscreenRes.x = GLBNativeFunctions
		 * .DefaultOffscreenViewportWidth(); offscreenRes.y = GLBNativeFunctions
		 * .DefaultOffscreenViewportHeight();
		 * 
		 * settings.setOffscreenResolution(offscreenRes);
		 * 
		 * drawRunningScreen();
		 * 
		 * boolean isHighLevelTest = GLBNativeFunctions
		 * .IsTestHighLevel(currentTest.getId());
		 * 
		 * while (true) { if (!handleEvents()) { break; }
		 * 
		 * boolean isRunning = updateAndRender(currentTest, isHighLevelTest);
		 * 
		 * if (!isRunning) { break; } if (lostFocus) {
		 * GLBNativeFunctions.appSkipCurrentTest(); break; } } }
		 * 
		 * GLBNativeFunctions.appFinishCurrentTest(graphicsDevice
		 * .getActiveEGLConfigId()); }
		 * 
		 * if (GlobalVariables.IsCorporate()) {
		 * GLBNativeFunctions.saveResultsAndShowOnLogcat(); }
		 * 
		 * graphicsDevice.cleanUp(); GLBNativeFunctions.mainEnd();
		 * 
		 * GLBenchmarkActivity.renderThreadEnded();
		 */
	}

	private boolean needToWait() {
		return (isPaused || (!hasFocus) || (!hasSurface) || isContextLost);
	}

	@Override
	public void run() {
		lostFocus = false;
		/*
		 * When the android framework launches a second instance of an activity,
		 * the new instance's onCreate() method may be called before the first
		 * instance returns from onDestroy().
		 * 
		 * This semaphore ensures that only one instance at a time accesses EGL.
		 */
		try {
			try {
				eglSemaphore.acquire();
			} catch (InterruptedException e) {
				return;
			}
			guardedRun();
		} catch (InterruptedException e) {
			// fall thru and exit normally
			TLogger.Exception(e);
		} finally {
			eglSemaphore.release();
		}
	}

	public void setSurfaceHolder(SurfaceHolder holder) {
		surfaceHolder = holder;
	}

	public void surfaceCreated() {
		TLogger.Info("RenderThread::surfaceCreated");
		synchronized (this) {
			hasSurface = true;
			isContextLost = false;
			notify();
		}
	}

	public void surfaceDestroyed() {
		TLogger.Info("surfaceDestroyed");
		synchronized (this) {
			hasSurface = false;
			notify();
		}
	}

	public void onPause() {
		TLogger.Info("onPause");
		synchronized (this) {
			isPaused = true;
		}
	}

	public void onResume() {
		synchronized (this) {
			isPaused = false;
			notify();
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		synchronized (this) {
			this.hasFocus = hasFocus;
			if (hasFocus == true) {
				notify();
			}
		}
	}

	public void onWindowResize(int _width, int _height) {
		/*
		 * synchronized (this) {
		 * 
		 * if (GlobalVariables.RENDER_HEIGHT != 0 &&
		 * GlobalVariables.RENDER_WIDTH != 0) { width =
		 * GlobalVariables.RENDER_WIDTH; height = GlobalVariables.RENDER_HEIGHT;
		 * } else { width = _width; height = _height; } }
		 */
	}

	public void requestExitAndWait() {
		synchronized (this) {
			lostFocus = false;
			notify();
		}
		try {
			join();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Queue an "event" to be run on the GL rendering thread.
	 * 
	 * @param r
	 *            the runnable to be run on the GL rendering thread.
	 */
	public void queueEvent(Runnable r) {
		synchronized (this) {
			eventQueue.add(r);
		}
	}

	private Runnable getEvent() {
		synchronized (this) {
			if (eventQueue.size() > 0) {
				TLogger.Error("Queue size: " + eventQueue.size());
				return eventQueue.remove(0);
			}
		}
		return null;
	}

}
