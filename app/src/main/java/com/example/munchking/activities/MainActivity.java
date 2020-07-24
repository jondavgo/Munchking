package com.example.munchking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.munchking.R;
import com.example.munchking.dialogs.AddItemDialog;
import com.example.munchking.fragments.ComposeFragment;
import com.example.munchking.fragments.HomeFragment;
import com.example.munchking.fragments.MapsFragment;
import com.example.munchking.fragments.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private FragmentManager fragMan;
    final private Fragment fragment1 = new HomeFragment();
    final private Fragment fragment2 = new ComposeFragment();
    final private Fragment fragment3 = ProfileFragment.newInstance(ParseUser.getCurrentUser());
    final private Fragment fragment4 = new MapsFragment();

    private Toolbar toolbar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fragMan = getSupportFragmentManager();

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);

        setSupportActionBar(toolbar);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                String tag;
                switch (item.getItemId()) {
                    case R.id.action_home:
                    default:
                        fragment = fragment1;
                        tag = "home";
                        break;
                    case R.id.action_new:
                        fragment = fragment2;
                        tag = "compose";
                        break;
                    case R.id.action_profile:
                        fragment = fragment3;
                        tag = "profile";
                        break;
                    case R.id.action_map:
                        fragment = fragment4;
                        tag = "map";
                }
                fragMan.beginTransaction().replace(R.id.flContainer, fragment, tag).commit();
                return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.action_home);
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