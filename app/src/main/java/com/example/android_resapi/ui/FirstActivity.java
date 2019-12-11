package com.example.android_resapi.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;


import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;
import com.example.android_resapi.ui.apicall.UpdateShadow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    String urlbase = "https://4iyskfbmz1.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010";
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button autoButton;
    Button selfButton;
    Button offButton;
    Button oneButton;
    Button twoButton;
    Button threeButton;
    String mode = "auto";
    int step=0;

    int buttonNonClickedColor = Color.rgb(169, 208, 245);
    int buttonClickedColor = Color.rgb(88, 172, 250);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        autoButton = findViewById(R.id.autoButton);
        selfButton = findViewById(R.id.selfButton);
        offButton = findViewById(R.id.offButton);
        oneButton = findViewById(R.id.oneButton);
        twoButton = findViewById(R.id.twoButton);
        threeButton = findViewById(R.id.threeButton);

        autoButton.setOnClickListener(this);
        selfButton.setOnClickListener(this);
        offButton.setOnClickListener(this);
        oneButton.setOnClickListener(this);
        twoButton.setOnClickListener(this);
        threeButton.setOnClickListener(this);


        GetTemperature();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.autoButton:
                ControlMode(1, "auto");
                mode = "auto";
                step = 0;
                break;
            case R.id.selfButton:
                ControlMode(2, "self");
                mode = "self";
                step = 0;
                break;
            case R.id.offButton:
                ControlColor("0");
                step = 0;
                break;
            case R.id.oneButton:
                ControlColor("1");
                step = 1;
                break;
            case R.id.twoButton:
                ControlColor("2");
                step = 2;
                break;
            case R.id.threeButton:
                ControlColor("3");
                step= 3;
                break;

        }
        SendInformation(mode,step);

    }

    private void ControlMode(int button, String fm) {
        if (button == 1) {
            autoButton.setBackgroundColor(buttonClickedColor);
            selfButton.setBackgroundColor(buttonNonClickedColor);
            offButton.setBackgroundColor(buttonNonClickedColor);
            oneButton.setBackgroundColor(buttonNonClickedColor);
            twoButton.setBackgroundColor(buttonNonClickedColor);
            threeButton.setBackgroundColor(buttonNonClickedColor);

            offButton.setEnabled(false);
            oneButton.setEnabled(false);
            twoButton.setEnabled(false);
            threeButton.setEnabled(false);
        } else {
            autoButton.setBackgroundColor(buttonNonClickedColor);
            selfButton.setBackgroundColor(buttonClickedColor);
            offButton.setEnabled(true);
            oneButton.setEnabled(true);
            twoButton.setEnabled(true);
            threeButton.setEnabled(true);
            offButton.setBackgroundColor(buttonClickedColor);
            // + 모터 끄는 메소드
        }
    }


    private void ControlColor(String fp) {
        offButton.setBackgroundColor(buttonNonClickedColor);
        oneButton.setBackgroundColor(buttonNonClickedColor);
        twoButton.setBackgroundColor(buttonNonClickedColor);
        threeButton.setBackgroundColor(buttonNonClickedColor);


        if (fp.equals("0")) {
            offButton.setBackgroundColor(buttonClickedColor);
        } else if (fp.equals("1")) {
            oneButton.setBackgroundColor(buttonClickedColor);
        } else if (fp.equals("2")) {
            twoButton.setBackgroundColor(buttonClickedColor);
        } else if (fp.equals("3")) {
            threeButton.setBackgroundColor(buttonClickedColor);
        }

    }


    private void GetTemperature() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new GetThingShadow(FirstActivity.this, urlbase).execute();
            }
            },
                0, 2000);
    }


    void SendInformation(String mode, int step){
        JSONObject payload = new JSONObject();
        String temp = GetThingShadow.temperature;
            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject tag1 = new JSONObject();
                tag1.put("tagName", "user_mode");
                tag1.put("tagValue", mode);
                jsonArray.put(tag1);

                JSONObject tag2 = new JSONObject();
                tag2.put("tagName", "motor_step");
                tag2.put("tagValue", step);
                jsonArray.put(tag2);

                JSONObject tag3 = new JSONObject();
                tag3.put("tagName", "temperature");
                tag3.put("tagValue", temp);
                jsonArray.put(tag3);

                if (jsonArray.length() > 0)
                    payload.put("tags", jsonArray);
            } catch (JSONException e) {
                Log.e(TAG, "JSONEXception");
            }
            Log.i(TAG,"payload="+payload);
            if (payload.length() >0 )
                new UpdateShadow(FirstActivity.this,urlbase).execute(payload);
            else
                Toast.makeText(FirstActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();

    }


}
