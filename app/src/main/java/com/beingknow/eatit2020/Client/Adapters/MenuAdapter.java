package com.beingknow.eatit2020.Client.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.Models.Category;
import com.beingknow.eatit2020.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    Context context;
    private ArrayList<Category> categories = new ArrayList();
    private ItemClickListener mOnItemClickInterface;

    public MenuAdapter(Context context, ArrayList<Category> categories, ItemClickListener mOnItemClickInterface) {
        this.context = context;
        this.categories = categories;
        this.mOnItemClickInterface = mOnItemClickInterface;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.foodName.setText(categories.get(position).getName());
        Picasso.get().load(categories.get(position).getImage()).into(holder.foodImage);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodName;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.menu_name);
            foodImage = itemView.findViewById(R.id.menu_image);

        }

    }
}
