package com.tenture.securityalerter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.Nullable;

public class AlarmScreen extends Activity {

    Button alarmStopper, informer;
    View.OnClickListener alarmStop,inform;
    String lat,lon,username;
    RequestQueue queue;
    String url,server;
    JsonObjectRequest jsObjRequest;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);



        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        username=sharedPreferences.getString("username", "nill");
        server=sharedPreferences.getString("server", "nill");
        Log.d("chech", username);
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
                params.put("username", username);
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
                addVic();
            }
        };
    }
    public void addVic(){
        String url="http://"+server+"/AddVic.php?username="+username+"&lat="+lat+"&lon="+lon;
        //String url="http://192.168.1.6/polcon_server/shops/Signup.php?username="+uname+"&password="+pass+"&name="+pname+"&phone="+contact+"&gender="+gen;

        StringRequest req=new StringRequest(Request.Method.POST,url,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    Log.d("chech","Response received:"+response.toString());
                    JSONObject obj=new JSONObject(response);
                    Log.d("chech","object found");
                   /*String userid=obj.getString("userid");
                   String name=obj.getString("name");
                   Log.d("chech",userid);
*/
                    String qs=obj.getString("qs");
                    if(qs.equals("true")) {

                        Toast.makeText(AlarmScreen.this, "Informed the Police!", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(AlarmScreen.this, "Police already informed!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("chech","string not found");
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("chech",error.toString());
            }
        });
        RequestQueue rq= Volley.newRequestQueue(AlarmScreen.this);
        rq.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}
