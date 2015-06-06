package com.fyj.demo.es.jni;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fyj.demo.R;

public class GLImage {

	public static Bitmap testImage1 = null;
	public static Bitmap testImage2 = null;
	public static Bitmap testImage3 = null;

	public static void loadImage(Context ctx) {
		testImage1 = BitmapFactory.decodeResource(ctx.getResources(),
				R.drawable.bg);
		/*
		 * final BitmapFactory.Options decodeOptions = new
		 * BitmapFactory.Options(); decodeOptions.inJustDecodeBounds = true;
		 * 
		 * File pFile = new File("/sdcard/test.png"); InputStream in = null; try
		 * { in = new FileInputStream(pFile); testImage2 =
		 * BitmapFactory.decodeStream(in, null, decodeOptions); } catch (final
		 * IOException e) { Log.e("GLImage",
		 * "Failed loading Bitmap in GLImage. File: " +
		 * e.getLocalizedMessage()); } finally { try { in.close(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */

		URL imageURL = null;
		try {
			imageURL = new URL("https://www.google.com/images/nav_logo107.png");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			conn = (HttpURLConnection) imageURL.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			testImage3 = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated
			e.printStackTrace();
		}

	}
}
