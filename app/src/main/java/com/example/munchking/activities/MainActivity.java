package com.example.munchking.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Slide;

import com.example.munchking.R;
import com.example.munchking.fragments.ComposeFragment;
import com.example.munchking.fragments.HomeFragment;
import com.example.munchking.fragments.InboxFragment;
import com.example.munchking.fragments.MapsFragment;
import com.example.munchking.fragments.ProfileFragment;
import com.example.munchking.models.Friends;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.transition.MaterialElevationScale;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "MainActivity";
    private FragmentManager fragMan;
    final private Fragment fragment1 = new HomeFragment();
    final private Fragment fragment2 = new ComposeFragment();
    final private Fragment fragment4 = new InboxFragment();
    final private Fragment fragment3 = ProfileFragment.newInstance(ParseUser.getCurrentUser());

    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fragMan = getSupportFragmentManager();
        fragment1.setEnterTransition(new Slide(Gravity.LEFT));
        fragment1.setExitTransition(new Slide(Gravity.LEFT));
        fragment1.setReenterTransition(new MaterialElevationScale(true));
        fragment2.setEnterTransition(new MaterialElevationScale(true));
        fragment2.setExitTransition(new MaterialElevationScale(false));
        fragment3.setEnterTransition(new Slide(Gravity.RIGHT));
        fragment3.setReenterTransition(new MaterialElevationScale(true));
        fragment3.setExitTransition(new Slide(Gravity.RIGHT));
        fragment4.setEnterTransition(new MaterialElevationScale(true));
        fragment4.setExitTransition(new MaterialElevationScale(false));

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);

        setSupportActionBar(toolbar);
        getLocationPermission();
        getLocation();
        updateFriends();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Clear back stack
                for (int i = 0; i < fragMan.getBackStackEntryCount(); i++) {
                    fragMan.popBackStack();
                }
                // Go to requested fragment
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
                    case R.id.action_inbox:
                        fragment = fragment4;
                        tag = "inbox";
                }
                updateFriends();
                fragMan.beginTransaction().replace(R.id.flContainer, fragment, tag).commit();
                return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.action_home);
    }

    private void updateFriends() {
        ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
        query.whereEqualTo(Friends.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Friends.KEY_CONNECT, false);
        query.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseRelation<Friends> relation = ParseUser.getCurrentUser().getRelation(ProfileFragment.KEY_FRIEND);
                    Friends friend = objects.get(i);
                    friend.setConnection(true);
                    relation.add(friend);
                    friend.saveInBackground();
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error trying to add objects to relation.", e);
                                return;
                            }
                            Log.i(TAG, "Added background object to friends' list.");
                        }
                    });
                }
            }
        });
    }

    private void signOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getLocation();
    }

    private void getLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                MapsFragment.saveLocation(lastKnownLocation);
                            }
                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        getLocation();
    }
}