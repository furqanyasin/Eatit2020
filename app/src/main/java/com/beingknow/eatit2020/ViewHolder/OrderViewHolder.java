package com.beingknow.eatit2020.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beingknow.eatit2020.Interface.ItemClickListener;
import com.beingknow.eatit2020.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderID, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    private ItemClickListener itemClickListener;



    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderID = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

    }
}
