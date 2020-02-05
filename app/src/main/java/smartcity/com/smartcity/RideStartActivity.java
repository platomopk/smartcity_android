package smartcity.com.smartcity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import smartcity.com.smartcity.util.Util;

import static java.security.AccessController.getContext;

public class RideStartActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button rideStart;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    Util IP = new Util();
    boolean ride = false;
    private BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    ArrayList newDevice = new ArrayList();
    private FusedLocationProviderClient mFusedLocationClient;
    String mLatitudeLabel, mLongitudeLabel;
    View mapView;
    String tripID;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String sTime, admin, driver, vehicle;
    ImageView img;
    //Intent intent;// = new Intent(RideStartActivity.this, RideEndActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_start);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        admin = pref.getString("Admin", null);
        editor.putBoolean("Ride",false);
        editor.apply();
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        img = findViewById(R.id.settings);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideStartActivity.this, Settings.class);
                startActivity(intent);

            }
        });
        driver = pref.getString("UName", null);
        vehicle = pref.getString("Vehicle", null);
        Log.d("onCreate: ",admin+": "+driver+": "+vehicle);
        if (vehicle == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(RideStartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(RideStartActivity.this);
            }
            builder.setTitle("No Vehicle Found")
                    .setMessage("Please add a vehicle")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(RideStartActivity.this,SignupVehicle.class);
                            intent.putExtra("isAdmin",false);
                            intent.putExtra("Driver", driver);
                            intent.putExtra("Admin", admin);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }
        rideStart = findViewById(R.id.start);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        rideStart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                LocationManager lm = (LocationManager)RideStartActivity.this.getSystemService(Context.LOCATION_SERVICE);

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                boolean haveConnectedWifi = false;
                boolean haveConnectedMobile = false;

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnected())
                            haveConnectedWifi = true;
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnected())
                            haveConnectedMobile = true;
                }
                if (!BTAdapter.isEnabled()) {
                    Toast.makeText(RideStartActivity.this, "Enable Bluetooth", Toast.LENGTH_SHORT).show();
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                } else if(!gps_enabled){
                    Toast.makeText(RideStartActivity.this,"Please enable GPS!",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                else if (!haveConnectedWifi && !haveConnectedMobile ){
                    Toast.makeText(RideStartActivity.this,"Please turn on Internet",Toast.LENGTH_LONG).show();


                }
                else {
                    new startRide().execute();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setAllGesturesEnabled(false);

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLatitudeLabel = String.valueOf(location.getLatitude());
                            mLongitudeLabel = String.valueOf(location.getLongitude());
                            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                            // mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
//                                }
//                            },7000);

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if(mMap!=null){
            try {
                mMap.setMyLocationEnabled(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();

    }

    ProgressDialog pd;
    public class startRide extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(RideStartActivity.this);
            pd.setMessage("Please Wait ..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                start();

            } catch (Exception e) {
                Log.e("Starting Ride ", "doInBackground: " + e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //pd.dismiss();

            try {

                if (!result) {
                    Toast.makeText(getApplication(), "Sorry! Something went wrong", Toast.LENGTH_LONG).show();
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void start() {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(RideStartActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        pd.dismiss();
                        if (location != null) {
                            mLatitudeLabel = String.valueOf(location.getLatitude());
                            mLongitudeLabel = String.valueOf(location.getLongitude());
                            sTime = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
                            tripID = admin + "_" + driver + "_" + sTime;
                            sTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
                            String URL = IP.getBaseIP() + "registerTrips.php?id=" + tripID + "&stime=" + sTime + "&sloc=" + mLatitudeLabel + "," + mLongitudeLabel + "&status=In_Progress" +
                                    "&admin=" + admin + "&driver=" + driver + "&vehicle=" + vehicle;
                            Log.d("onSuccess: ", URL);
                            RequestQueue queue = Volley.newRequestQueue(RideStartActivity.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonArray = new JSONObject(response);
                                                Log.d("onResponse: ", jsonArray.getString("success"));
                                                if (jsonArray.getString("success").equals("Yes")) {
                                                    Intent intent = new Intent(RideStartActivity.this, RideEndActivity.class);
                                                    intent.putExtra("Lat", mLatitudeLabel);
                                                    intent.putExtra("Long", mLongitudeLabel);
                                                    intent.putExtra("tripID", tripID);
                                                    ride = true;
                                                    editor.putBoolean("Ride",ride);
                                                    editor.apply();
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("onResponse: ", "Failed");
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }
                });
    }
}
