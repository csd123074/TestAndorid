package com.example.nj_ba.testanroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener { //implements : 인터페이스 구현
    protected Button btHomepage, btdial, btcall, btsns, btmap, btrecog, btTts, btEcho, btContact;
    protected TextView tvRecog;
    protected EditText etTts;
    protected TextToSpeech tts;
    protected TextView etDelay;
    private static final int CODE_RECOG = 1234, CODE_ECHO = 1235, CODE_CONTACT = 1236; //비밀번호를 가지고 특정 앱을 갖고 위해 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent : 일종의 메시지 객체, 이것을 사용해 다른 앱 구성 요소로부터 작업을 요청할 수 있음
        //명시적 인텐트 : 시작할 구성 요소를 이름으로 지정합니다(완전히 정규화된 클래스 이름). 명시적 인텐트는 일반적으로 본인의 앱 안에서 구성 요소를 시작할 때 씁니다.
        // 시작하고자 하는 액티비티 또는 서비스의 클래스 이름을 알고 있기 때문입니다. 예를 들어, 사용자 작업에 응답하여 새 액티비티를 시작하거나
        // 백그라운드에서 파일을 다운로드하기 위해 서비스를 시작하는 것 등이 여기에 해당됩니다.
        //암시적 인텐트 : 특정 구성 요소의 이름을 대지 않지만, 그 대신 수행할 일반적일 작업을 선언하여 또 다른 앱의 구성 요소가 이를 처리할 수 있도록 해줍니다.
        // 예를 들어, 사용자에게 지도에 있는 한 위치를 표시해주고자 하는 경우, 암시적 인텐트를 사용하여 다른, 해당 기능을 갖춘 앱이 지정된 위치를 지도에 표시하도록 요청할 수 있습니다.
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
                voiceRecog(CODE_RECOG);

            }
        });

        etTts = (EditText) findViewById(R.id.etTts);
        btTts = (Button) findViewById(R.id.btTts);
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakStr(etTts.getText().toString());
            }
        });
        tts = new TextToSpeech(this, this);

        btEcho = (Button) findViewById(R.id.btEcho);
        etDelay = (TextView) findViewById(R.id.etDelay);
        btEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_ECHO);
            }
        });
        btContact = (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //Action_pick을 사용해서 우리가 직접 열람하는게 아니라 사용자가 허락했기 때문에 permission 필요 없음
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI); //ContactsContract 연락처 정보를 관장하는 계약자
                startActivityForResult(intent, CODE_CONTACT);
            }
        });
    }

    private void voiceRecog(int nCode) { //음성인식을 하는 인텐트를 이용해 음성인식
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //사용자의 음성을 인식하기위한 액티비티를 시작하도록 전달하는 상수 값으로, 이 상수값을 인자로
        //가지는 인텐트 객체를 이용하면 음성 인식 액티비티를 실행할 수 있음
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // free-form 기반의 음성 인식 모델을 사용
        //어떤 언어모델을 사용할 것인가 free_form:현재 내정보를 가지고 복원(말 소리만 듣고) web_form:웹의 정보까지 다양하게 오류 확인
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN); // 언어 인식 코드
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak"); // 내가 쓰고 싶은 마이크, 사용자한테 주는 마이크
        startActivityForResult(intent, nCode); //CODE_RECOG라는 비밀번호를 가지고 특정 액티비티를 가져옴(앱 탈취를 막기 위해)
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null); //진행중인 음성 출력을 끊고 이번 TTS의 음성 출력을 한다
        while (tts.isSpeaking())//tts가 소리를 만들고 있는지, 아닌지 확인하는 함수
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override //framework 제공하는 기능을 가져와서 내가 필요한 기능을 넣겠다
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //메세지가 사용자의 브로드캐스트가 됨
        super.onActivityResult(requestCode, resultCode, data); //super : 부모함수 호출
        if (requestCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODE_RECOG) {
                //음성입력을 받은다음에 정상적으로 동작했는지 확인
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //데이터 전달, ACTION_RECOGNIZE_SPEECH 를 이용하여 액티비티를 호출한 경우 사용자의 음성을 인식하고
                //해석된 결과를 onActivity 메소드내에서 ArrayList<String> 객체로 반환받기 위한 상수값
                String sRecog = arList.get(0); //이렇게만 두면 다른 프로그램에서 사용 할 수 없음
                tvRecog.setText(sRecog); //음성인식된 결과를 집어 넣음
            } else if (requestCode == CODE_ECHO) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //데이터 전달, ACTION_RECOGNIZE_SPEECH 를 이용하여 액티비티를 호출한 경우 사용자의 음성을 인식하고
                //해석된 결과를 onActivity 메소드내에서 ArrayList<String> 객체로 반환받기 위한 상수값
                String sRecog = arList.get(0); //이렇게만 두면 다른 프로그램에서 사용 할 수 없음
                String sDelay = etDelay.getText().toString();
                int nDelay = Integer.parseInt(sDelay); //sDelay를 인트형으로 변환
                try {
                    Thread.sleep(nDelay * 1000); //안드로이드 앱은 정지하면 안되는데 슬립은 정지하는거니까 트라이 캐치 사용
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakStr(sRecog);
            } else if (requestCode == CODE_CONTACT) {
                String[] sFilter = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}; //이름과 전화번호를 찾아 들어가는 기능
               Cursor cursor =  getContentResolver().query(data.getData(),sFilter,null,null,null);
               if(cursor != null){
                   cursor.moveToFirst();
                   String sName = cursor.getString(0);
                   String sPhoneNum = cursor.getString(1);
                   cursor.close();
                   Toast.makeText(this,sName + "=" + sPhoneNum,Toast.LENGTH_LONG).show();
               }
            }
        }

    }

    @Override
    public void onInit(int status) { //소리로 전환할 텍스트
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(0.6f); //기본 피치
            tts.setSpeechRate(1.0f); // 속도
        }

    }
}
