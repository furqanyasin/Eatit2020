package com.beingknow.eatit2020.Server.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beingknow.eatit2020.Client.Activities.FoodDetailsActivity;
import com.beingknow.eatit2020.Client.Activities.FoodListActivity;
import com.beingknow.eatit2020.Common.Common;
import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.Models.Category;
import com.beingknow.eatit2020.Models.Food;
import com.beingknow.eatit2020.R;
import com.beingknow.eatit2020.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodListActivityRes extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerOptions<Food> firebaseRecyclerOptions;
    String categoryId = "";

    private final int PICK_IMAGE_REQUEST = 71;

    TextInputEditText foodName, foodPrice, foodDiscount, foodDescription;
    MaterialButton btnSelect, btnUpload;
    RelativeLayout rootLayout;
    Food newFood;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list_res);

        rootLayout = findViewById(R.id.root_layout);

        //init firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList, Food.class).build();

        //Load menu
        recyclerView = findViewById(R.id.recycler_view_food_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();

            }
        });

        // get intent here
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra(Common.CATEGORY_ID);
            loadListFood(categoryId);
        }

    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivityRes.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill food information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_restaurant = inflater.inflate(R.layout.add_new_food, null);

        btnSelect = add_restaurant.findViewById(R.id.btn_select);
        btnUpload = add_restaurant.findViewById(R.id.btn_upload);
        foodName = add_restaurant.findViewById(R.id.et_name_food);
        foodPrice = add_restaurant.findViewById(R.id.et_food_price);
        foodDiscount = add_restaurant.findViewById(R.id.et_food_price_discount);
        foodDescription = add_restaurant.findViewById(R.id.et_food_description);


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
                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout, "New Food" + newFood.getName() + "was added", Snackbar.LENGTH_SHORT).show();
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

    private void loadListFood(String categoryId) {

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
                      /*  //Toast.makeText(HomeActivity.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                        //Get CategoryId and send to new activity
                        Intent foodDetails = new Intent(FoodListActivityRes.this, FoodDetailsActivity.class);
                        //Because foodId is a key, so we just key of this item
                        foodDetails.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetails);*/
                    }
                });

            }
        };
        //set adapter
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                            Toast.makeText(FoodListActivityRes.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            btnUpload.setText("Uploaded");
                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //set value for new food if image upload and we can get download it
                                            newFood = new Food();
                                            newFood.setName(foodName.getText().toString());
                                            newFood.setPrice(foodPrice.getText().toString());
                                            newFood.setDiscount(foodDiscount.getText().toString());
                                            newFood.setDescription(foodDescription.getText().toString());
                                            newFood.setMenuId(categoryId);
                                            newFood.setImage(uri.toString());
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivityRes.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void choseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Selected!");
        }
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
    }

}