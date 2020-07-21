package com.beingknow.eatit2020.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beingknow.eatit2020.Models.Users;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.databinding.ActivitySignInBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    public ActivitySignInBinding activitySignInBinding;
    String phoneNumber, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        activitySignInBinding.btnSignIn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == activitySignInBinding.btnSignIn) {
            buttonSignIn();
        }

    }

    public void buttonSignIn() {

        phoneNumber = activitySignInBinding.etPhoneNumber.getText().toString();
        password = activitySignInBinding.etPassword.getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = database.getReference("users");


        final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        table_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check if user not exit in database
                if (dataSnapshot.child(activitySignInBinding.etPhoneNumber.getText().toString()).exists()) {
                    //Get User Information
                    mDialog.dismiss();
                    Users users = dataSnapshot.child(activitySignInBinding.etPhoneNumber.getText().toString()).getValue(Users.class);
                    if (users.getPassword().equals(activitySignInBinding.etPassword.getText().toString())) {
                        Toast.makeText(SignInActivity.this, "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignInActivity.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "User not exists ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}