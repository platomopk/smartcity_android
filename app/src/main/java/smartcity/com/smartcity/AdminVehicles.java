package smartcity.com.smartcity;

//import android.support.v7.app.AlertController;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

import smartcity.com.smartcity.util.DriverModal;
import smartcity.com.smartcity.util.Util;
import smartcity.com.smartcity.util.VehicleModal;

public class AdminVehicles extends AppCompatActivity {
    String username;
    Util IP = new Util();
    Gson gson = new Gson();
    MyListAdapter myListAdapter;
    private RequestQueue requestQueue;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView data;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicles);
        data= findViewById(R.id.nodata);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        img = findViewById(R.id.settings);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminVehicles.this,Settings.class);
                startActivity(intent);

            }
        });
        recyclerView = findViewById(R.id.dView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        requestQueue = Volley.newRequestQueue(this);
        fetchPosts();

    }
    private void fetchPosts() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String admin = pref.getString("UName", null);
        String URL = IP.getBaseIP()+"getVehicles.php?username="+admin;
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mLatitudeLabel+","+mLongitudeLabel+"&rankby=distance&number=hospital&key=AIzaSyDKmUDWEuJmCC1e2Idce6AoEJaH3xkQzbA";
        StringRequest request = new StringRequest(Request.Method.GET, URL, onPostsLoaded, onPostsError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {

        @Override

        public void onResponse(String response) {
            VehicleModal vehicleModal = gson.fromJson(response, VehicleModal.class);
            //System.out.println("Test: "+ driverModal.getDrivers().get(0).getCity());
            if(vehicleModal.getVehicles().size()>0) {

                data.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                List<VehicleModal.VehiclesBean> results = vehicleModal.getVehicles();

                myListAdapter = new MyListAdapter(results);
                recyclerView.setLayoutManager(layoutManager);
                // progressBar.setVisibility(View.GONE);
                //fetchingTxt.setVisibility(View.GONE);
                recyclerView.setAdapter(myListAdapter);
            }else{data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
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
    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView fname, number, username;
      // RatingBar ratingBar;
       // GoogleMap googleMap;

        public MyViewHolderRes(View itemView) {
            super(itemView);
         //   this.googleMap= googleMap ;
            fname = (TextView) itemView.findViewById(R.id.name);
            username = (TextView) itemView.findViewById(R.id.driver);
            //time = (TextView) itemView.findViewById(R.id.time);
            number = (TextView) itemView.findViewById(R.id.number);
           // ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }

    }

    public class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {
        List<VehicleModal.VehiclesBean> results;
        //GoogleMap map;

        public MyListAdapter(List<VehicleModal.VehiclesBean> results) {
            this.results = results;
            //this.map = googleMap;
        }

        @Override
        public int getItemCount() {
            if (results == null) {
                return 0;
            } else return results.size();
        }

        @NonNull
        @Override
        public MyViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_vehicle, parent, false);
            MyViewHolderRes holder = new MyViewHolderRes(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRes holder, final int position) {
            String name = results.get(position).getMake()+" "+results.get(position).getModel();

            holder.fname.setText(name);
            holder.username.setText(results.get(position).getVDriver());
//            holder.time.setText("Open Now");
            holder.number.setText(results.get(position).getPlateNumber());
           // holder.ratingBar.setRating((float) results.get(position).getRating());
           // LatLng hospital = new LatLng(results.get(position).getGeometry().getLocation().getLat(), results.get(position).getGeometry().getLocation().getLng());
           // map.addMarker(new MarkerOptions().position(hospital).title(holder.name.getText().toString()));
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(hospital, 12));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //map.clear();
                    //LatLng hospital = new LatLng(results.get(position).getGeometry().getLocation().getLat(), results.get(position).getGeometry().getLocation().getLng());
                   // map.addMarker(new MarkerOptions().position(hospital).title(holder.name.getText().toString()));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(hospital, 12));
                }
            });

        }
    }
}
