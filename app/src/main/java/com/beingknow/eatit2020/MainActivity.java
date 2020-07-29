package com.beingknow.eatit2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.beingknow.eatit2020.Client.Activities.SignInActivity;
import com.beingknow.eatit2020.Client.Activities.SignUpActivity;
import com.beingknow.eatit2020.Server.Activities.SignInActivityRes;
import com.beingknow.eatit2020.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        activityMainBinding.txtSlogan.setTypeface(typeface);

        // set on click listener
        activityMainBinding.btnSignIn.setOnClickListener(this);
        activityMainBinding.btnSignUp.setOnClickListener(this);
        activityMainBinding.btnSignInRestaurant.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == activityMainBinding.btnSignIn) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);

        }
        if (view == activityMainBinding.btnSignUp) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        if (view == activityMainBinding.btnSignInRestaurant) {
            Intent intent = new Intent(this, SignInActivityRes.class);
            startActivity(intent);
        }

    }
}