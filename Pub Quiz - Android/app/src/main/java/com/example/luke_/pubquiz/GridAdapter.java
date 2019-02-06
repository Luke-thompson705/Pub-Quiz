package com.example.luke_.pubquiz;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class GridAdapter extends BaseAdapter {

    private String[] items;
    Context context;
    private LayoutInflater inflater;

    public GridAdapter(String[] data, Context context) {
        this.items = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        if(convertView == null) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_layout,null);
            gridView.setMinimumHeight(screenHeight/10);
        }

        TextView letter = (TextView) gridView.findViewById(R.id.Letters);
        letter.setHeight(screenHeight/10);
        letter.setText(items[position]);
        return gridView;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }





}
