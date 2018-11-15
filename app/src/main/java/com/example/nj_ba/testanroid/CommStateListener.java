package com.example.nj_ba.testanroid;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class CommStateListener extends PhoneStateListener { //PhoneStateListener는 완벽하게 구성된 클래스
    public CommStateListener(){

    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
    }
}
