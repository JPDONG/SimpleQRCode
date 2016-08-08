package com.learn.myqrcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
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
    private String TAG = "QRHistoryItemAdapter";
    private List<QRHistoryItem> historyItemList;
    private QRHistoryDBHelper qrHistoryDBHelper;
    private SQLiteDatabase db;


    public QRHistoryItemAdapter(Context context, int resource, List<QRHistoryItem> objects) {
        super(context, resource, objects);
        resourceId = resource;
        historyItemList = objects;
        qrHistoryDBHelper = new QRHistoryDBHelper(getContext(), "QRHistory.db", null, 1);
        db = qrHistoryDBHelper.getWritableDatabase();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        QRHistoryItem qrHistoryItem = getItem(position);
        ViewHolder holder;
        View view;
        view = LayoutInflater.from(getContext()).inflate(resourceId,null);
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
        if(QRHistoryActivity.isEditState) {
            ImageView deleteButton = (ImageView) view.findViewById(R.id.image_delete_item);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    QRHistoryItem item = historyItemList.get(position);
                    int id = item.getId();
                    db.delete("qr_history", "id=" + id, null);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            QRHistoryActivity.refreshListView(position);
                        }
                    });
                }
            });
        }

        /*优化
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder = new ViewHolder();
            if(QRHistoryActivity.isEditState){
                holder.deleteButton = (ImageView) view.findViewById(R.id.image_delete_item);
                *//*holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QRHistoryItem item = historyItemList.get(finalI);
                        int id = item.getId();
                        deleteHistoryItem(finalI, id);
                    }
                });*//*
            }
            holder.itemImage = (ImageView) view.findViewById(R.id.image_item);
            holder.titleText = (TextView) view.findViewById(R.id.tv_title);
            holder.dateText = (TextView) view.findViewById(R.id.tv_date);
            holder.messageText = (TextView) view.findViewById(R.id.tv_message);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Log.d(TAG, "getView: dataText = " + qrHistoryItem.getDate());
        holder.dateText.setText(qrHistoryItem.getDate());
        holder.messageText.setText(qrHistoryItem.getMessage());
        if(qrHistoryItem.getTitle().equals("URL")){
            holder.itemImage.setImageResource(R.drawable.internet);
        }else{
            holder.itemImage.setImageResource(R.drawable.text);
        }
        */
        dateText.setText(qrHistoryItem.getDate());
        messageText.setText(qrHistoryItem.getMessage());
        if(qrHistoryItem.getTitle().equals("URL")){
            itemImage.setImageResource(R.drawable.internet);
        }else{
            itemImage.setImageResource(R.drawable.text);
        }


        return view;
    }

    class ViewHolder{
        ImageView deleteButton;
        ImageView itemImage;
        TextView titleText;
        TextView dateText;
        TextView messageText;
    }
}
