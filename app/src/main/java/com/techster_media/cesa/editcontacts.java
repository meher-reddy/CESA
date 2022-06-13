package com.techster_media.cesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techster_media.cesa.variables.userID;

public class editcontacts extends AppCompatActivity {

    DatabaseReference db;
    EditText[] names = new EditText[6];
    EditText[] contacts = new EditText[6];
    Button[] delete = new Button[6];
    int contactscount;
    String[] phone = new String[6];
    String[] name = new String[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcontacts);
        for(int i=1; i<=5;i++)
        {
            String contactTextView = "contact" + i;
            String nameTextView = "name"+i;
            String deleteTextView = "delete"+i;
            int contactsresID = getResources().getIdentifier(contactTextView, "id", getPackageName());
            int namesresID = getResources().getIdentifier(nameTextView, "id", getPackageName());
            int deleteresID = getResources().getIdentifier(deleteTextView, "id", getPackageName());
            contacts[i] = findViewById(contactsresID);
            names[i]    = findViewById(namesresID);
            delete[i] = findViewById(deleteresID);

        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactscount = Integer.parseInt(snapshot.child("contactscount").getValue().toString());
                for(int i=1;i<=contactscount;i++) {
                    contacts[i].setText(snapshot.child("contacts/"+i+"/phone").getValue().toString());
                    names[i].setText(snapshot.child("contacts/"+i+"/name").getValue().toString());
                    name[i]=names[i].getText().toString();
                    phone[i] = contacts[i].getText().toString();
                    contacts[i].setVisibility(View.VISIBLE);
                    names[i].setVisibility(View.VISIBLE);
                }
                if(contactscount>0) {
                    delete[contactscount].setVisibility(View.VISIBLE);
                    delete[contactscount].setClickable(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void submit(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactscount = Integer.parseInt(snapshot.child("contactscount").getValue().toString());
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts");
                for(int i =1;i<=contactscount;i++)
                {
                    if(contacts[i].length()==10) {
                        sqldata sql = new sqldata();
                        sql.mysqlmod(contacts[i].getText().toString(),names[i].getText().toString(),name[i],phone[i],editcontacts.this);
                        db.child(i + "/phone").setValue(contacts[i].getText().toString());
                        db.child(i + "/name").setValue(names[i].getText().toString());
                    }
                    else if(contacts[i].length()!=10)
                    {
                        contacts[i].setError("Phone Number must be 10 digits without +91");
                    }
                }
                Toast.makeText(editcontacts.this, "contacts changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, viewcontacts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void editcontactsback(View view)
    {
        startActivity(new Intent(getApplicationContext(),viewcontacts.class));
    }
    public void delete1(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(1+"").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactscount").setValue(0);
        sqldata sql = new sqldata();
        sql.mysqldel(phone[1],this);
        contacts[1].setText("");
        contacts[1].setVisibility(View.GONE);
        names[1].setText("");
        names[1].setVisibility(View.GONE);
        delete[1].setVisibility(View.GONE);
        delete[1].setClickable(false);
    }
    public void delete2(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(2+"").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactscount").setValue(1);
        sqldata sql = new sqldata();
        sql.mysqldel(phone[2],this);
        contacts[2].setText("");
        contacts[2].setVisibility(View.GONE);
        names[2].setText("");
        names[2].setVisibility(View.GONE);
        delete[1].setClickable(true);
        delete[1].setVisibility(View.VISIBLE);
        delete[2].setVisibility(View.GONE);
        delete[2].setClickable(false);
    }
    public void delete3(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(3+"").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactscount").setValue(2);
        sqldata sql = new sqldata();
        sql.mysqldel(phone[3],this);
        contacts[3].setText("");
        contacts[3].setVisibility(View.GONE);
        names[3].setText("");
        names[3].setVisibility(View.GONE);
        delete[2].setClickable(true);
        delete[2].setVisibility(View.VISIBLE);
        delete[3].setVisibility(View.GONE);
        delete[3].setClickable(false);
    }
    public void delete4(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(4+"").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactscount").setValue(3);
        sqldata sql = new sqldata();
        sql.mysqldel(phone[4],this);
        contacts[4].setText("");
        contacts[4].setVisibility(View.GONE);
        names[4].setText("");
        names[4].setVisibility(View.GONE);
        delete[3].setClickable(true);
        delete[3].setVisibility(View.VISIBLE);
        delete[4].setVisibility(View.GONE);
        delete[4].setClickable(false);
    }
    public void delete5(View view)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts").child(5+"").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactscount").setValue(4);
        sqldata sql = new sqldata();
        sql.mysqldel(phone[5],this);
        contacts[5].setText("");
        contacts[5].setVisibility(View.GONE);
        names[5].setText("");
        names[5].setVisibility(View.GONE);
        delete[4].setClickable(true);
        delete[4].setVisibility(View.VISIBLE);
        delete[5].setVisibility(View.GONE);
        delete[5].setClickable(false);
    }

}