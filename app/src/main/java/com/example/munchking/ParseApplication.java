package com.example.munchking;

import android.app.Application;

import com.parse.Parse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gomez-munchking") // should correspond to APP_ID env variable
                .clientKey("Nat20IsWhatIRolled")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://gomez-munchking.herokuapp.com/parse/").build());
    }
}
