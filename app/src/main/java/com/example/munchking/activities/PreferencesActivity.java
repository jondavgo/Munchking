package com.example.munchking.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.munchking.fragments.ComposeFragment.CAMERA_REQUEST_CODE;
import static com.example.munchking.fragments.ComposeFragment.GALLERY_REQUEST_CODE;

public class PreferencesActivity extends AppCompatActivity {

    public static final String TAG = "PreferencesActivity";
    public static final String KEY_PREFERENCES = "favGames";
    private Chip[] chips;
    private ChipGroup chipGroup;
    private String[] games;
    private Button btnCont;
    private Button btnSkip;
    private final String photoFileName = "image.jpg";
    private Button btnCam;
    private TextView tvTitle;
    private ParseUser user;
    private Button btnGallery;
    private ImageView ivPfp;
    private File photoFile;
    private ParseFile file;
    private TextInputEditText etUsername;

    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        // Return result
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }

    private void loadBoxes() {
        for (int i = 0; i < chips.length; i++) {
            chips[i] = new Chip(this);
            chips[i].setCheckable(true);
            chips[i].setChipBackgroundColorResource(R.color.colorAccent);
            chipGroup.addView(chips[i]);
            chips[i].setText(games[i]);
        }
    }

    private void loadPreferences() throws JSONException {
        List<String> array = fromJSONArray(user.getJSONArray(KEY_PREFERENCES));
        for (int i = 0; i < games.length; i++) {
            if (array.contains(games[i])) {
                chips[i].setChecked(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        games = getResources().getStringArray(R.array.games_array);
        btnCam = findViewById(R.id.btnCam);
        btnGallery = findViewById(R.id.btnGallery);
        tvTitle = findViewById(R.id.tvPreferences);
        ivPfp = findViewById(R.id.ivPfp);
        chips = new Chip[games.length];
        chipGroup = findViewById(R.id.cgChips);
        etUsername = findViewById(R.id.etUsername);
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

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToCamera();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToGallery();
            }
        });

        etUsername.setVisibility(View.GONE);

        // If this is being accessed from Profile Fragment, edit passed in user
        if (user != null) {
            file = user.getParseFile("profilePic");
            if (file != null) {
                Glide.with(this).load(file.getUrl()).transform(new RoundedCorners(30)).into(ivPfp);
            }
            etUsername.setVisibility(View.VISIBLE);
            etUsername.setText(user.getUsername());
            tvTitle.setText(String.format("%s's Settings", user.getUsername()));
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

    // For use when allowing user to edit their preferences
    public static List<String> fromJSONArray(JSONArray arr) throws JSONException {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            strings.add(arr.getString(i));
        }
        return strings;
    }

    public static JSONArray toJSONArray(String[] strings) {
        JSONArray arr = new JSONArray();
        for (String string : strings) {
            arr.put(string);
        }
        return arr;
    }

    private void savePreferences() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> preferences = new ArrayList<>();
        for (int i = 0; i < chips.length; i++) {
            if (chips[i].isChecked()) {
                preferences.add(games[i]);
            }
        }
        user.put(KEY_PREFERENCES, new JSONArray(preferences));
        if (file != null) {
            user.put("profilePic", file);
        }
        user.setUsername(etUsername.getText().toString());
        Toast.makeText(this, "Saving...please wait.", Toast.LENGTH_SHORT).show();
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    // Failure
                    Log.e(TAG, "Error saving preferences");
                    Toast.makeText(PreferencesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginActivity.goToMain(PreferencesActivity.this);
            }
        });
    }

    private void GoToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    private void GoToCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.gomez-munchking", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + "image.jpg");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                    ivPfp.setImageBitmap(takenImage);
                    file = new ParseFile(photoFile);
                    break;
                case GALLERY_REQUEST_CODE:
                    if (data != null) {
                        Uri photoUri = data.getData();
                        // Load the image located at photoUri into selectedImage
                        Bitmap selectedImage = loadFromUri(photoUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] image = stream.toByteArray();
                        file = new ParseFile(photoFileName, image);
                        // Load the selected image into a preview
                        ivPfp.setImageBitmap(selectedImage);
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}