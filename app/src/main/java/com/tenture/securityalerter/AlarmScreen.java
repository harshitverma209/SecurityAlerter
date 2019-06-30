package com.tenture.securityalerter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.Nullable;

public class AlarmScreen extends Activity {

    Button alarmStopper, informer;
    View.OnClickListener alarmStop,inform;
    String lat,lon,userid;
    RequestQueue queue;
    String url;
    JsonObjectRequest jsObjRequest;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        userid=sharedPreferences.getString("userid", "nill");
        Log.d("chech", userid);
        alarmStopper=findViewById(R.id.alarmStopper);
        informer=findViewById(R.id.informer);

        queue = Volley.newRequestQueue(AlarmScreen.this);
        url = "https://polconserver.herokuapp.com/postdata"; // your URL

        queue.start();

        initializeListeners();
        alarmStopper.setOnClickListener(alarmStop);
        informer.setOnClickListener(inform);
    }
    private void initializeListeners(){
        alarmStop=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsReceiver.mp.stop();
            }
        };
        inform=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String,String>();
                params.put("userid", userid);
                params.put("lat", getIntent().getStringExtra("lat")); // the entered data as the body.
                params.put("lon", getIntent().getStringExtra("lon"));
                JSONObject jsonObject= new JSONObject(params);
//                Log.d("chech",String.valueOf(loc.getLatitude()));
//                        try {
//                            jsonObject.put("location",locationImp);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                jsObjRequest = new
                        JsonObjectRequest(Request.Method.POST,
                        url,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //DisplayText.setText(response.getString("message"));
                                    Toast.makeText(AlarmScreen.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //DisplayText.setText("That didn't work!");
                        Toast.makeText(AlarmScreen.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                        Log.d("chech",error.toString());
                    }
                });
                queue.add(jsObjRequest);
            }
        };
    }
}
