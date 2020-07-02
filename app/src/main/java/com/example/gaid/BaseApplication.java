package com.example.gaid;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.airbnb.lottie.LottieAnimationView;

public class BaseApplication extends Application {
    public static BaseApplication baseApplication;
    private LottieAnimationView animationView2;
    AppCompatDialog progressDialog;
    public static BaseApplication getInstance() {
        baseApplication = new BaseApplication();
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();
        }
        animationView2 = (LottieAnimationView)progressDialog.findViewById(R.id.animation_view);
        animationView2.setAnimation("elevator.json");
        animationView2.loop(true);

        //Lottie Animation start
        animationView2.playAnimation();
//        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
//        if (!TextUtils.isEmpty(message)) {
//            tv_progress_message.setText(message);
//        }
    }

    public void progressSET(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
//        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
//        if (!TextUtils.isEmpty(message)) {
//            tv_progress_message.setText(message);
//        }
    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

