package com.learn.myqrcode;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.learn.myqrcode.database.QRHistoryDBHelper;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements ZXingScannerView.ResultWorker {
    private ZXingScannerView mScannerView;
    private String TAG = "MainActivity";
    private QRHistoryDBHelper qrHistoryDBHelper;
    private ImageView historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: true");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        qrHistoryDBHelper = new QRHistoryDBHelper(this,"QRHistory.db",null,1);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        historyButton = (ImageView) findViewById(R.id.bt_qr_history);
        initEvents();

    }

    private void initEvents() {
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,QRHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: true");
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        resumePreview();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: true");
        super.onPause();
    }

    @Override
    public void workResult(final Result var) {
        /*Toast.makeText(this, "Contents = " + var.getText() +
                ", Format = " + var.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();*/
        String title = "";
        String date = getDate();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if(isHomepage(var.getText())) {
            buildURLDialog(dialogBuilder,var);
            title ="URL";
            insertHistory(var,title,date);

        }else{
            buildTextDialog(dialogBuilder,var);
            title = "文字";
            insertHistory(var,title, date);
        }
        AlertDialog dialog = dialogBuilder.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if (dialog.isShowing()) {
            resumePreviewWithoutResult();
        }


        /*dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                resumePreview();
            }
        });*/

    }

    private String getDate() {
        Calendar cal=Calendar.getInstance();
        int y=cal.get(Calendar.YEAR);
        int m=cal.get(Calendar.MONTH);
        int d=cal.get(Calendar.DATE);
        return y + "/" + (m + 1) + "/" + d;
    }

    private void insertHistory(Result var, String title, String date) {
        SQLiteDatabase db = qrHistoryDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("date",date);
        values.put("message",var.getText());
        db.insert("qr_history",null,values);
    }

    private void buildTextDialog(AlertDialog.Builder dialogBuilder, final Result var) {
        dialogBuilder.setMessage(var.getText()).setTitle(R.string.title_text);
        dialogBuilder.setNegativeButton(R.string.finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resumePreview();
            }
        });
        dialogBuilder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("QR_Result", var.getText());
                cbm.setPrimaryClip(clipData);
                resumePreview();
            }
        });
    }

    private void buildURLDialog(AlertDialog.Builder dialogBuilder, final Result var) {
        dialogBuilder.setMessage(var.getText()).setTitle(R.string.title_url);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resumePreview();
            }
        });
        dialogBuilder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("QR_Result", var.getText());
                cbm.setPrimaryClip(clipData);
                resumePreview();
            }
        });
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(var.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public static boolean isHomepage(String str) {
        Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    private void resumePreviewWithoutResult() {
        mScannerView.resumeCameraPreview();
    }

    private void resumePreview() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MainActivity.this);
            }
        });
    }
}
