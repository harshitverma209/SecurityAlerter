package com.tenture.securityalerter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class LocationActivity extends FragmentActivity{
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    OnMapReadyCallback omrc;
    Boolean mapObtained=false,permGranted=false;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private Location currentLocation;
    Button locSelector;
    private LatLng location;
    String server,userid,qs="false";
    private LocationRequest lq;
    private LocationCallback lc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        server=getIntent().getStringExtra("server");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        locSelector=findViewById(R.id.locSelector);


            TedPermission.with(LocationActivity.this)
                .setPermissionListener(perLisInitializer())
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        omrc = new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            public void onMapReady(final GoogleMap googleMap) {


                locSelector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        userid=getIntent().getStringExtra("userid");
                        updateShopLocation();
                        Intent i=new Intent(LocationActivity.this,MainActivity.class);
                        i.putExtra("loc",location );
                        i.putExtra("server",getIntent().getStringExtra("server"));
                        i.putExtra("number", getIntent().getStringExtra("number"));
                        startActivity(i);
                    }
                });



                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here");
                CameraUpdate zoom= CameraUpdateFactory.zoomTo(12.5f);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(zoom);

                //Adding the created the marker on the map
                googleMap.addMarker(markerOptions);
                location =markerOptions.getPosition();
                googleMap.setMyLocationEnabled(true);


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(point);
                        markerOptions.title(point.latitude + " : " + point.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                        googleMap.addMarker(markerOptions);
                        location =markerOptions.getPosition();
                    }
                });
            }

        };



        if (ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        fetchLastLocation();



    }

    private void updateShopLocation() {
        Log.d("chech", location.toString());
        String url="http://"+server+"/LocationSet.php?UserId="+userid+"&lat="+location.latitude+"&lon="+location.longitude;
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
                        //signup.setProgress(100);
                        //signup.setText("Success");
                        Toast.makeText(LocationActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                        Intent i=new Intent(LocationActivity.this,LoginActivity.class);
                        i.putExtra("server",server);
                        //i.putExtra("number", contact);
                        //i.putExtra("userid",userid);
                        finish();
                        startActivity(i);

                    }else{
                        Toast.makeText(LocationActivity.this, "Username already taken", Toast.LENGTH_LONG).show();
                        //signup.setText("Error!");
                        //signup.setProgress(-1);
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
        RequestQueue rq= Volley.newRequestQueue(LocationActivity.this);
        rq.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void fetchLastLocation(){
//        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        lq=new LocationRequest()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(100);
        lc=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult==null){
                    Toast.makeText(LocationActivity.this,"No Location recorded",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        currentLocation = location;
                        Toast.makeText(LocationActivity.this,currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(omrc);
                        fusedLocationProviderClient.removeLocationUpdates(lc);
                    }

                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(lq,lc,null);

    }
    PermissionListener perLisInitializer(){
        PermissionListener permissionlistener = new PermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted() {
                Toast.makeText(LocationActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                permGranted=true;
                fetchLastLocation();
                Log.d("chech", "granted!");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(LocationActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        return permissionlistener;
    }

}
