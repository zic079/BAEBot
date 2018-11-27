package com.sponge.baebot;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User implements Parcelable {


    private String username;
    private String email;
    // See the database schema for knowing what the idx is for each options.
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(Parcel parcel) {
        username = parcel.readString();
        email = parcel.readString();
    }

    protected User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public String getName(){
        return this.username;
    }

    public String getEmail(){
        return this.email;
    }

    public void writeUserToDB( String id, User user) {
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
//        })
            mDatabase.child("users").child(id).setValue(user);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
    }

    @Override
    public int describeContents(){
        return 0;
    }

}
