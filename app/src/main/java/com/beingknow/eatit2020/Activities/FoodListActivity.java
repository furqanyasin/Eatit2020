package com.beingknow.eatit2020.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.Models.Category;
import com.beingknow.eatit2020.Models.Food;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodListActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerViewFood;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerOptions<Food> firebaseRecyclerOptions;
    String categoryId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Foods");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(category, Food.class).build();

        //Load menu
        recyclerViewFood = findViewById(R.id.recycler_view_food_list);
        recyclerViewFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewFood.setLayoutManager(layoutManager);

        // get intent here
        if (getIntent() != null){
            categoryId = getIntent().getStringExtra(Common.CATEGORY_ID);
            loadFood();
        }
    }

    private void loadFood() {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.txtFoodName.setText(model.getName());
                Picasso.get().load(model.getImage())
                        .into(holder.foodImage);
                final Food clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(HomeActivity.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        //Get CategoryId and send to new activity
                        Intent foodDetails = new Intent(FoodListActivity.this, FoodDetailsActivity.class);
                        //Because foodId is a key, so we just key of this item
                        foodDetails.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });

            }
        };
        //set adapter
        recyclerViewFood.setAdapter(adapter);
        adapter.startListening();
    }

}