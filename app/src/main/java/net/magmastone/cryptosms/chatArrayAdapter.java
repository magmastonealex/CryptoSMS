package net.magmastone.cryptosms;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class chatArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> values;
    private final String contactPicURI;
    public chatArrayAdapter(Context context, String[] values,String cpicuri) {
        super(context, R.layout.chatlayout, values);
        this.context = context;
        this.values = new ArrayList(Arrays.asList(values));
        this.contactPicURI = cpicuri;

    }
    @Override
    public void add(String s){
        values.add(s);

    }
    public void newMess(String[]newest){
        super.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        View rowView;
        if(values.get(position).charAt(0) == 'S'){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.contactlayout_y, parent, false);
        textView = (TextView) rowView.findViewById(R.id.chatText);
        }else{
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.chatlayout, parent, false);
            textView = (TextView) rowView.findViewById(R.id.chatText);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.personImgC);
            if(contactPicURI!=null) {
                imageView.setImageURI(Uri.parse(contactPicURI));
            }
        }
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).substring(1));
        // change the icon for Windows and iPhone
        //String s = values[position];
        //if (s.startsWith("iPhone")) {
        //    imageView.setImageResource(R.drawable.no);
        //} else {
        //    imageView.setImageResource(R.drawable.ok);
        //}

        return rowView;
    }
}