package com.fyj.demo.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyj.demo.R;

public class SimpleMathServiceActivity extends Activity {

	private ISimpleMathService mService;
	private boolean bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_math_service_actitivy_main);

		inputa = (EditText) findViewById(R.id.a);
		inputb = (EditText) findViewById(R.id.b);
		output = (TextView) findViewById(R.id.result);

		addButton = (Button) findViewById(R.id.add);
		addButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					int result = mService.add(
							Integer.parseInt(inputa.getText().toString()),
							Integer.parseInt(inputb.getText().toString()));
					output.setText(String.valueOf(result));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		subtractButton = (Button) findViewById(R.id.subtract);
		subtractButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					int result = mService.subtract(
							Integer.parseInt(inputa.getText().toString()),
							Integer.parseInt(inputb.getText().toString()));
					output.setText(String.valueOf(result));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		echoButton = (Button) findViewById(R.id.echo);
		echoButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				output.setText("This is a Test");
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (bound) {
			this.unbindService(mConnection);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!bound) {
			this.bindService(new Intent(SimpleMathServiceActivity.this,
					SimpleMathService.class), mConnection,
					Context.BIND_AUTO_CREATE);
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// TODO Auto-generated method stub
			mService = ISimpleMathService.Stub.asInterface(service);
			Toast.makeText(SimpleMathServiceActivity.this,
					"connected to Service", Toast.LENGTH_SHORT).show();
			bound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			// TODO Auto-generated method stub
			mService = null;
			Toast.makeText(SimpleMathServiceActivity.this,
					"disconnected from Service", Toast.LENGTH_SHORT).show();
			bound = false;
		}

	};

	private EditText inputa;
	private EditText inputb;
	private TextView output;
	private Button addButton;
	private Button subtractButton;
	private Button echoButton;
}
