package com.plugin.gcm;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

@SuppressLint("NewApi")
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	//private Intent putExtra;
	
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	public void onRegistered(Context context, String regId) {

		Log.v(TAG, "onRegistered: "+ regId);

		JSONObject json;

		try
		{
			json = new JSONObject().put("event", "registered");
			json.put("regid", regId);

			Log.v(TAG, "onRegistered: " + json.toString());

			// Send this JSON data to the JavaScript application above EVENT should be set to the msg type
			// In this case this is the registration ID
			PushPlugin.sendJavascript( json );

		}
		catch( JSONException e)
		{
			// No message to the user is sent, JSON failed
			Log.e(TAG, "onRegistered: JSON exception");
		}
	}

	@Override
	public void onUnregistered(Context context, String regId) {
		Log.d(TAG, "onUnregistered - regId: " + regId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d(TAG, "onMessage - context: " + context);
		// Extract the payload from the message
		Bundle extras = intent.getExtras();
		if (extras != null){
			Log.d(TAG, "extrast not null");
			// if we are in the foreground, just surface the payload, else post it to the statusbar
			if (PushPlugin.isInForeground()) {
				Log.d(TAG, "foreground");
				extras.putBoolean("foreground", true);
				PushPlugin.sendExtras(extras);
			}
			else {
				Log.d(TAG, "not foreground");
				extras.putBoolean("foreground", false);
				// Send a notification if there is a message
				if (extras.getString("message") != null && extras.getString("message").length() != 0) {
					Log.d(TAG, "not foreground and message not null");
					createNotification(context, extras);
				}
			}
		}
	}


	/*
	protected void onMessage(Context context, Intent intent) {
	    Log.d(TAG, "onMessage - context: " + context);

	    // Extract the payload from the message
	    Bundle extras = intent.getExtras();
	    if (extras != null)
	    {
	        boolean foreground = PushPlugin.isInForeground();

	        extras.putBoolean("foreground", foreground);
	        String message = extras.getString("message");
            String title = extras.getString("title");
            Notification notif = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis() );
            notif.flags = Notification.FLAG_AUTO_CANCEL;
            notif.defaults |= Notification.DEFAULT_SOUND;
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            
            Intent notificationIntent = new Intent(context, NotifActivity.class);
            //here you pass the information
            notificationIntent.putExtra ("role",extras.getString("role"));
            notificationIntent.putExtra ("category",extras.getString("category"));
            
            Log.d(TAG, "kategori di GCM intent "+extras.getString("category"));
            
            notificationIntent.putExtra ("i",extras.getString("i"));
            notificationIntent.putExtra ("k",extras.getString("k"));
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            notif.setLatestEventInfo(context, title, message, contentIntent);
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
            mNotificationManager.notify(1, notif);
	       
	        if (foreground){
	        	//PushPlugin.sendExtras(extras);
	        }
	    }
	  }
	  */
	public void createNotification(Context context, Bundle extras)
	{
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String appName = getAppName(this);

		Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra("pushBundle", extras);
		

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		int defaults = Notification.DEFAULT_ALL;

		if (extras.getString("defaults") != null) {
			try {
				defaults = Integer.parseInt(extras.getString("defaults"));
			} catch (NumberFormatException e) {}
		}
		
		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(context)
				.setDefaults(defaults)
				.setSmallIcon(context.getApplicationInfo().icon)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(extras.getString("title"))
				.setTicker(extras.getString("title"))
				.setContentIntent(contentIntent)
				.setAutoCancel(true);

		String message = extras.getString("message");
		if (message != null) {
			mBuilder.setContentText(message);
		} else {
			mBuilder.setContentText("<missing message content>");
		}

		String msgcnt = extras.getString("msgcnt");
		if (msgcnt != null) {
			mBuilder.setNumber(Integer.parseInt(msgcnt));
		}
		
		int notId = 0;
		
		try {
			notId = Integer.parseInt(extras.getString("notId"));
		}
		catch(NumberFormatException e) {
			Log.e(TAG, "Number format exception - Error parsing Notification ID: " + e.getMessage());
		}
		catch(Exception e) {
			Log.e(TAG, "Number format exception - Error parsing Notification ID" + e.getMessage());
		}
		
		mNotificationManager.notify((String) appName, notId, mBuilder.build());
	}
	
	private static String getAppName(Context context)
	{
		CharSequence appName = 
				context
					.getPackageManager()
					.getApplicationLabel(context.getApplicationInfo());
		
		return (String)appName;
	}
	
	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "onError - errorId: " + errorId);
	}

}
