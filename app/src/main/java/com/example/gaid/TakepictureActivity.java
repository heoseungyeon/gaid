package com.example.gaid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class TakepictureActivity extends Activity {

    SurfaceView sv_viewFinder;
    SurfaceHolder sh_viewFinder;
    Camera camera;
    Button btn_shutter;
    ImageView iv_preview;
    boolean inProgress = false;
    private String filename="";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);
        // findViewById
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        sv_viewFinder = (SurfaceView) findViewById(R.id.sv_viewFinder);
        sh_viewFinder = sv_viewFinder.getHolder();
        sh_viewFinder.addCallback(surfaceListener);
        sh_viewFinder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btn_shutter = (Button) findViewById(R.id.btn_shutter);


        // setListener
        btn_shutter.setOnClickListener(onClickListener_btn_shutter);

        // 3초 뒤 자동촬영
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {

            @Override

            public void run() {
                startTakePicture();
            }

        };

        timer.schedule(tt, 3000);

    }


    public SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i("1", "sufraceListener 카메라 미리보기 활성");
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.startPreview();
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i("1", "sufraceListener 카메라 오픈");
            int int_cameraID = 0;

            /* 카메라가 여러개 일 경우 그 수를 가져옴  */
            int numberOfCameras = Camera.getNumberOfCameras();
            CameraInfo cameraInfo = new CameraInfo();

            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                // 전면카메라
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
                    int_cameraID = i;
                // 후면카메라
                //if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
                //  int_cameraID = i;
            }
            camera = Camera.open(int_cameraID);
            try {
                camera.setPreviewDisplay(sh_viewFinder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i("1", "sufraceListener 카메라 해제");
            camera.release();
            camera = null;
        }
    };


    /**
     * @brief : 사진 찍기
     * @date : 2014. 10. 14.
     * @author : 김동영
     */

    public void startTakePicture() {
        if (camera != null && inProgress == false) {
            camera.takePicture(null, null, takePicture);
            inProgress = true;
        }
    }


    View.OnClickListener onClickListener_btn_shutter = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startTakePicture();
        }
    };


    public Camera.PictureCallback takePicture = new Camera.PictureCallback() {

        @Override

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("1", "=== takePicture ===");
            if (data != null) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile != null && pictureFile.getAbsolutePath() != null) {

                    Log.v("1", "takePicture JPEG 사진 찍음");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    iv_preview.setImageBitmap(bitmap);
                    camera.startPreview();
                    inProgress = false;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    long now = System.currentTimeMillis();

                    System.out.println("ssook1");
                    Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                    System.out.println("ssook2");
                    try {
                        intent.putExtra("key", saveBitmap(bitmap));
                        intent.putExtra("filename","n"+filename);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ssook3");
                    //Log.d("pictureFilePath : ", pictureFile.getAbsolutePath());
                    //intent.putExtra("pictureFilePath", pictureFile.getAbsolutePath());
                    startActivity(intent);
                    System.out.println("ssook4");

                } else {
                    Log.d("pictureFile", "Error creating media file, check storage permissions");
                }
            } else {
                Log.e("1", "takePicture data null");
            }
        }

    };

    public String saveBitmap(Bitmap bitmap) throws FileNotFoundException {
//        File file = new File("filepath");
//        final FileOutputStream filestream;
//        File file2= Environment.getExternalStorageDirectory();
//        filestream = new FileOutputStream(file);
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, filestream);
        String path = null;
        OutputStream out = null;

        try {
            filename=System.currentTimeMillis()+".jpg";
            path = Environment.getExternalStorageDirectory() + "/Pictures/MyCameraApp/n" + filename;

            out = new FileOutputStream(path);
            if(out==null){
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "MyCameraApp");
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("MyCameraApp", "failed to create directory");
                        return null;
                    }
                }

            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath(),
                    "IMG_" + timeStamp + ".jpg");
//            File tempFile = new File(storage, fileName);

        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

//    public void passImage(){
//        System.out.println("ssook1");
//        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
//        System.out.println("ssook2");
//        intent.putExtra("key", key);
//        System.out.println("ssook3");
//        Log.d("pictureFilePath : ", pictureFile.getAbsolutePath());
//        intent.putExtra("pictureFilePath", pictureFile.getAbsolutePath());
//        startActivity(intent);
//        System.out.println("ssook4");
//    }
}



