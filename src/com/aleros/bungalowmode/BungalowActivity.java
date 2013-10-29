package com.aleros.bungalowmode;

import com.aleros.bungalowmode.BungalowModeService.LocalBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class BungalowActivity extends PreferenceActivity {
	Intent i;
	private boolean mBounded = false;
	private BungalowModeService mServer;
	 ServiceConnection mConnection = new ServiceConnection() {
		  
	  public void onServiceDisconnected(ComponentName name) {
	   mBounded = false;
	   mServer = null;
	  }
	  
	  public void onServiceConnected(ComponentName name, IBinder service) {
	   mBounded = true;
	   LocalBinder mLocalBinder = (LocalBinder)service;
	   mServer = mLocalBinder.getServerInstance();
	  }
	 };
	 @Override
	 protected void onStop() {
	  super.onStop();
	  if(mBounded) {
	   unbindService(mConnection); 
	   mBounded = false;
	  }
	 };
	 
	 @Override
	 protected void onStart() {
	  super.onStart();
	  	Intent mIntent = new Intent(this, BungalowModeService.class);
	    bindService(mIntent, mConnection, BIND_AUTO_CREATE);
	 }; 
	  	 
	@Override 
	 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		ServiceState st = new ServiceState();
		st.setIsManualSelection(true);
		ConnectivityManager tm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		st.setRoaming(true);
		Preference isOnline = (Preference)findPreference("online");
		Preference bungalowModeOn = (Preference)findPreference("bungalow");
		i = new Intent(this, BungalowModeService.class);
		startService(i);
		bungalowModeOn.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(BungalowActivity.this, BungalowModeService.class);
			    bindService(mIntent, mConnection, BIND_AUTO_CREATE);
			    Boolean val = (Boolean)newValue;
			    if(val)
			    	mServer.activeBungalowMode();
			    else
			    	mServer.deactivateBungalowMode();
				return true;
			}
		});
		isOnline.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
			
				return true;
			}
		});
		
		
		
	}
	
	
}
