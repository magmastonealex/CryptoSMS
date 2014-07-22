package net.magmastone.cryptosms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Alex on 14-07-21.
 */
public class keySMSreceived extends BroadcastReceiver{
    final static String GROUP_KEY_MESSAGES = "cryptosms_messages";
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
         if(recMsgString.substring(0,2).equals("CS")){
            System.out.println("KeySMS! "+ recMsgString.substring(2,3));
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("keyparts-"+fromAddress+recMsgString.substring(2,3),recMsgString.substring(4));
            editor.commit();
            if((!sharedPref.getString("keyparts-"+fromAddress+"1","xxx").equals("xxx"))&&(!sharedPref.getString("keyparts-"+fromAddress+"2","xxx").equals("xxx"))&&(!sharedPref.getString("keyparts-"+fromAddress+"3","xxx").equals("xxx"))&&(!sharedPref.getString("keyparts-"+fromAddress+"4","xxx").equals("xxx"))) {
                String ourKey = sharedPref.getString("keyparts-" + fromAddress + "1", "xxx") + sharedPref.getString("keyparts-" + fromAddress + "2", "xxx") + sharedPref.getString("keyparts-" + fromAddress + "3", "xxx") + sharedPref.getString("keyparts-" + fromAddress + "4", "xxx");

                SharedPreferences.Editor editor2 = sharedPref.edit();
                //editor2.putString("pkey-"+fromAddress,ourKey);
                editor2.putString("keyparts-" + fromAddress + "1", "xxx");
                editor2.putString("keyparts-" + fromAddress + "2", "xxx");
                editor2.putString("keyparts-" + fromAddress + "3", "xxx");
                editor2.putString("keyparts-" + fromAddress + "4", "xxx");

                String randString = new BigInteger(130, new SecureRandom()).toString(32);
                System.out.println("Adding new key to: "+fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));
                editor2.putString("skey-"+fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""),randString);
                editor2.commit();
                System.out.println("Replying with: "+randString);
                CharSequence text = "Replied to Key Request";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
                String enc= CryptoClass.encryptMessage(ourKey,randString);
                System.out.println("Replying to: "+fromAddress+"with string: "+enc);

                Notification.Builder mBuilder = new Notification.Builder(context)
                        .setContentTitle("New Contact!")
                        .setContentText(fromAddress+"added you to his or her contacts!")
                        .setSmallIcon(R.drawable.ic_fa_smile_o);
                Intent resultIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                stackBuilder.addParentStack(MainActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Random r = new Random();
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mNotificationManager.notify(r.nextInt(), mBuilder.build());

                String CS1=enc.substring(0,100);
                String CS2=enc.substring(100,200);
                String CS3=enc.substring(200,300);
                String CS4=enc.substring(300);
                SMSSender.sendKeySMS(fromAddress, "CR1:"+CS1);
                SMSSender.sendKeySMS(fromAddress, "CR2:"+CS2);
                SMSSender.sendKeySMS(fromAddress, "CR3:"+CS3);
                SMSSender.sendKeySMS(fromAddress, "CR4:"+CS4);
            }
         }else if(recMsgString.substring(0,2).equals("CR")){
             System.out.println("KeyRSMS! "+ recMsgString.substring(2,3));
             SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
             SharedPreferences.Editor editor = sharedPref.edit();
             editor.putString("sharedparts-"+fromAddress+recMsgString.substring(2,3),recMsgString.substring(4));
             editor.commit();
             if((!sharedPref.getString("sharedparts-"+fromAddress+"1","xxx").equals("xxx"))&&(!sharedPref.getString("sharedparts-"+fromAddress+"2","xxx").equals("xxx"))&&(!sharedPref.getString("sharedparts-"+fromAddress+"3","xxx").equals("xxx"))&&(!sharedPref.getString("sharedparts-"+fromAddress+"4","xxx").equals("xxx"))) {
                 String ourKey = sharedPref.getString("sharedparts-" + fromAddress + "1", "xxx") + sharedPref.getString("sharedparts-" + fromAddress + "2", "xxx") + sharedPref.getString("sharedparts-" + fromAddress + "3", "xxx") + sharedPref.getString("sharedparts-" + fromAddress + "4", "xxx");
                 SharedPreferences.Editor editor2 = sharedPref.edit();
                 editor2.putString("sharedparts-" + fromAddress + "1", "xxx");
                 editor2.putString("sharedparts-" + fromAddress + "2", "xxx");
                 editor2.putString("sharedparts-" + fromAddress + "3", "xxx");
                 editor2.putString("sharedparts-" + fromAddress + "4", "xxx");
                 CryptoClass c = new CryptoClass(context);
                 String dec = c.decryptMessage(ourKey);
                 System.out.println("Decrypted: " + dec);
                 System.out.println("Adding new key to: "+fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));
                 editor2.putString("skey-" + fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""), dec);

                 editor2.commit();
                 System.out.println("Data:"+fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));
                 String cid = sharedPref.getString("cid-" + fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""), "xxx");
                 if (!cid.equals("xxx")) {
                    SQLiteInteractor sq = new SQLiteInteractor(context);
                     sq.putContact(cid, dec);
                     System.out.println("Added: "+cid+" Key:" + dec);
                     Notification.Builder mBuilder = new Notification.Builder(context)
                             .setContentTitle("New Contact!")
                             .setContentText(fromAddress + "has been added!")
                             .setSmallIcon(R.drawable.ic_fa_smile_o);
                     Intent resultIntent = new Intent(context, MainActivity.class);
                     TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                     stackBuilder.addParentStack(MainActivity.class);

                     stackBuilder.addNextIntent(resultIntent);
                     PendingIntent resultPendingIntent =
                             stackBuilder.getPendingIntent(
                                     0,
                                     PendingIntent.FLAG_UPDATE_CURRENT
                             );
                     mBuilder.setContentIntent(resultPendingIntent);
                     NotificationManager mNotificationManager =
                             (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                     Random r = new Random();
                     mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                     mNotificationManager.notify(r.nextInt(), mBuilder.build());

                 }
             }
         }else if(recMsgString.substring(0,4).equals("CMG:")){
             SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
             String skey = sharedPref.getString("skey-" + fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""), "xxx");
             if(!skey.equals("xxx")){
                 AESCrypto aes = new AESCrypto(skey);
                 String dec=aes.decrypt(recMsgString.substring(4));
                 System.out.println("Decrypted!-" + dec);
                 Notification.Builder mBuilder = new Notification.Builder(context)
                         .setContentTitle("New mail from " + fromAddress)
                         .setContentText(dec)
                         .setSmallIcon(R.drawable.small_not);
                 String cid = sharedPref.getString("cid-" + fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""), "xxx");
                 if (!cid.equals("xxx")) {
                     SQLiteInteractor sq = new SQLiteInteractor(context);
                     sq.putMessage(cid, "R" + dec);

                     Intent resultIntent = new Intent(context, chatActivity.class);
                     //setTitle(intent.getStringExtra("chatID"));
                     //cid = intent.getStringExtra("contactID");
                     //phoneNum = intent.getStringExtra("phoneNum");
                     //String photo = intent.getStringExtra("photoURI");
                     resultIntent.putExtra("phoneNum", fromAddress.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));
                     String[] dat = MainActivity.getContactPhoneNumber(context, cid);
                     resultIntent.putExtra("contactID", cid);
                     resultIntent.putExtra("photoURI",dat[1]);
                     Random r = new Random();
                     int ourID=r.nextInt();
                     resultIntent.putExtra("notiID",ourID);
                     try {
                         mBuilder.setLargeIcon(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(dat[1])));
                     }catch(Exception e){
                         System.out.println("Couldn't find image.");
                     }

                     resultIntent.putExtra("chatID", dat[2]);
                     TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                     stackBuilder.addParentStack(MainActivity.class);
                     stackBuilder.addNextIntent(resultIntent);
                     PendingIntent resultPendingIntent =
                             stackBuilder.getPendingIntent(
                                     0,
                                     PendingIntent.FLAG_UPDATE_CURRENT
                             );
                     mBuilder.setContentIntent(resultPendingIntent);
                     NotificationManager mNotificationManager =
                             (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                     mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                     mNotificationManager.notify(ourID, mBuilder.build());
                 }
             }else{
                 System.out.println("MessSMS-Could Not Find");
             }

         }
        }
    }
}
