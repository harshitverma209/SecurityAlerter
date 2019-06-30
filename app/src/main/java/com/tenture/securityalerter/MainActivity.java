package com.tenture.securityalerter;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.adorsys.android.smsparser.SmsTool;



import com.mikepenz.materialdrawer.DrawerBuilder;


public class MainActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    Context context;
    String safeNumber="+000000000000",lat,lon,userid;
    TextView safe;
    Button inOut;
    Intent intent;
    RelativeLayout layout;
    View.OnClickListener inListener,outListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar = new Toolbar(getApplicationContext());
        toolbar=findViewById(R.id.toolbar);
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
        setSupportActionBar(toolbar);
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
        intent=new Intent(MainActivity.this,SmsRadarService.class);;
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
            inOut.setText("I'm in the office");
        }else{
            safe.setText("Currently idle");
            inOut.setOnClickListener(outListener);
        }




    }


    private void initializeListeners() {
        inListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button=(Button) view;
                button.setText("I'm going out!");
                stop(intent);
                button.setOnClickListener(outListener);

            }
        };
        outListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button=(Button) view;
                button.setText("I'm in the office");
                start(intent);
                button.setOnClickListener(inListener);
            }
        };
    }

    private void stop(Intent intent) {
        stopService(intent);
        safe.setText("Currently idle");
    }

    private void start(Intent intent) {

        safe.setText("Listening from : "+safeNumber );
        intent.putExtra("userid", userid);
        intent.putExtra("safeNumber", safeNumber);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        startService(intent);
    }

}
