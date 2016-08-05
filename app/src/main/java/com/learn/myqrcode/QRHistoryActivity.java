package com.learn.myqrcode;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.learn.myqrcode.database.QRHistoryDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryActivity extends Activity implements View.OnClickListener {
    private List<QRHistoryItem> itemList = new ArrayList<QRHistoryItem>();
    private QRHistoryDBHelper qrHistoryDBHelper;
    private TextView editHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_history);
        initViews();
        initEvents();
    }

    private void initEvents() {
        editHistory.setOnClickListener(this);
    }

    private void initViews() {
        setItemList();
        QRHistoryItemAdapter adapter = new QRHistoryItemAdapter(this,R.layout.qr_history_item,itemList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        editHistory = (TextView) findViewById(R.id.edit_history);

    }

    private void setItemList() {
        qrHistoryDBHelper = new QRHistoryDBHelper(this,"QRHistory.db",null,1);
        SQLiteDatabase db = qrHistoryDBHelper.getWritableDatabase();
        Cursor cursor = db.query("qr_history",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                QRHistoryItem item = new QRHistoryItem(title,message,date,id);
                itemList.add(item);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View view) {
        
    }
}
