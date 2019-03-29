package com.leonkianoapps.leonkiano.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class ProcessingActivity extends AppCompatActivity {


    Toolbar processingToolBar;
    ProgressBar processingProgressBar;


    Bitmap cameraImageBitmap, galleryImageBitmap;

    String imagePath, cameraImagePath;
    Uri imageUri;

    ImageView processingImageView;

    RelativeLayout processingRelativeLayout;

    CountDownTimer timer;

    Intent previewIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        //getting layout for snackBar
        processingRelativeLayout = findViewById(R.id.processingRelativeLayout);


        processingImageView = findViewById(R.id.processingImageView);

        //setting up toolBar

        processingToolBar = findViewById(R.id.processingToolBar);
        setSupportActionBar(processingToolBar);
        getSupportActionBar().setTitle("Processing..");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //ProgressBar

        processingProgressBar = findViewById(R.id.processingProgressBar);

        //get intents

        previewIntent = getIntent();
        imagePath = previewIntent.getStringExtra("imagePath");


        startTimer();


    }

    private void startTimer() {


        showSnack("Processing...");
        processingProgressBar.setVisibility(View.VISIBLE);


        //Start timer for processing
        timer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

                Log.i("TIMER", "TICK");
            }

            @Override
            public void onFinish() {

                Log.i("TIMER", "FINISH");


                if (imagePath == null || imagePath.equals("")) {

                    //this means the user to a picture with camera
                    cameraImagePath = previewIntent.getStringExtra("cameraImagePath");


                    cameraImageBitmap = BitmapFactory.decodeFile(cameraImagePath);
                    scatterImage(cameraImageBitmap);


                } else {
                    //Image is from gallery
                    imageUri = Uri.parse(imagePath);

                    try {
                        galleryImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

                        scatterImage(galleryImageBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }
        };

        timer.start();


    }

    private void scatterImage(Bitmap bitmap) {

        Log.i("SCATTER", "START");


        int numPixels = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[numPixels];   //getting the total pixel of the image


        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());  //gets the color pixels

        List<Integer> intList = new ArrayList<Integer>();   //creating an arrayList out of the pixel array
        for (int i : pixels) {
            intList.add(i);
        }

        Collections.shuffle(intList);   //shuffling the arrayList to mix them

        int[] scatteredArray = new int[intList.size()];   //for converting it back into an int[]array
        for (int i = 0; i < scatteredArray.length; i++) {
            scatteredArray[i] = intList.get(i).intValue();
        }


        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        Bitmap scatterBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  //creating a new bitmap from the scattered pixels
        scatterBitmap.setPixels(scatteredArray, 0, width, 0, 0, width, height);


        //converting the scatteredBitmap into a byteArray to be sent to the scatteredImageActivity

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scatterBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scatterBitmap, "scattered", null);  //image will be the last image in the gallery


        Uri scatteredImagePath = Uri.parse(path);


        Log.i("SCATTER", "DONE");


        Intent intent = new Intent(this, ScatteredImageActivity.class);
        intent.putExtra("picture", scatteredImagePath.toString());   //passing the image to the next activity
        startActivity(intent);


    }




    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;

    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void showSnack(String message) {

        final Snackbar snackbar = Snackbar.make(processingRelativeLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OKAY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                snackbar.dismiss();
            }
        });

        snackbar.show();


    }
}
