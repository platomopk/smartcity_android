package smartcity.com.smartcity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import smartcity.com.smartcity.util.Util;

public class MyService  extends Service {
    public ArrayList<LatLng> polygon  = new ArrayList<>();
    private GoogleMap mMap;
    Button rideEnd;
    Gson gson = new Gson();
    private BluetoothAdapter BTAdapter;
    private RequestQueue requestQueue;
    //private FusedLocationProviderClient mFusedLocationClient;
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
   // SharedPreferences pref;
   // SharedPreferences.Editor editor;
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
    Context context;
    boolean destroyed = false;

    Timer timerobj;


    //  Timer T = new Timer();
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
   // getA registerReceiver(bReciever, filter);
   // private BluetoothAdapter BTAdapter;

    //Util IP = new Util();
    private FusedLocationProviderClient mFusedLocationClient ;

    SharedPreferences pref;// = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
    SharedPreferences.Editor editor;// = pref.edit();

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }


    ScheduledExecutorService ses;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    Handler handler;
    Runnable runnable;



    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("Service Started.");
        destroyed = false;

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        timerobj = new Timer();



        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        tripId = pref.getString("tripID",null);
        System.out.println("tripID: "+tripId);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        getApplicationContext().registerReceiver(bReciever, filter);
//        getApplicationContext().registerReceiver(bReciever, filter1);
//        getApplicationContext().registerReceiver(bReciever, filter2);
//        getApplicationContext().registerReceiver(bReciever, filter3);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter.isDiscovering())
            BTAdapter.cancelDiscovery();
        BTAdapter.startDiscovery();
        final Handler handler = new Handler(Looper.getMainLooper());
        final int delay = 1000; //milliseconds
        context = this;

//        TimerTask timerTaskObj = new TimerTask() {
//            @Override
//            public void run() {
//                if (destroyed == false) {
//                    weather++;
//                    time++;
//                    count++;
//                    if (count > 28) {
//                        BTAdapter.startDiscovery();
//                        count = 0;
//                    }
//                    updateLocation();
//                    //handler.postDelayed(this, delay);
//                }
//            }
//        };
//
//        timerobj.schedule(timerTaskObj,0,1000);


        ses = Executors.newSingleThreadScheduledExecutor();
//        ses.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (destroyed == false) {
//                    weather++;
//                    time++;
//                    count++;
//                    if (count > 28) {
//                        if(BTAdapter.isDiscovering())
//                            BTAdapter.cancelDiscovery();
//                        BTAdapter.startDiscovery();
//                        count = 0;
//                        Toast.makeText(context, "Bluetooth in schedeuler", Toast.LENGTH_LONG).show();
//                    }
//                    updateLocation();
//
//                    //handler.postDelayed(this, delay);
//
//                }
//            }
//        },0,1, TimeUnit.SECONDS);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (destroyed == false) {
                    weather++;
                    time++;
                    count++;
                    if (count > 30) {
                        if(BTAdapter.isDiscovering())
                            BTAdapter.cancelDiscovery();
                        BTAdapter.startDiscovery();
                        count = 0;
//                        Toast.makeText(context, "Bluetooth in schedeuler", Toast.LENGTH_LONG).show();
                    }
                    updateLocation();

                    handler.removeCallbacks(this);
                    handler.postDelayed(this, 1000);

                }
            }
        };

        handler.post(runnable);




//        handler.postDelayed(new Runnable(){
//            public void run() {
//                if (destroyed == false) {
//                    weather++;
//                    time++;
//                    count++;
//                    if (count > 28) {
//                        BTAdapter.startDiscovery();
//                        count = 0;
//                    }
//                    updateLocation();
//
//                    handler.postDelayed(this, delay);
//
//                }
//            }
//        }, delay);
        return START_NOT_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @SuppressLint("MissingPermission")
    private void updateLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double speed = location.getSpeed();
                            speed = speed * 3.6;
                            speeds.add(Double.valueOf(location.getSpeed()));
                            double acc;
                            double current = Double.valueOf(location.getSpeed());
                            speedz.add(String.valueOf(speed));
                            if (speeds.size()-2>0){
                                acc = current- Double.valueOf(speeds.get(speeds.size()-2));}
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
                            if (weather > 120) {
                                getWeather(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                weather = 0;
                            }
                            if (time >= 6) {
                                time = 0;
                                Intent intent=new Intent("trackride");
                                String times = String.valueOf(new Date().getTime());
                                intent.putExtra("lat",location.getLatitude());
                                intent.putExtra("lng",location.getLongitude());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                sendBroadcast(intent);
                               // polygon.add(new LatLng(location.getLatitude(), location.getLongitude()));
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
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String URL = IP.getBaseIP() + "registerLocation.php?id=" + tripId + "&lat=" + latFinal + "&lng=" + lngFinal + "&speed=" + speedFinal + "&accelerate=" + accelerateFinal + "&datestamp=" + dateFinal + "&timestamp=" + timeFinal + "&altitude=" + altitudeFinal;
                                URL = URL.replaceAll(" ","%20");
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



    ArrayList<String> bnames= new ArrayList<>();
    ArrayList<String> baddresses= new ArrayList<>();
    ArrayList<String> bsignals= new ArrayList<>();
    ArrayList<String> bclassifications= new ArrayList<>();
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("names","device found");
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String signal="";

                if (rssi > -58) {
                    signal = "High";
                } else if (rssi > -74) {
                    signal = "Medium";
                } else {
                    signal = "Low";
                }

                if(!bnames.contains(device.getName())){
                    bnames.add(device.getName());
                    baddresses.add(device.getAddress());
                    bsignals.add(signal);
                    bclassifications.add(device.getBluetoothClass().toString());
                }

                //String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                // Create a new device item
                if (!newDevice.contains(device.getAddress())) {
                    if (rssi > -58) {
                        signal = "High";
                    } else if (rssi > -74) {
                        signal = "Medium";
                    } else {
                        signal = "Low";
                    }

                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("names","Finished bluetooth");
                Log.d("names",android.text.TextUtils.join(",",bnames));
                Log.d("addresses",baddresses.toString());
                Log.d("signals",bsignals.toString());

                //send these lists back to the server
                String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
                // newDevice.add(device.getAddress());// + ": " + rssi);
//                String name = device.getName();

//                    Log.d("names->",device.getBluetoothClass().toString());
//
                String Url = IP.getBaseIP() + "registerBluetooth.php?id=" + tripId + "&name=" + android.text.TextUtils.join(",",bnames) + "&mac=" + android.text.TextUtils.join(",",baddresses) + "&signal=" + android.text.TextUtils.join(",",bsignals) + "&classification=" + android.text.TextUtils.join(",",bclassifications) + "&timestamp=" + currentDateandTime;

//                    url friendly string
                Url = Url.replaceAll(" ","%20");
//                    single quotes
                Url = Url.replaceAll("'", "\\'");

                Log.d("names: ", Url);

//              Url=  "http://smartcityapplications.com.pk/scripts/registerBluetooth.php?id=kkapooor_kkapooor_190921111839&name=Q519,Galaxy%20Note5&mac=30:21:DD:1A:3A:6C,54:40:AD:B8:21:A3&signal=Medium,Medium&classification=240408,5a020c&timestamp=2019-09-21-11:18:53";


                StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("Yes")) {
                                    Log.d("onSuccess: ", "BLUETOOTH ADDED");
//                                        Intent intent = new Intent(RideEndActivity.this, RideStartActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onResponse: ", "Failed");
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(stringRequest);


            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("names","started bluetooth search");
                bnames.clear();
                baddresses.clear();
                bsignals.clear();
                bclassifications.clear();
//                Toast.makeText(context, "Bluetooth has started searching", Toast.LENGTH_LONG).show();
            }
        }
    };
    public void getWeather(String lat, String lng) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URI = IP.getWeatherIP() + lat + "," + lng + IP.getParam();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject currently = jsonObject.getJSONObject("currently");
                            temp = String.valueOf(currently.getString("temperature"));
                            Log.d("Weather: ", temp);
                            updateWeather(temp);
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

    public void updateWeather(String temp) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        String URL = IP.getBaseIP() + "registerWeather.php?id=" + tripId + "&temp=" + temp + "&timestamp=" + time;
        URL= URL.replaceAll(" ","%20");
        Log.d("Weather: ", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.contains("Yes")) {
                            Toast.makeText(getApplicationContext(),"Weather failed!!",Toast.LENGTH_LONG).show();
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




    @Override
    public void onDestroy() {
        destroyed = true;
        if(handler!=null)
            handler.removeCallbacks(runnable);
        ses.shutdownNow();
        wakeLock.release();

        //timerobj.cancel();
        System.out.println("Service stopped.");
        super.onDestroy();
    }
}
