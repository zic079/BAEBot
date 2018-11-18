package com.sponge.baebot;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

@IgnoreExtraProperties
public class User {


    public String username;
    public String email;
    // See the database schema for knowing what the idx is for each options.
    //public boolean[] settings = new boolean[6];
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;

        //boolean[] defaultSetting = new boolean [6];
        //this.settings = defaultSetting;
    }


    public void writeUserToDB(String userId, String name, String email) {
//        DatabaseReference userRef = database.getReference("users");
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (user == null){
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });

            User user = new User(name, email);
            mDatabase.child("users").child(userId).setValue(user);
    }

}
