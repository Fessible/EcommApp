/*
 * Copyright (C) 2010 ZXing authors
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

package com.example.com.ecommapp.zxing.decode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.zxing.app.CaptureActivity;
import com.example.com.ecommapp.zxing.camera.CameraManager;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;

final class DecodeHandler extends Handler
{

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final CaptureActivity activity;
	private final MultiFormatReader multiFormatReader;

	DecodeHandler(CaptureActivity activity, Hashtable<DecodeHintType, Object> hints)
	{
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message)
	{
		if (message.what == R.id.decode)
		{
			// Log.d(TAG, "Got decode message");
			decode((byte[]) message.obj, message.arg1, message.arg2);
		}
		else if (message.what == R.id.quit)
		{
			Looper.myLooper().quit();
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
	 * reuse the same reader objects from one decode to the next.
	 *
	 * @param data   The YUV preview frame.
	 * @param width  The width of the preview frame.
	 * @param height The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height)
	{
		long start = System.currentTimeMillis();
		Result rawResult = null;
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width; // Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;
		data = rotatedData;
		// 构造基于平面的YUV亮度源，即包含二维码区域的数据源
		PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
		// 构造二值图像比特流，使用HybridBinarizer算法解析数据源
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try
		{
			//解析结果
			rawResult = multiFormatReader.decodeWithState(bitmap);
		}
		catch (ReaderException re)
		{
			// continue
		}
		finally
		{
			multiFormatReader.reset();
		}

		if (rawResult != null)
		{
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
			Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
			message.setData(bundle);
			 Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();

			//扫描成功后跳转到浏览器
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(rawResult.toString());
			intent.setData(content_url);
			activity.startActivity(intent);
		}
		else
		{
			Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
			message.sendToTarget();
		}
	}

}
