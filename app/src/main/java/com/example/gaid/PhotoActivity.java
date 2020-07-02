package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private static final int MILLISINFUTURE = 20 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;


    private ImageView photoView;
    private Bitmap bitmap_photo, bitmap_qr;
    private Button btn_qr;
    private String qr_url;
    private SendPictureRepository mSendPictureRepository;
    private PicturePresenter mPicturePresenter;
    private int flag = 0;
    private int count = 20;
    private CountDownTimer countDownTimer;
    private TextView tv_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        System.out.println("머야");
        photoView = (ImageView) findViewById(R.id.photoview);
        tv_count = (TextView) findViewById(R.id.tv_count);
        btn_qr = (Button) findViewById(R.id.btn_qr);

        generateQRCode("www.naver.com");
        //generateQRCode("http://172.16.16.136:8080/viewImage/"+qr_url);
        //http://172.16.16.136:8080/viewImage/n123213411
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(qr_url + "Tlqk");
                if (flag == 0) {
                    System.out.println("0ssook" + flag);
                    photoView.setImageBitmap(bitmap_qr);
                    btn_qr.setText("사진 보기");
                    flag = 1;
                } else if (flag == 1) {
                    System.out.println("1ssook" + flag);
                    bitmap_photo = BitmapFactory.decodeFile(getIntent().getStringExtra("key"));
                    photoView.setImageBitmap(bitmap_photo);
                    btn_qr.setText("QR코드 보기");
                    flag = 0;

                }

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
        qr_url = getIntent().getStringExtra("filename");
        if (getIntent() != null) {
            bitmap_photo = BitmapFactory.decodeFile(getIntent().getStringExtra("key"));
            photoView.setImageBitmap(bitmap_photo);

/*
            RequestBody imgFileReqBody = RequestBody.create(MediaType.parse("image/*"), pictureFile);
            MultipartBody.Part image2 = MultipartBody.Part.createFormData("files", pictureFile.getName(), imgFileReqBody);
            mSendPictureRepository = new SendPictureRepository(image2);
            mPicturePresenter = new PicturePresenter(mSendPictureRepository, this);
            mPicturePresenter.sendPictureDataToServer(image2);


 */
            countDownTimer();
            countDownTimer.start();
        }
    }

    public void generateQRCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            bitmap_qr = toBitmap(qrCodeWriter.encode("www.naver.com", QR_CODE, 100, 100));
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
        System.out.println(sendPictureResponseDTO.getMsg() + "anjdi");
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        System.out.println(message + "dlgudrjf");
    }

    //////////////////////////////////////////////////////////////////////////////
    public void countDownTimer() {

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                tv_count.setText(String.valueOf(count)+"초");
                count--;
                if(count==0){
                    Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                    startActivity(intent);
                }
                System.out.println(count + "ssssssss");
            }

            public void onFinish() {
                System.out.println("ssss");
//                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
//                startActivity(intent);
            }
        };
    }


//    private class ExampleThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                // 스레드에게 수행시킬 동작들 구현
//                Thread.sleep(60000); // 1초간 Thread를 잠재운다
//                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
//                startActivity(intent);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }


}
