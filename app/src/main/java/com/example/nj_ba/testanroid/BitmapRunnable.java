package com.example.nj_ba.testanroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//Runnable은 지금 실행 하는게 아니라 실행 할 수 있는 코드를 담아서 보내는 역할
public class BitmapRunnable implements Runnable { //implement : interface를 직접 구축 한다는 뜻(다시 만든다는 뜻)
    protected ImageView ivBitmap;
    protected String sBitmapUrl;
    public BitmapRunnable(ImageView ivBitmap, String sBitmapUrl){
        this.ivBitmap = ivBitmap;
        this.sBitmapUrl = sBitmapUrl;
    }
    @Override
    public void run() {
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream((InputStream)(new URL(sBitmapUrl).getContent()));
            //ivBitmap.setImageBitmap(bitmap); : 이 코드는 심각한 문제를 가지고 있음
            ivBitmap.post(new Runnable() {
                @Override
                public void run() {
                    ivBitmap.setImageBitmap(bitmap); //중간에 bitmap이 변하면 안되니까 키워드 표시
                    //final : C언어로 치면 const(상수) 한번 선언되면 초기화되지 않음
                }
            }); //post :
            //내가 생성한 thread는 UITread에 접근하면 문제 일으킴 -> UITread는 앱을 구동 시키는 중이기 때문에, 접근을 감지하면 오동작 할 수 밖에 없음 -> Activity에 메세지를 날려서 MessageQueue(Looper가 돌림) 기능 이용
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
