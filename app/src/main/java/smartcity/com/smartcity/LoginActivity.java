package smartcity.com.smartcity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import smartcity.com.smartcity.util.Util;

public class LoginActivity extends AppCompatActivity {
    Button signin, signup;
    String ip;
    EditText username,password;
    CheckBox checkBox;
    Util IP = new Util();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String URL;
    Spinner vehicle;
    ArrayAdapter<String> adapter;
    TextView forgotpassword;
    List<String> vehiclesList=new ArrayList<String>();
    List<String> vehiclesNumber=new ArrayList<String>();
    Spinner accountType;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkBox = findViewById(R.id.checkBox);
        vehicle = findViewById(R.id.vSpinner_signin);
        forgotpassword = findViewById(R.id.forgotp);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);

            }
        });
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        ip= IP.getBaseIP();
        username= findViewById(R.id.username_signin);

        password= findViewById(R.id.passwd_signin);
        Log.d("onCreate: ",ip);
        signin= findViewById(R.id.btnlogin_signin);
        signup= findViewById(R.id.btnsignup_signin);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
//                    show progress dialog
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Please Wait ..");
                    pd.setCancelable(false);
                    pd.show();


                    RequestQueue que = Volley.newRequestQueue(LoginActivity.this);
                    String URL = ip+"getDriverVehicles.php?username="+username.getText().toString();
                    Log.d("onFocusChange: ",URL);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    pd.dismiss();
                                    // Display the first 500 characters of the response string.
                                    Log.d("onFocusChange: ",response);
                                    try {

                                        JSONArray jsonArray= new JSONArray(response);
                                        if(response.contains("Make")) {
                                            for(int i=0;i<jsonArray.length();i++){
                                                vehiclesNumber.add(jsonArray.getJSONObject(i).getString("PlateNumber"));
                                                vehiclesList.add(jsonArray.getJSONObject(i).getString("Make")+" "+jsonArray.getJSONObject(i).getString("Model")+" : "+jsonArray.getJSONObject(i).getString("PlateNumber"));
                                            }
                                            adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, vehiclesList);

                                            vehicle.setAdapter(adapter);

                                            System.out.println("onFocusChanged: "+String.valueOf(jsonArray.length()));//,Toast.LENGTH_SHORT).show();
                                            //  editor.putString("FName", jsonArray.getJSONObject(0).getString("Username"));
                                        }else {
                                            vehiclesList.clear();//add(jsonArray.getJSONObject(i).getString("Make")+" "+jsonArray.getJSONObject(i).getString("Model")+" : "+jsonArray.getJSONObject(i).getString("PlateNumber"));

                                        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, vehiclesList);

                                        vehicle.setAdapter(adapter);
//                                        vehicle.not
                                            //Toast.makeText(LoginActivity.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();
                                            }
                                        //Log.d("onResponse: ", jsonArray.getJSONObject(0).getString("FirstName"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
                    que.add(stringRequest);
                }
            }
        });
        accountType= findViewById(R.id.aSpinner_signin);
       // signup = findViewById(R.id.btnsignup_signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new loginTask().execute();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public class loginTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please Wait ..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                login();

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

    public void login(){
        if(accountType.getSelectedItem().toString().equals("Admin")){
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            URL= ip+"loginAdmin.php?username="+username.getText().toString().trim()+"&password="+password.getText().toString();
            Log.d("onClick: ",URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                JSONArray jsonArray= new JSONArray(response);
                                if(response.contains("FirstName")) {
                                    // Log.d("Length: ",String.valueOf(response.length()));
                                    editor.putString("FName", jsonArray.getJSONObject(0).getString("FirstName"));
                                    editor.putString("LName", jsonArray.getJSONObject(0).getString("LastName"));
                                    editor.putString("Gender", jsonArray.getJSONObject(0).getString("Gender"));
                                    editor.putString("UName", jsonArray.getJSONObject(0).getString("Username"));
                                    editor.putString("Email", jsonArray.getJSONObject(0).getString("Email"));
                                    editor.putString("Password", jsonArray.getJSONObject(0).getString("Password"));
                                    editor.putString("DOB", jsonArray.getJSONObject(0).getString("DOB"));
                                    editor.putString("City", jsonArray.getJSONObject(0).getString("City"));
                                    editor.putString("DLNumber", jsonArray.getJSONObject(0).getString("DLNumber"));
                                    editor.putString("DCompany", jsonArray.getJSONObject(0).getString("DCompany"));
                                    editor.putString("PNumber", jsonArray.getJSONObject(0).getString("PhoneNumber"));
                                    editor.putString("AType","Admin");
                                    //  editor.putString("FName", jsonArray.getJSONObject(0).getString("Username"));
                                    if(checkBox.isChecked()){
                                        editor.putBoolean("login", true);}
                                    editor.apply();
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, Admin.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    pd.dismiss();
                                    Toast.makeText(LoginActivity.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();}
                                //Log.d("onResponse: ", jsonArray.getJSONObject(0).getString("FirstName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
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
        else {
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            URL= ip+"loginDriver.php?username="+username.getText().toString().trim()+"&password="+password.getText().toString();
            Log.d("onClick: ",URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            try {
                                JSONArray jsonArray= new JSONArray(response);
                                if(response.contains("FirstName")) {
                                    // Log.d("Length: ",String.valueOf(response.length()));
                                    editor.putString("FName", jsonArray.getJSONObject(0).getString("FirstName"));
                                    editor.putString("LName", jsonArray.getJSONObject(0).getString("LastName"));
                                    editor.putString("Gender", jsonArray.getJSONObject(0).getString("Gender"));
                                    editor.putString("City", jsonArray.getJSONObject(0).getString("City"));
                                    editor.putString("UName", jsonArray.getJSONObject(0).getString("Username"));
                                    editor.putString("Email", jsonArray.getJSONObject(0).getString("Email"));
                                    editor.putString("DOB", jsonArray.getJSONObject(0).getString("Dob"));
                                    editor.putString("Password", jsonArray.getJSONObject(0).getString("Password"));
                                    editor.putString("PNumber", jsonArray.getJSONObject(0).getString("PhoneNumber"));
                                    editor.putString("Admin", jsonArray.getJSONObject(0).getString("DAdmin"));
                                    editor.putString("DLNumber", jsonArray.getJSONObject(0).getString("DLNumber"));
                                    editor.putString("DCompany", jsonArray.getJSONObject(0).getString("DCompany"));
                                    editor.putString("PNumber", jsonArray.getJSONObject(0).getString("PhoneNumber"));
                                    editor.putString("Vehicle", vehiclesNumber.get(vehicle.getSelectedItemPosition()));
                                    if(checkBox.isChecked()){
                                        editor.putBoolean("login", true);}
                                    editor.putString("AType","Driver");
                                    editor.apply();
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, RideStartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    pd.dismiss();
                                    Toast.makeText(LoginActivity.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();}
                                // Log.d("onResponse: ", jsonArray.getJSONObject(0).getString("FirstName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
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
