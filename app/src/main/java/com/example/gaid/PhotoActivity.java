package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileNotFoundException;

import static com.google.zxing.BarcodeFormat.QR_CODE;

public class PhotoActivity extends Activity {
    private ImageView photoView;
    private Bitmap image;
    private Button btn_qr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        System.out.println("머야");
        photoView = (ImageView) findViewById(R.id.photoview);
        btn_qr = (Button) findViewById(R.id.btn_qr);
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode("www.naver.com");
            }
        });
     /*   Intent intent = getIntent();
        Bitmap bit = (Bitmap) intent.getParcelableExtra("image");
        photoView = (ImageView) findViewById(R.id.photoview);
        photoView.setImageBitmap(bit);

      */
        Intent intent = getIntent();
//        byte[] arr=getIntent().getByteArrayExtra("image");
//        image= BitmapFactory.decodeByteArray(arr,0,arr.length);
//        photoView.setImageBitmap(image);
        if (getIntent() != null) {
            try {
                image = BitmapFactory.decodeStream(openFileInput(getIntent().getStringExtra("key")));
                photoView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


    }

    public void generateQRCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(contents, QR_CODE, 100, 100));
            photoView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }


}
