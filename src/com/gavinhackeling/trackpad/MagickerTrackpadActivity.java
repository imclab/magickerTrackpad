package com.gavinhackeling.trackpad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MagickerTrackpadActivity extends Activity implements OnClickListener, OnTouchListener {
	// For logging
	private static final String TAG = "TRACKPAD";
	private static final boolean D = true;
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	// Touch event data
	private int x = 0;
	private int y = 0;
	private String actionCode = "Z";
	// Gesture detection
	private GestureDetector mGestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	// BT services
	public String test;
	private SocketServices mSocketServices;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket mBluetoothSocket = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String mAddress = "00:02:72:22:33:69";
	private byte[] mMessageOutBuffer = null;
	// Clipboard list
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> values = 
			new ArrayList<String>(Arrays.asList("gavinhackeling@gmail.com"));
	private ListView listView1;

	private int currentButton = -1;

	// OnTouch listeners/handlers for mouse buttons
	private OnTouchListener onLeftButtonTouch = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			actionCode = "1";
			sendBTMessage(actionCode);
			return false;
		}
	};
	private OnTouchListener onMiddleButtonTouch = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			actionCode = "2";
			sendBTMessage(actionCode);
			return false;
		}
	};
	private OnTouchListener onRightButtonTouch = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			actionCode = "3";
			sendBTMessage(actionCode);
			return false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Create a gesture detector object to be called from to touch event handler
		mGestureDetector = new GestureDetector(new MyGestureDetector());	
		//imgBtn row
		final ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		final ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		final ImageButton imageButton3 = (ImageButton) findViewById(R.id.imageButton3);
		final ImageButton imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
		final ImageButton imageButton5 = (ImageButton) findViewById(R.id.imageButton5);
		final ImageButton imageButton6 = (ImageButton) findViewById(R.id.imageButton6);
		final ImageButton imageButton7 = (ImageButton) findViewById(R.id.imageButton7);
		final ImageButton imageButton8 = (ImageButton) findViewById(R.id.imageButton8);
		final ImageButton imageButton9 = (ImageButton) findViewById(R.id.imageButton9);
		final ImageButton imageButton10 = (ImageButton) findViewById(R.id.imageButton10);
		final ImageButton imageButton11 = (ImageButton) findViewById(R.id.imageButton11);
		//second imgBtn row
		final ImageButton imageButton22 = (ImageButton) findViewById(R.id.imageButton11);
		final ImageButton imageButton12 = (ImageButton) findViewById(R.id.imageButton12);
		final ImageButton imageButton13 = (ImageButton) findViewById(R.id.imageButton13);
		final ImageButton imageButton14 = (ImageButton) findViewById(R.id.imageButton14);
		final ImageButton imageButton15 = (ImageButton) findViewById(R.id.imageButton15);
		final ImageButton imageButton16 = (ImageButton) findViewById(R.id.imageButton16);
		final ImageButton imageButton17 = (ImageButton) findViewById(R.id.imageButton17);
		final ImageButton imageButton18 = (ImageButton) findViewById(R.id.imageButton18);
		final ImageButton imageButton19 = (ImageButton) findViewById(R.id.imageButton19);
		final ImageButton imageButton20 = (ImageButton) findViewById(R.id.imageButton20);
		final ImageButton imageButton21 = (ImageButton) findViewById(R.id.imageButton21);
		imageButton1.setOnClickListener(this);
		imageButton2.setOnClickListener(this);
		imageButton3.setOnClickListener(this);
		imageButton4.setOnClickListener(this);
		imageButton5.setOnClickListener(this);
		imageButton6.setOnClickListener(this);
		imageButton7.setOnClickListener(this);
		imageButton8.setOnClickListener(this);
		imageButton9.setOnClickListener(this);
		imageButton10.setOnClickListener(this);
		imageButton11.setOnClickListener(this);
		imageButton22.setOnClickListener(this);
		imageButton12.setOnClickListener(this);
		imageButton13.setOnClickListener(this);
		imageButton14.setOnClickListener(this);
		imageButton15.setOnClickListener(this);
		imageButton16.setOnClickListener(this);
		imageButton17.setOnClickListener(this);
		imageButton18.setOnClickListener(this);
		imageButton19.setOnClickListener(this);
		imageButton20.setOnClickListener(this);
		imageButton21.setOnClickListener(this);
		//trackpad column
		final ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
		final ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
		final ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
		final ImageView imageView4 = (ImageView) findViewById(R.id.imageView4);
		imageView1.setOnClickListener(MagickerTrackpadActivity.this);
		imageView1.setOnTouchListener(this);
		imageView2.setOnTouchListener(this);
		imageView3.setOnTouchListener(this);
		imageView4.setOnTouchListener(this);

		//clipboard list
		listView1 = (ListView) findViewById(R.id.listView1);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
		listView1.setAdapter(mAdapter);
		listView1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {			
				String text = (String) ((TextView) view).getText();			
				sendBTToClipboard("4", text);			
				//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, 
					"Bluetooth is not available.", 
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, 
					"Please enable your BT and re-run this program.", 
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (D)
			Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");

		mSocketServices = new SocketServices();
		mSocketServices.execute("junk string");
	}

	// touch event handler. get x,y position of event and pass to gesture detector
	public boolean onTouch(View v, MotionEvent e) {
		if(v.getId() == R.id.imageView1 && e.getActionMasked() == MotionEvent.ACTION_UP){
			actionCode = "Y";
			sendBTMessage(actionCode);
			return true;
		} else if(v.getId() == R.id.imageView1){
			currentButton = 1;
			x = (int)e.getX();		
			y = (int)e.getY();
			Log.d(TAG, String.valueOf(x));
			Log.d(TAG, String.valueOf(y));
			sendBTMessage("X");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				Log.e("onTouch", "unable to sleep thread");
			}
		} else if(v.getId() == R.id.imageView2){
			currentButton = 2;			
		} else if(v.getId() == R.id.imageView3){
			currentButton = 3;			
		} else if(v.getId() == R.id.imageView4){
			currentButton = 4;			
		}
		if (mGestureDetector.onTouchEvent(e)) {
			return true;
		}
		return false;
	}

	// override common methods
	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (D) {
			Log.e(TAG, "+ ON RESUME +");
			Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
		}
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
		try {
			mBluetoothSocket = device.createRfcommSocketToServiceRecord(mUUID);
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Socket creation failed.", e);
		}
		mBluetoothAdapter.cancelDiscovery();
		try {
			mBluetoothSocket.connect();
			Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
		} catch (IOException e) {
			try {
				mBluetoothSocket.close();
			} catch (IOException e2) {
				Log.e(TAG, 
						"ON RESUME: Unable to close socket during connection failure", e2);
			}
		}
		try {
			mOutputStream = mBluetoothSocket.getOutputStream();
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		if (mOutputStream != null) {
			try {
				mOutputStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
			}
		}
		try	{
			mBluetoothSocket.close();
		} catch (IOException e2) {
			Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
		mSocketServices.cancel(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
		mSocketServices.cancel(true);
	}

	// onclick listener
	public void onClick(View v) {
		switch(v.getId()){	
		//imgBtn column
		case R.id.imageButton1:
			Toast.makeText(getBaseContext(), "b1", Toast.LENGTH_SHORT).show();
			actionCode = "A";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton2:
			Toast.makeText(getBaseContext(), "b2", Toast.LENGTH_SHORT).show();
			actionCode = "B";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton3:
			Toast.makeText(getBaseContext(), "b3", Toast.LENGTH_SHORT).show();
			actionCode = "C";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton4:
			Toast.makeText(getBaseContext(), "b4", Toast.LENGTH_SHORT).show();
			actionCode = "D";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton5:
			Toast.makeText(getBaseContext(), "b5", Toast.LENGTH_SHORT).show();
			actionCode = "E";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton6:
			Toast.makeText(getBaseContext(), "b6", Toast.LENGTH_SHORT).show();	
			actionCode = "F";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton7:
			Toast.makeText(getBaseContext(), "b2", Toast.LENGTH_SHORT).show();
			actionCode = "G";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton8:
			Toast.makeText(getBaseContext(), "b3", Toast.LENGTH_SHORT).show();	
			actionCode = "H";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton9:
			Toast.makeText(getBaseContext(), "b4", Toast.LENGTH_SHORT).show();
			actionCode = "I";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton10:
			Toast.makeText(getBaseContext(), "b5", Toast.LENGTH_SHORT).show();
			actionCode = "J";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton11:
			Toast.makeText(getBaseContext(), "b6", Toast.LENGTH_SHORT).show();
			actionCode = "K";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton12:
			Toast.makeText(getBaseContext(), "b2", Toast.LENGTH_SHORT).show();
			actionCode = "a";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton13:
			Toast.makeText(getBaseContext(), "b3", Toast.LENGTH_SHORT).show();
			actionCode = "b";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton14:
			Toast.makeText(getBaseContext(), "b4", Toast.LENGTH_SHORT).show();
			actionCode = "c";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton15:
			Toast.makeText(getBaseContext(), "b5", Toast.LENGTH_SHORT).show();
			actionCode = "d";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton16:
			Toast.makeText(getBaseContext(), "b6", Toast.LENGTH_SHORT).show();	
			actionCode = "e";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton17:
			Toast.makeText(getBaseContext(), "b2", Toast.LENGTH_SHORT).show();
			actionCode = "f";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton18:
			Toast.makeText(getBaseContext(), "b3", Toast.LENGTH_SHORT).show();	
			actionCode = "g";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton19:
			Toast.makeText(getBaseContext(), "b4", Toast.LENGTH_SHORT).show();
			actionCode = "h";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton20:
			Toast.makeText(getBaseContext(), "b5", Toast.LENGTH_SHORT).show();
			actionCode = "i";
			sendBTMessage(actionCode);
			break;
		case R.id.imageButton21:
			Toast.makeText(getBaseContext(), "b6", Toast.LENGTH_SHORT).show();
			actionCode = "j";
			sendBTMessage(actionCode);
			break;	
		case R.id.imageButton22:
			Toast.makeText(getBaseContext(), "b1", Toast.LENGTH_SHORT).show();
			actionCode = "k";
			sendBTMessage(actionCode);
			break;	
		}
	}

	// send messages
	public void sendBTMessage(String _message){
		String packet = "0" + "," + _message + "," + x + "," + y + "&";
		Log.d("sendBTMessage", packet);
		try {
			mOutputStream.flush();
		} catch (IOException e1) {
			Log.e("SendBTMessage", "Cannot flush outputstream");
		}
		mMessageOutBuffer = null;
		mMessageOutBuffer = packet.getBytes();
		try {
			mOutputStream.write(mMessageOutBuffer);
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Exception during write.", e);
		}
		//		try {
		//			mOutputStream.flush();
		//		} catch (IOException e) {
		//			Log.e("Output stream", "flush failed");
		//		}
	}

	// send messages
	public void sendBTToClipboard(String _message, String _text){
		String packet = "0" + "," + _message + "," + _text + "," + "0" + "&";
		Log.d("sendBTToClipboard", packet);
		mMessageOutBuffer = packet.getBytes();
		try {
			mOutputStream.write(mMessageOutBuffer);
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Exception during write.", e);
		}
		//		try {
		//			mOutputStream.flush();
		//		} catch (IOException e) {
		//			Log.e("Output stream", "flush failed");
		//		}
	}


	// create a new thread to read the bt socket
	private class SocketServices extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {			
			Timer mTimer = new Timer();		
			mTimer.schedule(new TimerTask(){
				@Override
				public void run(){
					TimerMethod();				
				}
				private void TimerMethod() {
					byte[] buffer = new byte[1024];
					String s;

					try{
						mInputStream = mBluetoothSocket.getInputStream();
					} catch (IOException e) {
						Log.e(TAG, "error in getting input stream");
					}					
					try {
						while(mInputStream.available() > 0){						
							mInputStream.read(buffer, 0, mInputStream.available());
							s = new String(buffer);
							s = s.trim();
							publishProgress(s);
							Log.d("values", values.toString());							
						}
					} catch (IOException e) {
						Log.e(TAG, "error in reading input stream");
					}
				}
			}, 0, 200);		
			//return str;
			return null;
		}
		@Override
		protected void onProgressUpdate(String... _s) {
			super.onProgressUpdate(_s);
			Log.d("onProgressUpdate", _s[0]);
			mAdapter.add(_s[0]);
			mAdapter.notifyDataSetChanged();
		}
		@Override
		protected void onPostExecute(String _s){
			super.onPostExecute(_s);
			Log.d("onPostExecute", "running");
		}	
		@Override
		protected void onCancelled(){
			super.onCancelled();
			Log.i(TAG, "socket services thread cancelled");
		}
	}

	// extend the gesture detector object
	private class MyGestureDetector extends SimpleOnGestureListener {	
		@Override
		public boolean onDown(MotionEvent ev) {
			Log.d("onSingleTapUp",ev.toString());
			switch(currentButton){
			case 1:
				actionCode = "W";
				break;
			case 2:
				actionCode = "W2";
				break;
			case 3:
				actionCode = "W3";
				break;
			case 4:
				actionCode = "W4";
				break;
			}
			sendBTMessage(actionCode);
			return true;
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent ev) {
			Log.d("onSingleTapUp",ev.toString());

			if(currentButton == 1){
				actionCode = "Q1";
				sendBTMessage(actionCode);
				return true;
			} else if(currentButton == 2){
				actionCode = "Q2";
				sendBTMessage(actionCode);
				return true;
			} else if(currentButton == 3){
				actionCode = "Q3";
				sendBTMessage(actionCode);
				return true;
			} else if(currentButton == 4){
				actionCode = "Q4";
				sendBTMessage(actionCode);
				return true;
			}
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent ev) {
			Log.d("onDoubleTapUp",ev.toString());
			actionCode = "R";
			sendBTMessage(actionCode);
			return true;
		}
		// TODO, next need to get all pointer ids
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH && Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe, else if: left to right swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MagickerTrackpadActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
					Log.d("swipe", "left");

					actionCode = "S";
					sendBTMessage(actionCode);
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MagickerTrackpadActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
					Log.d("swipe", "right");
					actionCode = "T";
					sendBTMessage(actionCode);
				}
				// down to up swipe, else if: up to down swipe
				if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MagickerTrackpadActivity.this, "Up Swipe", Toast.LENGTH_SHORT).show();
					Log.d("swipe", "up");
					actionCode = "U";
					sendBTMessage(actionCode);
				} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					Toast.makeText(MagickerTrackpadActivity.this, "Down Swipe", Toast.LENGTH_SHORT).show();
					Log.d("swipe", "down");
					actionCode = "V";
					sendBTMessage(actionCode);
				}
			} catch (Exception e) {
			}
			return false;
		}
	}
}




