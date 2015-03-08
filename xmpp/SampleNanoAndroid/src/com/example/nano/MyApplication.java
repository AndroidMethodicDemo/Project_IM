package com.example.nano;

import java.util.HashMap;

import com.example.nano.common.AndroidUtils;

import android.app.Application;
import android.content.Intent;
import android.view.WindowManager;

public class MyApplication extends Application {

	public static float mWidth;
	public static float mHeight;
	public static float mDensity;
	public static float mDensityDpi;
	private static MyApplication instance = null;
	
	@Override
	public void onCreate() {
//		myStrictMode();
		super.onCreate();
		WindowManager wm = (WindowManager)getSystemService(Application.WINDOW_SERVICE); 
		HashMap<String, Object> map =AndroidUtils.getAndroidScreenInfos(this, wm);
		mWidth = (Float)map.get("width");
		mHeight =(Float)map.get("height");	
		mDensity =(Float)map.get("density");
		mDensityDpi =(Float)map.get("densityDpi");
		
	}
}
