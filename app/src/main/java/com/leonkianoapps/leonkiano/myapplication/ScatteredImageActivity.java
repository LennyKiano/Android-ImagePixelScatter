package com.leonkianoapps.leonkiano.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScatteredImageActivity extends AppCompatActivity {

    Toolbar toolbar;

    ImageView scatterImageView;

    Intent processingIntent;

    RelativeLayout relativeLayout;

    String imagePath;
    Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scattered_image);

        relativeLayout = findViewById(R.id.scatteredRelativeLayout);


        //setting up toolbar
        toolbar  = findViewById(R.id.scatteredToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Scattered Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //ImageView

        scatterImageView = findViewById(R.id.scatteredImageView);

        //receiving  the intent
        processingIntent = getIntent();
        imagePath = processingIntent.getStringExtra("picture");


         Uri imageUri = Uri.parse(imagePath);

        try {

            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            scatterImageView.setImageBitmap(imageBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }



        Button shareButton = findViewById(R.id.shareScatterImageButton);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               shareImage(imageBitmap);

            }
        });





    }

    private void shareImage(Bitmap imageBitmap) {

        showSnack("Sharing..");

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "scattered_image.jpg");

        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());


        } catch (IOException e) {
            e.printStackTrace();
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/scattered_image.jpg"));
        startActivity(Intent.createChooser(share,"Share Image"));


    }


    public void showSnack(String message){

        final Snackbar snackbar = Snackbar.make(relativeLayout,message,Snackbar.LENGTH_SHORT);
        snackbar.setAction("OKAY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                snackbar.dismiss();
            }
        });

        snackbar.show();



    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
