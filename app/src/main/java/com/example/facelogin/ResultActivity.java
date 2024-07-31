package com.example.facelogin;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import java.util.Calendar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView nameText = findViewById(R.id.nameResult);
        TextView birthText = findViewById(R.id.birthResult);
        ImageView loginImage = findViewById(R.id.login_image);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String birth = intent.getStringExtra("birth");
        Bitmap image = intent.getParcelableExtra("image");

        nameText.setText(name);
        birthText.setText(birth);
        loginImage.setImageBitmap(image);
    }
}
