package com.example.mitchell.UI;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mitchell on 23/02/2018.
 */

public class EntryAdapter extends CursorAdapter{
    Context context;

    public EntryAdapter(Context context, int RId, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.entry_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //finding text to fill
        TextView date = (TextView) view.findViewById(R.id.listDate);
        TextView efficiency = (TextView) view.findViewById((R.id.listEfficiency));
        String cursDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        double cursLitres = cursor.getDouble(cursor.getColumnIndexOrThrow("litres"));

        date.setText(cursDate);
        efficiency.setText(String.valueOf(cursLitres));



    }
}
