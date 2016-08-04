package com.learn.myqrcode;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dongjiangpeng on 2016/8/2 0002.
 */
public class ZXingScannerView extends BarcodeScannerView {

    private ZXingScannerView.ResultWorker mResultWorker;
    private MultiFormatReader mMultiFormatReader;
    private List<BarcodeFormat> mFormats;
    //private Boolean isDialogShowing = false;
    private static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<>();

    static {
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
    }

    public interface ResultWorker {
        void workResult(Result var);
    }

    public ZXingScannerView(Context context) {
        super(context);
        initMultiFormatReader();
    }


    public ZXingScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMultiFormatReader();
    }

    private void initMultiFormatReader() {
        Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        initMultiFormatReader();
    }

    public Collection<BarcodeFormat> getFormats() {
        if (mFormats == null) {
            return ALL_FORMATS;
        }
        return mFormats;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        /*if(isDialogShowing){
            return;
        }*/
        if (mResultWorker == null) {
            return;
        }

        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            int tmp = width;
            width = height;
            height = tmp;
            data = rotatedData;
        }
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, width, height);
        if (source != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = mMultiFormatReader.decodeWithState(binaryBitmap);
            } catch (ReaderException re) {
                // continue
            } catch (NullPointerException npe) {
                // This is terrible
            } catch (ArrayIndexOutOfBoundsException aoe) {

            } finally {
                mMultiFormatReader.reset();
            }
        }
        if (rawResult != null) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            final Result finalRawResult = rawResult;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ResultWorker resultHandler = mResultWorker;
                    mResultWorker = null;
                    stopCameraPreview();
                    if (resultHandler != null) {
                        resultHandler.workResult(finalRawResult);
                    }
                }
            });
        } else {
            camera.setOneShotPreviewCallback(this);
        }
    }



    private PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        PlanarYUVLuminanceSource source = null;
        source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
        return source;
    }


    public void setResultHandler(ZXingScannerView.ResultWorker resultWorker) {
        mResultWorker = resultWorker;
    }

    public void resumeCameraPreview(ZXingScannerView.ResultWorker resultWorker) {
        mResultWorker = resultWorker;
        super.resumeCameraPreview();
    }

}
