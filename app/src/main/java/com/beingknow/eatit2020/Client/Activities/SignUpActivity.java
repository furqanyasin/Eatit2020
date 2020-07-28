package com.beingknow.eatit2020.Client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beingknow.eatit2020.Models.ClientUsers;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignUpBinding activitySignUpBinding;
    String phoneNumber, name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        activitySignUpBinding.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == activitySignUpBinding.btnSignUp) {
            buttonSignUp();
        }

    }

    public void buttonSignUp() {

        phoneNumber = activitySignUpBinding.etPhoneNumber.getText().toString();
        password = activitySignUpBinding.etPassword.getText().toString();
        name = activitySignUpBinding.etName.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = database.getReference("users");

        final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        table_users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user not exit in database
                if (dataSnapshot.child(phoneNumber).exists()) {
                    //Get User Information
                    mDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Phone Number already exists", Toast.LENGTH_SHORT).show();

                } else {
                    mDialog.dismiss();
                    ClientUsers clientUsers = new ClientUsers(name, password);
                    table_users.child(phoneNumber).setValue(clientUsers);
                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}