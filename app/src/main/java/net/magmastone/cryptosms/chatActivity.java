package net.magmastone.cryptosms;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.magmastone.cryptosms.R;

import java.util.ArrayList;

public class chatActivity extends Activity {
    chatArrayAdapter simpleAdpt;
    String mainKey;
    String cid;
    String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("chatID"));
        cid = intent.getStringExtra("contactID");
        int notid = intent.getIntExtra("notiID", -1);
        if(notid != -1){
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notid);
        }
        phoneNum = intent.getStringExtra("phoneNum");
        String photo = intent.getStringExtra("photoURI");
        final SQLiteInteractor sq = new SQLiteInteractor(this);
        ArrayList<String> listms = new ArrayList<String>();
        String[] listAll = sq.getMessages(cid);
        for (String mes : listAll){
            listms.add(sq.getMessage(mes));
        }
        System.out.println("Phone: "+phoneNum.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", "").replace("+", ""));
        mainKey=sq.getPhoneKey(phoneNum.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));
        simpleAdpt = new chatArrayAdapter(this,listms.toArray(new String[0]),photo);
        ListView lv = (ListView) findViewById(R.id.chatListView);
        lv.setAdapter(simpleAdpt);

        Button sendMess = (Button)findViewById(R.id.NewSend);
        sendMess.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    TextView mess = (TextView)findViewById(R.id.messageEdit);
                    System.out.println("Key:" + mainKey);
                    AESCrypto aes=new AESCrypto(mainKey);
                    String encr = aes.encrypt(mess.getText().toString());
                    System.out.println("SMS ToSend="+encr);
                    SMSSender.sendMessageSMS(phoneNum, "CMG:"+encr);
                    sq.putMessage(cid, "S"+mess.getText().toString());
                    simpleAdpt.add(mess.getText().toString());
                    mess.setText("");
                    simpleAdpt.notifyDataSetChanged();
                    return true;
                }

                return false;
            }});

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
