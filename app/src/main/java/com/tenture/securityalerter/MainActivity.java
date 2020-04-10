package com.tenture.securityalerter;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import de.adorsys.android.smsparser.SmsTool;



import com.mikepenz.materialdrawer.DrawerBuilder;


public class MainActivity extends AppCompatActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "com.tenture.securityAlerter";
    androidx.appcompat.widget.Toolbar toolbar;
    Context context;
    String safeNumber="+000000000000",lat,lon;
    TextView safe;
    Button inOut;
    Intent intent;
    RelativeLayout layout;
    View.OnClickListener inListener,outListener;
    String DE_ACT_STRING,ACT_STRING;
    private String intruderChannelName;
    private NotificationChannel intruderChannel;
    private String channelName;
    private NotificationChannel chan;
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent settings=new Intent(MainActivity.this,AppSettings.class);
                settings.putExtra("safeNumber", safeNumber);
                settings.putExtra("lat", lat);
                settings.putExtra("lon", lon);
                startActivity(settings);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
//      }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        
        
        
        
        
        
        
        
        
        
        
        
        
        


        DE_ACT_STRING=getResources().getString(R.string.de_act_sys);
        ACT_STRING=getResources().getString(R.string.act_sys);
        //toolbar = new Toolbar(getApplicationContext());
//        toolbar=findViewById(R.id.toolbar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch(item.getItemId()){
//                    case R.id.action_settings:
//                        Intent intent=new Intent(MainActivity.this,appSettings.class);
//
//                        break;
//                }
//                return false;
//            }
//        });
//        setSupportActionBar(toolbar);
       // ActionBar actionBar=getSupportActionBar();
        //actionBar






//Creates a NavDrawer
        new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        //pass your items here
                )
                .build();
//sets Background Color
        layout=findViewById(R.id.layout);

        layout.setBackgroundColor(getResources().getColor(R.color.appBackground));

//other shit
        intent=new Intent(MainActivity.this,SmsRadarService.class);
        initializeListeners();
        safeNumber=getIntent().getStringExtra("number");
        lat=getIntent().getStringExtra("lat");
        lon=getIntent().getStringExtra("lon");
        safe=findViewById(R.id.safe);
        inOut=findViewById(R.id.inOut);
        context=MainActivity.this;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsTool.requestSMSPermission(MainActivity.this);
        }

        if(SmsRadarService.isRunning()){
            safe.setText("Listening from : "+safeNumber );
            inOut.setOnClickListener(inListener);
            inOut.setText(DE_ACT_STRING);
        }else{
            safe.setText("Currently idle");
            inOut.setOnClickListener(outListener);
        }




    }


    private void initializeListeners() {
        inListener= view -> {
            Button button=(Button) view;
            button.setText(ACT_STRING);
            stop(intent);
//            showNotification();
            button.setOnClickListener(outListener);

        };
        outListener= view -> {
            Button button=(Button) view;
            button.setText(DE_ACT_STRING);
            start(intent);
//            showNotification();
            button.setOnClickListener(inListener);
        };
    }

    private void stop(Intent intent) {
        stopService(intent);
        safe.setText("Currently idle");
    }

    private void start(Intent intent) {

        safe.setText("Listening from : "+safeNumber );
        intent.putExtra("safeNumber", safeNumber);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        startService(intent);
    }
//    private void showNotification() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = ("a");
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
//            notificationManager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
////                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//        notificationManager.notify(101, builder.build());
//        Log.d("chech", "Showed notification");
//    }

}
