package com.uestc.xr.snnd;

//import java.util.UUID;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.content.BroadcastReceiver;
import android.content.Intent;
//import android.content.IntentFilter;
//import android.util.Log;

public class MainActivity extends Activity {
	
	Button set;
	Button exit;
	Button connect;
	TextView state;
	TGDevice  tgDevice;
	BluetoothAdapter btAdapter;
	final boolean rawEnabled = false;
	private static final int REQUEST_ENABLE_BT = 1;
//	private static final UUID localUUID = UUID.fromString("e58438b6-d09e-4fea-bf8d-f7d62bd67c2e");
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        state = (TextView)findViewById(R.id.main_tv_state);
        state.setText("");
        state.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();        
        if(btAdapter == null) {
        	state.append("Bluetooth is unconnect.\n");
    	}else {
    		state.append("Bluetooth connection is ok.\n");
    		tgDevice = new TGDevice(btAdapter, handler);
    		tgDevice.connect(true);
    	}
    	if(!btAdapter.isEnabled()) {
    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    	}
    }

    @Override
    protected void onDestroy() {
    	tgDevice.close();
    	super.onDestroy();
    }
    
    /**
     * Handles messages from TGDevice
     */
    @SuppressLint("HandlerLeak") private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	//state.append(msg.toString());
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:  //TGD的状态改变

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE://初始化TGD，没有连到头部设备
	                    break;
	                case TGDevice.STATE_CONNECTING://Attempting a connection to the headset		                	
	                	state.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED://可用的设备找到了，数据正在被接受
	                	state.append("Connected.\n");
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND://无法连接到设备
	                	state.append("Can't find\n");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED://找不到可用设备
	                	state.append("not paired\n");
	                	break;
	                case TGDevice.STATE_DISCONNECTED://丢失连接
	                	state.append("Disconnected mang\n");
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
            	state.append("PoorSignal: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_RAW_DATA:	//Raw EEG data  
            		//raw1 = msg.arg1;
            		//tv.append("Got raw: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_HEART_RATE://Heart rate data
            	state.append("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:  //Attention level data
            		//att = msg.arg1;
            	state.append("Attention: " + msg.arg1 + "\n");
            		//Log.v("HelloA", "Attention: " + att + "\n");
            	break;
            case TGDevice.MSG_MEDITATION:

            	break;
            case TGDevice.MSG_BLINK://Strength of detected blink
            	state.append("Blink: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_RAW_COUNT:
            		//tv.append("Raw Count: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:  //Multi-channel raw data
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            default:
            	break;
        }
        }
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void doStuff(View view) {
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(rawEnabled);   
    }
    
}
