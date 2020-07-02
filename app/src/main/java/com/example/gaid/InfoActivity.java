package com.example.gaid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gaid.model.get_info.GetInfoRequestDTO;
import com.example.gaid.model.get_info.GetInfoResponseDTO;
import com.example.gaid.util.RestApiUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends Activity {
    private ImageView imgview;
    private TextView tv_profname,tv_etc,tv_tel,tv_dept;
    private Intent intent;
    private String mRoomNumber="";

    private RestApiUtil mRestApiUtil;
    private GetInfoRequestDTO mGetInfoRequestDTO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tv_etc=(TextView)findViewById(R.id.tv_etc);
        tv_profname=(TextView)findViewById(R.id.tv_profname);
        tv_tel=(TextView)findViewById(R.id.tv_tel);

        intent=getIntent();
        mRoomNumber=intent.getStringExtra("roomNo");
        System.out.println("roomNo :" + mRoomNumber);
        ///형걸이형 여기야여기!
        mRestApiUtil = new RestApiUtil();
        mGetInfoRequestDTO = new GetInfoRequestDTO();
        getInfo();
    }

    public void getInfo() {
        mRestApiUtil.getApi().get_info(mGetInfoRequestDTO).enqueue(new Callback<GetInfoResponseDTO>() {
            @Override
            public void onResponse(Call<GetInfoResponseDTO> call, Response<GetInfoResponseDTO> response) {
                if(response.isSuccessful()) {

                }
                else {

                }
            }

            @Override
            public void onFailure(Call<GetInfoResponseDTO> call, Throwable t) {

            }
        });
    }
}