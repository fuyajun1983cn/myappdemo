package com.fyj.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class SimpleMathService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private final ISimpleMathService.Stub mBinder = new ISimpleMathService.Stub() {

		public int subtract(int a, int b) throws RemoteException {
			// TODO Auto-generated method stub
			return a - b;
		}

		public int nextPrime(int integer) throws RemoteException {
			// TODO Auto-generated method stub
			int start = integer;
			while (start % 2 == 0)
				start++;
			return start;
		}

		public String echo(String msg) throws RemoteException {
			// TODO Auto-generated method stub
			return msg;
		}

		public int add(int a, int b) throws RemoteException {
			// TODO Auto-generated method stub
			return a + b;
		}
	};

}
