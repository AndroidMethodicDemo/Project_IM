package com.androidsnippets.wordpress;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.androidsnippets.wordpress.DateTimePicker.ICustomDateTimeListener;

public class MainActivity extends Activity implements ICustomDateTimeListener 
{
	private TextView textView;
	private DateTimePicker dateTimePicker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = (TextView) findViewById(R.id.textview);
         
        dateTimePicker = new DateTimePicker(MainActivity.this, this);
        dateTimePicker.set24HourFormat(true);
        dateTimePicker.showDialog();
    }
    
	@Override
	public void onSet(Calendar calendarSelected, Date dateSelected, int year,
			String monthFullName, String monthShortName, int monthNumber,
			int date, String weekDayFullName, String weekDayShortName,
			int hour24, int hour12, int min, int sec, String AM_PM) 
	 {
		textView.setText(dateSelected.toLocaleString());
	 }

	@Override
	public void onCancel() 
	{
		Log.d("datetimepickerdialog", "canceled");
	}
}
