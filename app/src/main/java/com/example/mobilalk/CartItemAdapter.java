package com.example.mobilalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private ArrayList<ShoppingItem> cartItems;
    private Context context;

    public CartItemAdapter(Context context, ArrayList<ShoppingItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem currentItem = cartItems.get(position);
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView priceText;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.cart_item_title);
            priceText = itemView.findViewById(R.id.cart_item_price);
            imageView = itemView.findViewById(R.id.cart_item_image);
        }

        public void bindTo(ShoppingItem currentItem) {
            titleText.setText(currentItem.getName());
            priceText.setText(currentItem.getPrice());
            Glide.with(context).load(currentItem.getImageResource()).into(imageView);
        }
    }
}