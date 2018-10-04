package com.example.nj_ba.testanroid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    protected Button btHomepage,btdial,btcall,btsns, btmap;

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
        btdial = (Button) findViewById(R.id.btHomepage);
        btdial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btcall = (Button) findViewById(R.id.btcall);
        btcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:0428297670"));
            }
        });
        btsns = (Button) findViewById(R.id.btsns);
        btsns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto: 0428297670"));
                intent.putExtra("sms_body","Mokwon University");
                startActivity(intent);
            }
        });
        btmap = (Button) findViewById(R.id.btmap);
        btmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:36.321609,127.337957?z=20"));
                startActivity(intent);
            }
        });
    }
}
