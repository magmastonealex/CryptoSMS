package net.magmastone.cryptosms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    CryptoClass cryp;
    contactArrayAdapter simpleAdpt;
    String[] cons;
    static final int PICK_CONTACT_REQUEST=103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cryp=new CryptoClass(this);
        final String pub = cryp.getPublicKey();
        System.out.println("public: "+pub);
        setContentView(R.layout.activity_main);
        SQLiteInteractor sq = new SQLiteInteractor(this);
        cons = sq.getAllContact();
        System.out.println("Contact:"+cons);
        ArrayList<String> listAll = new ArrayList<String>();
        ArrayList<String> listPic = new ArrayList<String>();
        for (String con : cons){
            System.out.println("Data!"+con);
            String[] dat = getContactPhoneNumber(this,con);
            listAll.add(dat[2]);
            listPic.add(dat[1]);
        }
        simpleAdpt = new contactArrayAdapter(this,listAll.toArray(new String[0]),listPic.toArray(new String[0]));
        ListView lv = (ListView) findViewById(R.id.cListView);
        lv.setAdapter(simpleAdpt);
        final Context c = getApplicationContext();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,long id) {
               TextView clickedView = (TextView) view.findViewById(R.id.personName);
               Intent intent = new Intent(c, chatActivity.class);
               intent.putExtra("chatID", clickedView.getText().toString());
               intent.putExtra("contactID",cons[position]);
               String[] dat = getContactPhoneNumber(c,cons[position]);
               intent.putExtra("phoneNum",dat[0]);
               intent.putExtra("photoURI",dat[1]);
               //setTitle(intent.getStringExtra("chatID"));
               //cid = intent.getStringExtra("contactID");
               //phoneNum = intent.getStringExtra("phoneNum");
               //String photo = intent.getStringExtra("photoURI");
               startActivity(intent);
            }
        });

        /*Button sendKey = (Button)findViewById(R.id.sendKeyButton);
        sendKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    return true;
                }

                return false;
            }
        });
        */

        /*final Context c=(Context)this;

        Button sendMess = (Button)findViewById(R.id.sendButton);
        sendMess.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
                    TextView phoneNum = (TextView)findViewById(R.id.phoneSend);
                    String key = sharedPref.getString("skey-"+phoneNum.getText().toString(),"xxx");
                    System.out.println("tapped, looked up "+key);
                    TextView mess = (TextView)findViewById(R.id.messageSendText);
                    AESCrypto aes=new AESCrypto(key);
                    String encr = aes.encrypt(mess.getText().toString());
                    System.out.println("SMS ToSend="+encr);
                    SMSSender.sendMessageSMS(phoneNum.getText().toString(), encr);

                    return true;
                }

                return false;
            }
        });*/

        //String CS1=pub.substring(0,100);
        //String CS2=pub.substring(100,200);
        //String CS3=pub.substring(200,300);
        //String CS4=pub.substring(300);
        //SMSSender.sendKeySMS("6478083846", "CS1:"+CS1);
        //SMSSender.sendKeySMS("6478083846", "CS2:"+CS2);
        //SMSSender.sendKeySMS("6478083846", "CS3:"+CS3);
        //SMSSender.sendKeySMS("6478083846", "CS4:"+CS4);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {

        super.onActivityResult( requestCode, resultCode, intent );
        if ( requestCode == PICK_CONTACT_REQUEST ) {

            if ( resultCode == RESULT_OK ) {
                Uri pickedPhoneNumber = intent.getData();
                String[] phone=getContactPhoneNumber(this, pickedPhoneNumber.getLastPathSegment());
                System.out.println("Picked: "+phone[0].replace("(","").replace(")","").replace(" ","").replace("-","").replace("+",""));
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("cid-"+phone[0].replace("(","").replace(")","").replace(" ","").replace("-","").replace("+1","").replace("+",""),pickedPhoneNumber.getLastPathSegment());
                editor.commit();
                final String pub = cryp.getPublicKey();
                String CS1=pub.substring(0,100);
                String CS2=pub.substring(100,200);
                String CS3=pub.substring(200,300);
                String CS4=pub.substring(300);
                SMSSender.sendKeySMS(phone[0], "CS1:"+CS1);
                SMSSender.sendKeySMS(phone[0], "CS2:"+CS2);
                SMSSender.sendKeySMS(phone[0], "CS3:"+CS3);
                SMSSender.sendKeySMS(phone[0], "CS4:"+CS4);
                System.out.println("Adding!");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Adding contact").setMessage("You will receive a notification when negotiation completes").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
           /*
            */
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private static final String TAG = "PhoneUtils";

    public static String[] getContactPhoneNumber(Context context, String contactId) {
        String phoneNumber = null;
        String picID=null;
        String cName=null;
        String[] whereArgs = new String[] { String.valueOf(contactId) };

        Log.d(TAG, "Got contact id: "+contactId);

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone._ID + " = ?",
                whereArgs,
                null);

        int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

        if (cursor != null) {
            Log.d(TAG, "Returned contact count: "+cursor.getCount());
            try {
                if (cursor.moveToFirst()) {
                    phoneNumber = cursor.getString(phoneNumberIndex);
                    cName=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    picID=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                }
            } finally {
                cursor.close();
            }
        }

        Log.d(TAG, "Returning phone number: " + phoneNumber);
        String[] retdat =  {phoneNumber, picID, cName};
        return retdat;
    }



}
