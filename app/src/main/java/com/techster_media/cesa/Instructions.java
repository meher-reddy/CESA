package com.techster_media.cesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Instructions extends AppCompatActivity {

    CheckBox a,b,c,d,e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        a = findViewById(R.id.checkBox1);
        b = findViewById(R.id.checkBox2);
        c = findViewById(R.id.checkBox3);
        d = findViewById(R.id.checkBox4);
        e = findViewById(R.id.checkBox5);
        a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    a.setTextColor(Color.parseColor("#80FF00"));
                }
                if(!isChecked)
                {
                    a.setTextColor(Color.parseColor("#F44336"));
                }
            }
        });
        b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    b.setTextColor(Color.parseColor("#80FF00"));
                }
                if(!isChecked)
                {
                    b.setTextColor(Color.parseColor("#F44336"));
                }
            }
        });
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    c.setTextColor(Color.parseColor("#80FF00"));
                }
                if(!isChecked)
                {
                    c.setTextColor(Color.parseColor("#F44336"));
                }
            }
        });
        d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    d.setTextColor(Color.parseColor("#80FF00"));
                }
                if(!isChecked)
                {
                    d.setTextColor(Color.parseColor("#F44336"));
                }
            }
        });
        e.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    e.setTextColor(Color.parseColor("#80FF00"));
                }
                if(!isChecked)
                {
                    e.setTextColor(Color.parseColor("#F44336"));
                }
            }
        });

    }
    public void next(View view)
    {
        if(a.isChecked()&&b.isChecked()&&c.isChecked()&&d.isChecked()&&e.isChecked()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("IsNew").removeValue();
            startActivity(new Intent(getApplicationContext(), reasons.class));
        }
        else
        {
            if(!a.isChecked())
                a.setError("Please check to continue");
            if(!b.isChecked())
                b.setError("Please check to continue");
            if(!c.isChecked())
                c.setError("Please check to continue");
            if(!d.isChecked())
                d.setError("Please check to continue");
            if(!e.isChecked())
                e.setError("Please check to continue");
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please check all boxes and press I Agree and proceed button to Continue",Toast.LENGTH_SHORT).show();
    }
}