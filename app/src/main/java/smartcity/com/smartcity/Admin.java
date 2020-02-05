package smartcity.com.smartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import smartcity.com.smartcity.util.Util;

public class Admin extends AppCompatActivity {
    CardView driver,vehicle,trips;
    Util IP = new Util();
    String username;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        driver= findViewById(R.id.driver_view);
        vehicle= findViewById(R.id.vehicle_view);
        trips= findViewById(R.id.trips_view);
        img = findViewById(R.id.settings);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this,Settings.class);
                startActivity(intent);

            }
        });
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //setSupportActionBar(findViewById(R.id.my_toolbar));
       // String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
       // Log.d("onCreate: ",currentDateandTime);
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(Admin.this,AdminDrivers.class);
               startActivity(intent);
            }
        });
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this,AdminVehicles.class);
                startActivity(intent);
            }
        });
        trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this,AdminTrips.class);
                startActivity(intent);
            }
        });
    }
}
