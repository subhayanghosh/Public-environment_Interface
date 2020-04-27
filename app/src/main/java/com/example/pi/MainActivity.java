package com.example.pi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = findViewById(R.id.start);
        final Button about = findViewById(R.id.about);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSendActivity();
            }
        });
    }

    public void toSendActivity()
    {
        Intent intent = new Intent(this, sendActivity.class);
        startActivity(intent);
    }
}
