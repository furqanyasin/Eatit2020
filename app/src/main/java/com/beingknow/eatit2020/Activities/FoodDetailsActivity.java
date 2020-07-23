package com.beingknow.eatit2020.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Models.Food;
import com.beingknow.eatit2020.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetailsActivity extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    FirebaseDatabase database;
    DatabaseReference food;
    String mFoodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btn_cart);

        food_name = findViewById(R.id.food_name);
        food_description = findViewById(R.id.food_description);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.food_image);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpanededAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance((R.style.CollapsedAppBar));


        //init firebase
        database = FirebaseDatabase.getInstance();
        food = database.getReference("Foods");


        // get intent here
        if (getIntent() != null)
            mFoodId = getIntent().getStringExtra("FoodId");
        if (!mFoodId.isEmpty()){
            getFoodDetails();
        }
    }

    private void getFoodDetails() {
        food.child(mFoodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                Picasso.get().load(food.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(food.getFood());
                food_name.setText(food.getFood());
                food_price.setText(food.getPrice());
                food_description.setText(food.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}