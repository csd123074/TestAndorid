package com.example.nj_ba.testanroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    protected Button btHomepage, btDial, btCall, btSms, btMap, btRecog, btTts,
            btEcho, btContact, btBitmap, btToastPs, btService, btLocation, btRecogcall;
    protected TextView tvRecog;
    protected EditText etTts, etDelay;
    public ImageView ivBitmap;
    protected TextToSpeech tts;
    private static final int CODE_RECOG = 1215, CODE_ECHO = 1227, CODE_CONTACT = 1529, CODE_CONTACT_CALL = 1403, CODE_CONTACT2 = 1530,CODE_CONTACT3 = 1531;//비밀번호를 가지고 특정 앱을 갖고 위해 설정
    protected boolean bService = false;
    protected String sBitmapUrl = "https://sites.google.com/site/yongheuicho/_/rsrc/1313446792839/config/customLogo.gif",sCallName; ;
    protected TelephonyManager telephonyManager;
    protected CommStateListener commStateListener;
    protected LocationManager locationManager;
    protected MyLocationListener myLocationListener;
    protected SensorManager sensorManager;
    protected Sensor sensorAccel;
    protected MySensorListener mySensorListener;
    protected double latitude, longitude, altitude;
    protected String name = "";


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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.321609,127.337957?z=15"));
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, CODE_CONTACT);
            }
        });
        ivBitmap = (ImageView) findViewById(R.id.ivBitmap);
        btBitmap = (Button) findViewById(R.id.btBitmap);
        btBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new BitmapRunnable(ivBitmap, sBitmapUrl)).start();
            }
        });

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        commStateListener = new CommStateListener(telephonyManager, this);
        btToastPs = (Button) findViewById(R.id.btToastPs);
        btToastPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastPhoneState();
            }
        });

        btService = (Button) findViewById(R.id.btService);
        btService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateService();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        long minTime = 1000; // in ms
        float minDistance = 0;
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
        btLocation = (Button) findViewById(R.id.btLocation);
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
                speakLocation(latitude, longitude);
            }
        });

        btRecogcall = (Button) findViewById(R.id.btRecogcall);
        btRecogcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceRecog(CODE_CONTACT_CALL);
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorListener = new MySensorListener(this);
        if (sensorAccel != null) {
            sensorManager.registerListener(mySensorListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void speakLocation(double latitude, double longitude) {
        Geocoder geocoder;
        geocoder = new Geocoder(getApplicationContext(), Locale.KOREAN);
        List<Address> lsAddress;
        try {
            lsAddress = geocoder.getFromLocation(latitude, longitude, 1);
            String address = lsAddress.get(0).getAddressLine(0);
            String city = lsAddress.get(0).getLocality();
            String state = lsAddress.get(0).getAdminArea();
            String country = lsAddress.get(0).getCountryName();
            String postalCode = lsAddress.get(0).getPostalCode();
            String knownName = lsAddress.get(0).getFeatureName();
            speakStr("현재 있는 나라는" + country + "현재있는 도시는" + city + "현재있는 건물은" + knownName + "입니다.");

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + "," + longitude + "?z=15"));    //"geo:s36.321609,127.337957?z=20"
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLocation() {

        latitude = myLocationListener.latitude;
        longitude = myLocationListener.longitude;
        altitude = myLocationListener.altitude;
        Toast.makeText(this, "Latitude: " + latitude + ", Longitude = " + longitude + ", Altitude  " + altitude, Toast.LENGTH_SHORT).show();
    }

    private void updateService() {
        Intent intent = new Intent(this, PhoneCallService.class);
        if (bService) {
            stopService(intent);
            bService = false;
            btService.setText("Start Svc");
        } else {
            startService(intent);
            bService = true;
            btService.setText("Stop Svc");
        }

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
        int nRssi = commStateListener.nRssi;
        Toast.makeText(this, "RSSI = " + nRssi, Toast.LENGTH_SHORT).show();
    }

    private void voiceRecog(int nCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");
        startActivityForResult(intent, nCode);
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhoneNumFromName(String sName) {
        String sPhoneNum = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(sName));
        String[] arProjection = new String[]{ContactsContract.Contacts._ID};
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
            } else if (requestCode == CODE_CONTACT_CALL) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                if (sRecog.equals("전화 걸기") == true) {
                    speakStr("누구에게 전화 걸까요");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    voiceRecog(CODE_CONTACT2);
                }
            } else if (requestCode == CODE_CONTACT2) {
                ArrayList<String> arList1 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sCallName = arList1.get(0);
                tvRecog.setText(sCallName);
                if (sCallName.equals(sCallName) == true) {
                    speakStr(sCallName + "에게 전화 걸까요?");
                    voiceRecog(CODE_CONTACT3);
                }
            } else if (requestCode == CODE_CONTACT3) {
                ArrayList<String> arList2 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sAnswer = arList2.get(0);
                if (sAnswer.equals("예") == true) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getPhoneNumFromName(sCallName)));
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bService) {
            Intent intent = new Intent(this, PhoneCallService.class);
            stopService(intent);
        }
        sensorManager.unregisterListener(mySensorListener);
        super.onDestroy();
    }
}
