package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techster_media.cesa.variables.userID;

public class viewcontacts extends AppCompatActivity {

    DatabaseReference db;
    FirebaseAuth fAuth;
    TextView[] contacts = new TextView[6];
    TextView[] names = new TextView[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontacts);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        for(int i=1; i<=5;i++) {
            String contactTextView = "contact" + i;
            String nameTextView = "name" + i;
            int contactsresID = getResources().getIdentifier(contactTextView, "id", getPackageName());
            int namesresID = getResources().getIdentifier(nameTextView, "id", getPackageName());
            contacts[i] = findViewById(contactsresID);
            names[i] = findViewById(namesresID);
        }
            SQLiteDatabase db2;
            db2 = openOrCreateDatabase("NumberDB", Context.MODE_PRIVATE, null);
            Cursor c=db2.rawQuery("SELECT * FROM details", null);
            int j=1;
            while(c.moveToNext())
            {
                contacts[j].setText(c.getString(1));
                names[j].setText(c.getString(0));
                contacts[j].setVisibility(View.VISIBLE);
                names[j].setVisibility(View.VISIBLE);
                j++;
            }
            db2.close();

        db = FirebaseDatabase.getInstance().getReference();

            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = Integer.parseInt(snapshot.child("Users/"+userID+"/contactscount").getValue().toString());
                    for(int i=1;i<=count;i++) {
                        contacts[i].setText(snapshot.child("Users/"+userID+"/contacts/"+i+"/phone").getValue().toString());
                        names[i].setText(snapshot.child("Users/"+userID+"/contacts/"+i+"/name").getValue().toString());
                        contacts[i].setVisibility(View.VISIBLE);
                        names[i].setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }
    public void viewcontactsback(View view)
    {
        startActivity(new Intent(getApplicationContext(),home.class));
    }
    public void refresh(View view)
    {
        finish();
        startActivity(getIntent());
    }
    public void addcontacts(View view)
    {
        startActivity(new Intent(getApplicationContext(),contacts.class));
    }
    public void editcontactsview(View view)
    {
        startActivity(new Intent(getApplicationContext(),editcontacts.class));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
