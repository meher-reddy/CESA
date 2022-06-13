package com.techster_media.cesa;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static com.techster_media.cesa.variables.userID;

public class GeocoderHandlerr extends Handler {
    String pincode, locality;
    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("locations");
    @Override
    public void handleMessage(Message message) {

        if (message.what == 1) {
            Bundle bundle = message.getData();
            pincode = bundle.getString("pincodegeo");
            locality = bundle.getString("localitygeo");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot3) {
                    if (snapshot3.child("pincodes/" + pincode).child("i").exists()) {
                        if (snapshot3.child("pincodes/" + pincode).child(userID).exists()) {
                            Log.d(TAG, "onDataChange: OK-already-there");
                        } else {

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child("lastlocation").child("pincode").exists()) {
                                        String temppincode = snapshot.child("lastlocation").child("pincode").getValue().toString();
                                        db.child("pincodes").child(temppincode).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        db.child("pincodes").child(temppincode).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int m = Integer.parseInt(snapshot.child("i").getValue().toString());
                                                m--;
                                                db.child("pincodes").child(temppincode).child("i").setValue(m);
                                                int i = Integer.parseInt(snapshot3.child("pincodes/" + pincode).child("i").getValue().toString());
                                                db.child("pincodes/" + pincode).child(userID).setValue("OK");
                                                i++;
                                                db.child("pincodes/" + pincode).child("i").setValue(i);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("pincode").setValue(pincode);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else
                                    {
                                        int i = Integer.parseInt(snapshot3.child("pincodes/" + pincode).child("i").getValue().toString());
                                        db.child("pincodes/" + pincode).child(userID).setValue("OK");
                                        i++;
                                        db.child("pincodes/" + pincode).child("i").setValue(i);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("pincode").setValue(pincode);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("lastlocation").child("pincode").exists()) {
                                    String temppincode = snapshot.child("lastlocation").child("pincode").getValue().toString();
                                    db.child("pincodes").child(temppincode).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    db.child("pincodes").child(temppincode).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int m = Integer.parseInt(snapshot.child("i").getValue().toString());
                                            m--;
                                            db.child("pincodes").child(temppincode).child("i").setValue(m);
                                            int h = 0;
                                            db.child("pincodes/" + pincode).child(userID).setValue("OK");
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("pincode").setValue(pincode);
                                            h++;
                                            db.child("pincodes/" + pincode).child("i").setValue(h);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else
                                {
                                    int h = 0;
                                    db.child("pincodes/" + pincode).child(userID).setValue("OK");
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("pincode").setValue(pincode);
                                    h++;
                                    db.child("pincodes/" + pincode).child("i").setValue(h);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                    if (snapshot2.child("regions/" + locality).child("i").exists()) {
                        if (snapshot2.child("regions/" + locality).child(userID).exists()) {
                            Log.d(TAG, "onDataChange: OK-already-there");
                        } else {

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child("lastlocation").child("regions").exists()) {
                                        String templocality = snapshot.child("lastlocation").child("regions").getValue().toString();
                                        db.child("regions").child(templocality).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        db.child("regions").child(templocality).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int n = Integer.parseInt(snapshot.child("i").getValue().toString());
                                                n--;
                                                db.child("regions").child(templocality).child("i").setValue(n);
                                                int j = Integer.parseInt(snapshot2.child("regions/" + locality).child("i").getValue().toString());
                                                db.child("regions/" + locality).child(userID).setValue("OK");
                                                j++;
                                                db.child("regions/" + locality).child("i").setValue(j);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("regions").setValue(locality);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else
                                    {
                                        int j = Integer.parseInt(snapshot2.child("regions/" + locality).child("i").getValue().toString());
                                        db.child("regions/" + locality).child(userID).setValue("OK");
                                        j++;
                                        db.child("regions/" + locality).child("i").setValue(j);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("regions").setValue(locality);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("lastlocation").child("regions").exists()) {
                                    String templocality = snapshot.child("lastlocation").child("regions").getValue().toString();
                                    db.child("regions").child(templocality).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    db.child("regions").child(templocality).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int n = Integer.parseInt(snapshot.child("i").getValue().toString());
                                            n--;
                                            db.child("regions").child(templocality).child("i").setValue(n);
                                            int k = 0;
                                            db.child("regions/" + locality).child(userID).setValue("OK");
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("regions").setValue(locality);
                                            k++;
                                            db.child("regions/" + locality).child("i").setValue(k);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else
                                {
                                    int k = 0;
                                    db.child("regions/" + locality).child(userID).setValue("OK");
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastlocation").child("regions").setValue(locality);
                                    k++;
                                    db.child("regions/" + locality).child("i").setValue(k);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Log.d(TAG, "handleMessage: " + pincode + locality);
        }

    }
}
