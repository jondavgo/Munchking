package com.example.munchking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.munchking.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    public static final String TAG = "PreferencesActivity";
    public static final String KEY_PREFERENCES = "favGames";
    private CheckBox[] checkBoxes;
    private String[] games;
    private Button btnCont;
    private Button btnSkip;
    private TextView tvTitle;
    LinearLayout llBoxes;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        games = getResources().getStringArray(R.array.games_array);
        tvTitle = findViewById(R.id.tvPreferences);
        checkBoxes = new CheckBox[games.length];
        llBoxes = findViewById(R.id.llBoxes);
        // There has to be a better way to do this???
//        checkBoxes[0] = findViewById(R.id.cbFirst);
//        checkBoxes[1] = findViewById(R.id.cbSecond);
//        checkBoxes[2] = findViewById(R.id.cbThird);
//        checkBoxes[3] = findViewById(R.id.cbFourth);
//        checkBoxes[4] = findViewById(R.id.cbFifth);
//        checkBoxes[5] = findViewById(R.id.cbSixth);
//        checkBoxes[6] = findViewById(R.id.cbSeventh);
//        checkBoxes[7] = findViewById(R.id.cbEighth);
        loadBoxes();
        btnCont = findViewById(R.id.btnContinue);
        btnSkip = findViewById(R.id.btnSkip);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        // Make no changes, go back to main
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.goToMain(PreferencesActivity.this);
            }
        });

        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
            }
        });

        // If this is being accessed from Profile Fragment, edit passed in user
        if(user != null){
            tvTitle.setText(String.format("%s's Favorites!", user.getUsername()));
            try {
                loadPreferences();
            } catch (JSONException e) {
                Log.e(TAG, "Error getting favs!!!", e);
            }
            btnSkip.setText(R.string.cancel);
            btnCont.setText(R.string.save_favs);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void loadBoxes() {
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(getApplicationContext());
            llBoxes.addView(checkBoxes[i]);
            checkBoxes[i].setText(games[i]);
        }
    }

    private void loadPreferences() throws JSONException {
        List<String> array = fromJSONArray(user.getJSONArray(KEY_PREFERENCES));
        for (int i = 0; i < games.length; i++) {
            if(array.contains(games[i])){
                checkBoxes[i].setChecked(true);
            }
        }
    }

    private void savePreferences() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> preferences = new ArrayList<>();
        for (int i = 0; i < checkBoxes.length; i++) {
            if(checkBoxes[i].isChecked()){
                preferences.add(games[i]);
            }
        }
        user.put(KEY_PREFERENCES, new JSONArray(preferences));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    // Failure
                    Log.e(TAG, "Error saving preferences");
                    return;
                }
                LoginActivity.goToMain(PreferencesActivity.this);
            }
        });
    }

    // For use when allowing user to edit their preferences
    public static List<String> fromJSONArray(JSONArray arr) throws JSONException {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            strings.add(arr.getString(i));
        }
        return strings;
    }

    public static JSONArray toJSONArray(String[] strings){
        JSONArray arr = new JSONArray();
        for (String string : strings) {
            arr.put(string);
        }
        return arr;
    }
}