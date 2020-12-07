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
    Bitmap bitmap;
    TextInputLayout name,num,email;
    private Button download,generate;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);
        name = findViewById(R.id.name);
        num = findViewById(R.id.num);
        email = findViewById(R.id.email);
        download = findViewById(R.id.download);
        download.setVisibility(View.INVISIBLE);
        generate = findViewById(R.id.generate);
        iv = findViewById(R.id.image1);

        generate.setOnClickListener(v -> {
            if (name.getEditText().getText().toString().trim().length() == 0){
                name.getEditText().requestFocus();
                name.setError("Enter Name");
            }else if (num.getEditText().getText().toString().trim().length() == 0){
                num.getEditText().requestFocus();
                num.setError("Enter Number");
            }else if (email.getEditText().getText().toString().trim().length() == 0){
                email.getEditText().requestFocus();
                email.setError("Enter Number");
            } else if (name.getEditText().getText().toString().trim().length() != 0 &&
                    num.getEditText().getText().toString().trim().length() != 0 &&
                    email.getEditText().getText().toString().trim().length() != 0){
                name.setError(null);
                num.setError(null);
                email.setError(null);
                try {
                    JSONObject person = new JSONObject();
                    try {
                        person.put("Name", name.getEditText().getText().toString());
                        person.put("Number", num.getEditText().getText().toString());
                        person.put("Email", email.getEditText().getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String jsonStr = person.toString();
                    System.out.println("jsonString: "+jsonStr);


                    bitmap = textToImageEncode(jsonStr);
                    iv.setImageBitmap(bitmap);
                    download.setVisibility(View.VISIBLE);
                    download.setOnClickListener(v1 -> shareImage());
                }catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap textToImageEncode(String value) throws WriterException{
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
        }catch (IllegalArgumentException e){
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0;y < bitMatrixHeight;y++){
            int offSet = y*bitMatrixHeight;
            for (int x=0; x<bitMatrixWidth; x++){
                pixels[offSet + x] = bitMatrix.get(x,y)?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth,bitMatrixHeight,Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void shareImage(){

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bt = drawable.getBitmap();
        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name)+ ".png");
        Intent shareint;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            bt.compress(Bitmap.CompressFormat.PNG,100,outputStream);

            outputStream.flush();
            outputStream.close();
            shareint = new Intent(Intent.ACTION_SEND);
            shareint.setType("images/*");
            shareint.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(shareint,"share"));
    }
}