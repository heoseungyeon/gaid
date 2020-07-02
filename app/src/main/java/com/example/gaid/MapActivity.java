package com.example.gaid;


import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;

import retrofit2.http.HEAD;

import static java.lang.Thread.sleep;

public class MapActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextView tv1;
    private ArrayList<Integer> paths;
    private LottieAnimationView animationView;
    private LottieAnimationView animationView2;

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
    private ArrayList<Integer> room;
    private ArrayList<Integer> path;
    private TextToSpeech textToSpeech;
    private GridView gv;

    ViewDialog viewDialog;

    Intent myintent;
    String mRoomNumber;

    private Button button1f;
    private Button button2f;

    AppCompatDialog progressDialog;

    private BaseApplication baseApplication;

    private TextView floortext;

    private Button btn_toinfo;
    private ImageButton ib_back;
    private int[] img;

    private int floor = 1;

    private int first = 1;

    private boolean firstinit = true;

    //1f_room&path
    int room1list[] = {7, 13, 21, 22, 23, 37, 45, 46, 47, 55};
    int path1list[] = {8, 14, 15, 19, 20, 26, 27, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 56};

    //2f_room&path
    int room2list[] = {1, 10, 19, 25, 35, 49, 57, 59};
    int path2list[] = {2, 3, 15, 21, 22, 26, 27, 31, 32, 33, 39, 40, 41, 42, 43, 44, 45, 46, 47, 50, 51};


    private boolean isoninit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_1f);
        intent = getIntent();
        mRoomNumber = intent.getStringExtra("roomNo");
        goal = Integer.parseInt(mRoomNumber);

        baseApplication = new BaseApplication();
        textToSpeech = new TextToSpeech(this, this);


        btn_toinfo = (Button) findViewById(R.id.btn_toinfo);
        btn_toinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_in = new Intent(getApplicationContext(), InfoActivity.class);
                intent_in.putExtra("roomNo", Integer.toString(goal));
                startActivity(intent_in);
            }
        });
        ib_back = (ImageButton) findViewById(R.id.ib_back);
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
        viewDialog = new ViewDialog(this);

        // 1. 다량의 데이터
        // 2. Adapter
        // 3. AdapterView - GridView

        floortext = (TextView) findViewById(R.id.floortext);

        //1 floor&map init
        init();
        init_1f();

        //gridview init
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

        //button init
        button1f = (Button) findViewById(R.id.button1f);
        button1f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1f.setBackgroundColor(Color.YELLOW);
                button2f.setBackgroundColor(Color.WHITE);
                init();
                init_1f();
                floor = 1;
                // 커스텀 아답타 생성
                MyAdapter adapter = new MyAdapter(
                        getApplicationContext(),
                        R.layout.row,       // GridView 항목의 레이아웃 row.xml
                        img, n, paths);    // 데이터
                gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

            }

        });

        button2f = (Button) findViewById(R.id.button2f);
        button2f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                init_2f();
                button2f.setBackgroundColor(Color.YELLOW);
                button1f.setBackgroundColor(Color.WHITE);
                floor = 2;
                // 커스텀 아답타 생성
                MyAdapter adapter = new MyAdapter(
                        getApplicationContext(),
                        R.layout.row,       // GridView 항목의 레이아웃 row.xml
                        img, n, paths);    // 데이터
                gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
            }
        });

        button1f.setBackgroundColor(Color.YELLOW);
        button2f.setBackgroundColor(Color.WHITE);

        //back init
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // GridView 아이템을 클릭하면 상단 텍스트뷰에 position 출력
        // JAVA8 에 등장한 lambda expression 으로 구현했습니다. 코드가 많이 간결해지네요
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                boolean check = false;

                if (floor == 1) {
                    button1f.setBackgroundColor(Color.YELLOW);
                    button2f.setBackgroundColor(Color.WHITE);
                    init();
                    init_1f();
                    // 커스텀 아답타 생성
                    MyAdapter adapter = new MyAdapter(
                            getApplicationContext(),
                            R.layout.row,       // GridView 항목의 레이아웃 row.xml
                            img, n, paths);    // 데이터
                    gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
                } else if (floor == 2) {
                    init();
                    init_2f();
                    button2f.setBackgroundColor(Color.YELLOW);
                    button1f.setBackgroundColor(Color.WHITE);
                    // 커스텀 아답타 생성
                    MyAdapter adapter = new MyAdapter(
                            getApplicationContext(),
                            R.layout.row,       // GridView 항목의 레이아웃 row.xml
                            img, n, paths);    // 데이터
                    gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
                }

                for (int i = 0; i < room.size(); i++) {
                    Log.d("roomint", Integer.toString(room.get(i)));
                    if (position == room.get(i)) {
                        check = true;
                    }
                }

                if (check == true) {
                    goal=position;
                    search(n[39], n[position]);
                    Toast.makeText(getApplicationContext(), Integer.toString(position) + "호실 경로입니다!", Toast.LENGTH_SHORT).show();
                } else {
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

        //퍼미션 체크
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

                speakOut("안내시작합니다");

                //1,2층에 있는건지 확인
                boolean check = false;
                for(int i=0;i< room1list.length;i++){
                    if (room1list[i]==goal) {
                        check = true;
                    }
                }
                for(int i=0;i< room2list.length;i++){
                    if (room2list[i]==goal) {
                        check = true;
                    }
                }
                boolean anotherfloor21 = false;
                boolean anotherfloor12 = false;
                if (check == true) {
                    //2층인데 1층 찾을 경우
                    for(int i=0;i< room1list.length;i++){
                        if(floor==2 && room1list[i]==goal){
                            anotherfloor21=true;
                        }
                    }
                    //1층인데 2층 찾을 경우
                    for(int i=0;i< room2list.length;i++) {
                        if (floor == 1 && room2list[i] == goal) {
                            anotherfloor12=true;
                        }
                    }
                    if(anotherfloor21==true){
                        //엘레베이터 탑승
                        search(n[39], n[29]);
                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터

                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewDialog.showDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        speakOut("엘레베이터를 타고 1층으로 내려갑니다");
                                        speakOut("1층 경로입니다!");
                                    }
                                }).start();                            }
                        });



                        button1f.setBackgroundColor(Color.YELLOW);
                        button2f.setBackgroundColor(Color.WHITE);
                        init();
                        init_1f();
                        search(n[41], n[goal]);

                        floor = 1;
                        // 커스텀 아답타 생성
                        MyAdapter adapter2 = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터
                        gv.setAdapter(adapter2);  // 커스텀 아답타를 GridView 에 적용

                    }else if(anotherfloor12==true){
                        //엘레베이터 탑승
                        search(n[39], n[29]);
                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터


                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewDialog.showDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        speakOut("엘레베이터를 타고 2층으로 올라갑니다");
                                        speakOut("2층 경로입니다!");
                                    }
                                }).start();


                            }
                        });

                        init();
                        init_2f();
                        search(n[41], n[goal]);
                        button2f.setBackgroundColor(Color.YELLOW);
                        button1f.setBackgroundColor(Color.WHITE);
                        floor = 2;
                        // 커스텀 아답타 생성
                        MyAdapter adapter2 = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터
                        gv.setAdapter(adapter2);  // 커스텀 아답타를 GridView 에 적용

                    } else{
                        search(n[39], n[goal]);

                        Toast.makeText(getApplicationContext(), Integer.toString(goal) + "호실 경로입니다!", Toast.LENGTH_SHORT).show();

                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터

                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "해당 호실은 없습니다ㅠ.ㅠ", Toast.LENGTH_SHORT).show();
                }
                isoninit = true;
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
        room = new ArrayList<>();
        path = new ArrayList<>();

        n = new Node[60];
        for (int i = 0; i < n.length; i++) {
            n[i] = new Node();
            n[i].setData(i);
            n[i].setKind("W");
        }
        n[29].setKind("E");
        //location init
        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                n[k].setXY(j, i);
                k++;
            }
        }
    }

    public void init_1f() {
        //clear 2f neighbor
        floortext.setText("1F");
        if (first == 0) {
            n[1].clearNeighbors(n[2]);
            n[2].clearNeighbors(n[1], n[3]);
            n[3].clearNeighbors(n[2], n[15]);
            n[10].clearNeighbors(n[22]);
            n[15].clearNeighbors(n[3], n[27]);
            n[19].clearNeighbors(n[31]);
            n[21].clearNeighbors(n[22], n[33]);
            n[22].clearNeighbors(n[10], n[21]);
            n[25].clearNeighbors(n[26]);
            n[26].clearNeighbors(n[25], n[27]);
            n[27].clearNeighbors(n[15], n[26], n[39]);
            n[31].clearNeighbors(n[19], n[32], n[43]);
            n[32].clearNeighbors(n[31], n[33], n[44]);
            n[33].clearNeighbors(n[21], n[32], n[45]);
            n[35].clearNeighbors(n[47]);
            n[39].clearNeighbors(n[27], n[40], n[51]);
            n[40].clearNeighbors(n[39], n[41]);
            n[41].clearNeighbors(n[40], n[42]);
            n[42].clearNeighbors(n[41], n[43]);
            n[43].clearNeighbors(n[31], n[42], n[44]);
            n[44].clearNeighbors(n[32], n[43], n[45]);
            n[45].clearNeighbors(n[33], n[44], n[46]);
            n[46].clearNeighbors(n[45], n[47]);
            n[47].clearNeighbors(n[35], n[46], n[59]);
            n[49].clearNeighbors(n[50]);
            n[50].clearNeighbors(n[49], n[51]);
            n[51].clearNeighbors(n[39], n[50]);
            n[57].clearNeighbors(n[45]);
            n[59].clearNeighbors(n[47]);
        }
        //variable init
        paths = new ArrayList<>();
        gMaps = new HashMap<Node, Integer>();
        fMaps = new HashMap<Node, Integer>();
        openList = new PriorityQueue<Node>(initialCapacity, new fCompare());
        closedList = new ArrayList<Node>();


        room.clear();
        path.clear();
        for (int i = 0; i < room1list.length; i++) {
            room.add(room1list[i]);
        }
        for (int i = 0; i < path1list.length; i++) {
            path.add(path1list[i]);
        }
        for (int i = 0; i < room.size(); i++) {
            n[room.get(i)].setKind("R");
        }
        for (int i = 0; i < path.size(); i++) {
            n[path.get(i)].setKind("P");
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

        first = 0;
    }

    public void init_2f() {
        floortext.setText("2F");
        //clear 1f neighbor
        n[7].clearNeighbors(n[19], n[8]);
        n[8].clearNeighbors(n[7], n[20]);
        n[13].clearNeighbors(n[14]);
        n[14].clearNeighbors(n[13], n[26], n[15]);
        n[15].clearNeighbors(n[14], n[27]);
        n[19].clearNeighbors(n[7], n[20], n[31]);
        n[20].clearNeighbors(n[8], n[19], n[32]);
        n[21].clearNeighbors(n[33]);
        n[22].clearNeighbors(n[34]);
        n[23].clearNeighbors(n[35]);
        n[26].clearNeighbors(n[14], n[27], n[38]);
        n[27].clearNeighbors(n[15], n[26], n[39]);
        n[29].clearNeighbors(n[41]);                  //E
        n[31].clearNeighbors(n[19], n[32], n[43]);
        n[32].clearNeighbors(n[20], n[31], n[33], n[44]);
        n[33].clearNeighbors(n[21], n[32], n[34], n[45]);
        n[34].clearNeighbors(n[22], n[33], n[35], n[46]);
        n[35].clearNeighbors(n[23], n[34], n[47]);
        n[37].clearNeighbors(n[28]);
        n[38].clearNeighbors(n[26], n[37], n[39]);
        n[39].clearNeighbors(n[27], n[38], n[40]);
        n[40].clearNeighbors(n[39], n[41]);
        n[41].clearNeighbors(n[40], n[29], n[42]);
        n[42].clearNeighbors(n[41], n[43]);
        n[43].clearNeighbors(n[31], n[42], n[44], n[55]);
        n[44].clearNeighbors(n[32], n[43], n[56]);
        n[45].clearNeighbors(n[33]);
        n[46].clearNeighbors(n[34]);
        n[47].clearNeighbors(n[35]);
        n[55].clearNeighbors(n[43], n[56]);
        n[56].clearNeighbors(n[44], n[55]);

        //variable init
        paths = new ArrayList<>();
        gMaps = new HashMap<Node, Integer>();
        fMaps = new HashMap<Node, Integer>();
        openList = new PriorityQueue<Node>(initialCapacity, new fCompare());
        closedList = new ArrayList<Node>();


        room.clear();
        path.clear();
        for (int i = 0; i < room2list.length; i++) {
            room.add(room2list[i]);
        }
        for (int i = 0; i < path2list.length; i++) {
            path.add(path2list[i]);
        }
        for (int i = 0; i < room.size(); i++) {
            n[room.get(i)].setKind("R");
        }
        for (int i = 0; i < path.size(); i++) {
            n[path.get(i)].setKind("P");
        }


        n[1].addNeighbors(n[2]);
        n[2].addNeighbors(n[1], n[3]);
        n[3].addNeighbors(n[2], n[15]);
        n[10].addNeighbors(n[22]);
        n[15].addNeighbors(n[3], n[27]);
        n[19].addNeighbors(n[31]);
        n[21].addNeighbors(n[22], n[33]);
        n[22].addNeighbors(n[10], n[21]);
        n[25].addNeighbors(n[26]);
        n[26].addNeighbors(n[25], n[27]);
        n[27].addNeighbors(n[15], n[26], n[39]);
        n[31].addNeighbors(n[19], n[32], n[43]);
        n[32].addNeighbors(n[31], n[33], n[44]);
        n[33].addNeighbors(n[21], n[32], n[45]);
        n[35].addNeighbors(n[47]);
        n[39].addNeighbors(n[27], n[40], n[51]);
        n[40].addNeighbors(n[39], n[41]);
        n[41].addNeighbors(n[40], n[42]);
        n[42].addNeighbors(n[41], n[43]);
        n[43].addNeighbors(n[31], n[42], n[44]);
        n[44].addNeighbors(n[32], n[43], n[45]);
        n[45].addNeighbors(n[33], n[44], n[46], n[57]);
        n[46].addNeighbors(n[45], n[47]);
        n[47].addNeighbors(n[35], n[46], n[59]);
        n[49].addNeighbors(n[50]);
        n[50].addNeighbors(n[49], n[51]);
        n[51].addNeighbors(n[39], n[50]);
        n[57].addNeighbors(n[45]);
        n[59].addNeighbors(n[47]);
    }


    public void search(Node start, Node end) {
        openList.clear();
        closedList.clear();

        gMaps.put(start, 0);
        openList.add(start);
        paths.clear();

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
        } else if (text.equals("엘레베이터를 타고 1층으로 내려갑니다")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewDialog.hideDialog();
                }
            });
        } else if (text.equals("엘레베이터를 타고 2층으로 올라갑니다")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewDialog.hideDialog();
                }
            });
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

                //1,2층에 있는건지 확인
                boolean check = false;
                for (int i = 0; i < room1list.length; i++) {
                    if (room1list[i] == goal) {
                        check = true;
                    }
                }
                for (int i = 0; i < room2list.length; i++) {
                    if (room2list[i] == goal) {
                        check = true;
                    }
                }
                boolean anotherfloor21 = false;
                boolean anotherfloor12 = false;
                if (check == true) {
                    //2층인데 1층 찾을 경우
                    for (int i = 0; i < room1list.length; i++) {
                        if (floor == 2 && room1list[i] == goal) {
                            anotherfloor21 = true;
                        }
                    }
                    //1층인데 2층 찾을 경우
                    for (int i = 0; i < room2list.length; i++) {
                        if (floor == 1 && room2list[i] == goal) {
                            anotherfloor12 = true;
                        }
                    }
                    if (anotherfloor21 == true) {
                        //엘레베이터 탑승
                        search(n[39], n[29]);
                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터

                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewDialog.showDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        speakOut("엘레베이터를 타고 1층으로 내려갑니다");
                                        speakOut("1층 경로입니다!");
                                    }
                                }).start();
                            }
                        });


                        button1f.setBackgroundColor(Color.YELLOW);
                        button2f.setBackgroundColor(Color.WHITE);
                        init();
                        init_1f();
                        search(n[41], n[goal]);

                        floor = 1;
                        // 커스텀 아답타 생성
                        MyAdapter adapter2 = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터
                        gv.setAdapter(adapter2);  // 커스텀 아답타를 GridView 에 적용

                    } else if (anotherfloor12 == true) {
                        //엘레베이터 탑승
                        search(n[39], n[29]);
                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터


                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewDialog.showDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        speakOut("엘레베이터를 타고 2층으로 올라갑니다");
                                        speakOut("2층 경로입니다!");
                                    }
                                }).start();


                            }
                        });

                        init();
                        init_2f();
                        search(n[41], n[goal]);
                        button2f.setBackgroundColor(Color.YELLOW);
                        button1f.setBackgroundColor(Color.WHITE);
                        floor = 2;
                        // 커스텀 아답타 생성
                        MyAdapter adapter2 = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터
                        gv.setAdapter(adapter2);  // 커스텀 아답타를 GridView 에 적용

                    } else {
                        search(n[39], n[goal]);

                        Toast.makeText(getApplicationContext(), Integer.toString(goal) + "호실 경로입니다!", Toast.LENGTH_SHORT).show();

                        // 커스텀 아답타 생성
                        MyAdapter adapter = new MyAdapter(
                                getApplicationContext(),
                                R.layout.row,       // GridView 항목의 레이아웃 row.xml
                                img, n, paths);    // 데이터

                        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

                    }

                } else {
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

    public void progressON() {
        baseApplication.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        baseApplication.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        baseApplication.getInstance().progressOFF();
    }

}//Class