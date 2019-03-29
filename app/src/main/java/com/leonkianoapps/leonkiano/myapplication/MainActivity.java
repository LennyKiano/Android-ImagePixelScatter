package com.leonkianoapps.leonkiano.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

import static android.os.Environment.getExternalStoragePublicDirectory;  // Add this import manually for getExternalStoragePublicDirectory in order to store photo to the public storage


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView introTextView;
    Button takePicButton, openGalleryButton;


    private static int OPEN_GALLERY = 1;
    private static int OPEN_CAMERA = 20;

    String mCurrentPhotoPath;

    Bitmap uploadBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up ToolBar
        toolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(toolbar);

        //finding other views
        introTextView = findViewById(R.id.intro_textView);
        takePicButton = findViewById(R.id.takePicButton);
        openGalleryButton = findViewById(R.id.openGalleryButton);


        //setting onClickListeners

        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGalley();

            }
        });


    }


    public void takePicture() {

//Ask for permission to access the camera for marshmallow and greater h

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},    //Requesting the permission with the appropriate request code
                        OPEN_CAMERA);
            } else {

                getCameraPhoto();  //If the permission was already granted the first time it will run the method to open the Camera intent
            }
        }else {


            getCameraPhoto();
        }


    }


    public void openGalley() {
        //Ask for permission to access/read storage for marshmallow and greater

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //do runtime permission

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   //Requesting the permission with the appropriate request code
                        OPEN_GALLERY);


            } else {

                getPhoto();
            }


        } else {


            getPhoto();
        }


    }

    private void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, OPEN_GALLERY);  //Check onActivityResult on how to handle the photo selected
    }

    private void getCameraPhoto() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  //Intent to open camera


        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go

            File photoFile = null;

            try {

                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

                Toast.makeText(getApplicationContext(), "File could not be created", Toast.LENGTH_LONG).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                mCurrentPhotoPath = photoFile.getAbsolutePath();  //Getting the file photo path to be used later to store it in the device's photo gallery

                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        "com.leonkianoapps.leonkiano.myapplication.fileprovider",   //must match the authorities in the manifest, use app package name
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, OPEN_CAMERA);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == OPEN_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to access gallery

                getPhoto();

            } else {

                Toast.makeText(getApplicationContext(), "You have Disabled this feature", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == OPEN_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Now user should be able to access the Camera

                getCameraPhoto();

            } else {

                Toast.makeText(getApplicationContext(), "You have Disabled camera for this feature", Toast.LENGTH_LONG).show();

            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == OPEN_GALLERY && resultCode == RESULT_OK && data != null) {  //For the gallery intent

            Uri selectedImage = data.getData();

            Intent previewIntent = new Intent(this,PreviewActivity.class);

            if (selectedImage != null) {

                previewIntent.putExtra("imagePath",selectedImage.toString());
                startActivity(previewIntent);
            }



        } else if (requestCode == OPEN_CAMERA) {  //For the camera intent

            uploadBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);   //path of the photo file used to create a bitmap


            galleryAddPic();


            Intent previewIntent = new Intent(this,PreviewActivity.class);
            previewIntent.putExtra("cameraImagePath",mCurrentPhotoPath);
            startActivity(previewIntent);


        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);  //To make the photo file public to other apps.


        File image = null;
        try {
            image = File.createTempFile(timeStamp, ".jpg", storageDir);

        } catch (IOException p) {

            Toast.makeText(getApplicationContext(), "Something went wrong in the files", Toast.LENGTH_LONG).show();
            p.printStackTrace();
        }


        return image;
    }

    //Method to add picture taken to the phone Gallery

    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(mCurrentPhotoPath);   //Setting the path of the photo taken by the camera

        Uri contentUri = Uri.fromFile(f);       //Creating a Uri from the file

        mediaScanIntent.setData(contentUri);

        this.sendBroadcast(mediaScanIntent);
    }


}