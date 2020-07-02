package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gaid.model.get_info.GetInfoRequestDTO;
import com.example.gaid.model.get_info.GetInfoResponseDTO;
import com.example.gaid.util.RestApiUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends Activity {
    private ImageView imgview;
    private Button btn_tomap;
    private TextView tv_profname,tv_etc,tv_tel,tv_dept;
    private Intent intent;
    private String mRoomNumber="";

    private RestApiUtil mRestApiUtil;
    private RequestBody mRequestBody;
    private GetInfoResponseDTO mGetInfoResponseDTO;

    private String user_text = "";
    private int roomNo;

    private String mRoom_no = "";
    private String mRoom_floor = "";
    private String mRoom_department = "";
    private String mRoom_professor = "";
    private String mRoom_tel = "";
    private String mRoom_name = "";
    private String mRoom_business = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tv_etc=(TextView)findViewById(R.id.tv_etc);
        tv_profname=(TextView)findViewById(R.id.tv_profname);
        tv_tel=(TextView)findViewById(R.id.tv_tel);
        btn_tomap=(Button)findViewById(R.id.btn_tomap);
        btn_tomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_in =new Intent(getApplicationContext(),MapActivity.class);
                intent_in.putExtra("roomNo",roomNo);
                startActivity(intent_in);
            }
        });
        intent = getIntent();
        mRoomNumber = intent.getStringExtra("roomNo");
        System.out.println("roomNo :" + mRoomNumber);

        mRestApiUtil = new RestApiUtil();
        mRequestBody = RequestBody.create(MediaType.parse("text/plain"), mRoomNumber);
        getInfo();
    }

    public void getInfo() {
        mRestApiUtil.getApi().get_info(mRequestBody).enqueue(new Callback<GetInfoResponseDTO>() {
            @Override
            public void onResponse(Call<GetInfoResponseDTO> call, Response<GetInfoResponseDTO> response) {
                if(response.isSuccessful()) {
                    mGetInfoResponseDTO = response.body();
                    setInfo();
                }
                else {
                    Log.d("onResponse", "onResponse 에서 에러남");
                }
            }

            @Override
            public void onFailure(Call<GetInfoResponseDTO> call, Throwable t) {
                Log.d("onFailure", t.getMessage());
            }
        });
    }

    public void setInfo() {
        mRoom_no = mGetInfoResponseDTO.getRoom_no();
        mRoom_floor = mGetInfoResponseDTO.getRoom_floor();
        mRoom_department = mGetInfoResponseDTO.getRoom_department();
        mRoom_professor = mGetInfoResponseDTO.getRoom_professor();
        mRoom_tel = mGetInfoResponseDTO.getRoom_tel();
        mRoom_name = mGetInfoResponseDTO.getRoom_name();
        mRoom_business = mGetInfoResponseDTO.getRoom_business();
        System.out.println(mRoom_no + mRoom_floor + mRoom_department + mRoom_professor + mRoom_professor + mRoom_tel + mRoom_name + mRoom_business);
        //은석아 여기야 여기!!!!
    }
}
