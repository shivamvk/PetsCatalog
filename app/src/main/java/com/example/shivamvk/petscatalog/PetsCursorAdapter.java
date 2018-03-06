package com.example.shivamvk.petscatalog;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shivamvk.petscatalog.data.PetsContract;

public class PetsCursorAdapter extends CursorAdapter {
    public PetsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView breed = (TextView) view.findViewById(R.id.summary);

        int indexname = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
        int indexbreed = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);

        String petname = cursor.getString(indexname);
        String petbreed = cursor.getString(indexbreed);

        name.setText(petname);
        breed.setText(petbreed);
    }
}
