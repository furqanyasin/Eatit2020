package com.beingknow.eatit2020.Server.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Models.RestaurantUser;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.databinding.ActivitySignInResBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivityRes extends AppCompatActivity implements View.OnClickListener {

    public ActivitySignInResBinding activitySignInResBinding;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInResBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in_res);

        activitySignInResBinding.btnSignIn.setOnClickListener(this);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");


    }

    @Override
    public void onClick(View view) {
        if (view == activitySignInResBinding.btnSignIn) {
            //buttonSignIn();
            buttonSignIn(activitySignInResBinding.etPhoneNumber.getText().toString(), activitySignInResBinding.etPassword.getText().toString());
        }

    }

    private void buttonSignIn(String Phone, String Password) {



        final ProgressDialog mDialog = new ProgressDialog(SignInActivityRes.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String LocPhone =Phone;
        final String LocPassword = Password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check if user not exit in database
                if (dataSnapshot.child(LocPhone).exists()) {
                    //Get User Information
                    mDialog.dismiss();
                    RestaurantUser user = dataSnapshot.child(LocPhone).getValue(RestaurantUser.class);
                    user.setPhone(LocPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())) {
                        if (user.getPassword().equals(LocPassword)) {
                            Toast.makeText(SignInActivityRes.this, "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivityRes.this, HomeActivityRes.class);
                            Common.restaurantUser = user;
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(SignInActivityRes.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(SignInActivityRes.this, "Please Login with Staff Account", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignInActivityRes.this, "User not exists ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}