package com.example.munchking;

import android.app.Application;

import com.example.munchking.models.CharPost;
import com.example.munchking.models.Comment;
import com.example.munchking.models.FriendRequest;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseFacebookUtils;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseObject.registerSubclass(CharPost.class);
        ParseObject.registerSubclass(FriendRequest.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gomez-munchking") // should correspond to APP_ID env variable
                .clientKey("Nat20IsWhatIRolled")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://gomez-munchking.herokuapp.com/parse/").build());
        ParseFacebookUtils.initialize(this);
    }
}
