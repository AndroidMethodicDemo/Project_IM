/**     
 * @Title: MyWebView.java   
 * @Package com.isoftstone.view.webview   
 * @Description: TODO
 * @author    
 * @date 2014Âπ?Êú?Êó?‰∏äÂçà11:23:33   
 * @version    
 */   
package com.example.nano.common;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.nano.MyApplication;

/**@TypeName:MyWebView
 * @Description: TODO
 * @author mfma
 * @date 2014Âπ?Êú?Êó?
 */
public class MyWebView extends WebView{

	private MyWebChomeClient mChomeClient;

	private MyWebViewClient mViewClient;

	
	/**
	 * @param context
	 */
	public MyWebView(Context context) {
		super(context);
		initWebView();
	}


	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWebView();
	}

	private void initWebView(){
		this.setMinimumWidth((int)(MyApplication.mWidth));
		this.setMinimumHeight((int)(MyApplication.mHeight));
		WebSettings ws = this.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setAllowFileAccess(true);
		ws.setAllowContentAccess(true);
		ws.setEnableSmoothTransition(true);
		ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		ws.setDomStorageEnabled(true);
		ws.setDatabaseEnabled(true);
		ws.setBuiltInZoomControls(false);
		String ua = ws.getUserAgentString() + " " + "xnative/1.0";
		ws.setUserAgentString(ua);
		mChomeClient = new MyWebChomeClient();
		mViewClient = new MyWebViewClient();
		this.setWebChromeClient(mChomeClient);	
		this.setWebViewClient(mViewClient);
	}


	private class MyWebViewClient extends WebViewClient{
	}

	private class MyWebChomeClient extends WebChromeClient{
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			if (consoleMessage.message().contains("Uncaught ReferenceError")) {
			}
			return super.onConsoleMessage(consoleMessage);
		}
	}

	/**
	 * @Title: startFileServer
	 * @Description: ÂêØÂä®Êñá‰ª∂ÊúçÂä°Âô?
	 * @param 
	 * @return void
	 * @throws
	 * @author mfma
	 * @date 2014Âπ?Êú?Êó?
	 */
	public void startFileServer(String fileName){
		loadUrl("http://127.0.0.1:8181");
	}
	
	/**
	 * @Title: releaseResource
	 * @Description: ÈáäÊîæËµÑÊ∫ê
	 * @param 
	 * @return void
	 * @throws
	 * @author mfma
	 * @date 2014Âπ?Êú?Êó?
	 */
	public void releaseResource(){

	    this.stopLoading();
	    this.clearFormData();
	    this.clearAnimation();
	    this.clearDisappearingChildren();
	    this.clearHistory();
	    this.destroyDrawingCache();
	    this.freeMemory();
	    this.clearHistory();
	    this.removeAllViews();
	}

}
