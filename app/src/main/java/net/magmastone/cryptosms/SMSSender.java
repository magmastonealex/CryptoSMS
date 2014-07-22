package net.magmastone.cryptosms;

import android.telephony.SmsManager;

/**
 * Created by Alex on 14-07-21.
 */
public class SMSSender {
    public static void sendKeySMS(String recp, String key) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            short port=9090;
            smsManager.sendDataMessage(recp, null, port, key.getBytes("ASCII"), null, null);
        }catch (Exception e){
            System.err.println(e.getLocalizedMessage());
        }
    }

    public static void sendMessageSMS(String recp, String enc) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            short port=9080;
            smsManager.sendDataMessage(recp, null, port, enc.getBytes(), null, null);
        }catch (Exception e){
            System.err.println(e.getLocalizedMessage());
        }
    }

    public static void sendTextSMS(String recp, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(recp, null, message, null, null);
        }catch (Exception e){
            System.err.println(e.getLocalizedMessage());
        }
    }
}
