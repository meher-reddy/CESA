package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.techster_media.cesa.SendNotificationPack.MyFireBaseMessagingService;

import java.util.Map;

import io.grpc.Server;

public class track extends AppCompatActivity {

    Map<String, Long> pincodes;
    long timestamp;
    TextView textView17;
    String[] uids=new String[5];
    int uidstore,i,j;
    TextView[] tw = new TextView[5] ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        textView17 = findViewById(R.id.textView17);
        for(int i=0; i<5;i++)
        {
            String contactTextView = "textVie" + i;
            int contactsresID = getResources().getIdentifier(contactTextView, "id", getPackageName());
            tw[i] = findViewById(contactsresID);
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("timestamp").setValue(ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timestamp = Long.parseLong(snapshot.child("timestamp").getValue().toString());
                if(snapshot.child("notificationalert").exists()) {
                    pincodes = (Map<String, Long>) snapshot.child("notificationalert").getValue();
                    for (Map.Entry<String, Long> entry : pincodes.entrySet()) {
                        if (timestamp - entry.getValue() > 10000000)
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationalert").child(entry.getKey()).removeValue();
                        else {
                            if (uidstore < 5) {
                                uids[uidstore] = entry.getKey();
                                textView17.setVisibility(View.GONE);
                                tw[uidstore].setVisibility(View.VISIBLE);
                                tw[uidstore].setClickable(true);
                                uidstore = uidstore + 1;


                            } else break;
                        }
                    }
                    for (i = 0; i < uidstore; i++) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uids[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tw[j].setText(snapshot.child("name").getValue().toString());
                                j++;

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void a(View v)
    {
        Intent intent = new Intent(track.this, viewlocation.class);
        intent.putExtra("userid",uids[0]);
        startActivity(intent);
    }
    public void b(View v)
    {
        Intent intent = new Intent(track.this, viewlocation.class);
        intent.putExtra("userid",uids[1]);
        startActivity(intent);
    }
    public void c(View v)
    {
        Intent intent = new Intent(track.this, viewlocation.class);
        intent.putExtra("userid",uids[2]);
        startActivity(intent);
    }
    public void d(View v)
    {
        Intent intent = new Intent(track.this, viewlocation.class);
        intent.putExtra("userid",uids[3]);
        startActivity(intent);
    }
    public void e(View v)
    {
        Intent intent = new Intent(track.this, viewlocation.class);
        intent.putExtra("userid",uids[4]);
        startActivity(intent);
    }
}
