package com.techster_media.cesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class customkeyword extends AppCompatActivity {

    EditText customkeyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customkeyword);
        customkeyword = findViewById(R.id.customkeyword);

    }
    public void next(View view)
    {
        if(!customkeyword.getText().toString().equals(" ") || !customkeyword.getText().toString().equals(""))
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customkeyword").setValue(customkeyword.getText().toString());
            Boolean isnew = getIntent().getBooleanExtra("new",false);
            if(isnew)
            startActivity(new Intent(getApplicationContext(), Instructions.class));
            else
                startActivity(new Intent(getApplicationContext(), home.class));
        }
        else
        {
            customkeyword.setError("Please Enter Your Custom Keyword");
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please enter your a word and press next!!!",Toast.LENGTH_SHORT).show();
    }
}