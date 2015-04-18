/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amazon.rvspeedtest;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"onHandleIntent called");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String downloadSpeed = testDownloadSpeed();
                sendSpeedToServer(downloadSpeed);
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Send download speed to server
    private void sendSpeedToServer(String downloadSpeed) {
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // Get the download speed
    private String testDownloadSpeed() {
        String downloadSpeed = null;
        String  url = "http://upload.wikimedia.org/wikipedia/commons/2/2d/Snake_River_%285mb%29.jpg";
        byte[] buf = new byte[1024];
        int n = 0;
        long BeforeTime = System.nanoTime();
        long TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
        Log.i(TAG, "Before test bytes :" + TotalRxBeforeTest);
        //  long TotalTxBeforeTest = TrafficStats.getTotalRxBytes();
        try {
            InputStream is = new URL(url).openStream();
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1) {
                n++;
            }
            Log.i(TAG, "Value of n " + n);
            long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
            Log.i(TAG, "After test bytes :" + TotalRxAfterTest);
            // long TotalTxAfterTest = TrafficStats.getTotalRxBytes();
            long AfterTime = System.nanoTime();

            double TimeDifference = AfterTime - BeforeTime;
            Log.i(TAG, "Time difference " + TimeDifference);

            double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
            //Convert into kb
            rxDiff /= 1024;
//            double txDiff = TotalTxAfterTest - TotalTxBeforeTest;

            if ((rxDiff != 0)) {
                double rxBPS = (rxDiff / (TimeDifference)) * Math.pow(10, 9); // total rx bytes per second.
                downloadSpeed = Double.toString(rxBPS);
//            double txBPS = (txDiff / (TimeDifference/1000)); // total tx bytes per second.
                Log.i(TAG, String.valueOf(rxBPS) + "KBps. Total rx = " + rxDiff);
//            testing[1] = String.valueOf(txBPS) + "bps. Total tx = " + txDiff;
            } else {
                Log.e(TAG, "Download speed is 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            Integer linkSpeed = wifiInfo.getLinkSpeed(); //measured using WifiInfo.LINK_SPEED_UNITS
            downloadSpeed = Integer.toString(linkSpeed);
            Log.i(TAG, "link speed : " + linkSpeed);
        }
        return downloadSpeed;
    }
}
