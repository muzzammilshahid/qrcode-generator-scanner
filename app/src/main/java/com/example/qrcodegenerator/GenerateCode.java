package com.example.qrcodegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class GenerateCode extends AppCompatActivity {

    //vars
    public final static int QRCodeWidth = 500;

    //widgets
    private Bitmap bitmap;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutNumber;
    private TextInputLayout textInputLayoutEmail;
    private Button downloadButton;
    private Button generateButton;
    private ImageView imageViewQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        textInputLayoutName = findViewById(R.id.name_input_layout);
        textInputLayoutNumber = findViewById(R.id.number_input_layout);
        textInputLayoutEmail = findViewById(R.id.email_input_layout);
        downloadButton = findViewById(R.id.share_button);
        downloadButton.setVisibility(View.INVISIBLE);
        generateButton = findViewById(R.id.qrcode_generate_button);
        imageViewQrcode = findViewById(R.id.qrcode_imageview);

        generateButton.setOnClickListener(v -> {
            if (textInputLayoutName.getEditText().getText().toString().trim().length() == 0) {
                textInputLayoutName.getEditText().requestFocus();
                textInputLayoutName.setError("Enter Name");
            } else if (textInputLayoutNumber.getEditText().getText().toString().trim().length() == 0) {
                textInputLayoutNumber.getEditText().requestFocus();
                textInputLayoutNumber.setError("Enter Number");
            } else if (textInputLayoutEmail.getEditText().getText().toString().trim().length() == 0) {
                textInputLayoutEmail.getEditText().requestFocus();
                textInputLayoutEmail.setError("Enter Number");
            } else if (textInputLayoutName.getEditText().getText().toString().trim().length() != 0 &&
                    textInputLayoutNumber.getEditText().getText().toString().trim().length() != 0 &&
                    textInputLayoutEmail.getEditText().getText().toString().trim().length() != 0) {
                textInputLayoutName.setError(null);
                textInputLayoutNumber.setError(null);
                textInputLayoutEmail.setError(null);
                try {
                    JSONObject person = new JSONObject();
                    try {
                        person.put("Name", textInputLayoutName.getEditText().getText().toString());
                        person.put("Number", textInputLayoutNumber.getEditText().getText().toString());
                        person.put("Email", textInputLayoutEmail.getEditText().getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String jsonStr = person.toString();
                    System.out.println("jsonString: " + jsonStr);


                    bitmap = textToImageEncode(jsonStr);
                    imageViewQrcode.setImageBitmap(bitmap);
                    downloadButton.setVisibility(View.VISIBLE);
                    downloadButton.setOnClickListener(v1 -> shareImage());
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap textToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
        } catch (IllegalArgumentException e) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offSet = y * bitMatrixHeight;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offSet + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void shareImage() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        BitmapDrawable drawable = (BitmapDrawable) imageViewQrcode.getDrawable();
        Bitmap bt = drawable.getBitmap();
        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + ".png");
        Intent shareIntent;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            bt.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("images/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(shareIntent, "share"));
    }
}