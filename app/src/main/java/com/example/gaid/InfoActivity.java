package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private ImageView imgview;
    private TextView tv_profname,tv_etc,tv_tel,tv_dept;
    private Intent intent;
    private String user_text="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tv_etc=(TextView)findViewById(R.id.tv_etc);
        tv_profname=(TextView)findViewById(R.id.tv_profname);
        tv_tel=(TextView)findViewById(R.id.tv_tel);

        intent=getIntent();
        user_text=intent.getStringExtra("text");
        System.out.println("testtext :"+user_text );
        ///형걸이형 여기야여기!
    }
}
