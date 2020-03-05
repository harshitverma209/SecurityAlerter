package com.tenture.securityalerter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity {

    Button login,signup;
    EditText usernameBox,passwordBox;
    String username;
    String password;
    String server;
    String userid;
    String name;
    boolean qs;
    String number="+000000000000";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String lat,lon;
    private boolean locationSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //server = "192.168.1.6/polcon_server/shops";
        server="policeconsole.000webhostapp.com/php/shops";

        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        if (sharedPreferences.contains("username")) {
            number = sharedPreferences.getString("number", "+000000000000");
            userid=sharedPreferences.getString("userid", "nill");
            if(sharedPreferences.contains("lat")){
                lat=sharedPreferences.getString("lat", "0");
                lon=sharedPreferences.getString("lon", "0");
                startWork();
                finish();
            }else{
                Intent intent = new Intent(LoginActivity.this,LocationActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("server", server);
                Toast.makeText(LoginActivity.this, "Location not set!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        }else{
            setContentView(R.layout.activity_login);
            login = findViewById(R.id.login);
            signup = findViewById(R.id.signup);
            usernameBox = findViewById(R.id.username);
            passwordBox = findViewById(R.id.password);




            login.setOnClickListener(view -> {
                username = usernameBox.getText().toString();
                password = passwordBox.getText().toString();
                userLogin();
            });
            signup.setOnClickListener(view -> {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                i.putExtra("server", server);
                startActivity(i);
            });
        }
    }

    public void userLogin(){
        String url="http://"+server+"/Login.php?username="+username+"&password="+password;

        StringRequest req=new StringRequest(Request.Method.POST,url, response -> {
            try {
                Log.d("chech","Response received:"+ response);
                JSONObject obj=new JSONObject(response);
                Log.d("chech","object found");
                username=obj.getString("uid");
                userid=obj.getString("userid");
                name=obj.getString("name");
                locationSet=obj.getBoolean("locationSet");
                if(locationSet) {
                    lat = obj.getString("lat");
                    lon = obj.getString("lon");
                }

                number=("+91" + obj.getString("number")).replaceAll(" ", "");
                Log.d("chech",userid);
                qs=obj.getBoolean("qs");
                //Toast.makeText(MainActivity.this,"Welcome, "+name,Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.d("chech","string not found");
                e.printStackTrace();
            }
            if(qs){
                editor = sharedPreferences.edit();
                editor.putString("userid", userid);
                editor.putString("username", username);
                editor.putString("number", number);

                editor.putString("server", server);
                if(locationSet){
                    editor.putString("lat", lat);
                    editor.putString("lon", lon);
                }
                editor.apply();
                if(locationSet){
                    startWork();
                }else{
                    Intent intent = new Intent(LoginActivity.this,LocationActivity.class);
                    intent.putExtra("userid",userid);
                    intent.putExtra("server", server);
                    Toast.makeText(LoginActivity.this, "Location not set!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                finish();

            }else{
                Toast.makeText(LoginActivity.this,"Error, Please try again ",Toast.LENGTH_LONG).show();
                //log.setText("Try Again");
                //log.setProgress(-1);
            }
        }, error -> Log.d("chech",error.toString()));
        RequestQueue rq= Volley.newRequestQueue(LoginActivity.this);
        rq.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void startWork() {
        Intent i=new Intent(LoginActivity.this,MainActivity.class);
        i.putExtra("server",server);
        i.putExtra("userid",userid);
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        i.putExtra("number", number);
        startActivity(i);
    }
}
