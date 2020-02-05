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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import smartcity.com.smartcity.util.DriverModal;
import smartcity.com.smartcity.util.Util;
import smartcity.com.smartcity.util.WeatherModal;

public class RideEndActivity extends AppCompatActivity implements OnMapReadyCallback {

    public List<LatLng> polygon;
    private GoogleMap mMap;
    Button rideEnd;
    Gson gson = new Gson();
    private BluetoothAdapter BTAdapter;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient mFusedLocationClient;
    LinkedHashSet<String> newDevice = new LinkedHashSet<>();
    //ArrayList newDevice = new ArrayList();
    ArrayList<Double> speeds = new ArrayList<Double>();
    ArrayList<String> lats = new ArrayList<>();
    ArrayList<String> lngs = new ArrayList<>();
    ArrayList<String> timestamps = new ArrayList<>();
    ArrayList<String> datestamps = new ArrayList<>();
    ArrayList<String> speedz = new ArrayList<>();
    ArrayList<String> accelerate = new ArrayList<>();
    ArrayList<String> altitude = new ArrayList<>();
    View mView;
    Double lat;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Double lng;
    Double lastSpeed = 1.0;
    String mLatitudeLabel, mLongitudeLabel, tripId;
    Util IP = new Util();
    //String tripID;
    String temp;
    String eTime;
    int count = 0;
    int centerCount = 0;
    int weather = 0;
    int time = 0;
  //  Timer T = new Timer();
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    Timer T = new Timer();
    public void timer() {
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 28) {
                            BTAdapter.startDiscovery();
                            count = 0;
                        }
                        //updateLocation();
                        centerCount++;
                        weather++;
                        time++;
                        count++;
                    }
                });
            }
        }, 1, 1000);
    }

    IntentFilter bfilter;
    //Receiver receiver;
    Intent serviceIntent;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_end);

//        keep on lockscreen as well
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


         bfilter = new IntentFilter("com.toxy.LOAD_URL");
        //receiver = new Receiver();

        //this.registerReceiver(receiver, filter);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("trackride"));

        serviceIntent = new Intent( RideEndActivity.this,MyService.class);

        startService(serviceIntent);




        //timer();
        Log.d("On", "onCreate: ");
       // getWeather("33.721481", "73.043289");

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        tripId = getIntent().getStringExtra("tripID");
        editor.putString("tripID",tripId);
        editor.apply();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rideEnd = findViewById(R.id.end_ride);
        rideEnd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(RideEndActivity.this);
                adb.setCancelable(false);
                adb.setMessage("Are you sure you want to end this ride?");
                adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            //Intent intet = new Intent(RideEndActivity.this, MyService.class);
                            //unregisterReceiver(receiver);
                            //stopService(serviceIntent);
                        }catch (Exception e){
                            Log.e("service stop", "onClick: "+e.getMessage());
                        }
                        new endRide().execute();


                    }
                });
                adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog ad=null;
                ad=adb.create();
                ad.show();
            }
        });


        //BTAdapter = BluetoothAdapter.getDefaultAdapter();

//        registerReceiver(bReciever, filter);

        polygon = new ArrayList<>();


        // initialize polygon with old locations .....
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

       // BTAdapter.startDiscovery();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }


//    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                Log.d("DEVICELIST", "Bluetooth device found\n");
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
//                String signal;
//                //String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
//                // Create a new device item
//                if (!newDevice.contains(device.getAddress())) {
//                    if (rssi > -58) {
//                        signal = "High";
//                    } else if (rssi > -74) {
//                        signal = "Medium";
//                    } else {
//                        signal = "Low";
//                    }
//                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
//                   // newDevice.add(device.getAddress());// + ": " + rssi);
//                    String name = device.getName();
//                    String Url = IP.getBaseIP() + "registerBluetooth.php?id=" + tripId + "&name=" + name + "&mac=" + device.getAddress() + "&signal=" + signal + "&timestamp=" + currentDateandTime;
//                    Log.d("onSuccess: ", Url);
//                    StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    if (response.contains("Yes")) {
//                                        Log.d("onSuccess: ", "BLUETOOTH ADDED");
////                                        Intent intent = new Intent(RideEndActivity.this, RideStartActivity.class);
////                                        startActivity(intent);
////                                        finish();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("onResponse: ", "Failed");
//                        }
//                    });
//                    RequestQueue queue = Volley.newRequestQueue(RideEndActivity.this);
//                    queue.add(stringRequest);
//
//                }
//            }
//        }
//    };


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double lat = intent.getDoubleExtra("lat",0.0);
            double lng = intent.getDoubleExtra("lng",0.0);
            updatePolygon(lat,lng);
            System.out.println( "onReceiveB: ");
            LatLng s = new LatLng(lat,lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s,17));
            //WebView webview =(WebView)findViewById(R.id.webView);
            //webview.loadUrl(url);
        }
    };

    @Override
    protected void onDestroy() {
        try{
            stopService(serviceIntent);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            if(mMap!=null){

                try {
                    mMap.setMyLocationEnabled(false);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            //unregisterReceiver(receiver);
            //stopService(serviceIntent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        lat = Double.valueOf(getIntent().getStringExtra("Lat"));
        lng = Double.valueOf(getIntent().getStringExtra("Long"));
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        View locationButton = ((View) mView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        LatLng sydney = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
    }

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(RideEndActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double speed = location.getSpeed();
                            speed = speed * 3.6;
                            speeds.add(Double.valueOf(speed));
                            double acc;
                            double current = Double.valueOf(location.getSpeed());
                            speedz.add(String.valueOf(speed));
                            if (speedz.size()-2>0){
                            acc = current- Double.valueOf(speedz.get(speedz.size()-2));}
                            else {acc = current;}
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                            lats.add(String.valueOf(location.getLatitude()));
                            lngs.add(String.valueOf(location.getLongitude()));
                            datestamps.add(currentDate);
                            timestamps.add(currentTime);
                            //speedz.add(String.valueOf(speed));
                            accelerate.add(String.valueOf(acc));
                            altitude.add(String.valueOf(location.getAltitude()));
                            if (time >= 6) {
                                time = 0;
                                String latFinal = "";
                                String lngFinal = "";
                                String speedFinal = "";
                                String accelerateFinal = "";
                                String altitudeFinal = "";
                                String timeFinal = "";
                                String dateFinal = "";
                                for (int i = 0; i < lats.size(); i++) {
                                    if (i == 0) {
                                        latFinal = lats.get(i);
                                        lngFinal = lngs.get(i);
                                        speedFinal = speedz.get(i);
                                        accelerateFinal = accelerate.get(i);
                                        altitudeFinal = altitude.get(i);
                                        dateFinal = datestamps.get(i);
                                        timeFinal = timestamps.get(i);
                                    } else {
                                        latFinal = latFinal + "," + lats.get(i);
                                        lngFinal = lngFinal + "," + lngs.get(i);
                                        speedFinal = speedFinal + "," + speedz.get(i);
                                        accelerateFinal = accelerateFinal + "," + accelerate.get(i);
                                        altitudeFinal = altitudeFinal + "," + altitude.get(i);
                                        dateFinal = dateFinal + "," + datestamps.get(i);
                                        timeFinal = timeFinal + "," + timestamps.get(i);
                                    }
                                }
                                Log.d("ALL LATS: ", latFinal);
                                RequestQueue queue = Volley.newRequestQueue(RideEndActivity.this);
                                String URL = IP.getBaseIP() + "registerLocation.php?id=" + tripId + "&lat=" + latFinal + "&lng=" + lngFinal + "&speed=" + speedFinal + "&accelerate=" + accelerateFinal + "&datestamp=" + dateFinal + "&timestamp=" + timeFinal + "&altitude=" + altitudeFinal;
                                Log.d("updateLocation: ", URL);
                                StringRequest request = new StringRequest(Request.Method.GET, URL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.contains("Yes")) {

                                                    lats.clear();
                                                    lngs.clear();
                                                    speedz.clear();
                                                    accelerate.clear();
                                                    datestamps.clear();
                                                    timestamps.clear();
                                                }
                                                Log.d("UpdateLocation: ", response);

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("onResponse: ", "Failed");
                                    }
                                });
                                queue.add(request);
                            }
                        }
                    }
                });
    }

//    @Override
//    public void onLocationChanged(Location location) {
////        if (weather > 60) {
////            getWeather(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
////            weather = 0;
////        }
//        if (centerCount > 15) {
//            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
//            centerCount = 0;
//        }
//       // updatePolygon(location.getLatitude(), location.getLongitude());
//    }

//    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//
//    }

    public void updatePolygon(double latitude, double longitude) {
        polygon.add(new LatLng(latitude, longitude));
        mMap.addPolyline(new PolylineOptions()
                .addAll(polygon)
                .width(8)
                .color(Color.BLACK)
        );
    }

    @Override
    protected void onResume() {
//        mMap.addPolyline(new PolylineOptions()
//                .addAll(polygon)
//                .width(8)
//                .color(Color.BLACK)
//        );
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class endRide extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ProgressDialog pd = new ProgressDialog(RideEndActivity.this);
            pd.setMessage("Please Wait ..");
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                end();

            } catch (Exception e) {
                Log.e("Ending Ride ", "doInBackground: " + e.getMessage());
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void end() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(RideEndActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            RequestQueue queue = Volley.newRequestQueue(RideEndActivity.this);
                            mLatitudeLabel = String.valueOf(location.getLatitude());
                            mLongitudeLabel = String.valueOf(location.getLongitude());
                            eTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
                            String URL = IP.getBaseIP() + "updateTrips.php?id=" + tripId + "&etime=" + eTime + "&eloc=" + mLatitudeLabel + "," + mLongitudeLabel + "&status=Completed";
                            Log.d("onSuccess: ", URL);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.contains("Yes")) {
                                                Intent intent = new Intent(RideEndActivity.this, RideStartActivity.class);
                                                startActivity(intent);
                                                editor.putBoolean("Ride", false);
                                                editor.apply();
                                                T.cancel();
                                             //   unregisterReceiver(bReciever);
                                                try {
                                                    stopService(serviceIntent);
                                                       //unregisterReceiver(receiver);
                                                    //Intent intet = new Intent(RideEndActivity.this, MyService.class);

                                                }catch (Exception e){
                                                    Log.e("-<>", "onResponse: "+ e.getMessage());
                                                }

                                                finish();
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

//    public  class Receiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context arg0, Intent arg1) {
//            double lat = arg1.getDoubleExtra("lat",0.0);
//            double lng = arg1.getDoubleExtra("lng",0.0);
//            updatePolygon(lat,lng);
//            LatLng s = new LatLng(lat,lng);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s,18));
//            System.out.println( "onReceiveB: ");
//            //WebView webview =(WebView)findViewById(R.id.webView);
//            //webview.loadUrl(url);
//        }
//    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(RideEndActivity.this);
        adb.setCancelable(false);
        adb.setMessage("Are you sure you want to go back? Application will not send data to the server if it is not visible!");
        adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
            }
        });
        adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog ad=null;
        ad=adb.create();
        ad.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER){
            Toast.makeText(this, "Power button was pressed", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
