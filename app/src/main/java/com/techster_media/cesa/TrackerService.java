package com.techster_media.cesa;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.techster_media.cesa.SendNotificationPack.APIService;
import com.techster_media.cesa.SendNotificationPack.Client;
import com.techster_media.cesa.SendNotificationPack.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.techster_media.cesa.variables.userID;

public class TrackerService extends Service implements SpeechDelegate, Speech.stopDueToDelay {
    private static final String TAG = TrackerService.class.getSimpleName();
    double longitude, latitude;
    Dialog dialog;
    CountDownTimer cTimer;
    String pincode, locality;
    String[] phonenumber = new String[6];
    Map<String, String> pincodes;
    Map<String, String> regions;
    String[] userIDs;
    String[] finaluserIDs;
    int totalcount;
    int countuserspincodes, countusersregions;
    String str_address;
    String linkloc = "https://maps.google.com/?q=";
    public static SpeechDelegate delegate;
    private APIService apiService;
    int count, i = 0;
    String customkey;
    FirebaseAuth fAuth;
    private static final int PERMISSIONS_REQUEST = 1;
    DatabaseReference db;

    @Override
    public void onCreate() {
        super.onCreate();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference();
        ringer();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        db = FirebaseDatabase.getInstance().getReference();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customkeyword").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    customkey = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ringer").setValue("false");
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkperms();
        } else {
            requestLocationUpdates();
        }


    }

    public void ringer() {
        final MediaPlayer siren = MediaPlayer.create(this, R.raw.siren);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ringer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "outer method: " + snapshot.getValue().toString());
                if (snapshot.getValue().toString().equals("true")) {
                    Log.d(TAG, "inner method: " + snapshot.getValue().toString());
                    AudioManager am =
                            (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                   /* am.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                            am.FLAG_SHOW_UI);
                            am.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            am.getStreamMaxVolume(AudioManager.STREAM_RING),
                            0);
                    */
                    siren.start();
                }
                if (snapshot.getValue().toString().equals("false")) {
                    if (siren != null) {
                        siren.stop();
                        siren.prepareAsync();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void checkperms() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
            requestLocationUpdatess();

        }
    }

    private void requestLocationUpdatess() {
        LocationRequest request = new LocationRequest();
        request.setInterval(150000);
        request.setFastestInterval(150000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    GPSTracker gps = new GPSTracker(TrackerService.this);
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
            }, null);
        }

    }


    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + userID);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);
                        ref.child("location").setValue(location);

                    }
                }
            }, null);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean panic = intent.getBooleanExtra("panic", false);
        if (panic)
            panicactivated();
        else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((AudioManager) Objects.requireNonNull(
                            getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Speech.init(this);
            delegate = this;
            Speech.getInstance().setListener(this);

            if (Speech.getInstance().isListening()) {
                Speech.getInstance().stopListening();
            } else {
                System.setProperty("rx.unsafe-disable", "True");
                RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        try {
                            Speech.getInstance().stopTextToSpeech();
                            Speech.getInstance().startListening(null, this);
                        } catch (SpeechRecognitionNotAvailable exc) {
                            //showSpeechNotSupportedDialog();

                        } catch (GoogleVoiceTypingDisabledException exc) {
                            //showEnableGoogleVoiceTyping();
                        }
                    } else {
                        Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d("Result", partial + "");
        }
    }

    @Override
    public void onSpeechResult(String result) {


        Log.d("Result", result + "");
        if (!TextUtils.isEmpty(result)) {
            if (result.contains("help") || result.contains("Bachao") || result.contains("please help me") || result.contains(customkey)||result.contains("Help")||result.contains("bachao")) {
                Toast.makeText(this, "You Said : " + result, Toast.LENGTH_SHORT).show();
                /*dialog  = new Dialog(TrackerService.this);
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
                        panicactivated();
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cTimer.cancel();
                        dialog.dismiss();
                    }
                });*/
                panicactivated();
            }
        }
    }

    public void panicactivated() {
        i = 0;
        Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();
        GPSTracker gps;
        gps = new GPSTracker(TrackerService.this);
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


            db = FirebaseDatabase.getInstance().getReference();
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    count = Integer.parseInt(snapshot.child("Users/" + userID + "/contactscount").getValue().toString());
                    for (int k = 1; k <= count; k++) {
                        phonenumber[k] = snapshot.child("Users/" + userID + "/contacts/" + k + "/phone").getValue().toString();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            RGeocoder RGeocoder = new RGeocoder();
            RGeocoder.getAddressFromLocation(latitude, longitude, getApplicationContext(), new TrackerService.GeocoderHandler());
            Intent intent = new Intent(Intent.ACTION_CALL);
            SQLiteDatabase db2;
            db2 = openOrCreateDatabase("NumberDB", Context.MODE_PRIVATE, null);
            Cursor c=db2.rawQuery("SELECT * FROM details", null);
            while(c.moveToNext())
            {
                String target_ph_number=c.getString(1);
                intent.setData(Uri.parse("tel:" + target_ph_number));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                startActivity(intent);
                break;
            }
            db2.close();


        }
        else{
            db = FirebaseDatabase.getInstance().getReference();
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    count = Integer.parseInt(snapshot.child("Users/" + userID + "/contactscount").getValue().toString());
                    for (int k = 1; k <= count; k++) {
                        phonenumber[k] = snapshot.child("Users/" + userID + "/contacts/" + k + "/phone").getValue().toString();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            RGeocoder RGeocoder = new RGeocoder();
            latitude= longitude= Double.parseDouble(null);
            RGeocoder.getAddressFromLocation(latitude, longitude, getApplicationContext(), new TrackerService.GeocoderHandler());
            Intent intent = new Intent(Intent.ACTION_CALL);
            SQLiteDatabase db2;
            db2 = openOrCreateDatabase("NumberDB", Context.MODE_PRIVATE, null);
            Cursor c=db2.rawQuery("SELECT * FROM details", null);
            while(c.moveToNext())
            {
                String target_ph_number=c.getString(1);
                intent.setData(Uri.parse("tel:" + target_ph_number));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                startActivity(intent);
                gps.showSettingsAlert();
                break;
            }
            db2.close();
        }


    }
    public class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            Toast.makeText(getApplicationContext(), "tracker service geocoderhandler started", Toast.LENGTH_SHORT).show();


            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    pincode = bundle.getString("pincodegeo");
                    locality = bundle.getString("localitygeo");
                    linkloc = "https://maps.google.com/?q=" + latitude+","+longitude;
                    SQLiteDatabase db2;
                    db2 = openOrCreateDatabase("NumberDB", Context.MODE_PRIVATE, null);
                    Cursor c=db2.rawQuery("SELECT * FROM details", null);
                    while(c.moveToNext())
                    {
                        String target_ph_number=c.getString(1);
                        Log.d(TAG, "handleMessage: "+target_ph_number);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("+91"+target_ph_number, null, "I'm in danger this is my last location please respond ASAP", null, null);
                        smsManager.sendTextMessage("+91"+target_ph_number, null, "location link: " + linkloc, null, null);
                        Toast.makeText(getApplicationContext(), "Target:" + target_ph_number, Toast.LENGTH_SHORT).show();
                    }
                    db2.close();
                    db = FirebaseDatabase.getInstance().getReference();
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d(TAG, "locality"+locality);
                            Log.d(TAG, "pincode "+pincode);
                            if(snapshot.child("locations/pincodes/" +pincode).child("i").exists()) {
                                countuserspincodes = Integer.parseInt(snapshot.child("locations/pincodes/" + pincode).child("i").getValue().toString());
                                pincodes = (Map<String, String>) snapshot.child("locations").child("pincodes/" +pincode).getValue();
                            }
                            else countuserspincodes = 0;
                            //set if exists and set regions to zero
                            if(snapshot.child("locations/regions/" +locality).child("i").exists()) {
                                countusersregions = Integer.parseInt(snapshot.child("locations/regions/" + locality).child("i").getValue().toString());
                                regions = (Map<String, String>) snapshot.child("locations").child("regions/" +locality).getValue();
                            }
                            else countusersregions = 0;
                            totalcount = countuserspincodes+countusersregions;

                            userIDs= new String[totalcount];

                            if(snapshot.child("locations/pincodes/" +pincode).exists()) {
                                for (Map.Entry<String, String> entry : pincodes.entrySet()) {
                                    double userlatitude, userlongitude;
                                    if (String.valueOf(entry.getKey()).equals("i")) {
                                        continue;
                                    } else {
                                        String userlat = snapshot.child("Users/" + entry.getKey() + "/inaccloc/latitude").getValue().toString();
                                        String userlong = snapshot.child("Users/" + entry.getKey() + "/inaccloc/longitude").getValue().toString();
                                        userlatitude = Double.parseDouble(userlat);
                                        userlongitude = Double.parseDouble(userlong);
                                        if (distance(latitude, longitude, userlatitude, userlongitude) < 32.1) {
                                            try {
                                                userIDs[i] = entry.getKey();
                                                Log.d("Number is::::::::::::::", "user id###############################-pincodes" + userIDs[i]);
                                                i++;
                                            }
                                            catch (ArrayIndexOutOfBoundsException e)
                                            {
                                                Log.d(TAG, "onDataChange: "+e);
                                            }
                                        }
                                    }
                                }
                            }

                            if(snapshot.child("locations/regions/" +locality).exists()) {
                                for (Map.Entry<String, String> entry : regions.entrySet()) {
                                    double userlatitude, userlongitude;
                                    if (String.valueOf(entry.getKey()).equals("i")) {
                                        continue;
                                    } else {
                                        String userlat = snapshot.child("Users/" + entry.getKey() + "/inaccloc/latitude").getValue().toString();
                                        String userlong = snapshot.child("Users/" + entry.getKey() + "/inaccloc/longitude").getValue().toString();
                                        userlatitude = Double.parseDouble(userlat);
                                        userlongitude = Double.parseDouble(userlong);
                                        if (distance(latitude, longitude, userlatitude, userlongitude) < 32.1) {
                                            try {
                                                userIDs[i] = entry.getKey();
                                                Log.d("Number is::::::::::::::", "user id###############################-regions" + userIDs[i]);
                                                i++;
                                            }
                                            catch (ArrayIndexOutOfBoundsException e)
                                            {
                                                Log.d(TAG, "onDataChange: "+e);
                                            }
                                        }
                                    }
                                }
                            }
                            LinkedHashSet<String> lhSetColors =
                                    new LinkedHashSet<String>(Arrays.asList(userIDs));
                            finaluserIDs = lhSetColors.toArray(new String[ lhSetColors.size() ]);
                            for( int j = 0; j<finaluserIDs.length; j++) {
                                try {
                                    Log.d("Number is::::::::::::::", "user id###############################-notification" + finaluserIDs[j]);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(finaluserIDs[j]).child("token/token/token").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String usertoken = snapshot.getValue(String.class);
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String name = snapshot.getValue().toString();
                                                    sendNotifications(usertoken, "Alert", name+" is in danger.", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                catch (NullPointerException e)
                                {
                                    Log.d(TAG, "onDataChange: "+e);
                                }
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
                default:
                    str_address = null;
            }
            Toast.makeText(getApplicationContext(), str_address, Toast.LENGTH_SHORT).show();

        }
    }


    public void sendNotifications(String usertoken, String title, String message,String uid) {
        Data data = new Data(title, message, uid);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(TrackerService.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
        }
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), TrackerService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }
}
