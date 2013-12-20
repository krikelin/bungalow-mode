package com.aleros.bungalowmode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder; 
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class BungalowModeService extends Service {
	public void checkBungalowMode(Date _time) {
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		BungalowDatabase bd = new BungalowDatabase(BungalowModeService.this);
		SQLiteDatabase db = bd.getWritableDatabase();
		SharedPreferences prefs = (SharedPreferences)PreferenceManager.getDefaultSharedPreferences(this);
		
		try {		
			int timeOfDay = _time.getHours() * 60 + _time.getMinutes();
			Cursor t = db.query("periods", new String[] {"startTime", "endTime", "lockout", "silence"}, "? BETWEEN startTime AND endTime", new String[] { String.valueOf(timeOfDay)}, null, null, null);
			while(t.moveToNext()) { 
				boolean offline = !prefs.getBoolean("online", t.getInt(t.getColumnIndex("lockout")) == 1);
				@SuppressWarnings("unused")
				boolean silence = t.getInt(t.getColumnIndex("silence")) == 1;
				
				// Disable bungalow mode
				
					if(offline) {
						try {	
							audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
							setMobileDataEnabled(BungalowModeService.this, false);
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TO DO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// Create notification
				/*	NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
					NotificationCompat.Builder builder = new Builder(BungalowModeService.this);
					Intent i = new Intent(BungalowModeService.this, BungalowActivity.class);
					PendingIntent pi = PendingIntent.getActivity(BungalowModeService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(pi);
					builder.setPriority(1);
					builder.setContentTitle(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
					builder.setContentText(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
					builder.setContentInfo(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode));
					builder.setSmallIcon(com.aleros.bungalowmode.R.drawable.ic_status_bungalow);
					builder.setOngoing(true);*/
					NotificationCompat.Builder mBuilder =
					        new NotificationCompat.Builder(this)
					        .setSmallIcon(R.drawable.ic_status_bungalow)
					        .setContentTitle(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode))
					        .setContentText(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
					// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(this, BungalowActivity.class);

					// The stack builder object will contain an artificial back stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity leads out of
					// your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					// Adds the back stack for the Intent (but not the Intent itself)
					stackBuilder.addParentStack(BungalowActivity.class);
					// Adds the Intent that starts the Activity to the top of the stack
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent =
					        stackBuilder.getPendingIntent(
					            0,
					            PendingIntent.FLAG_UPDATE_CURRENT
					        );
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager =
					    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(11215, mBuilder.build());
				 
				
			}
			if(t.getCount() < 1) {
				
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.ic_status_normal)
				        .setContentTitle(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode))
					    .setContentText(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, BungalowActivity.class);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(BungalowActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(11215, mBuilder.build());
			
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.close();
		}
	}
	private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		   final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
		   final Class<?> conmanClass = Class.forName(conman.getClass().getName());
		   final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		   iConnectivityManagerField.setAccessible(true);
		   final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		   final Class<?> iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
		   final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		   setMobileDataEnabledMethod.setAccessible(true);

		   setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		}
	Handler mHandler;
	Runnable mRunnable;
	public class LocalBinder extends Binder {
	  public BungalowModeService getServerInstance() {
	   return BungalowModeService.this;
	  }
	}
	private IBinder mBinder = new LocalBinder();
	@SuppressWarnings("unused")
	private static final int PERIOD = 60 * 1000;
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//BungalowModeService.this.checkBungalowMode(new Date(2013,1,1, 9,15));
		SharedPreferences prefs = (SharedPreferences)PreferenceManager.getDefaultSharedPreferences(this);
		boolean bungalow = prefs.getBoolean("bungalow", false);
		
		setNotification(bungalow ? BungalowModeService.STATE_BUNGALOW_ON : BungalowModeService.STATE_BUNGALOW_IDLE);
	   
		
	}
	/**
	 * Activate bungalow mode. Data connection will be disabled if offline preference is active
	 */
	public void activeBungalowMode() {
		setNotification(BungalowModeService.STATE_BUNGALOW_ON);
	}
	
	/**
	 * Deactivate bungalow mode. Data traffic is turned on
	 */
	public void deactivateBungalowMode() {
		setNotification(BungalowModeService.STATE_BUNGALOW_IDLE);
	}
	public static final int STATE_BUNGALOW_IDLE = 0x0;
	public static final int STATE_BUNGALOW_ON = 0x1;
	
	public void setNotification(int type) {
		try {
			AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
			SharedPreferences prefs = (SharedPreferences)PreferenceManager.getDefaultSharedPreferences(this);
			NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
			NotificationCompat.Builder builder = new Builder(BungalowModeService.this);
			Intent i = new Intent(BungalowModeService.this, BungalowActivity.class);
			PendingIntent pi = PendingIntent.getActivity(BungalowModeService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			switch(type) {
			case STATE_BUNGALOW_ON:
			//	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				//audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				
		//		setMobileDataEnabled(BungalowModeService.this, prefs.getBoolean("offline", false));
				
				
				builder.setContentTitle(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
				builder.setContentText(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode_desc));
				builder.setContentInfo(getResources().getString(com.aleros.bungalowmode.R.string.bungalow_mode));
				builder.setSmallIcon(com.aleros.bungalowmode.R.drawable.ic_status_bungalow);
				break;
				 
			case STATE_BUNGALOW_IDLE:
			//	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
		//		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				
			//	setMobileDataEnabled(BungalowModeService.this, true);
				
				builder.setPriority(1);
				builder.setContentTitle("");
				builder.setContentText("");
				builder.setContentInfo("");
				
				builder.setSmallIcon(com.aleros.bungalowmode.R.drawable.ic_status_normal);
				
				break;
			}
			builder.setOngoing(true);
			nm.notify(126123, builder.build());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			/*	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//BungalowModeService.this.checkBungalowMode(new Date(2013,1,1, 9,15));
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

}
