package com.techster_media.cesa;

import android.content.Intent;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import static com.techster_media.cesa.variables.email;
import static com.techster_media.cesa.variables.fullName;
import static com.techster_media.cesa.variables.userID;

public class signup extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName,mEmail;
    Button mRegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    DatabaseReference db;
    FirebaseFirestore fStore;
    String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        db = FirebaseDatabase.getInstance().getReference();
        mFullName   = findViewById(R.id.fullName);
        mEmail      = findViewById(R.id.Email);
        mRegisterBtn= findViewById(R.id.registerBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        Phone = getIntent().getStringExtra("phone");


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                fullName = mFullName.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);


                // register the user in firebase


                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("fName", fullName);
                user.put("email", email);
                user.put("phone",Phone);
                db.child("Users/" + userID + "/contactscount").setValue(0);
                db.child("Users/" + userID + "/id").setValue(userID);
                db.child("Users/" + userID + "/name").setValue(fullName);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(signup.this, "User Created.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),welcomeaddcontacts.class));
                        Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            }
        });


    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Complete registration Press Register to continue",Toast.LENGTH_SHORT).show();
    }
}
