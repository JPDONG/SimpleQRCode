package com.learn.myqrcode;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements ZXingScannerView.ResultWorker {
    private ZXingScannerView mScannerView;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: true");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if(isHomepage(var.getText())) {
            dialogBuilder.setMessage(var.getText()).setTitle("开启URL");
            dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resumePreview();
                }
            });
            dialogBuilder.setNeutralButton("复制", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("QR_Result", var.getText());
                    cbm.setPrimaryClip(clipData);
                    resumePreview();
                }
            });
            dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri uri = Uri.parse(var.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }else{
            dialogBuilder.setMessage(var.getText()).setTitle("文本内容");
            dialogBuilder.setNegativeButton("完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resumePreview();
                }
            });
            dialogBuilder.setNeutralButton("复制", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("QR_Result", var.getText());
                    cbm.setPrimaryClip(clipData);
                    resumePreview();
                }
            });
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

    public static boolean isHomepage(String str) {
        /*String regex = "http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*";
        Pattern pattern = Pattern.compile(regex);*/
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
