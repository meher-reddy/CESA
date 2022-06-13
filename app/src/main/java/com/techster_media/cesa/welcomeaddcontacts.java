package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class welcomeaddcontacts extends AppCompatActivity {

    DatabaseReference db;
    TextView cname,cphone;
    String cName;
    String cPhone;
    FirebaseAuth fAuth;
    int contactscount;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomeaddcontacts);
        next = findViewById(R.id.next);
        next.setClickable(false);
        next.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        cname = findViewById(R.id.cname);
        cphone = findViewById(R.id.cphone);
        db = FirebaseDatabase.getInstance().getReference();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactscount = Integer.parseInt(snapshot.child("Users/"+userID+"/contactscount").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    public void submit(View view)
    {
        if(contactscount<5) {
            if(cphone.length() == 10) {
                contactscount = contactscount + 1;
                cName = cname.getText().toString();
                cPhone = cphone.getText().toString();
                sqldata sql = new sqldata();
                sql.mysqladd(cName,cPhone,this);
                db.child("Users/" + userID + "/contacts/" + contactscount + "/name").setValue(cName);
                db.child("Users/" + userID + "/contacts/" + contactscount + "/phone").setValue(cPhone);
                db.child("Users/" + userID + "/contactscount").setValue(contactscount);
                cname.setText("");
                cphone.setText("");
                Toast.makeText(welcomeaddcontacts.this, "Added", Toast.LENGTH_SHORT).show();
                next.setClickable(true);
                next.setVisibility(View.VISIBLE);
            }
            else if(cphone.length()!=10)
            {
                cphone.setError("Phone Number length must be 10 digits without +91");
            }
        }
        else Toast.makeText(welcomeaddcontacts.this, "Contacts Maxed Out", Toast.LENGTH_LONG).show();
    }
    public void next(View view)
    {
        Intent intent = new Intent(this, customkeyword.class);
        intent.putExtra("new",true);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Add a contact and press proceed to continue!!!",Toast.LENGTH_SHORT).show();
    }
}
