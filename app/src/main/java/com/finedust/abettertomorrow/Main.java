package com.finedust.abettertomorrow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main extends AppCompatActivity {

    private Button btnData, btnCommunity, btnNews, btnCtrl;

    // Back Pressed
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Button
        btnData = (Button)findViewById(R.id.buttonData);
        btnCommunity = (Button)findViewById(R.id.buttonCoummuity);
//        btnNews = (Button)findViewById(R.id.buttonNews);
//        btnCtrl = (Button)findViewById(R.id.buttonControl);

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TodayWeeksAdapter.class);
                startActivity(intent);
            }
        });

        btnCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityAdapter.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
            super.onBackPressed();
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료 됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
}
