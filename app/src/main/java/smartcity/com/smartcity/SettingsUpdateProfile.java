package smartcity.com.smartcity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import smartcity.com.smartcity.util.Util;

public class SettingsUpdateProfile extends AppCompatActivity {
    Spinner day,month,year,code,exp,country;
    String number,dob,aType,username;
    EditText email, city, mobile,dlnumber,company;
    Button update;
    Util IP = new Util();
    SharedPreferences pref;
    ImageView imageViewC;
    EditText dobview;
    AlertDialog ad=null;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_update_profile);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imageViewC = findViewById(R.id.imgC);
        imageViewC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar();
            }
        });
        dobview= findViewById(R.id.dob);
        dobview.setEnabled(false);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        aType= pref.getString("AType",null);
        username = pref.getString("UName", null);
        dobview.setText(pref.getString("DOB", null));
        //day= findViewById(R.id.day_signup);
       // month= findViewById(R.id.month_signup);
        //year= findViewById(R.id.year_signup);
        code= findViewById(R.id.code_signup);
        exp= findViewById(R.id.exp_signup);
        country= findViewById(R.id.country_signup);

        email= findViewById(R.id.email_signup);
        email.setText(pref.getString("Email", null));
        city= findViewById(R.id.city_signup);
        city.setText(pref.getString("Email", null));
        mobile= findViewById(R.id.number_signup);
       // mobile.setText(pref.getString("Email", null));
        dlnumber= findViewById(R.id.license_signup);
        dlnumber.setText(pref.getString("DLNumber", null));
        company= findViewById(R.id.compay_signup);
        company.setText(pref.getString("DCompany",null));
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dob= day.getSelectedItem().toString()+"-"+month.getSelectedItem().toString()+"-"+year.getSelectedItem().toString();
                number = code.getSelectedItem().toString()+mobile.getText().toString();
                RequestQueue queue = Volley.newRequestQueue(SettingsUpdateProfile.this);
                String URL = IP.getBaseIP()+"updateProfile.php?username="+username+"&dob="+dob+"&aType="+aType+"&email="+email.getText().toString()+"&country="+country.getSelectedItem().toString()+"&city="+city.getText().toString()+"&number="+number+"&dlnumber="+dlnumber.getText().toString()+"&exp="+exp.getSelectedItem().toString()+"&company="+company.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("Yes")) {
                                    Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Internet is not working", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);

            }
        });
    }
    public void showCalendar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(false);
        builder.setTitle("Please select your DOB");
        builder.setView(picker);
        // builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // toast(String.valueOf(picker.getDayOfMonth())+String.valueOf(picker.getMonth()+1));

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                DateFormat dateFormat = new SimpleDateFormat("DD-MMMM-YYYY");
                String month =String.valueOf(calendar2.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));//= dateFormat.format(calendar2);
                String day =String.valueOf(calendar2.get(Calendar.DAY_OF_MONTH));
                String year =String.valueOf(calendar2.get(Calendar.YEAR));
                String finalDOB= day+"-"+month+"-"+year;
                dobview.setText(finalDOB);
//                editor.apply();
                //calendar2.get(Calendar.YEAR);
                // toast(String.valueOf(week));
            }
        });
        builder.create();
        builder.show();
    }
}
