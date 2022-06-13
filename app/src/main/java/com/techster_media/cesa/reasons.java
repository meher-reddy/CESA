package com.techster_media.cesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class reasons extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reasons);

    }
    public void proceed(View view)
    {
        startActivity(new Intent(getApplicationContext(), home.class));
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please press I ACKNOWLEDGE to continue",Toast.LENGTH_SHORT).show();
    }
}