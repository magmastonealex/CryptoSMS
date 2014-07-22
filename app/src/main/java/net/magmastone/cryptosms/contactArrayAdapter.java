package net.magmastone.cryptosms;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class contactArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String[] pics;
    public contactArrayAdapter(Context context, String[] values,String[]pics) {
        super(context, R.layout.contactlayout, values);
        this.context = context;
        this.values = values;
        this.pics=pics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contactlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.personName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.personImg);
        if(pics[position] != null) {
            imageView.setImageURI(Uri.parse(pics[position]));
        }
       // imageView.setImageResource(new Integer(pics[position]));
        textView.setText(values[position]);
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