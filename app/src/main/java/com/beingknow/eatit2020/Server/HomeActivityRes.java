package com.beingknow.eatit2020.Server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beingknow.eatit2020.Client.Activities.CartActivity;
import com.beingknow.eatit2020.Client.Activities.FoodListActivity;
import com.beingknow.eatit2020.Client.Activities.HomeActivity;
import com.beingknow.eatit2020.Client.Activities.OrderStatusActivity;
import com.beingknow.eatit2020.Client.Activities.SignInActivity;
import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.Models.Category;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeActivityRes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference category;
    TextView textFullName;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseRecyclerOptions<Category> firebaseRecyclerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_res);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Restaurant Management");
        setSupportActionBar(toolbar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Category>().setQuery(category, Category.class).build();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent  =new Intent(HomeActivityRes.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        final View headerView = navigationView.getHeaderView(0);
        textFullName = headerView.findViewById(R.id.text_full_name);
        textFullName.setText(Common.restaurantUser.getName());

        //Load menu
        recyclerMenu = findViewById(R.id.recyclerview_menu1);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerMenu.setLayoutManager(layoutManager);
        //loadMenu();


    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getImage())
                        .into(holder.menuImage);

                final Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(HomeActivity.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        //Get CategoryId and send to new activity
                     /*   Intent foodList = new Intent(HomeActivityRes.this, FoodListActivity.class);
                        //Because CategoryId is a key, so we just key of this item
                        foodList.putExtra(Common.CATEGORY_ID,adapter.getRef(position).getKey());
                        startActivity(foodList);*/
                    }
                });
            }
        };
        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
          /*  Intent cartIntent = new Intent(HomeActivityRes.this,CartActivity.class);
            startActivity(cartIntent);*/

        } else if (id == R.id.nav_orders) {
           /* Intent orderIntent = new Intent(HomeActivityRes.this, OrderStatusActivity.class);
            startActivity(orderIntent);*/

        } else if (id == R.id.nav_sign_out) {
            Intent signIn = new Intent(HomeActivityRes.this, SignInActivityRes.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}