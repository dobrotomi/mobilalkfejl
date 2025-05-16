package com.example.mobilalk;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView countTextView;

    private List<ShoppingItem> cart = new ArrayList<>();
    private int cartItems = 0;
    private int gridNumber = 1;

    // Member variables.
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    private ShoppingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;

    private CollectionReference mItems;

    private SharedPreferences preferences;

    private NotificationHelper mNotificationHelper;
    private AlarmManager mAlarmManager;

    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_list);

        // Apply insets to prevent content from going under system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recycleView), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    insets.top,
                    view.getPaddingRight(),
                    insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        // Check notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissions(new String[] {android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE);

        }



        mNotificationHelper = new NotificationHelper(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // recycle view
        mRecyclerView = findViewById(R.id.recycleView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        // Initialize the ArrayList that will contain the data.
        mItemsData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ShoppingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void queryData() {
        // Clear the existing data (to avoid duplication).
        mItemsData.clear();

        //mItems.whereEqualTo();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                ShoppingItem item = doc.toObject(ShoppingItem.class);
                item.setId(doc.getId());
                mItemsData.add(item);
            }
            if(mItemsData.size() == 0) {
                initializeData();
                queryData();
            }
            // Notify the adapter of the change.
            mAdapter.notifyDataSetChanged();
        });


    }

    public void deleteItem(ShoppingItem item) {
        DocumentReference ref = mItems.document(item._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
        mNotificationHelper.cancel();
    }

    private void queryItemsByRatingRange(float minRating, float maxRating) {
        // Clear the existing data
        mItemsData.clear();

        // Create a complex query with multiple conditions
        mItems.whereGreaterThanOrEqualTo("ratedInfo", minRating)
                .whereLessThanOrEqualTo("ratedInfo", maxRating)
                .orderBy("ratedInfo", Query.Direction.DESCENDING)
                .orderBy("name")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ShoppingItem item = doc.toObject(ShoppingItem.class);
                        item.setId(doc.getId());
                        mItemsData.add(item);
                    }

                    if (mItemsData.isEmpty()) {
                        Toast.makeText(ShopListActivity.this,
                                "No items found in the specified rating range",
                                Toast.LENGTH_SHORT).show();
                    }

                    // Notify the adapter of the change
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error querying by rating range", e);
                    Toast.makeText(ShopListActivity.this,
                            "Error loading items: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }


    private void initializeData() {
        // Get the resources from the XML file.
        String[] itemsList = getResources()
                .getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResources =
                getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);




        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShoppingItem(
                    itemsImageResources.getResourceId(i, 0),
                    itemsInfo[i],
                    itemsList[i],
                    itemsPrice[i],
                    itemRate.getFloat(i, 0),
                    0));
        }

        // Recycle the typed array.
        itemsImageResources.recycle();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (id == R.id.setting_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        } else if (id == R.id.cart) {
            Log.d(LOG_TAG, "Cart clicked!");
            Intent intent = new Intent(this, CartActivity.class);
            intent.putParcelableArrayListExtra("cart", new ArrayList<>(cart));
            startActivity(intent);
            return true;
        } else if (id == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.ic_view_grid, 1);
            } else {
                changeSpanCount(item, R.drawable.ic_view_row, 2);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item) {
        /*
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });

        mNotificationHelper.send(item.getName());
        queryData();*/
        cart.add(item);
        cartItems = cart.size();
        countTextView.setText(String.valueOf(cartItems));
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });

        mNotificationHelper.send(item.getName());
        queryData();
    }

    private void setmAlarmManager() {
        long repeatInterval = 60000; // AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);
    }
}