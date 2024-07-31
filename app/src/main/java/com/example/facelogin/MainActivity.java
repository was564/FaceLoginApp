package com.example.facelogin;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 101;
    private final int CAMERA_PERMISSION_CODE = 102;
    public static final int REQUEST_CODE_LOGIN = 103;

    private ImageView imageView;
    private TextView resultText;
    private Bitmap faceImage = null;

    private Button buttonLogin;
    private Button buttonCamera;
    private Button buttonRegister;
    private Button buttonDatePicker;
    private EditText editName;

    private DatePickerDialog datePickerDialog;
    private TextView Date;

    private Retrofit retrofit;
    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
        imageView = findViewById(R.id.image_view);
        resultText = findViewById(R.id.text_result);
        buttonCamera = findViewById(R.id.btn_camera);
        buttonLogin = findViewById(R.id.btn_login);
        buttonRegister = findViewById(R.id.btn_register);
        buttonDatePicker = findViewById(R.id.btn_datepick);
        editName = findViewById(R.id.NameEdit);
        Date = findViewById(R.id.text_birth);

        retrofit = new Retrofit.Builder()
            .baseUrl("http://xxx.xxx.xxx.xxx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = checkLoginPermission();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.getText().toString().isEmpty() || Date.getText().toString().isEmpty() || faceImage == null){
                    Toast.makeText(getBaseContext(), "Not Enough Info", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean result = registerMember(editName.getText().toString(), Date.getText().toString());
            }
        });

        buttonDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //오늘 날짜(년,월,일) 변수에 담기
                Calendar calendar = Calendar.getInstance();
                int pYear = calendar.get(Calendar.YEAR); //년
                int pMonth = calendar.get(Calendar.MONTH);//월
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);//일
                datePickerDialog = new DatePickerDialog(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            //1월은 0부터 시작하기 때문에 +1을 해준다.
                                month = month + 1;
                                String date =
                                        String.format("%04d", year) + "-" +
                                        String.format("%02d", month) + "-" +
                                        String.format("%02d", day);
                                Date.setText(date);
                            }
                        }, pYear, pMonth, pDay);
                datePickerDialog.show();
            } //onClick
        });
    }


    private FaceDataResource resultData;

    public boolean checkLoginPermission() {
        File file = saveBitmap("picture_image");
        if(file == null) return false;

        buttonLogin.setEnabled(false);
        FaceAuthService service = retrofit.create(FaceAuthService.class);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part postBody = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        Call<FaceDataResource> call = service.AuthFace(postBody);

        call.enqueue(new Callback<FaceDataResource>() {
            @Override
            public void onResponse(Call<FaceDataResource> call, Response<FaceDataResource> response) {
                buttonLogin.setEnabled(true);
                if(response.isSuccessful()){
                    FaceDataResource result = response.body();
                    resultText.setText(result.name);
                    resultData = result;
                    resultCode = result.statusResult;

                    if(resultCode == 200) {
                        Intent resultActivity = new Intent(MainActivity.this, ResultActivity.class);

                        resultActivity.putExtra("image", faceImage);
                        resultActivity.putExtra("name", resultData.name);
                        resultActivity.putExtra("birth", resultData.birth);

                        startActivityForResult(resultActivity, REQUEST_CODE_LOGIN);
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "정보를 불러올 수 없음", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onFailure(Call<FaceDataResource> call, Throwable t) {
                buttonLogin.setEnabled(true);
                Log.d("Fail", "연결이 원활하지 않습니다. :" + t.getMessage());
            }
        });


        return true;
    }


    public boolean registerMember(String name, String birth) {
        File file = saveBitmap(name);
        if(file == null) return false;

        buttonRegister.setEnabled(false);
        FaceAuthService service = retrofit.create(FaceAuthService.class);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part postBody = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        Call<FaceDataResource> call = service.RegisterFace(postBody, name, birth);

        call.enqueue(new Callback<FaceDataResource>() {
            @Override
            public void onResponse(Call<FaceDataResource> call, Response<FaceDataResource> response) {
                buttonRegister.setEnabled(true);
                if(response.isSuccessful()){
                    FaceDataResource result = response.body();
                    resultText.setText(result.name);

                    Log.d("Success", "성공");
                }
                else {
                    Log.d("Fail", "비정상적 접근");
                }

            }

            @Override
            public void onFailure(Call<FaceDataResource> call, Throwable t) {
                buttonRegister.setEnabled(true);
                Log.d("Fail", "연결이 원활하지 않습니다. :" + t.getMessage());
            }
        });


        return true;
    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            faceImage = imageBitmap;
            imageView.setImageBitmap(faceImage);
        }
        else if(requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK){

        }
    }


    private File saveBitmap(String pictureName)
    {
        if(faceImage == null) {
            Toast.makeText(getBaseContext(), "No Image", Toast.LENGTH_SHORT);
            return null;
        }
        File file = new File(getApplicationContext().getFilesDir(), pictureName + ".png");
        OutputStream out = null;
        try{
            file.createNewFile();

            out = new FileOutputStream(file);

            faceImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return file;
    }
}