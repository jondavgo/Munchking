package com.example.munchking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.munchking.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnSignup;
    EditText etUser;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Login Persistence
        if(ParseUser.getCurrentUser() != null){
            goToMain(this);
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        etUser = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSignUp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            // show the signup or login screen
        }
    }

    private void signIn() {
        String password = etPassword.getText().toString();
        String username = etUser.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    goToMain(LoginActivity.this);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.e("LoginActivity", "Log in failed!!!", e);
                    Toast.makeText(LoginActivity.this, "Unable to log in. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Pass through the email and password put into the edit text if any, bring user to Sign Up
    private void toSignUp() {
        String username = etUser.getText().toString();
        String password = etPassword.getText().toString();
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.putExtra("user", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public static void goToMain(AppCompatActivity context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }
}