package com.learn.myqrcode;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjiangpeng on 2016/8/5 0005.
 */
public class QRHistoryActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<QRHistoryItem> itemList = new ArrayList<QRHistoryItem>();
    public static QRHistoryItemAdapter adapter;
    private QRHistoryDBHelper qrHistoryDBHelper;
    private TextView editHistory;
    private TextView historyTitle;
    private ImageView backButton;
    private ListView listView;
    private SQLiteDatabase db;
    public static boolean isEditState = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_history);
        qrHistoryDBHelper = new QRHistoryDBHelper(this, "QRHistory.db", null, 1);
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

        adapter = new QRHistoryItemAdapter(this, R.layout.qr_history_item, itemList);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        editHistory = (TextView) findViewById(R.id.edit_history);
        backButton = (ImageView) findViewById(R.id.image_back);
        historyTitle = (TextView) findViewById(R.id.history_title);
    }

    private void setItemList() {

        Cursor cursor = db.query("qr_history", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                QRHistoryItem item = new QRHistoryItem(title, message, date, id);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                if(isEditState){
                    isEditState = false;
                    editHistory.setText("编辑");
                    historyTitle.setText("扫描记录");
                    adapter = new QRHistoryItemAdapter(this, R.layout.qr_history_item, itemList);
                    listView.setAdapter(adapter);
                }else{
                    finish();
                }
                break;
            case R.id.edit_history:
                if (!isEditState) {
                    editHistory.setText("删除全部");
                    historyTitle.setText("删除项目");
                    isEditState = true;
                    adapter = new QRHistoryItemAdapter(this, R.layout.qr_history_item, itemList);
                    listView.setAdapter(adapter);
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setMessage("确定要删除全部的扫描记录？").setTitle("删除全部");
                    dialogBuilder.setNegativeButton(R.string.cancel, null);
                    dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.delete("qr_history", null, null);
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final QRHistoryItem clickItem = itemList.get(i);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(clickItem.getMessage()).setTitle(clickItem.getTitle());
        if(clickItem.getTitle().equals("URL")){
            dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri uri = Uri.parse(clickItem.getMessage());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, null);
        }else{
            dialogBuilder.setNegativeButton("完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        dialogBuilder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("QR_Result", clickItem.getMessage());
                cbm.setPrimaryClip(clipData);

            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public static void refreshListView(int position){
        adapter.remove(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(isEditState){
            isEditState = false;
            editHistory.setText("编辑");
            historyTitle.setText("扫描记录");
            adapter = new QRHistoryItemAdapter(this, R.layout.qr_history_item, itemList);
            listView.setAdapter(adapter);
        }else{
            finish();
        }
    }
}
