package smartcity.com.smartcity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import smartcity.com.smartcity.util.Util;

public class UpdatePassword extends AppCompatActivity {
    Util IP = new Util();
    Button update;
    EditText oldpass, newpass, newpass2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String oldPassword, newPassword, username, aType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        update = findViewById(R.id.update);
        newpass = findViewById(R.id.newPass);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        newpass2 = findViewById(R.id.newPass2);
        oldpass = findViewById(R.id.oldpass);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        oldPassword = pref.getString("Password", null);
        username = pref.getString("UName", null);
        aType = pref.getString("AType", null);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newpass.getText().toString().equals(newpass2.getText().toString())) {
                    if (oldPassword.equals(oldpass.getText().toString())) {
                        newPassword = newpass.getText().toString();
                        RequestQueue queue = Volley.newRequestQueue(UpdatePassword.this);
                        String URL = IP.getBaseIP() + "updatePassword.php?username=" + username + "&newPassword=" + newPassword + "&aType=" + aType;

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.contains("Yes")) {
                                            editor.putString("Password",newPassword);
                                            editor.apply();
                                            Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("onResponse: ", "Failed");
                            }
                        });
                        queue.add(stringRequest);

                    }else {Toast.makeText(getApplicationContext(),"Old Password is wrong",Toast.LENGTH_SHORT).show();}
                }else {Toast.makeText(getApplicationContext(),"New Passwords don't match",Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
