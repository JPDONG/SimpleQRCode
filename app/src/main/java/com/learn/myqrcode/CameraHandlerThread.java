package com.learn.myqrcode;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by dongjiangpeng on 2016/8/2 0002.
 */
public class CameraHandlerThread extends HandlerThread {

    private BarcodeScannerView mScannerView;

    public CameraHandlerThread(String name, BarcodeScannerView scannerView) {
        super(name);
        mScannerView = scannerView;
        start();
    }

    public void startCamera(final int cameraId){
        Handler localHandler = new Handler();
        localHandler.post(new Runnable() {
            @Override
            public void run() {
                final Camera camera = CameraUtils.getCameraInstance(cameraId);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.setupCameraPreview(CameraWrapper.getWrapper(camera,cameraId));
                    }
                });
            }
        });

    }

}
