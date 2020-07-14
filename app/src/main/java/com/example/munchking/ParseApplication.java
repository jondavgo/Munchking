package com.example.munchking;

import android.app.Application;

import com.example.munchking.models.Character;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseObject.registerSubclass(Character.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gomez-munchking") // should correspond to APP_ID env variable
                .clientKey("Nat20IsWhatIRolled")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://gomez-munchking.herokuapp.com/parse/").build());
    }
}
