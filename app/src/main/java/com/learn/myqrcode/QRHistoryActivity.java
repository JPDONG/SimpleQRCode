package com.learn.myqrcode;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.learn.myqrcode.database.QRHistoryDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<QRHistoryItem> itemList = new ArrayList<QRHistoryItem>();
    private QRHistoryItemAdapter adapter;
    private QRHistoryDBHelper qrHistoryDBHelper;
    private TextView editHistory;
    private ImageView backButton;
    private ListView listView;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_history);
        qrHistoryDBHelper = new QRHistoryDBHelper(this,"QRHistory.db",null,1);
        db = qrHistoryDBHelper.getWritableDatabase();
        setItemList();
        initViews();
        initEvents();
    }

    private void initEvents() {
        editHistory.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        backButton.setOnClickListener(this);

    }

    private void initViews() {

        adapter = new QRHistoryItemAdapter(this,R.layout.qr_history_item,itemList);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        editHistory = (TextView) findViewById(R.id.edit_history);
        backButton = (ImageView) findViewById(R.id.image_back);

    }

    private void setItemList() {

        Cursor cursor = db.query("qr_history",null,null,null,null,null,"id desc");
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
        switch (view.getId()){
            case R.id.image_back:
                onKeyDown(KeyEvent.KEYCODE_BACK, null);
                break;
            case R.id.edit_history:
                for (int i = 0; i < itemList.size(); i++) {
                    LinearLayout itemView = (LinearLayout) listView.getChildAt(i);
                    ImageView deleteButton = (ImageView) itemView.findViewById(R.id.image_delete_item);
                    if(deleteButton.getVisibility() == View.VISIBLE){
                        deleteButton.setVisibility(View.GONE);
                    }else{
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    final int finalI = i;
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            QRHistoryItem item = itemList.get(finalI);
                            int id = item.getId();
                            deleteHistoryItem(finalI,id);
                        }
                    });
                }
                break;
        }
    }

    private void deleteHistoryItem(int finalI, int id) {
        db.delete("qr_history", "id="+id,null);
        adapter.remove(adapter.getItem(finalI));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final QRHistoryItem clickItem = itemList.get(i);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(clickItem.getMessage()).setTitle(clickItem.getTitle());
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialogBuilder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("QR_Result",clickItem.getMessage());
                cbm.setPrimaryClip(clipData);

            }
        });
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(clickItem.getMessage());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        AlertDialog dialog = dialogBuilder.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
