package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;


public class SoundActivity extends Activity implements TextToSpeech.OnInitListener{
    private TextToSpeech textToSpeech;
    private LottieAnimationView animationView;
    private TextView sttResultTextView;
    private ImageButton ib_back;
    final int PERMISSION = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;

    private String mCase;
    //Komoran 형태소 분석기
    private Komoran mKomoran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        ib_back=(ImageButton)findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(intent);
            }
        });

        init();
        //Lottie Animation
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("listen.json");
        animationView.loop(true);
        animationView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.playAnimation();
            }
        });
        //Lottie Animation start
        animationView.playAnimation();
        mRecognizer.startListening(intent);
    }

    public void init()
    {

        sttResultTextView = findViewById(R.id.sttResult);
        textToSpeech = new TextToSpeech(this, this);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
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

            mKomoran = new Komoran("models_full");
            mKomoran.setFWDic("user_data/fwd.user");
            mKomoran.setUserDic("user_data/dic.user");

            String text = "";
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            animationView.cancelAnimation();

//            for(int i = 0; i < matches.size() ; i++){
//                sttResultTextView.setText(matches.get(i));
//            }

            sttResultTextView.setText(matches.get(0));
            text = sttResultTextView.getText().toString();

            KomoranResult analyzeResultList = mKomoran.analyze(text);
            //List<Token> tokenList = analyzeResultList.getTokenList();
            mCase = checkCase(analyzeResultList.getMorphesByTags("VV", "NP", "NNG", "SN")); //NNB


            if (mCase.contains("MapActivity")) {
                speakOut("길찾기기능을 찾으셨군요");
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
<<<<<<< HEAD
            }
            else if (text.contains("소개")) {
                speakOut("소개 해달라구요?");

            }
            else if (mCase.contains("TakepictureActivity")) {
=======
            } else if (text.contains("소개")||text.contains("소계")||text.contains("속의")) {
                speakOut("소개 해달라구요?");
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);

            } else if (text.contains("사진")) {
>>>>>>> c2fbbb25dea3c83ee7c67cbef7574f326c204077
                speakOut("기념사진찍어드릴게요");
                Intent intent = new Intent(getApplicationContext(), TakepictureActivity.class);
                startActivity(intent);
            }
            else if (text.contains("대양")) {
                speakOut("대양이를 불르셨어요?");
                Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
                startActivity(intent);
<<<<<<< HEAD
=======
            } else {
                speakOut("다시 한번 말해주세요");
                mRecognizer.startListening(intent);

>>>>>>> c2fbbb25dea3c83ee7c67cbef7574f326c204077
            }
            else {
                speakOut("다시 한번 말해주세요.");
            }

            //            if (text.contains("길")||text.contains("어떻게 가")||text.contains("어디에 있어")) {
//                speakOut("길찾기기능을 찾으셨군요");
//                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
//                startActivity(intent);
//            } else if (text.contains("소개")) {
//                speakOut("소개 해달라구요?");
//            } else if (text.contains("사진")) {
//                speakOut("기념사진찍어드릴게요");
//                Intent intent = new Intent(getApplicationContext(), TakepictureActivity.class);
//                startActivity(intent);
//            }else if (text.contains("대양")) {
//                speakOut("대양이를 불르셨어요?");
//                Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
//                startActivity(intent);
//            } else {
//                speakOut("다시 한번 말해주세요");
//            }
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

    public String checkCase(List<String> analyzeResultList) {
        String checkCase = null;
        if (analyzeResultList.contains("가")) { //길 찾기
            checkCase = "MapActivity";
        } else if (analyzeResultList.contains("찾")) { //길 찾기
            checkCase = "MapActivity";
        } else if (analyzeResultList.contains("찍")) { //기념사진
            checkCase = "TakepictureActivity";
        }

        if (analyzeResultList.contains("어디")) { //길 찾기
            checkCase = "MapActivity";
        }

        if (analyzeResultList.contains("길")) { //길 찾기
            checkCase = "MapActivity";
        } else if (analyzeResultList.contains("소개")) { //안내

        } else if (analyzeResultList.contains("사진")) { // 기념사진
            checkCase = "TakepictureActivity";
        } else if (analyzeResultList.contains("촬영")) { //기념사진
            checkCase = "TakepictureActivity";
        }

        return checkCase;
    }
}
