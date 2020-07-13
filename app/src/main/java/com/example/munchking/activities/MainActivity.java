package com.example.munchking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.munchking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragMan;

    Toolbar toolbar;
    BottomNavigationView bottomNav;
    FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragMan = getSupportFragmentManager();

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);
        flContainer = findViewById(R.id.flContainer);

        setSupportActionBar(toolbar);

        // TODO: Set up BottomNavigation

        // TODO: Set up Frame Navigation

        //TODO: Set up Toolbar
    }
}