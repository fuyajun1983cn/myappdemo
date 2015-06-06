package com.fyj.demo.messagetest;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessengerService extends Service {
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Message reply = Message.obtain();
			reply.copyFrom(msg);
			try {
				System.out.println("receive handler call from remote...");
				System.out.println("ARG1: " + msg.arg1);
				System.out.println("ARG2: " + msg.arg2);
				msg.replyTo.send(reply);
			} catch (RemoteException e) {
			}
		}
	};

	private final Messenger mMessenger = new Messenger(mHandler);

	public MessengerService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
}
