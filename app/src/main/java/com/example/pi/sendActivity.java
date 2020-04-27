package com.example.pi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class sendActivity extends AppCompatActivity {


    ImageView picture;
    String currentImagePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        final Button next = findViewById(R.id.next);
        final Button takePhoto = findViewById(R.id.takePhoto);
        picture = (ImageView) findViewById(R.id.picture);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               captureImage(v);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSelectOptionActivity();
            }
        });
    }


    public void captureImage(View v)
    {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePic.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = getFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.pi.fileprovider", imageFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePic, 1);
            }
        }
    }
    public File getFile() throws IOException
    {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;

    }

    public void toSelectOptionActivity()
    {
        Intent intent = new Intent(this, selectOptionActivity.class);
        intent.putExtra("imagePath", currentImagePath);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            File file = new File(currentImagePath);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), Uri.fromFile(file));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            if(bitmap != null)
                picture.setImageBitmap(bitmap);
        }
    }
}
