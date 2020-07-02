package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class PopUpActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextToSpeech textToSpeech;
    private Button mButton_info;
    private Button mButton_map;
    private SpeechRecognizer mRecognizer;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        init();
        speakOut("어떤 기능을 원하시나요");
        initListener();
    }

    public void init() {
        mButton_info = findViewById(R.id.btn_info);
        mButton_map = findViewById(R.id.btn_map);
        textToSpeech = new TextToSpeech(this, this);
        mIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
//        mRecognizer= SpeechRecognizer.createSpeechRecognizer(this);
//        mRecognizer.setRecognitionListener(listener);
    }

    public void initListener() {
        mButton_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut("길 찾기 기능을 찾으셨군요");
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mButton_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut("안내 기능을 찾으셨군요");
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        boolean speakingEnd = textToSpeech.isSpeaking();
        Log.d("TTS", "SPOKE");
        do{
            speakingEnd = textToSpeech.isSpeaking();
        } while (speakingEnd);
        Log.d("done","spoke Done");
//        if(text.equals("안녕하세요 대양AI센터 입니다 무엇을 도와드릴까요?")) {
//            mRecognizer.startListening(mIntent);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.KOREA);
            // tts.setPitch(5); // set pitch level
            // tts.setSpeechRate(2); // set speech speed rate
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            }
            else {
            }
        }
        else {
            Log.e("TTS", "Initilization Failed");
        }
    }
}
