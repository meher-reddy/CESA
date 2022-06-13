package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.ktx.Firebase;
import com.techster_media.cesa.SendNotificationPack.*;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static com.techster_media.cesa.variables.userID;

public class home extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    DatabaseReference db;
    FirebaseAuth fAuth;
    Button panicbutton;
    CountDownTimer cTimer;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseDatabase.getInstance().getReference().child("locations");
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        panicbutton = findViewById(R.id.panicb);
        UpdateToken();
        if(checkSelfPermission()) {
            Toast.makeText(getApplicationContext(), "Ready to go!!!", Toast.LENGTH_SHORT).show();
            updatelocinacc();
        }
        else if(!checkSelfPermission())
            requestPermission();
        Intent intent = new Intent(this, TrackerService.class);
        intent.putExtra("panic",false);
        startService(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }
    public void updatelocinacc()
    {
        GPSTracker gps = new GPSTracker(home.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("inaccloc").child("longitude").setValue(longitude);
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("inaccloc").child("latitude").setValue(latitude);
            RGeocoder RGeocoder = new RGeocoder();
            RGeocoder.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandlerr());
        } else {
            gps.showSettingsAlert();
        }
    }
    private boolean checkSelfPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_BACKGROUND_LOCATION);
        }
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED || result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION, RECORD_AUDIO,CALL_PHONE,SEND_SMS}, PERMISSION_REQUEST_CODE);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, RECORD_AUDIO, CALL_PHONE, SEND_SMS}, PERMISSION_REQUEST_CODE);
        }
        updatelocinacc();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean backgroundaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean microphoneaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean smsaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;


                    if (locationAccepted && backgroundaccepted && microphoneaccepted && phoneaccepted && smsaccepted)
                        Toast.makeText(getApplicationContext(), "All permissions granted!!!", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(getApplicationContext(), "You need to grant all permissions", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        (dialog, which) -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION, RECORD_AUDIO,CALL_PHONE,SEND_SMS},
                                                        PERMISSION_REQUEST_CODE);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,  ACCESS_BACKGROUND_LOCATION, RECORD_AUDIO,CALL_PHONE,SEND_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new Builder(home.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void panic(View view)
    {
        Toast.makeText(getApplicationContext(), "Panic Button pressed Please Wait...", Toast.LENGTH_SHORT).show();
        dialog  = new Dialog(home.this);
        dialog.setContentView(R.layout.confirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button confirm = dialog.findViewById(R.id.confirm);
        Button cancel = dialog.findViewById(R.id.cancel);
        dialog.show();
        cTimer = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                confirm.setText("Confirm("+seconds+")");
            }
            public void onFinish() {
                confirm.performClick();
                dialog.dismiss();
            }
        };
        cTimer.start();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackerService.class);
                intent.putExtra("panic",true);
                startService(intent);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cTimer.cancel();
                dialog.dismiss();
            }
        });

    }
    public void profile(View view)
    {
        startActivity(new Intent(getApplicationContext(),profile.class));

    }
    public void contacts(View view)
    {
        startActivity(new Intent(getApplicationContext(),viewcontacts.class));
    }

    private void UpdateToken(){
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token/token").setValue(token);
    }

    public void lasteme(View view)
    {
        startActivity(new Intent(getApplicationContext(),track.class));
    }
}
