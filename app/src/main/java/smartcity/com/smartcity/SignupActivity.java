package smartcity.com.smartcity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    EditText fname,lname,email,username,passsword,city,phone;
    Spinner country, code;
    Spinner day,month,year;
    Button next;
    EditText dobview;
    RadioButton male,female;
    String sex,dob;
    String mobilenumber;
    ImageView imageViewC;
    AlertDialog ad=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
//        dobview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showCalendar();
//            }
//        });

        fname = findViewById(R.id.fname_signup);
        lname = findViewById(R.id.lname_signup);
        email = findViewById(R.id.email_signup);
        username = findViewById(R.id.username_signup);
        passsword = findViewById(R.id.password_signup);
        country = findViewById(R.id.country_signup);
        city = findViewById(R.id.city_signup);
        code= findViewById(R.id.code_signup);
        phone= findViewById(R.id.number_signup);
        next = findViewById(R.id.next_signup);
        male = findViewById(R.id.male_signup);
        female = findViewById(R.id.female_signup);
       // day =  findViewById(R.id.day_signup);
       // month =  findViewById(R.id.month_signup);
        // =  findViewById(R.id.year_signup);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fname.getText().toString().length()>0 && lname.getText().toString().length()>0 && email.getText().toString().length()>0&& username.getText().toString().length()>0&& passsword.getText().toString().length()>0 && city.getText().toString().length()>0 && phone.getText().toString().length()>0){
                    mobilenumber = code.getSelectedItem().toString()+phone.getText().toString();
                    if (male.isChecked()){
                        sex= "Male";
                    }
                    else sex= "Female";
                    dob = dobview.getText().toString();//day.getSelectedItem().toString()+"-"+month.getSelectedItem().toString()+"-"+year.getSelectedItem();
                    Intent intent= new Intent(SignupActivity.this,DrivingDetails.class);
                    intent.putExtra("fname",fname.getText().toString());
                    intent.putExtra("lname",lname.getText().toString());
                    intent.putExtra("email",email.getText().toString());
                    intent.putExtra("username",username.getText().toString());
                    intent.putExtra("passsword",passsword.getText().toString());
                    intent.putExtra("country",country.getSelectedItem().toString());
                    intent.putExtra("city",city.getText().toString());
                    intent.putExtra("gender",sex);
                    intent.putExtra("dob",dob);
                    intent.putExtra("number",mobilenumber);
                    startActivity(intent);

                }
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
