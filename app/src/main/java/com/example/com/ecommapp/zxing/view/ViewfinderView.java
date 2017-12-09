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

package com.example.com.ecommapp.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.zxing.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;

    private int ScreenRate;
    //边角的宽度
    private static final int CORNER_WIDTH = 10;
    //密度
    private static float density;
    //每次扫描的距离
    private static final int SPEEN_DISTANCE = 5;
    //滑动条的顶部高度
    private int slideTop;
    //滑动条的底部高度
    private int slideBottom;
    //是否第一次滑动
    private boolean isFirst;

    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
//        laserColor = resources.getColor(R.color.viewfinder_laser);
        laserColor = resources.getColor(R.color.color_5a79b7);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
        density = context.getResources().getDisplayMetrics().density;
        ScreenRate = (int) (15 * density);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = cameraManager.get().getFramingRect();
        Rect previewFrame = cameraManager.get().getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }

        //扫码框的上下左右
        int frameLeft = frame.left;
        int frameTop = frame.top;
        int frameRight = frame.right;
        int frameBottom = frame.bottom;

        //初始化滑动条
        if (!isFirst) {
            isFirst = true;
            slideTop = frameTop;
            slideBottom = frameBottom;
        }

        //屏幕的宽高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        //绘制扫描框以外的矩形
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frameTop, paint);
        canvas.drawRect(0, frameTop, frame.left, frameBottom + 1, paint);
        canvas.drawRect(frame.right + 1, frameTop, width, frameBottom + 1, paint);
        canvas.drawRect(0, frameBottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
//
//            float scaleX = frame.width() / (float) previewFrame.width();
//            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;

            //绘制扫描框内的效果
            paint.setColor(laserColor);
            //绘制边角,一共绘制8个矩形拼凑成四个角
            canvas.drawRect(frameLeft, frameTop, frameLeft + ScreenRate, frameTop + CORNER_WIDTH, paint);
            canvas.drawRect(frameLeft, frameTop, frameLeft + CORNER_WIDTH, frameTop + ScreenRate, paint);

            canvas.drawRect(frameRight - ScreenRate, frameTop, frameRight, frameTop + CORNER_WIDTH, paint);
            canvas.drawRect(frameRight - CORNER_WIDTH, frameTop, frameRight, frameTop + ScreenRate, paint);

            canvas.drawRect(frameLeft, frameBottom - CORNER_WIDTH, frameLeft + ScreenRate, frameBottom, paint);
            canvas.drawRect(frameLeft, frameBottom - ScreenRate, frameLeft + CORNER_WIDTH, frameBottom, paint);

            canvas.drawRect(frameRight - ScreenRate, frameBottom - CORNER_WIDTH, frameRight, frameBottom, paint);
            canvas.drawRect(frameRight - CORNER_WIDTH, frameBottom - ScreenRate, frameRight, frameBottom, paint);
            //每次移动的距离
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= slideBottom) {
                slideTop = frameTop;
            }

            //绘制扫描线
            Rect lineRect = new Rect();
            lineRect.left = frameLeft;
            lineRect.right = frameRight;
            lineRect.top = slideTop;
            lineRect.bottom = slideTop + 18;
            canvas.drawBitmap(((BitmapDrawable) (getResources()
                    .getDrawable(R.drawable.fle))).getBitmap(), null, lineRect, paint);


            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);

//                synchronized (currentPossible) {
//                    for (ResultPoint point : currentPossible) {
//                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
//                                frameTop + (int) (point.getY() * scaleY),
//                                POINT_SIZE, paint);
//                    }
//                }
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
//                synchronized (currentLast) {
//                    float radius = POINT_SIZE / 2.0f;
//                    for (ResultPoint point : currentLast) {
//                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
//                                frameTop + (int) (point.getY() * scaleY),
//                                radius, paint);
//                    }
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }
        }

        // Request another update at the animation interval, but only repaint the laser line,
        // not the entire viewfinder mask.
//            postInvalidateDelayed(ANIMATION_DELAY,
//                    frame.left - POINT_SIZE,
//                    frame.top - POINT_SIZE,
//                    frame.right + POINT_SIZE,
//                    frame.bottom + POINT_SIZE);
        //刷新局部内容
        postInvalidateDelayed(ANIMATION_DELAY,
                frame.left,
                frame.top,
                frame.right,
                frame.right);
    }


    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();

    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

}

