package com.tenture.securityalerter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.adorsys.android.smsparser.SmsConfig;

public class SmsRadarService extends Service {
    Context context;
    static String safeNumber,lat,lon;
    Intent intent;
    @NonNull
    private LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver SmsReceiver;
    private static boolean isRunning=false;
    NotificationChannel chan;
    NotificationManagerCompat notificationManager;
    String NOTIFICATION_CHANNEL_ID = "com.tenture.securityAlerter";
    NotificationChannel intruderChannel;
    String channelName;
    private String intruderChannelName;


    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intruderChannelName="Intruder Channel";
            intruderChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID+".intruderChan",intruderChannelName , NotificationManager.IMPORTANCE_HIGH);
            channelName = "Background Service Teller";
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager =  NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(chan);
            notificationManager.createNotificationChannel(intruderChannel);
        }






    }

    private void startMyOwnForeground(){


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                //.setSmallIcon(R.drawable.icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1337, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //this.intent=intent;
        isRunning = true;
        safeNumber=intent.getStringExtra("safeNumber");
        lat=intent.getStringExtra("lat");
        lon=intent.getStringExtra("lon");
        Log.d("chech", lat);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1337, notification);

        // get an instance of the receiver in your service



        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        // filter.addAction("anotherAction");
        SmsReceiver = new SmsReceiver();
        registerReceiver(SmsReceiver, filter);

        registerReceiver(intruderBroadcastReceiver, new IntentFilter("Intruder"));


        Toast.makeText(getApplicationContext(), "Service started!", Toast.LENGTH_SHORT).show();
        context=getApplicationContext();
        Intent broadcastedIntent=new Intent(this, SmsReceiver.class);
        broadcastedIntent.putExtra("safeNumber",safeNumber);
        broadcastedIntent.putExtra("lat", lat);
        broadcastedIntent.putExtra("lon", lat);
        sendBroadcast(broadcastedIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    BroadcastReceiver intruderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Intruder Alert Here!
            showNotification();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(SmsReceiver);
        unregisterReceiver(intruderBroadcastReceiver);
        isRunning = false;
        super.onDestroy();
    }
    private void showNotification() {
        Log.d("chech", "Showing Notification!");
        Intent intent1=new Intent(this,AlarmScreen.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setTicker("Intruder Alert!!")
//                .setOngoing(true)
//                .setSmallIcon(android.R.drawable.ic_menu_report_image)
//                .setContentTitle("Intruder Alert")
//                .setContentText("Your office is under attack!")
////                .setContentIntent(pi)
//                .setChannelId(intruderChannelName)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
////                .setAutoCancel(true)
////                .build();
////        notification.flags|= Notification.FLAG_NO_CLEAR;
//
//        assert notificationManager != null;
//        notificationManager.notify(29345, builder.build());


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setTicker("Intruder Alert!!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Intruder Alert")
                .setContentText("Your office is under attack!")
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
        notificationManager.notify(101, builder.build());
        Log.d("chech", "Showed notification");
    }
    public static boolean isRunning() {
        return isRunning;
    }
}
