package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.gaid.data.LocationWeatherRepository;
import com.example.gaid.data.WeatherRepository;
import com.example.gaid.model.Weather;
import com.example.gaid.weather.WeatherContract;
import com.example.gaid.weather.WeatherPresenter;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener, WeatherContract.View {

    private TextView tv_temperature;
    private TextView tv_weather;
    private WeatherRepository mRepository;
    private WeatherPresenter mPresenter;

    private VideoView mVideoview;
    private TextToSpeech textToSpeech;
    final int PERMISSION = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoview = (VideoView) findViewById(R.id.vv_main);


        //play video
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.backvideo);
        mVideoview.setVideoURI(uri);
        mVideoview.start();
        //loop
        mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0,0);
            }
        });
        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        init();

        //Intent intent=new Intent(getApplicationContext(),DetectorActivity.class);
        //startActivity(intent);

        mVideoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
                startActivity(intent);
            }
        });
    }
    public void init()
    {
        textToSpeech = new TextToSpeech(this, this);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        tv_weather = findViewById(R.id.tv_weatherSummary);
        tv_temperature = findViewById(R.id.tv_weatherTemperature);
        mRepository = new LocationWeatherRepository();
        mPresenter = new WeatherPresenter(mRepository, this);
        mPresenter.loadWeatherData();
    }
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        boolean speakingEnd = textToSpeech.isSpeaking();
        Log.d("TTS", "SPOKE");
        do{
            speakingEnd = textToSpeech.isSpeaking();
        } while (speakingEnd);
        Log.d("done","spoke Done");
        if(text.equals("안녕하세요 대양AI센터 입니다 무엇을 도와드릴까요?")){
            mRecognizer.startListening(intent);
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:

                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.

            String text = "";
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < matches.size(); i++) {
                text += matches.get(i);
            }

            if (text.contains("길")||text.contains("어떻게 가")||text.contains("어디에 있어")) {
                speakOut("길찾기기능을 찾으셨군요");
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            } else if (text.contains("소개")) {
                speakOut("소개 해달라구요?");
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("text",text);
                startActivity(intent);

            } else if (text.contains("사진")) {
                speakOut("기념사진찍어드릴게요");
            }else if (text.contains("대양")) {
                speakOut("대양이를 불르셨어요?");
                Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
                startActivity(intent);
            } else {
                speakOut("제대로 좀 말해주세요 ㅋ");
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };



        @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.KOREA);


            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

    @Override
    public void showWeatherData(Weather weather) {
        try {
            double temperature = weather.getCurrently().getTemperature();
            temperature = (int)((temperature - 32) / 1.8);
            String weatherType = weatherType(weather);
            tv_weather.setText(weatherType);
            System.out.println(weatherType+temperature+"ssook!!");
            tv_temperature.setText(Double.toString(temperature));

        }
        catch (Exception e) {
            tv_weather.setText("일일 허용량 초과");
            tv_temperature.setText("일일 허용량 초과");
        }
    }

    @Override
    public void showLoadError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public String weatherType(Weather weather) {
        String weatherSummary = weather.getCurrently().getSummary();
        if (weatherSummary.contains("Overcast")) {
            weatherSummary = "  흐림  ";
        }
        return weatherSummary;
    }

    protected void onRestart() {
            super.onRestart();
            mVideoview = (VideoView) findViewById(R.id.vv_main);

            //play video
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backvideo);
            mVideoview.setVideoURI(uri);
            mVideoview.start();
            //loop
            mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    mp.setVolume(0, 0);
                }
            });
        }
}

