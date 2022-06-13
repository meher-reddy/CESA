package com.techster_media.cesa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techster_media.cesa.variables.userID;

public class contacts extends AppCompatActivity {
    DatabaseReference db;
    TextView cname,cphone;
    String cName;
    String cPhone;
    FirebaseAuth fAuth;
    boolean boooo = false;
    int contactscount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        cname = findViewById(R.id.cname);
        cphone = findViewById(R.id.cphone);
        db = FirebaseDatabase.getInstance().getReference();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactscount = Integer.parseInt(snapshot.child("Users/"+userID+"/contactscount").getValue().toString());
                boooo=true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }
    public void addcontactsBACK(View view)
    {
        startActivity(new Intent(getApplicationContext(),viewcontacts.class));

    }
    public void submit(View view)
    {
        if(boooo) {
            if (contactscount < 5) {
                if (cphone.length() == 10) {
                    contactscount = contactscount + 1;
                    cName = cname.getText().toString();
                    cPhone = cphone.getText().toString();
                    db.child("Users/" + userID + "/contacts/" + contactscount + "/name").setValue(cName);
                    db.child("Users/" + userID + "/contacts/" + contactscount + "/phone").setValue(cPhone);
                    db.child("Users/" + userID + "/contactscount").setValue(contactscount);
                    sqldata sql = new sqldata();
                    sql.mysqladd(cName, cPhone, this);
                    cname.setText("");
                    cphone.setText("");
                    Toast.makeText(contacts.this, "Added", Toast.LENGTH_SHORT).show();
                } else if (cphone.length() != 10) cphone.setError("Phone Number length must be 10 digits without +91");
            } else Toast.makeText(contacts.this, "Contacts Maxed Out", Toast.LENGTH_LONG).show();
        } else Toast.makeText(contacts.this, "Please have a proper internet connection and try again.", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, viewcontacts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
