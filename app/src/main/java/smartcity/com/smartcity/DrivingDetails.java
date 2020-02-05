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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import smartcity.com.smartcity.util.Util;

public class DrivingDetails extends AppCompatActivity {
    EditText admin, dlnumber, company;
    Spinner account, experience;
    TextView aType;
    Button back, next;
    LinearLayout lin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Util IP = new Util();
    String fname, lname, email, uname, passsword, country, city, mobilenumber, gender, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_details);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        fname = getIntent().getStringExtra("fname");
        lname = getIntent().getStringExtra("lname");
        email = getIntent().getStringExtra("email");
        uname = getIntent().getStringExtra("username");
        dob = getIntent().getStringExtra("dob");
        passsword = getIntent().getStringExtra("passsword");
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        mobilenumber = getIntent().getStringExtra("number");
        gender = getIntent().getStringExtra("gender");
        admin = findViewById(R.id.admin_signup);
        dlnumber = findViewById(R.id.license_signup);
        company = findViewById(R.id.compay_signup);
        aType = findViewById(R.id.aType);
        lin = findViewById(R.id.lin);
        account = findViewById(R.id.user_signup);
        experience = findViewById(R.id.exp_signup);
        account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!account.getSelectedItem().toString().equals("Admin")) {
                    admin.setVisibility(View.VISIBLE);
                    lin.setVisibility(View.VISIBLE);

                } else {
                    admin.setVisibility(View.GONE);
                    lin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new signUp().execute();

            }
        });

    }
    public class signUp extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd = new ProgressDialog(DrivingDetails.this);
        @Override
        protected void onPreExecute() {

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


            try {
                pd.dismiss();
                pd.cancel();
                if (!result) {
                    Toast.makeText(getApplication(), "Sorry! Something went wrong", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void signup(){
        if (account.getSelectedItem().toString().equals("Admin")) {
            RequestQueue queue = Volley.newRequestQueue(DrivingDetails.this);
            String URL = IP.getBaseIP() + "registerAdmin.php?fname=" + fname.trim() + "&lname=" + lname.trim() + "&gender=" + gender + "&dob=" + dob + "&email=" + email.trim() + "&username=" + uname.trim() + "&password=" + passsword.trim() + "&country=" + country + "&city=" + city.trim() + "&mobile=" + mobilenumber.trim() + "&dlnumber=" + dlnumber.getText().toString().trim() + "&dexp=" + experience.getSelectedItem().toString() + "&dcompany=" + company.getText().toString();
            Log.d( "onClick: ", URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Yes")) {
                                // Log.d("Length: ",String.valueOf(response.length()));
                                editor.putString("FName", fname);
                                editor.putString("LName", lname);
                                editor.putString("Gender", gender);
                                editor.putString("UName", uname);
                                editor.putString("Password", passsword);
                                editor.putString("PNumber", mobilenumber);
                                editor.putString("Email", email);
                                editor.putString("DOB", dob);
                                editor.putString("Country",country);
                                editor.putString("City",city);
                                editor.putBoolean("login", true);
                                editor.putString("AType", "Admin");
                                editor.putString("DLNumber",dlnumber.getText().toString());
                                editor.putString("DLNumber",company.getText().toString());
                                editor.putString("Experience",experience.getSelectedItem().toString());//.getText().toString());
                                // editor.putString("DLNumber",company.getText().toString());
                                editor.apply();
                                Intent intent = new Intent(DrivingDetails.this, Admin.class);
                                intent.putExtra("isAdmin",true);
                                intent.putExtra("Admin", uname);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DrivingDetails.this, "Username/Email Already Exists!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("onResponse: ", "Failed");
                    //.setText("That didn't work!");
                }
            });
            queue.add(stringRequest);

        } else if (account.getSelectedItem().toString().equals("Driver")) {
            RequestQueue queue = Volley.newRequestQueue(DrivingDetails.this);
            String URL = IP.getBaseIP() + "registerDriver.php?fname=" + fname + "&lname=" + lname + "&gender=" + gender + "&dob=" + dob + "&email=" + email + "&username=" + uname + "&password=" + passsword + "&country=" + country + "&city=" + city + "&mobile=" + mobilenumber + "&dlnumber=" + dlnumber.getText().toString() + "&dexp=" + experience.getSelectedItem().toString() + "&dcompany=" + company.getText().toString() + "&admin=" + admin.getText().toString();
            Log.d( "onClick: ", URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Yes")) {
                                editor.putString("FName", fname);
                                editor.putString("LName", lname);
                                editor.putString("Gender", gender);
                                editor.putString("UName", uname);
                                editor.putString("Password", passsword);
                                editor.putString("PNumber", mobilenumber);
                                editor.putString("Email", email);
                                editor.putString("DOB", dob);
                                editor.putString("Country",country);
                                editor.putString("City",city);
                                editor.putString("Admin", admin.getText().toString());
                                editor.putString("AType", "Driver");
                                editor.putBoolean("login", true);
                                editor.putString("DLNumber",dlnumber.getText().toString());
                                editor.putString("DLNumber",company.getText().toString());
                                editor.putString("Experience",experience.getSelectedItem().toString());
                                editor.apply();
                                Intent intent = new Intent(DrivingDetails.this, SignupVehicle.class);
                                intent.putExtra("isAdmin",false);
                                intent.putExtra("Driver", uname);
                                intent.putExtra("Admin", admin.getText().toString());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DrivingDetails.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
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

    }
}
