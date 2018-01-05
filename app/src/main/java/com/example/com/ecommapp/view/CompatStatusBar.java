package com.example.com.ecommapp.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("deprecation")
public class CompatStatusBar extends FrameLayout {

	public CompatStatusBar(Context context) {
        super(context);
		setup();
	}

	public CompatStatusBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public CompatStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setup();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CompatStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setup();
	}

	private void setup() {
		adaptPlatform(getContext());
	}

	private void adaptPlatform(Context context) {
		if (context != null) {
			if (context instanceof Activity) {
				adaptPlatform(context, ((Activity) context).getWindow());
			} else if (context instanceof ContextWrapper) {
				adaptPlatform(((ContextWrapper) context).getBaseContext());
			}
		}
	}

	private void adaptPlatform(Context themeContext, Window window) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final int[] attrs = { android.R.attr.windowTranslucentStatus, android.support.v7.appcompat.R.attr.isLightTheme };
			TypedArray ta = themeContext.getTheme().obtainStyledAttributes(attrs);
			if (ta.getBoolean(Arrays.binarySearch(attrs, android.R.attr.windowTranslucentStatus), false)) {

				final int statusBarHeightResId = themeContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
				setMinimumHeight(themeContext.getResources().getDimensionPixelSize(statusBarHeightResId));

				boolean lightStatusBar = ta.getBoolean(Arrays.binarySearch(attrs, android.support.v7.appcompat.R.attr.isLightTheme), true);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					final int[] mattrs = { android.R.attr.windowLightStatusBar };
					TypedArray mta = themeContext.getTheme().obtainStyledAttributes(mattrs);
					lightStatusBar = mta.getBoolean(Arrays.binarySearch(mattrs, android.R.attr.windowLightStatusBar), true);
					mta.recycle();
				}

				if (new MIUIStatusBar().adaptPlatform(window, true, lightStatusBar)) {
					// do nothing
				} else if (new FlymeStatusBar().adaptPlatform(window, true, lightStatusBar)) {
					// do nothing
				} else if (new AndroidMStatusBar().adaptPlatform(window, true, lightStatusBar)) {
					// do nothing
				} else if (new AndroidLollipopStatusBar().adaptPlatform(window, true, lightStatusBar)) {
					// do nothing
					if (lightStatusBar) {
						ColorDrawable foreground = new ColorDrawable(Color.parseColor("#33000000"));
						setForeground(foreground);
					}
				} else {
					// do nothing
					if (lightStatusBar) {
						ColorDrawable foreground = new ColorDrawable(Color.parseColor("#33000000"));
						setForeground(foreground);
					}
				}
			}
			ta.recycle();
		}
	}

	static abstract class StatusBar {
		public abstract boolean adaptPlatform(Window window, boolean statusBarAvailable, boolean lightStatusBar);
	}

	private static abstract class DrawsSystemStatusBar extends StatusBar {

	}

	private static class AndroidLollipopStatusBar extends DrawsSystemStatusBar {

		@Override
		public boolean adaptPlatform(Window window, boolean statusBarAvailable, boolean lightStatusBar) {
			if (statusBarAvailable && window != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

				}
				return true;
			}
			return false;
		}
	}

	private static class AndroidMStatusBar extends DrawsSystemStatusBar {

		@Override
		public boolean adaptPlatform(Window window, boolean statusBarAvailable, boolean lightStatusBar) {
			if (statusBarAvailable && window != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					View decor = window.getDecorView();
					int ui = decor.getSystemUiVisibility();
					if (lightStatusBar) {
						ui |=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
					} else {
						ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
					}
					decor.setSystemUiVisibility(ui);

					// 去掉系统状态栏下的 windowContentOverlay
					View v = window.findViewById(android.R.id.content);
					if (v != null) {
						v.setForeground(null);
					}
				}
				return true;
			}
			return false;
		}
	}

	private static class MIUIStatusBar extends StatusBar {

		@Override
		public boolean adaptPlatform(Window window, boolean statusBarAvailable, boolean lightStatusBar) {
			if (statusBarAvailable && window != null) {
				Class clazz = window.getClass();
				try {
					Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
					Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
					int darkModeFlag = field.getInt(layoutParams);
					Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
					if (lightStatusBar) {
						extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
					} else {
						extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
					}
					// Android 6.0
					new AndroidMStatusBar().adaptPlatform(window, statusBarAvailable, lightStatusBar);
					return true;
				} catch (Exception ignored) {
//					ignored.printStackTrace();
				}
			}
			return false;
		}
	}

	private static class FlymeStatusBar extends StatusBar {

		@Override
		public boolean adaptPlatform(Window window, boolean statusBarAvailable, boolean lightStatusBar) {
			if (statusBarAvailable && window != null) {
				try {
					WindowManager.LayoutParams lp = window.getAttributes();
					Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
					Field flymeFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
					darkFlag.setAccessible(true);
					flymeFlags.setAccessible(true);
					int bit = darkFlag.getInt(null);
					int value = flymeFlags.getInt(lp);
					if (lightStatusBar) {
						value |= bit;
					} else {
						value &= ~bit;
					}
					flymeFlags.setInt(lp, value);
					window.setAttributes(lp);
					return true;
				} catch (Exception ignored) {
//					ignored.printStackTrace();
				}
			}
			return false;
		}
	}
}