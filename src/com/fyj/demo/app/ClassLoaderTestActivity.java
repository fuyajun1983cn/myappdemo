package com.fyj.demo.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fyj.demo.R;

public class ClassLoaderTestActivity extends Activity {

	private EditText log = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.classloadertest);
		log = (EditText) findViewById(R.id.editText1);

	}

	public void testClass(View view) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();// 该Activity由其他Activity启动，所有它只能加载包内的类。
		Class c = null;
		try {
			c = loader
					.loadClass("com.google.android.media.effect.effects.FaceTrackingEffect");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.append(e.getLocalizedMessage());
		}
		/*
		 * if (c == null) { Log.e("ClassLoaderTestActivity",
		 * "Cannot Load Class"); log.append("Failed to load class\n"); try {
		 * Field f = c.getField("EFFECT_FACE_TANNING ");
		 * log.setText(f.getName()); } catch (NoSuchFieldException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		// } else {
		if (c != null)
			log.setText("Success!");
		// }
	}

}
