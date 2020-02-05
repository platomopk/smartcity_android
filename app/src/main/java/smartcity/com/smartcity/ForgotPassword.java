package smartcity.com.smartcity;

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

public class ForgotPassword extends AppCompatActivity {
    Util IP = new Util();
    EditText email;
    Button submit;
    Spinner aType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.email_forgot);
        submit = findViewById(R.id.submit);
        aType = findViewById(R.id.aType_forgot);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    public void forgotPassword(){
        RequestQueue que = Volley.newRequestQueue(ForgotPassword.this);
        String URL = IP.getBaseIP()+"forgotPassword.php?email="+email.getText().toString()+"&aType="+aType.getSelectedItem().toString();
        Log.d("onFocusChange: ",URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("yes")){
                            Toast.makeText(getApplicationContext(),"Check your Inbox/Spam",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Wrong Email",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Check Internet",Toast.LENGTH_SHORT).show();
                //.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        que.add(stringRequest);
    }
}
