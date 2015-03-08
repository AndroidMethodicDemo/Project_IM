package com.yang.datetimepicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yang.datetimepicker.DateTimePicker2.ICustomDateTimeListener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	private TextView time;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		time = (TextView) findViewById(R.id.time);
	}


	public void click(View v) {
		int key = v.getId();
		switch (key) {
		case R.id.button1:
			createDateTimeDialog();
			break;
		case R.id.button2:
			createDateTimeDialog2();
			break;
		case R.id.button3:
			createDateTimeDialog3();
			break;
		}
		
	}

	private void createTimeDialog() {
		Calendar calendar = Calendar.getInstance();
		Dialog dialog = null;

		TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			}

		};

		dialog = new TimePickerDialog(this, timeListener,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.DAY_OF_MONTH), true);
		dialog.show();
	}

	private void createDateDialog() {
		Calendar calendar = Calendar.getInstance();
		Dialog dialog = null;
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year,
					int monthOfYear, int dayOfMonth) {
				String month = "0" + (monthOfYear + 1);
				String day = "0" + dayOfMonth;
				String seldate = year + "-"
						+ month.substring(month.length() - 2) + "-"
						+ day.substring(day.length() - 2);
				time.setText(seldate);
			}
		};
		dialog = new DatePickerDialog(this, dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	private void createDateTimeDialog2() {
		DateTimePicker2 timePicker = new DateTimePicker2(this,
				new ICustomDateTimeListener() {
					@Override
					public void onSet(Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						time.setText(dateSelected.toLocaleString());
					}

					@Override
					public void onCancel() {
						Log.d("datetimepickerdialog", "canceled");
					}
				});
		timePicker.set24HourFormat(true);
		timePicker.showDialog();
	}

	private void createDateTimeDialog() {
		DateTimePicker timePicker = new DateTimePicker(this,
				new DateTimePicker.ICustomDateTimeListener() {
					@Override
					public void onSet(Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						time.setText(dateSelected.toLocaleString());
					}

					@Override
					public void onCancel() {
						Log.d("datetimepickerdialog", "canceled");
					}
				});
		timePicker.set24HourFormat(true);
		timePicker.showDialog();
	}
	private void createDateTimeDialog3() {
		DateTimePickerDialog timePicker = new DateTimePickerDialog(this,
				new DateTimePickerDialog.ICustomDateTimeListener() {
					@Override
					public void onSet(Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						time.setText(format.format(dateSelected));
					}

					@Override
					public void onCancel() {
						Log.d("datetimepickerdialog", "canceled");
					}
				});
		timePicker.set24HourFormat(true);
		timePicker.showDialog();
	}
}
