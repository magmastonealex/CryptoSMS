package net.magmastone.cryptosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Alex on 14-07-21.
 */
public class messageSMSreceive extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String recMsgString = "";
        String fromAddress = "";
        SmsMessage recMsg = null;
        byte[] data = null;
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                recMsg = SmsMessage.createFromPdu((byte[]) pdus[i]);

                try {
                    data = recMsg.getUserData();
                } catch (Exception e) {

                }
                if (data != null) {
                    for (int index = 0; index < data.length; ++index) {
                        recMsgString += Character.toString((char) data[index]);
                    }
                }

                fromAddress = recMsg.getOriginatingAddress();
            }
            Log.d("messageSMS", recMsgString);
        }
    }
}
