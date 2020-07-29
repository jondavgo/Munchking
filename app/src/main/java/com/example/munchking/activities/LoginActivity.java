package com.example.munchking.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.parse.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    List<String> permissions;
    private Button btnLogin;
    private Button btnSignup;
    private Button btnFacebook;
    private EditText etUser;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Login Persistence
        if(ParseUser.getCurrentUser() != null){
            goToMain(this);
        }

        permissions = new ArrayList<>();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);
        btnFacebook = findViewById(R.id.btnFacebook);
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

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if(err != null){
                            Log.d(TAG, "something went wrong");
                            return;
                        }
                        if (user == null) {
                            Log.d(TAG, "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d(TAG, "User signed up and logged in through Facebook!");
                            toFBSignUp();
                        } else {
                            Log.d(TAG, "User logged in through Facebook!");
                            goToMain(LoginActivity.this);
                        }
                    }
                });
            }
        });
    }

    private void toFBSignUp() {
        String username = "";
        String password = "";
        Intent intent = new Intent(LoginActivity.this, FBSignUpActivity.class);
        intent.putExtra("user", username);
        intent.putExtra("password", password);
        startActivity(intent);
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
                    Log.e(TAG, "Log in failed!!!", e);
                    Toast.makeText(LoginActivity.this, "Unable to log in. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }
}