package com.example.mobilalk;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private ArrayList<ShoppingItem> cartItems;
    private RecyclerView recyclerView;
    private TextView emptyCartText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get cart items from intent
        cartItems = getIntent().getParcelableArrayListExtra("cart");

        recyclerView = findViewById(R.id.cart_recycler_view);
        emptyCartText = findViewById(R.id.empty_cart_text);

        if (cartItems != null && !cartItems.isEmpty()) {
            // Set up RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            CartItemAdapter adapter = new CartItemAdapter(this, cartItems);
            recyclerView.setAdapter(adapter);
            emptyCartText.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        }
    }
}