package com.example.munchking.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FBSignUpActivity extends SignupActivity {

    public static final String TAG = "FBSignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etEmail.setVisibility(View.GONE);
    }

    @Override
    protected void signUp(String username, String password, String email) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(username);
        user.setPassword(password);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    LoginActivity.goToMain(FBSignUpActivity.this);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Sign up failed!!!", e);
                    Toast.makeText(FBSignUpActivity.this, "Unable to sign up. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
