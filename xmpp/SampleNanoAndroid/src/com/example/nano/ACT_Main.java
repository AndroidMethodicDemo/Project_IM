package com.example.nano;

import com.example.nano.common.MyWebView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ACT_Main extends Activity {

	private MyWebView webView;
	private Intent service;
	Handler myHandler=new Handler(){
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		service = new Intent(this,SER_Web.class);
		startService(service);
		webView=(MyWebView) findViewById(R.id.webView);
		myHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				webView.startFileServer("");
			}
		}, 1000);
	}
	
	@Override
	public void onBackPressed() {
		stopService(service);
		super.onBackPressed();
	}
}
