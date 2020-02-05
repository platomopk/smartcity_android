package smartcity.com.smartcity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import smartcity.com.smartcity.util.LocationModal;
import smartcity.com.smartcity.util.Util;
import smartcity.com.smartcity.util.VehicleModal;

public class AdminTrackRide extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Gson gson = new Gson();
    Util IP = new Util();
    int count =0;
    TextView name,number,company;
    ImageView img;
    private RequestQueue requestQueue;
    public List<LatLng> polygon;
    List<LocationModal.LocationBean> results;
    String tripId, tripDriver,tripStatus,tripSLoc,tripELoc, TAG = "TRACKING";
    public void timer() {
        final Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count >10) {
                            updateLocation();
                            count = 0;
                        }
                        count++;
                    }
                });
            }
        }, 1, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_track_ride);
        name = findViewById(R.id.name);
        number = findViewById(R.id.pnumber);
        company = findViewById(R.id.company);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        img = findViewById(R.id.settings);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminTrackRide.this,Settings.class);
                startActivity(intent);

            }
        });

        //timer();
        polygon = new ArrayList<>();
        tripId = getIntent().getStringExtra("tripId");
        tripDriver = getIntent().getStringExtra("tripDriver");
        getDriver();
        tripStatus = getIntent().getStringExtra("tripStatus");
        tripSLoc = getIntent().getStringExtra("tripSLoc");
        tripELoc = getIntent().getStringExtra("tripELoc");
        System.out.println("tripELoc: "+tripELoc);
        requestQueue = Volley.newRequestQueue(this);
        if(!tripStatus.equals("Completed")){
            timer();
        }
        fetchPosts();
        Log.d(TAG, tripId+": "+tripDriver+": "+tripStatus);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void getDriver(){
        String URL = IP.getBaseIP()+"getTripDriver.php?tripID="+tripId;
        RequestQueue queue = Volley.newRequestQueue(AdminTrackRide.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (response.contains("FirstName")) {
                                String fname = jsonArray.getJSONObject(0).getString("FirstName")+ jsonArray.getJSONObject(0).getString("LastName");
                                name.setText(fname);
                                number.setText(jsonArray.getJSONObject(0).getString("PhoneNumber"));
                                company.setText(jsonArray.getJSONObject(0).getString("DCompany"));

                            }else {
                                Toast.makeText(AdminTrackRide.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();}
                            //Log.d("onResponse: ", jsonArray.getJSONObject(0).getString("FirstName"));
                        } catch (JSONException e) {
                            Log.d("DRIVER: ","Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onResponse: ", "Failed");
                //.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private void fetchPosts() {
        String URL = IP.getBaseIP()+"getLocation.php?id="+tripId;
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mLatitudeLabel+","+mLongitudeLabel+"&rankby=distance&number=hospital&key=AIzaSyDKmUDWEuJmCC1e2Idce6AoEJaH3xkQzbA";
        StringRequest request = new StringRequest(Request.Method.GET, URL, onPostsLoaded, onPostsError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {

        @Override

        public void onResponse(String response) {
            LocationModal locationModal = gson.fromJson(response, LocationModal.class);
            //System.out.println("Test: "+ driverModal.getDrivers().get(0).getCity());
            results = locationModal.getLocation();
            if(results.size()>0) {
                for (int i = 0; i < results.size(); i++) {
                  //tring[] separated = results.get(i).getLatLng().split(",");
                    polygon.add(new LatLng(Double.valueOf(results.get(i).getLat()), Double.valueOf(results.get(i).getLng())));
                    mMap.addPolyline(new PolylineOptions()
                            .addAll(polygon)
                            .width(8)
                            .color(Color.BLACK)
                    );

                    //mMap.addMarker(new MarkerOptions().position(latLng).title("End"));
                    //  results.add();
                    // LocationModal.LocationBean ok= new LocationModal.LocationBean();
                    //  results.add(ok);
                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
                LatLng latLng;

              //  String[] separat = results.get(results.size() - 1).get().split(",");
                latLng = new LatLng(Double.valueOf(results.get(results.size()-1).getLat()), Double.valueOf(results.get(results.size()-1).getLng()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }else{
               // Toast.makeText(AdminTrackRide.this,"Refresh",Toast.LENGTH_SHORT).show();
            }
        }

    };
    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {

        @Override

        public void onErrorResponse(VolleyError error) {

            //fetchingTxt.setText("Please check your internet connection and try again");
            //progressBar.setVisibility(View.GONE);
        }

    };
    private void updateLocation(){
        String URL = IP.getBaseIP()+"getLatestLocation.php?id="+tripId;//mohsin1234";
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mLatitudeLabel+","+mLongitudeLabel+"&rankby=distance&number=hospital&key=AIzaSyDKmUDWEuJmCC1e2Idce6AoEJaH3xkQzbA";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LocationModal locationModal = gson.fromJson(response, LocationModal.class);
                        List<LocationModal.LocationBean> test= locationModal.getLocation();
                        if (test.size() > 0){
                            //String[] separated = test.get(0).getLatLng().split(",");
                            polygon.add(new LatLng(Double.valueOf(test.get(0).getLat()), Double.valueOf(test.get(0).getLng())));
                            mMap.addPolyline(new PolylineOptions()
                                    .addAll(polygon)
                                    .width(8)
                                    .color(Color.BLACK)
                            );
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onResponse: ", "Failed");
            }
        });
        requestQueue.add(request);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String[] separated = tripSLoc.split(",");
        if (tripELoc != null){
            String[] separatedEnd = tripELoc.split(",");
            LatLng endy = new LatLng(Double.valueOf(separatedEnd[0]),Double.valueOf(separatedEnd[1]));
            mMap.addMarker(new MarkerOptions().position(endy).title("End Position"));
        }
//        Log.d("onMapReady: ",separated[0]+ ": "+separated[1]);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.valueOf(separated[0]),Double.valueOf(separated[1]));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Starting Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));
    }
}
