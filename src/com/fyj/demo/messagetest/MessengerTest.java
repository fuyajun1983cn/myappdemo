package com.fyj.demo.messagetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fyj.demo.R;

public class MessengerTest extends Activity {
	private Messenger mServiceMessenger;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			synchronized (MessengerTest.this) {
				mServiceMessenger = new Messenger(service);
				// MessengerTest.this.notifyAll();
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			mServiceMessenger = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hello);

		Button btn = (Button) findViewById(R.id.btn);

		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				(new TestThread()).doTest(1000);
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		this.unbindService(mConnection);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		this.bindService(
				new Intent(MessengerTest.this, MessengerService.class),
				mConnection, Context.BIND_AUTO_CREATE);

	}

	private class TestThread extends TestHandlerThread {
		private Handler mTestHandler;
		private Messenger mTestMessenger;

		public void go() {
			synchronized (MessengerTest.this) {
				mTestHandler = new Handler() {
					public void handleMessage(Message msg) {
						TestThread.this.handleMessage(msg);
					}
				};
				mTestMessenger = new Messenger(mTestHandler);
				TestThread.this.executeTest();
			}
		}

		public void executeTest() {
			Message msg = Message.obtain();
			msg.arg1 = 100;
			msg.arg2 = 1000;
			msg.replyTo = mTestMessenger;
			try {
				mServiceMessenger.send(msg);
			} catch (RemoteException e) {
			}
		}

		public void handleMessage(Message msg) {
			if (msg.arg1 != 100) {
				failure(new RuntimeException("Message.arg1 is not 100: "
						+ msg.arg1));
				return;
			}
			if (msg.arg2 != 1000) {
				failure(new RuntimeException("Message.arg2 is not 1000: "
						+ msg.arg2));
				return;
			}
			if (!mTestMessenger.equals(msg.replyTo)) {
				failure(new RuntimeException("Message.replyTo is not me: "
						+ msg.replyTo));
				return;
			}
			success();
		}
	};
}
