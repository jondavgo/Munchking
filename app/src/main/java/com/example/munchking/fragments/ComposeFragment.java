package com.example.munchking.fragments;

import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Slide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.models.CharPost;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {

    public static final int CAMERA_REQUEST_CODE = 1024;
    public static final int GALLERY_REQUEST_CODE = 4201;
    private static final String TAG = "ComposeFragment";
    private File photoFile;
    private ParseFile file;
    private final String photoFileName = "image.jpg";
    private String[] games;
    private String selectedGame;
    private Context context;

    private Button btnCam;
    private Button btnGallery;
    private Button btnPost;
    private ImageView ivPreview;
    private Spinner spinner;
    private TextInputEditText etCharName;
    private TextInputEditText etClass;
    private TextInputEditText etRace;
    private TextInputEditText etDesc;
    private CharPost post;

    public static ComposeFragment newInstance(CharPost post) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", Parcels.wrap(post));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        games = getResources().getStringArray(R.array.games_array);
        selectedGame = "";
        context = getContext();

        btnCam = view.findViewById(R.id.btnCam);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnPost = view.findViewById(R.id.btnPost);
        ivPreview = view.findViewById(R.id.ivPreview);
        spinner = view.findViewById(R.id.spinner);
        etCharName = view.findViewById(R.id.etCharName);
        etClass = view.findViewById(R.id.etClass);
        etRace = view.findViewById(R.id.etRace);
        etDesc = view.findViewById(R.id.etDesc);

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

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etCharName.getText().toString();
                String classes = etClass.getText().toString();
                String race = etRace.getText().toString();
                String description = etDesc.getText().toString();
                if (post != null) {
                    updatePost(name, selectedGame, file, classes, race, description);
                } else {
                    savePost(name, selectedGame, ParseUser.getCurrentUser(), file, classes, race, description);
                }
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.games_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGame = games[i];
                Log.i(TAG, "This character is from: " + selectedGame);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "Nothing to report.");
            }
        });

        if (getArguments() != null) {
            post = Parcels.unwrap(getArguments().getParcelable("post"));
        }
        if (post != null) {
            loadPost();
            btnPost.setText(R.string.save);
        }
    }

    private void updatePost(String name, String ttrpg, ParseFile myFile, String classes, String race, String description) {
        post.setName(name);
        post.setTtrpg(ttrpg);
        if (myFile != null) {
            post.setPhoto(myFile);
        }
        post.setClasses(classes);
        post.setRace(race);
        post.setDesc(description);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), R.string.edits_saved, Toast.LENGTH_SHORT).show();
                etCharName.setText("");
                etClass.setText("");
                etRace.setText("");
                etDesc.setText("");
                ivPreview.setImageResource(0);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadPost() {
        etCharName.setText(post.getName());
        etDesc.setText(post.getDesc());
        etClass.setText(post.getClasses());
        etRace.setText(post.getRace());
        ParseFile photo = post.getPhoto();
        if (photo != null) {
            Glide.with(context).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPreview);
        }
        for (int i = 0; i < games.length; i++) {
            if (games[i].equals(post.getTtrpg())) {
                spinner.setSelection(i);
            }
        }
    }

    private void savePost(String name, String ttrpg, ParseUser user, ParseFile myFile, String classes, String race, String description) {
        final CharPost post = new CharPost();
        post.setName(name);
        post.setTtrpg(ttrpg);
        if (myFile != null) {
            post.setPhoto(myFile);
        }
        post.setUser(user);
        post.setClasses(classes);
        post.setRace(race);
        post.setDesc(description);
        post.setTraits(new JSONArray());
        post.setEquipment(new JSONArray());
        Pair<String, String> item = new Pair<>("New Item", "Press and hold onto me to edit what's inside!");
        try {
            post.addTraitEquip(item, true);
            post.addTraitEquip(item, false);
        } catch (JSONException e) {
            Log.e(TAG, "Error while saving", e);
            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
            return;
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), R.string.posts_saved, Toast.LENGTH_SHORT).show();
                etCharName.setText("");
                etClass.setText("");
                etRace.setText("");
                etDesc.setText("");
                ivPreview.setImageResource(0);
                toDetails(post);
            }
        });
    }

    private void GoToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    private void GoToCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(context, "com.codepath.fileprovider.gomez-munchking", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri() {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
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
                    ivPreview.setImageBitmap(takenImage);
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
                        ivPreview.setImageBitmap(selectedImage);
                    }
                    break;
            }
        } else {
            Toast.makeText(getContext(), R.string.pic_not_taken, Toast.LENGTH_SHORT).show();
        }
    }

    private void toDetails(CharPost post) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", Parcels.wrap(post));
        fragment.setArguments(args);
        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        fragment.setExitTransition(new Slide(Gravity.BOTTOM));
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment,"details");
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.commit();
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

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
}