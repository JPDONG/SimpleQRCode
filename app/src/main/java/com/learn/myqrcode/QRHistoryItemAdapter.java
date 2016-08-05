package com.learn.myqrcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryItemAdapter extends ArrayAdapter<QRHistoryItem> {
    private int resourceId;

    /*public QRHistoryItemAdapter(Context context, int resource) {
        super(context, resource);
        resourceId = resource;
    }*/

    public QRHistoryItemAdapter(Context context, int resource, List<QRHistoryItem> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QRHistoryItem qrHistoryItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView itemImage = (ImageView) view.findViewById(R.id.image_item);
        TextView titleText = (TextView) view.findViewById(R.id.tv_title);
        TextView dateText = (TextView) view.findViewById(R.id.tv_date);
        TextView messageText = (TextView) view.findViewById(R.id.tv_message);
        titleText.setText(qrHistoryItem.getTitle());
        dateText.setText(qrHistoryItem.getDate());
        messageText.setText(qrHistoryItem.getMessage());
        if(qrHistoryItem.getTitle().equals("URL")){
            itemImage.setImageResource(R.drawable.internet);
        }else{
            itemImage.setImageResource(R.drawable.text);
        }
        return view;
    }
}
