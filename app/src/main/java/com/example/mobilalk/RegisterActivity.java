package com.example.mobilalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREFS_NAME = MainActivity.class.getPackage().toString();

    private static final int SECRET_KEY = 99;
    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordConfirmET;

    SharedPreferences preferences;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 1234) {
            finish();
        }

        usernameET = findViewById(R.id.editTextUserName);
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordConfirmET = findViewById(R.id.editTextPasswordAgain);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userName = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        usernameET.setText(userName);
        passwordET.setText(password);
        passwordConfirmET.setText(password);

        mAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void register(View view) {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();

        if(username.isEmpty()) {
            usernameET.setError("Username is required");
            usernameET.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailET.setError("Email is required");
            emailET.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;
        }

        if(passwordConfirm.isEmpty()) {
            passwordConfirmET.setError("Password confirmation is required");
            passwordConfirmET.requestFocus();
            return;
        }

        if(!password.equals(passwordConfirm)){
            passwordConfirmET.setError("Passwords do not match");
            passwordConfirmET.requestFocus();
            Log.e(LOG_TAG, "Passwords do not match!");
            return;
        }

        Log.i(LOG_TAG, "Username: " + username + " Email: " + email + " Password: " + password + " PasswordConfirm: " + passwordConfirm);
        // TODO actual registration

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "createUserWithEmail:success");
                        startShopping();
                    } else {
                        Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                    }
                });


        //startShopping();
    }

    public void startShopping() {
        Intent intent = new Intent(this, ShopListActivity.class);
        // intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}