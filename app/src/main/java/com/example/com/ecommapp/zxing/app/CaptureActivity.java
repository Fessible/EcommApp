/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.com.ecommapp.zxing.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.QrcodeActivity;
import com.example.com.ecommapp.util.Utils;
import com.example.com.ecommapp.zxing.camera.CameraManager;
import com.example.com.ecommapp.zxing.decode.BeepManager;
import com.example.com.ecommapp.zxing.decode.CaptureActivityHandler;
import com.example.com.ecommapp.zxing.decode.DecodeFormatManager;
import com.example.com.ecommapp.zxing.decode.FinishListener;
import com.example.com.ecommapp.zxing.decode.InactivityTimer;
import com.example.com.ecommapp.zxing.decode.Intents;
import com.example.com.ecommapp.zxing.decode.RGBLuminanceSource;
import com.example.com.ecommapp.zxing.util.Util;
import com.example.com.ecommapp.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The barcode reader activity itself. This is loosely based on the
 * CameraPreview example included in the Android SDK.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long INTENT_RESULT_DURATION = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
    private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
    private static final String ZXING_URL = "http://zxing.appspot.com/scan";
    private static final String RETURN_URL_PARAM = "ret";
    private String text;
    private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
    //图片返回码
    private static final int IMG_REQUEST = 1;
    //解析内容标识
    public static final String RESULT = "RESULT";
    public static final String QRCODE = "QR_CODE";

    private Bitmap scanBitmap;

    //动态权限
    public static final int READ_IMG = 1;

    static {
        DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
    }

    private enum Source {
        NATIVE_APP_INTENT, PRODUCT_SEARCH_LINK, ZXING_LINK, NONE
    }

    private CaptureActivityHandler handler;
    @BindView(R.id.imgview)
    ImageView imageView;

    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;

    @BindView(R.id.preview_view)
    SurfaceView surfaceView;

    @BindView(R.id.txt_flash)
    TextView txtFlash;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    SurfaceHolder surfaceHolder;

    private Result lastResult;
    private boolean hasSurface;
    private Source source;
    private String sourceUrl;
    private String returnUrlTemplate;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private boolean isFlash = false;
    private Uri uri;

    private int REQUEST_CODE = 3;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }


    @Override
    public void onCreate(Bundle icicle) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qcord_capture_layout);

        ButterKnife.bind(this);
        Util.currentActivity = this;
        CameraManager.init(getApplication());

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        handler = null;
        lastResult = null;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        // showHelpOnFirstLaunch();
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }


    @BindView(R.id.flash_btn)
    Button btnFlash;

    //打开闪光灯
    @OnClick(R.id.flash_btn)
    public void openFlash(View view) {
        if (!isFlash) {
            CameraManager.get().turnLightOn();
            txtFlash.setText(getString(R.string.qrcode_flash_close));
            btnFlash.setSelected(true);
        } else {
            CameraManager.get().turnLightOff();
            txtFlash.setText(getString(R.string.qrcode_flash_open));
            btnFlash.setSelected(false);
        }
        isFlash = !isFlash;
    }

    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
        }
    };

    @OnClick(R.id.photo_btn)
    public void openPhoto(View view) {
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapIntent = Intent.createChooser(innerIntent, getString(R.string.qrcode_photo_chose));
        startActivityForResult(wrapIntent, IMG_REQUEST);
    }

    @OnClick(R.id.qrcode_btn)
    public void createQrcode(View view) {
//        Bitmap b = createQRCode();
        text = Util.getIMEI(this);
        Bitmap b = Utils.createQRCode(300, 300, text);
        Intent intent = new Intent(this, QrcodeActivity.class);
        intent.putExtra(QRCODE, b);
        startActivity(intent);
        finish();
    }

    private Intent data;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMG_REQUEST) {
                this.data = data;
                //动态申请读写相册图片权限
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_IMG);
                } else {
                    parsePhoto(data);
                }
            }
        }
    }

    private void parsePhoto(final Intent data) {

        final Uri originalUri = data.getData();
        //开启线程解析数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = scanningImage(originalUri);
                if (result == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.qrcode_parse_err), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //处理扫描结果
                    String code = result.toString();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(code);
                    intent.setData(content_url);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {

        super.onResume();
        resetStatusView();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            Log.e("CaptureActivity", "onResume");
        }

        Intent intent = getIntent();
        String action = intent == null ? null : intent.getAction();
        String dataString = intent == null ? null : intent.getDataString();
        if (intent != null && action != null) {
            if (action.equals(Intents.Scan.ACTION)) {
                // Scan the formats the intent requested, and return the result
                // to the calling activity.
                source = Source.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
            } else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX)
                    && dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {
                // Scan only products and send the result to mobile Product
                // Search.
                source = Source.PRODUCT_SEARCH_LINK;
                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
            } else if (dataString != null && dataString.startsWith(ZXING_URL)) {
                // Scan formats requested in query string (all formats if none
                // specified).
                // If a return URL is specified, send the results there.
                // Otherwise, handle it ourselves.
                source = Source.ZXING_LINK;
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(sourceUrl);
                returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
            } else {
                // Scan all formats and handle the results ourselves (launched
                // from Home).
                source = Source.NONE;
                decodeFormats = null;
            }
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        } else {
            source = Source.NONE;
            decodeFormats = null;
            characterSet = null;
        }
        beepManager.updatePrefs();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (source == Source.NATIVE_APP_INTENT) {
                setResult(RESULT_CANCELED);
                finish();
                return true;
            } else if ((source == Source.NONE || source == Source.ZXING_LINK) && lastResult != null) {
                resetStatusView();
                if (handler != null) {
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Handle these events so they don't launch the Camera app
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param barcode   A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        if (barcode == null) {
            // This is from history -- no saved barcode
            handleDecodeInternally(rawResult, null);
        } else {
            beepManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, rawResult);
            switch (source) {
                case NATIVE_APP_INTENT:
                case PRODUCT_SEARCH_LINK:
                    handleDecodeExternally(rawResult, barcode);
                    break;
                case ZXING_LINK:
                    if (returnUrlTemplate == null) {
                        handleDecodeInternally(rawResult, barcode);
                    } else {
                        handleDecodeExternally(rawResult, barcode);
                    }
                    break;
                case NONE:
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
                        Toast.makeText(this, R.string.msg_bulk_mode_scanned, Toast.LENGTH_SHORT).show();
                        // Wait a moment or else it will scan the same barcode
                        // continuously about 3 times
                        if (handler != null) {
                            handler.sendEmptyMessageDelayed(R.id.restart_preview, BULK_MODE_SCAN_DELAY_MS);
                        }
                        resetStatusView();
                    } else {
                        handleDecodeInternally(rawResult, barcode);
                    }
                    break;
            }
        }
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of
     * the barcode.
     *
     * @param barcode   A bitmap of the captured image.
     * @param rawResult The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_minor_text));
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
            canvas.drawRect(border, paint);

            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1]);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A))
                    || (rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
                // Hacky special case -- draw two lines, for the barcode and
                // metadata
                drawLine(canvas, paint, points[0], points[1]);
                drawLine(canvas, paint, points[2], points[3]);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    canvas.drawPoint(point.getX(), point.getY(), paint);
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
        canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
    }

    // Put up our own UI for how to handle the decoded contents.
    @SuppressWarnings("unchecked")
    private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
        viewfinderView.setVisibility(View.GONE);

        Map<ResultMetadataType, Object> metadata = (Map<ResultMetadataType, Object>) rawResult.getResultMetadata();
        if (metadata != null) {
            StringBuilder metadataText = new StringBuilder(20);
            for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }
            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
            }
        }
    }

    // Briefly show the contents of the barcode, then handle the result outside
    // Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
        viewfinderView.drawResultBitmap(barcode);

        // Since this message will only be shown for a second, just tell the
        // user what kind of
        // barcode was found (e.g. contact info) rather than the full contents,
        // which they won't
        // have time to read.

        if (source == Source.NATIVE_APP_INTENT) {
            // Hand back whatever action they requested - this can be changed to
            // Intents.Scan.ACTION when
            // the deprecated intent is retired.
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            Message message = Message.obtain(handler, R.id.return_scan_result);
            message.obj = intent;
            handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        } else if (source == Source.PRODUCT_SEARCH_LINK) {
            // Reformulate the URL which triggered us into a query, so that the
            // request goes to the same
            // TLD as the scan URL.
            Message message = Message.obtain(handler, R.id.launch_product_query);
            // message.obj = sourceUrl.substring(0, end) + "?q=" +
            // resultHandler.getDisplayContents().toString()
            // + "&source=zxing";
            handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        } else if (source == Source.ZXING_LINK) {
            // Replace each occurrence of RETURN_CODE_PLACEHOLDER in the
            // returnUrlTemplate
            // with the scanned code. This allows both queries and REST-style
            // URLs to work.
            Message message = Message.obtain(handler, R.id.launch_product_query);
            // message.obj = returnUrlTemplate.replace(RETURN_CODE_PLACEHOLDER,
            // resultHandler.getDisplayContents()
            // .toString());
            handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            /**
             * use a CameraManager to manager the camera's life cycles
             */
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
            return;
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.e(TAG, "Unexpected error initializating camera", e);
            displayFrameworkBugMessageAndExit();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    //扫描二维码，并解析
    protected Result scanningImage(Uri path) {
        if (path == null || path.equals("")) {
            return null;
        }
        ContentResolver resolver = getContentResolver();
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        try {
            //将相册中的内容解析为Bitmap
            scanBitmap = MediaStore.Images.Media.getBitmap(resolver, path);
            RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            return reader.decode(bitmap1, hints);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建二维码
     *
     * @return
     */
    private  Bitmap createQRCode() {
        int QR_WIDTH = 300;
        int QR_HEIGHT = 300;

        try {
            // 需要引入core包
            QRCodeWriter writer = new QRCodeWriter();
            text = Util.getIMEI(this);
            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }
            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            System.out.println("w:" + martix.getWidth() + "h:" + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // cheng chen de er wei ma
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    //动态权限获取
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Util.REQUST_CODE) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            text = tm.getDeviceId();
        }
        if (requestCode == READ_IMG) {
            parsePhoto(data);
        }
    }
}
