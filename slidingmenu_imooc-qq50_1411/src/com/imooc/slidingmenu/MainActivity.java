package com.imooc.slidingmenu;

import com.imooc.slidingmenu.view.SlidingMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity
{

	private SlidingMenu mLeftMenu ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
	}
	
	public void toggleMenu(View view)
	{
		mLeftMenu.toggle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
