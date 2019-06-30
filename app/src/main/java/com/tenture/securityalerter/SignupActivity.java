package com.tenture.securityalerter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.jaredrummler.materialspinner.*;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    EditText username,password,number,name;
    String uname,pass,pname,contact,gen;
    MaterialSpinner gender;
    ActionProcessButton signup;
    String genders[]={"Male","Female","Others"};
    String server,qs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username=findViewById(R.id.groupid);
        password=findViewById(R.id.password);
        number=findViewById(R.id.number);
        name=findViewById(R.id.name);
        gender=findViewById(R.id.gender);
        signup=findViewById(R.id.signup);
        signup.setMode(ActionProcessButton.Mode.ENDLESS);
        server=getIntent().getStringExtra("server");
        ArrayAdapter<String> a=new ArrayAdapter<String>(SignupActivity.this,android.R.layout.simple_list_item_1,genders);
        gender.setAdapter(a);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup.setProgress(1);
                uname=username.getText().toString();
                pass=password.getText().toString();
                pname=name.getText().toString();
                contact=number.getText().toString().replaceAll(" ", "");
                gen=genders[gender.getSelectedIndex()];
                userSignup();
            }
        });
    }
    public void userSignup(){
        String url="http://"+server+"/Signup.php?username="+uname+"&password="+pass+"&name="+pname+"&phone="+contact+"&gender="+gen;
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
                    qs=obj.getString("qs");
                    if(qs.equals("true")) {
                        signup.setProgress(100);
                        signup.setText("Success");
                        Toast.makeText(SignupActivity.this, "Welcome, " + pname, Toast.LENGTH_LONG).show();
                        Intent i=new Intent(SignupActivity.this,LocationActivity.class);
                        i.putExtra("server",server);
                        i.putExtra("number", contact);
                        i.putExtra("userid",obj.getString("userid"));
                        finish();
                        startActivity(i);

                    }else{
                        Toast.makeText(SignupActivity.this, "Username already taken", Toast.LENGTH_LONG).show();
                        signup.setText("Error!");
                        signup.setProgress(-1);
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
        RequestQueue rq= Volley.newRequestQueue(SignupActivity.this);
        rq.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}
