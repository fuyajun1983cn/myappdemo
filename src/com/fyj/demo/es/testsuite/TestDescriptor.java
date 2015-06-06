package com.fyj.demo.es.testsuite;


public class TestDescriptor {
	private int id = 0;

	private String name = null;
	private String type = null;
	private String title = null;
	private String description = null;
	private String unitOfMeasure = null;

	private int viewportWidth = 0;
	private int viewportHeight = 0;
	private int colorBpp = 0;
	private int depthBpp = 0;
	private int fsaa = 0;
	private int frameStepTime = 0;
	private int playTime = 0;
	boolean battery = false;
	float brightness = 0.0f;
	int fps_limit = -1;

	private boolean showFps = false;
	private boolean isOffscreen = false;
	private boolean isWarmup = false;
	private boolean isEndless = false;

	public TestDescriptor(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param title
	 * @param description
	 * @param unitOfMeasure
	 * @param showFps
	 * @param isOffscreen
	 * @param isWarmup
	 * @param isEndless
	 * @param viewportWidth
	 * @param viewportHeight
	 * @param colorBpp
	 * @param depthBpp
	 * @param fsaa
	 * @param frameStepTime
	 * @param playTime
	 */
	public TestDescriptor(int id, String name, String type, String title,
			String description, String unitOfMeasure, boolean showFps,
			boolean isOffscreen, boolean isWarmup, boolean isEndless,
			int viewportWidth, int viewportHeight, int colorBpp, int depthBpp,
			int fsaa, int frameStepTime, int playTime, boolean battery,
			float brightness, int fps_limit) {

		this.id = id;
		this.name = name;
		this.type = type;
		this.title = title;
		this.description = description;
		this.unitOfMeasure = unitOfMeasure;
		this.showFps = showFps;
		this.isOffscreen = isOffscreen;
		this.isWarmup = isWarmup;
		this.isEndless = isEndless;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		this.colorBpp = colorBpp;
		this.depthBpp = depthBpp;
		this.fsaa = fsaa;
		this.frameStepTime = frameStepTime;
		this.playTime = playTime;
		this.battery = battery;
		this.brightness = brightness;
		this.fps_limit = fps_limit;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public boolean isShowFps() {
		return showFps;
	}

	public boolean isOffscreen() {
		return isOffscreen;
	}

	public boolean isWarmup() {
		return isWarmup;
	}

	public boolean isEndless() {
		return isEndless;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public int getColorBpp() {
		return colorBpp;
	}

	public int getDepthBpp() {
		return depthBpp;
	}

	public int getFsaa() {
		return fsaa;
	}

	public int getFrameStepTime() {
		return frameStepTime;
	}

	public int getPlayTime() {
		return playTime;
	}

	public boolean getBatteryTest() {
		return battery;
	}

	public float getBrightness() {
		return brightness;
	}

	public int getFps_limit() {
		return fps_limit;
	}

	public static TestDescriptor getStandardConfig() {
		return new TestDescriptor(0, "", "", "", "", "", false, false, false,
				false, 480, 800, 16, 16, 0, 0, 0, false, 0.0f, -1);
	}
}
