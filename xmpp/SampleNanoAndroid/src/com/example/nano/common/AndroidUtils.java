package com.example.nano.common;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.example.nano.MyApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class AndroidUtils {

	public final static int CWJ_HEAP_SIZE = 32 * 1024 * 1024;
	public final static float TARGET_HEAP_UTILIZATION = 0.75f;

	private AndroidUtils() {
	}

	/**
	 * 屏幕方向是否为纵�?
	 */
	public static boolean isOrientationPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	/**
	 * 屏幕方向是否为横�?
	 */
	public static boolean isOrientationLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static int getAndroidSdkVersionCode() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 获取屏幕的真实高，宽，状态栏的高，DPI四个数据
	 * android系统�?.1之前通过DisplayMetrics对象获取的宽，高值为屏幕真实大小�?.2
	 * 后改变为屏幕显示区域大小，且3.2，和4.x获取屏幕真实大小值得方式也发生改�?�?���?��根据sdk版本来进行区�?
	 * 
	 * @author: mmf
	 * @date: 2012-8-17 上午9:49:32
	 * @params:
	 * @return: HashMap<String,Integer>
	 */
	public static HashMap<String, Object> getAndroidScreenInfos(
			Context mContext, WindowManager wm) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		float width = 0;
		float height = 0;
		int statusBarHeight = 0;
		float densityDpi = 0;
		float density=0;
		// WindowManager wm=((MainActivity)mContext).getWindowManager();
		// View view=((MainActivity)mContext).getWindow().getDecorView();
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		Rect rect = new Rect();
		Display display = wm.getDefaultDisplay();
		int sdkVersion = getAndroidSdkVersionCode();
		// 获取屏幕的真实宽，高，Dpi
		if (sdkVersion <= 10) {
			densityDpi = metrics.densityDpi;
			density =metrics.density;
			int w = wm.getDefaultDisplay().getWidth();
			width = metrics.widthPixels;
			height = metrics.heightPixels;
		} else if (10 < sdkVersion && sdkVersion < 13) {
			densityDpi = metrics.densityDpi;
			density =metrics.density;
			width = metrics.widthPixels;
			height = metrics.heightPixels;
		} else if (sdkVersion >= 13 && sdkVersion < 14) {
			Class<?> c1;
			Method method;
			try {
				c1 = Class.forName("android.view.Display");
				method = c1.getMethod("getRealHeight");
				height = (Integer) method.invoke(display);
				method = c1.getMethod("getRealWidth");
				width = (Integer) method.invoke(display);
				densityDpi = metrics.densityDpi;
				density =metrics.density;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (sdkVersion >= 14) {
			Class<?> c;
			try {
				c = Class.forName("android.view.Display");
				Method method = c.getMethod("getRealMetrics",
						DisplayMetrics.class);
				method.invoke(display, metrics);
				height = metrics.heightPixels;
				width = metrics.widthPixels;
				densityDpi = metrics.densityDpi;
				density =metrics.density;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 获取状�?栏的高度
		Class<?> c2 = null;
		Object obj = null;
		Field field = null;
		try {
			c2 = Class.forName("com.android.internal.R$dimen");
			obj = c2.newInstance();
			field = c2.getField("status_bar_height");
			statusBarHeight = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = ((MyApplication) mContext).getResources()
					.getDimensionPixelSize(statusBarHeight);
		} catch (Exception e1) {
			Log.e("", "get status bar height fail");
		}

		map.put("width", width);
		map.put("height", height);
		map.put("statusBarHeight", statusBarHeight);
		map.put("densityDpi", densityDpi);
		map.put("density", density);
		return map;
	}

	/**
	 * 获取设备的唯�?D
	 * @author: mmf
	 * @date: 2012-9-10 下午6:36:56
	 * @params:
	 * @return: String
	 */
	public static String getDeviceId(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId =deviceUuid.toString();
		return uniqueId;
	}

	/**
	 * �?��视图的背景内�?
	 * @param view
	 * 
	 */
	public static void releaseResourceOfViewBG(View v) {
		if (v == null) {
			return;
		}
		Drawable drawable = v.getBackground();
		if (null == drawable) {
			return;
		}
		try {
			v.setBackgroundDrawable(null);
			drawable.setCallback(null);
			BitmapDrawable bd = (BitmapDrawable) drawable;
			releaseResourceOfBitmapDrawable(bd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			drawable = null;
		}
	}

	/**
	 * �?��imageview的资源图�?
	 * @param view
	 */
	public static void releaseResourceOfImageView(ImageView iv) {
		if (iv == null) {
			return;
		}
		Drawable drawable = iv.getDrawable();
		if (null == drawable) {
			return;
		}
		try {
			drawable.setCallback(null);
			iv.setImageDrawable(null);
			BitmapDrawable bd = (BitmapDrawable) drawable;
			releaseResourceOfBitmapDrawable(bd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			drawable = null;
		}
	}

	/**
	 * 释放bitmapDrawable资源
	 * @param bd
	 */
	public static void releaseResourceOfBitmapDrawable(BitmapDrawable bd) {
		if (bd != null && !bd.getBitmap().isRecycled()) {
			bd.setCallback(null);
			bd.getBitmap().recycle();
			bd = null;
			Log.i("BimapDrawable", "release BitmapDrawable resource");
		}
	}

	/**
	 * �?��drawable的资�?
	 * 
	 * @param drawble
	 */
	public static void destoryDrawable(Drawable drawble) {
		try {
			if (drawble != null) {
				/* 消除drawable的引�?*/
				drawble.setCallback(null);
				Bitmap bp = ((BitmapDrawable) drawble).getBitmap();
				if (!bp.isRecycled())
					bp.recycle();
				drawble = null;
				bp = null;
			}
		} catch (Exception e) {
			Log.e("destoryDrawable", "�?��drawable出现异常�?");
		}
	}

	/**
	 * 
	 * @Title: getAvailMemory
	 * @Description: 系统当前内存情况
	 * @param @param context
	 * @param @return
	 * @return String
	 * @throws
	 */
	private String getAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内�?

		return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格�?
	}

	private String used(Context context) {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return Formatter.formatFileSize(context, total - free);
	}

	/**
	 * 获取当前设备总共的容量，为设备激活提供参�?
	 * @author: mmf
	 * @date: 2012-9-25 上午11:03:14
	 * @params:
	 * @return: long
	 */
	public static long getMemoryCapacity(Context context) {
		long capacity = 0;
		File pathFile = android.os.Environment.getExternalStorageDirectory();
		StatFs statfs = new android.os.StatFs(pathFile.getPath());
		// 获取SDCard上每个block的SIZE
		long nBlocSize = statfs.getBlockSize();
		// 获取SDCard上BLOCK总数
		long nTotalBlocks = statfs.getBlockCount();
		// // 获取可供程序使用的Block的数�?
		// long nAvailaBlock = statfs.getAvailableBlocks();
		capacity = (nTotalBlocks * nBlocSize);
		return capacity;
	}

	/**
	 * 读取并缩放图�?
	 * @param content
	 * @param imagePath
	 *            图片地址
	 * @param width
	 *            �?
	 * @param height
	 *            �?
	 * @return Bitmap
	 */
	public static Bitmap readBitMap(Context content, String imagePath,
			int width, int height) {
		BitmapFactory.Options options = null;
		if (width > 0 && height > 0) {
			options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			options.inPurgeable = true;
			options.inInputShareable = true;
			float scaleW = options.outWidth / width;
			float scaleH = options.outHeight / height;
			if (scaleW > 1 && scaleH > 1) {
				if (scaleW > scaleH) {
					options.inSampleSize = (int) scaleW;
				} else {
					options.inSampleSize = (int) scaleH;
				}
			} else {
				options.inSampleSize = 2;
			}
			options.inJustDecodeBounds = false;
		}
		try {
			return BitmapFactory.decodeFile(imagePath, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	private final static int kSystemRootStateUnknow = -1;
	private final static int kSystemRootStateDisable = 0;
	private final static int kSystemRootStateEnable = 1;
	private static int systemRootState = kSystemRootStateUnknow;
	private static float mRate;

	/**
	 * 获得是否root
	 * 
	 * @author ronghao
	 * @data 2012-12-4 下午5:20:13
	 * @return
	 */
	public static boolean isRootSystem() {
		if (systemRootState == kSystemRootStateEnable) {
			return true;
		} else if (systemRootState == kSystemRootStateDisable) {

			return false;
		}
		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
				"/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					systemRootState = kSystemRootStateEnable;
					return true;
				}
			}
		} catch (Exception e) {
		}
		systemRootState = kSystemRootStateDisable;
		return false;
	}

	/**
	 * @Title: transform
	 * @Description: 根据像素密度计算�?
	 * @param @param num
	 * @param @return
	 * @return int
	 * @throws
	 * @author mfma
	 * @date 2014-3-25
	 */
	public static int transform(int num) {
		if (mRate == 0) {
			mRate = MyApplication.mDensity;
		}
		return (int) (num * mRate);
	}

	public static String getVersionCode(Context context) {
		// 获取packagemanager的实�?
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名�?代表是获取版本信�?
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		String version = packInfo.versionName;
		return version;
	}

	public static int compareVersionCode(String current,String otherCode) {
		String[] currents= current.split("\\.");
		String[] otherCodes= otherCode.split("\\.");
		if (currents.length != 3 ||otherCodes.length != 3) {
			return 0;
		}
		for (int i = 0; i < 3; i++) {
			int tmp1 = Integer.valueOf(currents[i]);
			int tmp2 = Integer.valueOf(otherCodes[i]);
			if (tmp1 > tmp2) {
				return 1;
			} else if (tmp1 < tmp2) {
				return -1;
			} else {
				if (i == 2) {
					return 0;
				}
			}
		}
		return 0;
	}

	/**
	 * 安装APK
	 * @param context
	 * @param path
	 */
	public static void installApk(Context context, String path) {
		File apkfile = new File(path);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
		killProcess(context);
	}

	/**
	 * @Title: killProcess
	 * @Description: �?��当前程序进程
	 * @param
	 * @return void
	 * @throws
	 * @author bobo
	 * @date 2014-1-21
	 */
	private static void killProcess(Context context) {
		ActivityManager activityMan = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> process = activityMan
				.getRunningAppProcesses();

		int len = process.size();
		for (int i = 0; i < len; i++) {
			if (process.get(i).processName.equals(context.getPackageName())) {
				android.os.Process.killProcess(process.get(i).pid);
			}
		}
	}

	/**
	 * @Title: isInstalled
	* @Description: 判断应用是否已经安装
	* @param @param packageName
	* @param @param context
	* @param @return
	* @return boolean
	* @throws
	* @author mfma
	* @date 2014�?�?�?
	 */
	public static boolean isInstalled(String packageName,Context context){
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo ==null){
			return false;
		}else{
			return true;
		}
	}
}
