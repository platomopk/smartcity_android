package smartcity.com.smartcity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import smartcity.com.smartcity.util.Util;

public class SignupVehicle extends AppCompatActivity {

    Button vadd, finish;
    Boolean admin;
    Spinner make, model, year, engine;
    EditText mileage, power, weight, pnumber;
    String vAdmin, vDriver;
    int count = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Util IP = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_vehicle);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        make = findViewById(R.id.vmake);
        model = findViewById(R.id.vmodel);
        year = findViewById(R.id.vyear);
        engine = findViewById(R.id.vengine);
        mileage = findViewById(R.id.vmileage);
        power = findViewById(R.id.vpower);
        pnumber = findViewById(R.id.vnumber);
        weight = findViewById(R.id.vweight);
        vadd = findViewById(R.id.add);
        admin = getIntent().getBooleanExtra("isAdmin", false);
        vAdmin = getIntent().getStringExtra("Admin");
        vDriver = getIntent().getStringExtra("Driver");
        finish = findViewById(R.id.finish);

        vadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pnumber.getText().toString().length() > 0) {
                    new signUpVehicle().execute();
                }else {
                    Toast.makeText(SignupVehicle.this,"Fill the required details",Toast.LENGTH_LONG).show();
                }
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count != 0 || pnumber.getText().toString().length() > 0) {
                    new finishVehicle().execute();
                }else {
                    Toast.makeText(SignupVehicle.this,"Fill the required details",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class signUpVehicle extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ProgressDialog pd = new ProgressDialog(SignupVehicle.this);
            pd.setMessage("Please Wait ..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                signup();

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

    public void signup() {
        count = count + 1;
        if (!admin) {
            if (pnumber.getText().toString().length() > 0) {
                RequestQueue queue = Volley.newRequestQueue(SignupVehicle.this);
                String URL = IP.getBaseIP() + "registerVehicle.php?make=" + make.getSelectedItem().toString() + "&model=" + model.getSelectedItem().toString() + "&year=" + year.getSelectedItem().toString() + "&pnumber=" + pnumber.getText().toString().trim() + "&mileage=" + mileage.getText().toString().trim() + "&power=" + power.getText().toString() + "&weight=" + weight.getText().toString() + "&admin=" + vAdmin + "&driver=" + vDriver;
                Log.d("onClick: ", URL);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("Yes")) {
                                    editor.putString("Vehicle", pnumber.getText().toString());
                                    editor.apply();
                                    pnumber.getText().clear();
                                    weight.getText().clear();
                                    power.getText().clear();
                                    mileage.getText().clear();
                                } else {
                                    Toast.makeText(SignupVehicle.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onResponse: ", "Failed");
                        Toast.makeText(SignupVehicle.this, "Check your internet", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);

            }
        }
    }

    public void finish() {
        if (count != 0 || pnumber.getText().toString().length() > 0) {
            if (admin) {
                Intent intent = new Intent(SignupVehicle.this, Admin.class);
                startActivity(intent);
                finish();
            } else {
                RequestQueue queue = Volley.newRequestQueue(SignupVehicle.this);
                String URL = IP.getBaseIP() + "registerVehicle.php?make=" + make.getSelectedItem().toString() + "&model=" + model.getSelectedItem().toString() + "&year=" + year.getSelectedItem().toString() + "&pnumber=" + pnumber.getText().toString().trim() + "&mileage=" + mileage.getText().toString() + "&power=" + power.getText().toString() + "&weight=" + weight.getText().toString() + "&admin=" + vAdmin + "&driver=" + vDriver;
                Log.d("finish: ", URL);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("Yes")) {
                                    editor.putString("Vehicle", pnumber.getText().toString());
                                    editor.apply();
                                    Intent intent = new Intent(SignupVehicle.this, RideStartActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignupVehicle.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onResponse: ", "Failed");
                        Toast.makeText(SignupVehicle.this, "Check your internet", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
            }
        }
    }

    public class finishVehicle extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ProgressDialog pd = new ProgressDialog(SignupVehicle.this);
            pd.setMessage("Please Wait ..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                finish();

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
}
