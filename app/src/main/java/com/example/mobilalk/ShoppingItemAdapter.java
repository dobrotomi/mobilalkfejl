package com.example.mobilalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<ShoppingItem> shoppingItems;
    private ArrayList<ShoppingItem> shoppingItemsAll;
    private Context context;
    private int lastPosition = -1;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
        this.shoppingItemsAll = shoppingItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = shoppingItems.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                results.count = shoppingItemsAll.size();
                results.values = shoppingItemsAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(ShoppingItem item : shoppingItemsAll) {
                    if(item.getName().toLowerCase().contains(filterPattern) ||
                            item.getInfo().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shoppingItems = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView infoText;
        private TextView priceText;
        private RatingBar ratingBar;
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.itemTitle);
            infoText = itemView.findViewById(R.id.subTitle);
            priceText = itemView.findViewById(R.id.price);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageView = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ShopListActivity", "Add to cart clicked");
                }
            });
        }

        public void bindTo(ShoppingItem currentItem) {
            titleText.setText(currentItem.getName());
            infoText.setText(currentItem.getInfo());
            priceText.setText(currentItem.getPrice());
            ratingBar.setRating(currentItem.getRatedInfo());

            Glide.with(context).load(currentItem.getImageResource()).into(imageView);

        }
    }
}


