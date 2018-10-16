package com.example.nj_ba.testanroid;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected Button btHomepage, btdial, btcall, btsns, btmap, btrecog;
    protected TextView tvRecog;
    private static int CODE_RECOG = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btHomepage = (Button) findViewById(R.id.btHomepage);
        btHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ice.mokwon.ac.kr"));
                startActivity(intent);
            }
        });
        btdial = (Button) findViewById(R.id.btdial);
        btdial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btcall = (Button) findViewById(R.id.btcall);
        btcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0428297670"));
            }
        });
        btsns = (Button) findViewById(R.id.btsns);
        btsns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: 0428297670"));
                intent.putExtra("sms_body", "Mokwon University");
                startActivity(intent);
            }
        });
        btmap = (Button) findViewById(R.id.btmap);
        btmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.321609,127.337957?z=20")); //z= 맵을 얼마나 확대할것인가
                startActivity(intent);
            }
        });
        btrecog = (Button) findViewById(R.id.btrecog);
        tvRecog = (TextView) findViewById(R.id.tvRecog);
        btrecog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog();

            }
        });


    }

    private void voiceRecog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREA);
        startActivityForResult(intent, CODE_RECOG); //CODE_RECOG라는 비밀번호를 가지고 특정 액티비티를 가져옴
    }
}
