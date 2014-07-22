package net.magmastone.cryptosms;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Random;

public class SQLiteInteractor {
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CID = "cid";
    private static final String KEY_KEY = "key";
    private static final String[] COLUMNS = {KEY_ID,KEY_CID,KEY_KEY};
    // Database Version
    private SharedPreferences sharedPref;
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BookDB";

    public SQLiteInteractor(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public String[] getMessages(String cid){
        String[] cons=new String[0];
        String contacts=sharedPref.getString("messages-"+cid,"xxx");
        if(!contacts.equals("xxx")){
            cons = contacts.split(",");
        }
        return cons;
    }
    public String getMessage(String mid){
        String contacts=sharedPref.getString("message-"+mid,"xxx");
        if(contacts.equals("xxx")){
           return "ERROR!";
        }
        return contacts;
    }
    public String getContactKey(String cid){
        return sharedPref.getString("contactkey-"+cid,"xxx");
    }
    public String getPhoneKey(String cid){
        return  sharedPref.getString("skey-"+cid,"xxx");
    }
    public void putMessage(String cid, String message){
        //cid,message
        String ext = sharedPref.getString("messages-"+cid, "xxx");
        String set=null;

        SharedPreferences.Editor editor = sharedPref.edit();
        Random r = new Random();
        String mesid=String.valueOf(r.nextInt());
        editor.putString("message-"+mesid, message);
        if(!ext.equals("xxx")){
            set=ext+","+mesid;
        }else{
            set=mesid;
        }
        editor.putString("messages-"+cid, set);
        editor.commit();
    }
    public String[] getAllContact(){
        String[] cons=new String[0];
        String contacts=sharedPref.getString("contactsAll","xxx");
        if(!contacts.equals("xxx")){
            cons = contacts.split(",");
        }
        return cons;
    }
    public void putContact(String cid, String key){
        String ext = sharedPref.getString("contactsAll", "xxx");
        String set=null;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("contact-"+cid, key);
        if(!ext.equals("xxx")){
            set=ext+","+cid;
        }else{
            set=cid;
        }
        editor.putString("contactsAll", set);
        editor.commit();
    }


}
