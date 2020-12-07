package com.example.qrcodegenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    //variables
    public static final int CAMERA_PERMISSION_CODE = 100;

    //widgets
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button generate,scan,gallery;
        generate = findViewById(R.id.generate);
        scan = findViewById(R.id.scan);
        gallery = findViewById(R.id.gallery);

        checkPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);

        generate.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,GenerateCode.class)));

        scan.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,ScanActivity.class)));

        gallery.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,Gallery.class));
        });
    }

    public void checkPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(MainActivity.this,permission)
        == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission},
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE)
            if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
    }

}