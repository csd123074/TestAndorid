package com.example.nj_ba.testanroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {//implements : 인터페이스 구현
    protected Button btHomepage, btDial, btCall, btSms, btMap, btRecog, btTts, btToastPS,
            btEcho, btContact, btBitmap;
    protected TextView tvRecog;
    protected EditText etTts, etDelay;
    protected TextToSpeech tts;
    public ImageView ivBitmap;
    private static final int CODE_RECOG = 1215, CODE_ECHO = 1227, CODE_CONTACT = 1529;//비밀번호를 가지고 특정 앱을 갖고 위해 설정
    protected String sBitmapUrl = "https://sites.google.com/site/yongheuicho/_/rsrc/1313446792839/config/customLogo.gif?revision=1"; //Bitmap을 가져올 주소
    protected TelephonyManager telephonyManager;
    protected CommStateListener commStateListener;

    //Intent : 일종의 메시지 객체, 이것을 사용해 다른 앱 구성 요소로부터 작업을 요청할 수 있음
    //명시적 인텐트 : 시작할 구성 요소를 이름으로 지정합니다(완전히 정규화된 클래스 이름). 명시적 인텐트는 일반적으로 본인의 앱 안에서 구성 요소를 시작할 때 씁니다.
    // 시작하고자 하는 액티비티 또는 서비스의 클래스 이름을 알고 있기 때문입니다. 예를 들어, 사용자 작업에 응답하여 새 액티비티를 시작하거나
    // 백그라운드에서 파일을 다운로드하기 위해 서비스를 시작하는 것 등이 여기에 해당됩니다.
    //암시적 인텐트 : 특정 구성 요소의 이름을 대지 않지만, 그 대신 수행할 일반적일 작업을 선언하여 또 다른 앱의 구성 요소가 이를 처리할 수 있도록 해줍니다.
    // 예를 들어, 사용자에게 지도에 있는 한 위치를 표시해주고자 하는 경우, 암시적 인텐트를 사용하여 다른, 해당 기능을 갖춘 앱이 지정된 위치를 지도에 표시하도록 요청할 수 있습니다.
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
        btDial = (Button) findViewById(R.id.btDial);
        btDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btCall = (Button) findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btSms = (Button) findViewById(R.id.btSms);
        btSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:0428297670"));
                intent.putExtra("sms_body", "Mokwon University");
                startActivity(intent);
            }
        });
        btMap = (Button) findViewById(R.id.btMap);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.321609,127.337957?z=20")); //z= 맵을 얼마나 확대할것인가
                startActivity(intent);
            }
        });
        tvRecog = (TextView) findViewById(R.id.tvRecog);
        btRecog = (Button) findViewById(R.id.btRecog);
        btRecog.setOnClickListener(new View.OnClickListener() {
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
        btEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_ECHO);
            }
        });
        etDelay = (EditText) findViewById(R.id.etDelay);
        btContact = (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //Action_pick을 사용해서 우리가 직접 열람하는게 아니라 사용자가 허락했기 때문에 permission 필요 없음
                startActivityForResult(intent, CODE_CONTACT);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);//ContactsContract 연락처 정보를 관장하는 계약자
            }
        });
        ivBitmap = (ImageView) findViewById(R.id.ivBitmap);
        btBitmap = (Button) findViewById(R.id.btBitmap);
        btBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new BitmapRunnable(ivBitmap, sBitmapUrl)).start(); // 여기서 만든 Thread는 내가 만들었기 때문에 반드시 new 를 해줘야함. 남이 만든 객체는 레퍼런스를 받아와야 하며 그 경우 new 사용 x
                //runnable 자체는 실행되는 코드는 아닌데 Thread에 들어가야 실행 되는것
                //여기에 Thread를 하나 더 만들었으니까 총 2개가 되는거임. 기본에 있던거 1개 지금 만든거 1개. Thread는 원하는 만큼 만들 수 있음
            }
        });


        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE); //getSystemService : 운영체제에 있는 여러 서비스를 가져옴
        //전화를 담당하는 운영체제가 서비스를 돌리고, 그것을 관리 하는 매지너가 TelephonyManager, 이를 이용해 정보를 얻을 수 있음
        commStateListener = new CommStateListener(); // 위는 레퍼런스를 받아서 옴. 하지만 이거는 상속받은 값이므로 내가 관리를 할거라 new를 통해 생성해서 commStateListener에 집어넣음

        btToastPS = (Button) findViewById(R.id.btToastPS);
        btToastPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastPhoneState();
            }
        });
    }

    private void toastPhoneState() {
        int nPhoneType = telephonyManager.getPhoneType();
        int nNetworkType = telephonyManager.getNetworkType();
        String sPhoneType;
        switch (nPhoneType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                sPhoneType = "Voice: GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                sPhoneType = "Voice: CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                sPhoneType = "Voice: SIP";
                break;
            default:
                sPhoneType = "Voice: 코드 번호 = " + nPhoneType;
        }
        String sNetworkType;
        switch (nNetworkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                sNetworkType = "Data: 2G CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                sNetworkType = "Data: 3G UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                sNetworkType = "Data: 3G HSPA+";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                sNetworkType = "Data: 4G LTE";
                break;
            default:
                sNetworkType = "Data: 코드 번호 = " + nNetworkType;
        }
        Toast.makeText(this, sPhoneType, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, sNetworkType, Toast.LENGTH_SHORT).show();
    }


    private void voiceRecog(int nCode) { //음성인식을 하는 인텐트를 이용해 음성인식
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //사용자의 음성을 인식하기위한 액티비티를 시작하도록 전달하는 상수 값으로, 이 상수값을 인자로
        //가지는 인텐트 객체를 이용하면 음성 인식 액티비티를 실행할 수 있음
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // free-form 기반의 음성 인식 모델을 사용
        //어떤 언어모델을 사용할 것인가 free_form:현재 내정보를 가지고 복원(말 소리만 듣고) web_form:웹의 정보까지 다양하게 오류 확인
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);// 언어 인식 코드
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");// 내가 쓰고 싶은 마이크, 사용자한테 주는 마이크
        startActivityForResult(intent, nCode);//CODE_RECOG라는 비밀번호를 가지고 특정 액티비티를 가져옴(앱 탈취를 막기 위해)
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);//진행중인 음성 출력을 끊고 이번 TTS의 음성 출력을 한다
        while (tts.isSpeaking()) {//tts가 소리를 만들고 있는지, 아닌지 확인하는 함수
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhoneNumFromName(String sName) {
        String sPhoneNum = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(sName));//Uri : 안드로이드에서 쓰는 여러 자원들을 하나의 체계로 합친것
        String[] arProjection = new String[]{ContactsContract.Contacts._ID};//ID를 이용해 정보 검색
        Cursor cursor = getContentResolver().query(uri, arProjection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String sId = cursor.getString(0);
            String[] arProjNum = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String sWhereNum = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
            String[] sWhereNumParam = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, sId};
            Cursor cursorNum = getContentResolver().query(ContactsContract.Data.CONTENT_URI, arProjNum, sWhereNum, sWhereNumParam, null);
            if (cursorNum != null && cursorNum.moveToFirst()) {
                sPhoneNum = cursorNum.getString(0);
            }
            cursorNum.close();
        }
        cursor.close();
        return sPhoneNum;
    }

    @Override //framework 제공하는 기능을 가져와서 내가 필요한 기능을 넣겠다
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//메세지가 사용자의 브로드캐스트가 됨
        super.onActivityResult(requestCode, resultCode, data);//super : 부모함수 호출
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODE_RECOG) {
                //음성입력을 받은다음에 정상적으로 동작했는지 확인
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //데이터 전달, ACTION_RECOGNIZE_SPEECH 를 이용하여 액티비티를 호출한 경우 사용자의 음성을 인식하고
                //해석된 결과를 onActivity 메소드내에서 ArrayList<String> 객체로 반환받기 위한 상수값
                String sRecog = arList.get(0);//이렇게만 두면 다른 프로그램에서 사용 할 수 없음
                tvRecog.setText(sRecog);//음성인식된 결과를 집어 넣음
            } else if (requestCode == CODE_ECHO) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                String sDelay = etDelay.getText().toString();
                int nDelay = Integer.parseInt(sDelay);  //sDelay를 인트형으로 변환
                try {
                    Thread.sleep(nDelay * 1000);  //안드로이드 앱은 정지하면 안되는데 슬립은 정지하는거니까 트라이 캐치 사용
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakStr(sRecog);
            } else if (requestCode == CODE_CONTACT) {
                String[] sFilter = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), sFilter, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String sName = cursor.getString(0);
                    String sPhoneNum = cursor.getString(1);
                    cursor.close();
                    Toast.makeText(this, sName + " = " + sPhoneNum, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onInit(int status) {//소리로 전환할 텍스트
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);//기본 피치
            tts.setSpeechRate(1.0f);// 속도
        }
    }

    @Override
    protected void onResume() { //Onstart 다음에 실행 되는것(켜질때)
        super.onResume();
        TelephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//LISTEN_SIGNAL_STRENGTHS : 핸드폰에 있는 안테나 개수
        //메세지가 날아올때 처리랄 callback 함수를 써줘야함
    }

    @Override
    protected void onPause() {//사용자에게 보여지다가 background로 들어간 상태(꺼질때)
        TelephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE); //LISTEN_NONE을 이용해 종료
        super.onPause();
    }
}