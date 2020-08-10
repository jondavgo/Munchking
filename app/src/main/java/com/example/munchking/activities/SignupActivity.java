package com.example.munchking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.munchking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    Button btnSignup;
    TextInputEditText etUsername;
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextInputEditText etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignup = findViewById(R.id.btnSignUp);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        etUsername.setText(getIntent().getStringExtra("user"));
        etPassword.setText(getIntent().getStringExtra("password"));

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String confirm = etConfirm.getText().toString();
                if(password.equals(confirm)) {
                    int i = email.indexOf("@");
                    String afterAt;
                    if (i > 0) {
                        afterAt = email.substring(i);
                    } else {
                        afterAt = "";
                    }
                    if (afterAt.contains(".")) {
                        signUp(username, password, email);
                    } else {
                        Toast.makeText(SignupActivity.this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void signUp(String username, String password, String email) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        if (username.length() > 13) {
            Toast.makeText(this, R.string.too_long, Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setUsername(username);
        }
        user.setPassword(password);
        user.setEmail(email);
        // Set custom properties
        user.put("filterFriends", false);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(SignupActivity.this, PreferencesActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Sign up failed!!!", e);
                    Toast.makeText(SignupActivity.this, R.string.signup_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}