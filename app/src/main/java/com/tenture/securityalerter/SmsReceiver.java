package com.tenture.securityalerter;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SmsReceiver extends BroadcastReceiver {
    String safeNumber="+000000000000";
    AudioManager am;
    static MediaPlayer mp;
    private static final String TAG =
            SmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";
    private String lat,lon;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(intent.getStringExtra("safeNumber")!=null)
        {
            lat=intent.getStringExtra("lat");
            lon=intent.getStringExtra("lon");
            safeNumber=intent.getStringExtra("safeNumber");
            Log.d("chech","Safenumber(NS):" +safeNumber);
            Toast.makeText(context, safeNumber, Toast.LENGTH_SHORT).show();
        }
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] =
                            SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                //strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                //strMessage += " :" + msgs[i].getMessageBody() + "\n";
                String sender=msgs[i].getOriginatingAddress();
                Toast.makeText(context,"sender:"+ sender, Toast.LENGTH_LONG).show();
                Log.d("chech", "sender:"+sender);
                //Toast.makeText(context, "SN:"+SmsRadarService.safeNumber, Toast.LENGTH_SHORT).show();

                mp =  MediaPlayer.create(context, R.raw.st);
                mp.setLooping(true);
                if(sender.equals(SmsRadarService.safeNumber)){
                    soundsOn();
/////////////////////Put it right under me!

                    //code
                    mp.setOnCompletionListener(MediaPlayer::release);
                    mp.start();
                    Intent toAlarm=new Intent(context,AlarmScreen.class);
                    toAlarm.putExtra("lat", SmsRadarService.lat);
                    toAlarm.putExtra("lon", SmsRadarService.lon);
                    toAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toAlarm);
                }
//                mp.start();
//                Intent toAlarm=new Intent(context,AlarmScreen.class);
//                toAlarm.putExtra("lat", SmsRadarService.lat);
//                toAlarm.putExtra("lon", SmsRadarService.lon);
//                toAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(toAlarm);
                Log.d(TAG, "onReceive: " + strMessage);
                //Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void soundsOn() {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING) , AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) , AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
    public static void stopAlarm(){
        mp.stop();
    }

    private void showNotification(Object... args) {
        Intent intent1=new Intent(this,HelperActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("args", args);
        PendingIntent pi = PendingIntent.getActivity(, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(this,getString(R.string.CHANNEL_ID))
                .setTicker("Help Needed!")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Please Help!")
                .setContentText("Someone needs help near you!")
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1, notification);

    }

}
