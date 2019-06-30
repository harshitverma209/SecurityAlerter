package com.tenture.securityalerter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppSettings extends AppCompatActivity {
    Switch autoguardSwitch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);
        autoguardSwitch=findViewById(R.id.autoguardSwitch);
        autoguardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent=new Intent(AppSettings.this, AutoGuardService.class);
                if(compoundButton.isChecked()){
                    intent.putExtra("safeNumber",getIntent().getStringExtra("safeNumber"));
                    intent.putExtra("lat", getIntent().getStringExtra("lat"));
                    intent.putExtra("lon", getIntent().getStringExtra("lon"));
                    startService(intent);

                }
                else{
                    stopService(intent);
                    //Toast.makeText(AppSettings.this, "Autoguard Service off!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
