package com.example.nano;

import com.example.nano.common.Constant;
import com.example.nano.web.SimpleWebServer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SER_Web extends Service{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String[] args={"-d",Constant.SYSTEM_BASE_DIR+Constant.STORY_LOCAL+"98f1a504937d474d965f97e3bf4f9d87"};
		SimpleWebServer.main(args);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		SimpleWebServer.stopServer();
		super.onDestroy();
	}

}
