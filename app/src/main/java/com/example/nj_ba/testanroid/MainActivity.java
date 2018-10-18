package com.example.nj_ba.testanroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    protected Button btHomepage, btdial, btcall, btsns, btmap, btrecog, btTts;
    protected TextView tvRecog;
    protected EditText etTts;
    protected TextToSpeech tts;
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

        etTts = (EditText) findViewById(R.id.etTts);
        btTts = (Button) findViewById(R.id.btTts);
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = etTts.getText().toString();
                tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        tts = new TextToSpeech(this, this);
    }

    private void voiceRecog() { //음성인식을 하는 인텐트를 이용해 음성인식
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //어떤 언어모델을 사용할 것인가 free_form:현재 내정보를 가지고 복원(말 소리만 듣고) web_form:웹의 정보까지 다양하게 오류 확인
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak"); // 내가 쓰고 싶은 마이크, 사용자한테 주는 마이크
        startActivityForResult(intent, CODE_RECOG); //CODE_RECOG라는 비밀번호를 가지고 특정 액티비티를 가져옴(앱 탈취를 막기 위해)
    }

    @Override //framewokr 제공하는 기능을 가져와서 내가 필요한 기능을 넣겠다
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //메세지가 사용자의 브로드캐스트가 됨
        super.onActivityResult(requestCode, resultCode, data); //super : 부모함수 호출
        if (requestCode == CODE_RECOG) {
            if (requestCode == Activity.RESULT_OK && data != null) ;
            {                //음성입력을 받은다음에 정상적으로 동작했는지 확인
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0); //이렇게만 두면 다른 프로그램에서 사용 할 수 없음
                tvRecog.setText(sRecog); //음성인식된 결과를 집어 넣음
            }

        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(0.6f); //기본 피치
            tts.setSpeechRate(1.0f); // 속도
        }

    }
}
