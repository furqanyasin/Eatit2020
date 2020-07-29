package com.beingknow.eatit2020.Server.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beingknow.eatit2020.Client.Activities.CartActivity;
import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.Models.Category;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class HomeActivityRes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextView textFullName;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseRecyclerOptions<Category> firebaseRecyclerOptions;
    Category newCategory;
    Uri saveUri;

    private final int PICK_IMAGE_REQUEST = 71;

    TextInputEditText restaurantName;
    MaterialButton btnSelect, btnUpload;
    DrawerLayout drawer;


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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Category>().setQuery(category, Category.class).build();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        final View headerView = navigationView.getHeaderView(0);
        textFullName = headerView.findViewById(R.id.text_full_name);
        textFullName.setText(Common.restaurantUser.getName());

        setRecyclerMenu();

    }

    private void setRecyclerMenu() {
        //Load menu
        recyclerMenu = findViewById(R.id.recyclerview_menu1);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerMenu.setLayoutManager(layoutManager);
        loadMenu();

    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivityRes.this);
        alertDialog.setTitle("Add new Restaurant");
        alertDialog.setMessage("Please fill your information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_restaurant = inflater.inflate(R.layout.add_new_restaurant, null);

        restaurantName = add_restaurant.findViewById(R.id.et_name_restaurant);
        btnSelect = add_restaurant.findViewById(R.id.btn_select);
        btnUpload = add_restaurant.findViewById(R.id.btn_upload);

        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImage(); //user select image from gallery & save URI of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(add_restaurant);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set buttons
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //create new category
                if (newCategory != null) {
                    category.push().setValue(newCategory);
                    Snackbar.make(drawer, "New Category" + newCategory.getName() + "was added", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivityRes.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            btnUpload.setText("Uploaded");
                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //set value for new category if image upload and we can get download it
                                            newCategory = new Category(restaurantName.getText().toString(), uri.toString());
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivityRes.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");

                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Selected !");
        }

    }

    private void choseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);
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

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(HomeActivityRes.this,FoodListActivityRes.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
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

    //update or delete Restaurant

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)) {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        category.child(key).removeValue();
        Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivityRes.this);
        alertDialog.setTitle("Update Restaurant");
        alertDialog.setMessage("Please fill your information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_restaurant = inflater.inflate(R.layout.add_new_restaurant, null);

        restaurantName = add_restaurant.findViewById(R.id.et_name_restaurant);
        btnSelect = add_restaurant.findViewById(R.id.btn_select);
        btnUpload = add_restaurant.findViewById(R.id.btn_upload);

        //set default name
        restaurantName.setText(item.getName());

        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImage(); //user select image from gallery & save URI of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_restaurant);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set setPositiveButton
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //update information
                item.setName(restaurantName.getText().toString());
                category.child(key).setValue(item);

            }
        });
        //set setNegativeButton
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

    private void changeImage(final Category item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivityRes.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            btnUpload.setText("Uploaded");
                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //set value for new category if image upload and we can get download it
                                            item.setImage(uri.toString());
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivityRes.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");

                        }
                    });
        }
    }
}