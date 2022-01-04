package com.example.qrcodegenerator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Gallery extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewNumber;
    private TextView textViewEmail;
    private Button browseButton;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        browseButton = findViewById(R.id.browse_button);
        textViewName = findViewById(R.id.textview_name);
        textViewNumber = findViewById(R.id.textview_number);
        textViewEmail = findViewById(R.id.textview_email);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result1 -> {
                    try {
                        final Uri imageUri = result1.getData().getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        try {
                            String contents;
                            int[] intArray = new int[selectedImage.getWidth() * selectedImage.getHeight()];
                            selectedImage.getPixels(intArray, 0, selectedImage.getWidth(), 0, 0, selectedImage.getWidth(), selectedImage.getHeight());
                            LuminanceSource source = new RGBLuminanceSource(selectedImage.getWidth(), selectedImage.getHeight(), intArray);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                            Reader reader = new MultiFormatReader();
                            Result result = reader.decode(bitmap);
                            contents = result.getText();

                            JSONObject jsonObject = new JSONObject(contents);
                            String name = jsonObject.getString("Name");
                            String num = jsonObject.getString("Number");
                            String email = jsonObject.getString("Email");
                            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(500);

                            textViewName.setText(name);
                            textViewNumber.setText(num);
                            textViewEmail.setText(email);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Pattern not match", Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                });

        browseButton.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            activityResultLauncher.launch(photoPickerIntent);
        });
    }
}