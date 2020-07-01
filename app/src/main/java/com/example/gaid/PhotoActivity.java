package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gaid.data.SendPictureRepository;
import com.example.gaid.model.send_picture.SendPictureResponseDTO;
import com.example.gaid.picture.PictureContract;
import com.example.gaid.picture.PicturePresenter;
import com.example.gaid.weather.WeatherPresenter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.google.zxing.BarcodeFormat.QR_CODE;

public class PhotoActivity extends Activity implements PictureContract.View {
    private ImageView photoView;
    private Bitmap image;
    private Button btn_qr;
    private String qr_url;
    private SendPictureRepository mSendPictureRepository;
    private PicturePresenter mPicturePresenter;
    private int flag=0;
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
                System.out.println(qr_url+"Tlqk");
                generateQRCode("http://172.16.16.136:8080/files/"+qr_url);
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
        qr_url=getIntent().getStringExtra("filename");
        if (getIntent() != null) {
            image = BitmapFactory.decodeFile(getIntent().getStringExtra("key"));
            photoView.setImageBitmap(image);
            //형걸이형 여기야 여기!!
            ///ㅋㅋㅋㅋㅋㅋㅋㅋ 개웃기누 < ㅄ

            String pictureFilePath = intent.getStringExtra("key");
            File pictureFile = new File(pictureFilePath);
            Bitmap bitmap2 = BitmapFactory.decodeFile(getIntent().getStringExtra("key"));
            photoView.setImageBitmap(image);

            RequestBody imgFileReqBody = RequestBody.create(MediaType.parse("image/*"), pictureFile);
            MultipartBody.Part image2 = MultipartBody.Part.createFormData("files", pictureFile.getName(), imgFileReqBody);
            mSendPictureRepository = new SendPictureRepository(image2);
            mPicturePresenter = new PicturePresenter(mSendPictureRepository, this);
            mPicturePresenter.sendPictureDataToServer(image2);

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


    @Override
    public void showServerResponse(SendPictureResponseDTO sendPictureResponseDTO) {
        Toast.makeText(this, sendPictureResponseDTO.getMsg(), Toast.LENGTH_LONG).show();
        System.out.println(sendPictureResponseDTO.getMsg()+"anjdi");
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        System.out.println(message+"dlgudrjf");
    }
}
