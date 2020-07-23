package com.example.munchking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.munchking.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatActivity {

    public static final String TAG = "PreferencesActivity";
    private CheckBox[] checkBoxes;
    private String[] games;
    private Button btnCont;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        games = getResources().getStringArray(R.array.games_array);
        checkBoxes = new CheckBox[games.length];
        // There has to be a better way to do this???
        checkBoxes[0] = findViewById(R.id.cbFirst);
        checkBoxes[1] = findViewById(R.id.cbSecond);
        checkBoxes[2] = findViewById(R.id.cbThird);
        checkBoxes[3] = findViewById(R.id.cbFourth);
        checkBoxes[4] = findViewById(R.id.cbFifth);
        checkBoxes[5] = findViewById(R.id.cbSixth);
        checkBoxes[6] = findViewById(R.id.cbSeventh);
        checkBoxes[7] = findViewById(R.id.cbEighth);
        btnCont = findViewById(R.id.btnContinue);
        btnSkip = findViewById(R.id.btnSkip);

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setText(games[i]);
        }

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
    }

    private void savePreferences() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> preferences = new ArrayList<>();
        for (int i = 0; i < checkBoxes.length; i++) {
            if(checkBoxes[i].isChecked()){
                preferences.add(games[i]);
            }
        }
        user.put("favGames", new JSONArray(preferences));
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
    public static String[] fromJSONArray(JSONArray arr) throws JSONException {
        String[] strings = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            strings[i] = arr.getString(i);
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