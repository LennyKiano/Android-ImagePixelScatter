package com.leonkianoapps.leonkiano.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import java.io.IOException;

public class PreviewActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView previewImageView;

    Bitmap cameraImageBitmap, galleryImageBitmap;

    String imagePath, cameraImagePath;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        //setting up toolbar
        toolbar =  findViewById(R.id.previewToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Preview Image");

        //imageView
        previewImageView = findViewById(R.id.previewImageView);

        //handling the intent

        final Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");

        if (imagePath == null || imagePath.equals("")) {

            //this means the user to a picture with camera
            cameraImagePath = intent.getStringExtra("cameraImagePath");

            cameraImageBitmap = BitmapFactory.decodeFile(cameraImagePath);

            previewImageView.setImageBitmap(cameraImageBitmap);


        } else {

            imageUri = Uri.parse(imagePath);

            try {

                galleryImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                previewImageView.setImageBitmap(galleryImageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


            previewImageView.setImageURI(imageUri);
        }

        //Setting up Button
        Button scatterImageButton = findViewById(R.id.scatterImageButton);

        scatterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent processingIntent = new Intent(view.getContext(),ProcessingActivity.class);

                if (imagePath == null || imagePath.equals("")) {

                    //pass cameraImage to processing activity
                    processingIntent.putExtra("cameraImagePath",cameraImagePath);
                    startActivity(processingIntent);


                }else {

                    //pass galleryImage to processing activity
                    processingIntent.putExtra("imagePath",imagePath);
                    startActivity(processingIntent);


                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;


    }
}
