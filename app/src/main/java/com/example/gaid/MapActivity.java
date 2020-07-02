package com.example.gaid;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;

public class MapActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextView tv1;
    private ArrayList<Integer> paths;
    private LottieAnimationView animationView;
    private View allview;
    private PriorityQueue<Node> openList;
    private ArrayList<Node> closedList;
    HashMap<Node, Integer> gMaps;
    HashMap<Node, Integer> fMaps;
    private Node n[];
    private int initialCapacity = 100;
    private int distanceBetweenNodes = 1;
    private int goal = 0;
    final int PERMISSION = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;
    private int room[];
    private int path[];
    private TextToSpeech textToSpeech;
    private GridView gv;

    private ImageButton ib_back;
    private int[] img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_1f);

        textToSpeech = new TextToSpeech(this, this);
        ib_back=(ImageButton)findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        setContentView(R.layout.activity_map);
//        System.out.println("맵액티비티");
//        tv1=findViewById(R.id.node1);
//
//        allview=findViewById(R.id.allview);
//        allview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });
        init();

        //goal = 6;
        //search(n[4], n[goal]);

        // 1. 다량의 데이터
        // 2. Adapter
        // 3. AdapterView - GridView

        img = new int[60];
        for (int i = 0; i < img.length; i++) {
            img[i] = R.drawable.path;
        }

        // 커스텀 아답타 생성
        MyAdapter adapter = new MyAdapter(
                getApplicationContext(),
                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                img, n, paths);    // 데이터

        gv = (GridView) findViewById(R.id.gridView1);
        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용


        // GridView 아이템을 클릭하면 상단 텍스트뷰에 position 출력
        // JAVA8 에 등장한 lambda expression 으로 구현했습니다. 코드가 많이 간결해지네요
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                paths.clear();

                boolean check = false;

                for(int i=0;i<room.length;i++){
                    if(position==room[i]){
                        check = true;
                    }
                }
                if(check==true){
                    search(n[39], n[position]);

                    Toast.makeText(getApplicationContext(), Integer.toString(position) + "호실 경로입니다!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "해당 위치는 경로를 제공하지 않습니다ㅠ.ㅠ", Toast.LENGTH_SHORT).show();

                }
                // 커스텀 아답타 생성
                MyAdapter adapter = new MyAdapter(
                        getApplicationContext(),
                        R.layout.row,       // GridView 항목의 레이아웃 row.xml
                        img, n, paths);    // 데이터

                gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용



            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        //Lottie Animation
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("listen.json");
        animationView.loop(true);
        animationView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.playAnimation();
                speakOut("가고싶은 호실을 말해주세요");
            }
        });
        //Lottie Animation start
        animationView.playAnimation();



    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.KOREA);
            Log.d("tts", "oninit");

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                speakOut("가고싶은 호실을 말해주세요");

            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }

/*
         * X = Walls
         * N1 => Start
         * N8 => Goal
         *
        N0 - N3 - N5 - N8
        |         |
        N1   X    N6    X
        |         |
        N2 - N4 - N7 - N9
         */

    /*
    N0 - N1 - N2 - N3
    |         |
    N4   N5   N6   N7
    |         |
    N8 - N9 - N10 - N11

     */
    public void init() {
        paths = new ArrayList<>();
        gMaps = new HashMap<Node, Integer>();
        fMaps = new HashMap<Node, Integer>();
        openList = new PriorityQueue<Node>(initialCapacity, new fCompare());
        closedList = new ArrayList<Node>();

        n = new Node[60];
        for (int i = 0; i < n.length; i++) {
            n[i] = new Node();
            n[i].setData(i);
            n[i].setKind("W");
        }
        n[29].setKind("E");
        room = new int[]{7, 13, 21, 22, 23, 37, 45, 46, 47, 55};
        path = new int[]{8, 14, 15, 19, 20, 26, 27, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 56};
        for (int i = 0; i < room.length; i++) {
            n[room[i]].setKind("R");
        }
        for (int i = 0; i < path.length; i++) {
            n[path[i]].setKind("P");
        }

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                n[k].setXY(j, i);
                k++;
            }
        }
        n[7].addNeighbors(n[19], n[8]);
        n[8].addNeighbors(n[7], n[20]);
        n[13].addNeighbors(n[14]);
        n[14].addNeighbors(n[13], n[26], n[15]);
        n[15].addNeighbors(n[14], n[27]);
        n[19].addNeighbors(n[7], n[20], n[31]);
        n[20].addNeighbors(n[8], n[19], n[32]);
        n[21].addNeighbors(n[33]);
        n[22].addNeighbors(n[34]);
        n[23].addNeighbors(n[35]);
        n[26].addNeighbors(n[14], n[27], n[38]);
        n[27].addNeighbors(n[15], n[26], n[39]);
        n[29].addNeighbors(n[41]);                  //E
        n[31].addNeighbors(n[19], n[32], n[43]);
        n[32].addNeighbors(n[20], n[31], n[33], n[44]);
        n[33].addNeighbors(n[21], n[32], n[34], n[45]);
        n[34].addNeighbors(n[22], n[33], n[35], n[46]);
        n[35].addNeighbors(n[23], n[34], n[47]);
        n[37].addNeighbors(n[28]);
        n[38].addNeighbors(n[26], n[37], n[39]);
        n[39].addNeighbors(n[27], n[38], n[40]);
        n[40].addNeighbors(n[39], n[41]);
        n[41].addNeighbors(n[40], n[29], n[42]);
        n[42].addNeighbors(n[41], n[43]);
        n[43].addNeighbors(n[31], n[42], n[44], n[55]);
        n[44].addNeighbors(n[32], n[43], n[56]);
        n[45].addNeighbors(n[33]);
        n[46].addNeighbors(n[34]);
        n[47].addNeighbors(n[35]);
        n[55].addNeighbors(n[43], n[56]);
        n[56].addNeighbors(n[44], n[55]);
    }


    public void search(Node start, Node end) {
        openList.clear();
        closedList.clear();

        gMaps.put(start, 0);
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.element();
            if (current.equals(end)) {
                System.out.println("Goal Reached");
                printPath(current);
                return;
            }
            closedList.add(openList.poll());
            ArrayList<Node> neighbors = current.getNeighbors();

            for (Node neighbor : neighbors) {
                int gScore = gMaps.get(current) + distanceBetweenNodes;
                int fScore = gScore + h(neighbor, current);

                if (closedList.contains(neighbor)) {

                    if (gMaps.get(neighbor) == null) {
                        gMaps.put(neighbor, gScore);
                    }
                    if (fMaps.get(neighbor) == null) {
                        fMaps.put(neighbor, fScore);
                    }

                    if (fScore >= fMaps.get(neighbor)) {
                        continue;
                    }
                }
                if (!openList.contains(neighbor) || fScore < fMaps.get(neighbor)) {
                    neighbor.setParent(current);
                    gMaps.put(neighbor, gScore);
                    fMaps.put(neighbor, fScore);
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);

                    }
                }
            }
        }
        System.out.println("FAIL");
    }

    private int h(Node node, Node goal) {
        int x = node.getX() - goal.getX();
        int y = node.getY() - goal.getY();
        return x * x + y * y;
    }

    private void printPath(Node node) {
        System.out.println(node.getData());
        paths.add(Integer.parseInt(node.getData().toString()));
        while (node.getParent() != null) {
            node = node.getParent();
            paths.add(Integer.parseInt(node.getData().toString()));
            System.out.println(node.getData());
        }
    }

    private void speakOut(String text) {
        boolean speakingEnd;

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        Log.d("TTS", "SPOKE");
        do {
            speakingEnd = textToSpeech.isSpeaking();
        } while (!speakingEnd);
        do {
            speakingEnd = textToSpeech.isSpeaking();
            Log.d("speaking", "speaking");
        } while (speakingEnd);
        Log.d("done", "spoke Done");
        if (text.equals("가고싶은 호실을 말해주세요")) {
            Log.d("done", "spoke Don222e");

            mRecognizer.startListening(intent);
        } else if (text.equals("다시 한번 말씀해 주세요")) {
            mRecognizer.startListening(intent);
        }
    }

    public class fCompare implements Comparator<Node> {

        public int compare(Node o1, Node o2) {
            if (fMaps.get(o1) < fMaps.get(o2)) {
                return -1;
            } else if (fMaps.get(o1) > fMaps.get(o2)) {
                return 1;
            } else {
                return 1;
            }
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
            speakOut("다시 한번 말씀해 주세요");
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
            paths.clear();

            String text = "";
            String intText = "";
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < matches.size(); i++) {
                Log.d("match", matches.get(i));
                intText = matches.get(i).replaceAll("[^0-9]", "");
                Log.d("intText", intText);
                if (!intText.equals("")) {
                    break;
                }
            }
            if (!intText.equals("")) {
                animationView.cancelAnimation();
                goal = Integer.parseInt(intText);
                boolean check = false;
                for (int i = 0; i < room.length; i++) {
                    if (room[i] == goal) {
                        check = true;
                    }
                }
                if (check == true) {
                    search(n[39], n[goal]);

                    Toast.makeText(getApplicationContext(), Integer.toString(goal) + "호실 경로입니다!", Toast.LENGTH_SHORT).show();

                    // 커스텀 아답타 생성
                    MyAdapter adapter = new MyAdapter(
                            getApplicationContext(),
                            R.layout.row,       // GridView 항목의 레이아웃 row.xml
                            img, n, paths);    // 데이터

                   gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                }
                else{
                    Toast.makeText(getApplicationContext(), "해당 호실은 없습니다ㅠ.ㅠ", Toast.LENGTH_SHORT).show();

                }
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };


}//Class