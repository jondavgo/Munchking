package com.example.munchking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.munchking.R;
import com.example.munchking.fragments.ComposeFragment;
import com.example.munchking.fragments.HomeFragment;
import com.example.munchking.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragMan;
    final private Fragment fragment1 = new HomeFragment();
    final private Fragment fragment2 = new ComposeFragment();
    final private Fragment fragment3 = new ProfileFragment();

    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity", "Logged in as " + ParseUser.getCurrentUser().getUsername());

        fragMan = getSupportFragmentManager();

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);
        flContainer = findViewById(R.id.flContainer);

        setSupportActionBar(toolbar);

        // TODO: Set up BottomNavigation
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = fragment1;
                        break;
                    case R.id.action_new:
                        fragment = fragment2;
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = fragment3;
                        break;
                }
                fragMan.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.action_home);
        // TODO: Set up Frame Navigation
    }

    private void signOut(){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.signout){
            signOut();
        }
        return true;
    }
}