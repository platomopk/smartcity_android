package smartcity.com.smartcity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Splash extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    SharedPreferences pref;
    Boolean login = false;
    String aType;
    boolean ride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            int secondsDelayed = 3;
            login = pref.getBoolean("login", false);
            aType = pref.getString("AType", "Driver");
            final int SPLASH_DISPLAY_LENGTH = 1000;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (login) {
                        if (aType.equals("Admin")) {
                            startActivity(new Intent(Splash.this, Admin.class));
                            finish();
                        } else {
                            startActivity(new Intent(Splash.this, RideStartActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(Splash.this, LoginActivity.class));
                        finish();
                    }
                }
            }, secondsDelayed * SPLASH_DISPLAY_LENGTH);

        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your SMS for Pin Verification",
                    123,
                    Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (login) {
                if (aType.equals("Admin")) {
                    startActivity(new Intent(Splash.this, Admin.class));
                    finish();
                } else {
                    startActivity(new Intent(Splash.this, RideStartActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(Splash.this, LoginActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (login) {
            if (aType.equals("Admin")) {
                startActivity(new Intent(Splash.this, Admin.class));
                finish();
            } else {
                startActivity(new Intent(Splash.this, RideStartActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        if (login) {
            if (aType.equals("Admin")) {
                startActivity(new Intent(Splash.this, Admin.class));
                finish();
            } else {
                startActivity(new Intent(Splash.this, RideStartActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }

    }
}
