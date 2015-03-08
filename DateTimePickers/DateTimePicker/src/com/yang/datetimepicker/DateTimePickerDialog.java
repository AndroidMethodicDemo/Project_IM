package com.yang.datetimepicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerDialog implements OnClickListener {
	private DatePicker datePicker;
	private TimePicker timePicker;


	private Button btn_set, btn_cancel;

	private Calendar calendar_date = null;

	private Activity activity;

	private ICustomDateTimeListener iCustomDateTimeListener = null;

	private Dialog dialog;

	private boolean is24HourView = true;

	public DateTimePickerDialog(Activity a,
			ICustomDateTimeListener customDateTimeListener) {
		activity = a;
		iCustomDateTimeListener = customDateTimeListener;

		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View dialogView = getDateTimePickerLayout();
		dialog.setContentView(dialogView);
	}

	public View getDateTimePickerLayout() {

		View view = View.inflate(activity, R.layout.datetime_picker_dialog, null);
		
		datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		timePicker = (TimePicker) view.findViewById(R.id.timePicker);

		btn_set = (Button)view.findViewById(R.id.btn_set);
		btn_set.setOnClickListener(this);

		btn_cancel =(Button)view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);


		return view;
	}

	public void showDialog() {
		if (!dialog.isShowing()) {
			if (calendar_date == null)
				calendar_date = Calendar.getInstance();

			timePicker.setIs24HourView(is24HourView);
			timePicker.setCurrentHour(calendar_date.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar_date.get(Calendar.MINUTE));

			datePicker.updateDate(calendar_date.get(Calendar.YEAR),
					calendar_date.get(Calendar.MONTH),
					calendar_date.get(Calendar.DATE));

			dialog.show();

		}
	}

	public void dismissDialog() {
		if (!dialog.isShowing())
			dialog.dismiss();
	}

	public void setDate(Calendar calendar) {
		if (calendar != null)
			calendar_date = calendar;
	}

	public void setDate(Date date) {
		if (date != null) {
			calendar_date = Calendar.getInstance();
			calendar_date.setTime(date);
		}
	}

	public void setDate(int year, int month, int day) {
		if (month < 12 && month >= 0 && day < 32 && day >= 0 && year > 100
				&& year < 3000) {
			calendar_date = Calendar.getInstance();
			calendar_date.set(year, month, day);
		}

	}

	public void setTimeIn24HourFormat(int hourIn24Format, int minute) {
		if (hourIn24Format < 24 && hourIn24Format >= 0 && minute >= 0
				&& minute < 60) {
			if (calendar_date == null)
				calendar_date = Calendar.getInstance();

			calendar_date.set(calendar_date.get(Calendar.YEAR),
					calendar_date.get(Calendar.MONTH),
					calendar_date.get(Calendar.DAY_OF_MONTH), hourIn24Format,
					minute);

			is24HourView = true;
		}
	}

	public void setTimeIn12HourFormat(int hourIn12Format, int minute,
			boolean isAM) {
		if (hourIn12Format < 13 && hourIn12Format > 0 && minute >= 0
				&& minute < 60) {
			if (hourIn12Format == 12)
				hourIn12Format = 0;

			int hourIn24Format = hourIn12Format;

			if (!isAM)
				hourIn24Format += 12;

			if (calendar_date == null)
				calendar_date = Calendar.getInstance();

			calendar_date.set(calendar_date.get(Calendar.YEAR),
					calendar_date.get(Calendar.MONTH),
					calendar_date.get(Calendar.DAY_OF_MONTH), hourIn24Format,
					minute);

			is24HourView = false;
		}
	}

	public void set24HourFormat(boolean is24HourFormat) {
		is24HourView = is24HourFormat;
	}

	public interface ICustomDateTimeListener {
		public void onSet(Calendar calendarSelected, Date dateSelected,
				int year, String monthFullName, String monthShortName,
				int monthNumber, int date, String weekDayFullName,
				String weekDayShortName, int hour24, int hour12, int min,
				int sec, String AM_PM);

		public void onCancel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_set:
			if (dialog.isShowing())
				dialog.dismiss();
			if (iCustomDateTimeListener != null) {
				int month = datePicker.getMonth();
				int year = datePicker.getYear();
				int day = datePicker.getDayOfMonth();

				calendar_date.set(year, month, day);

				int hourOfDay = timePicker.getCurrentHour().intValue();
				int minute = timePicker.getCurrentMinute().intValue();

				calendar_date.set(year, month, day, hourOfDay, minute);

				iCustomDateTimeListener.onSet(calendar_date, calendar_date
						.getTime(), calendar_date.get(Calendar.YEAR),
						getMonthFullName(calendar_date.get(Calendar.MONTH)),
						getMonthShortName(calendar_date.get(Calendar.MONTH)),
						calendar_date.get(Calendar.MONTH), calendar_date
								.get(Calendar.DAY_OF_MONTH),
						getWeekDayFullName(calendar_date
								.get(Calendar.DAY_OF_WEEK)),
						getWeekDayShortName(calendar_date
								.get(Calendar.DAY_OF_WEEK)), calendar_date
								.get(Calendar.HOUR_OF_DAY),
						getHourIn12Format(calendar_date
								.get(Calendar.HOUR_OF_DAY)), calendar_date
								.get(Calendar.MINUTE), calendar_date
								.get(Calendar.SECOND), getAMPM(calendar_date));
			}
			resetData();
			break;

		case R.id.btn_cancel:
			if (dialog.isShowing())
				dialog.dismiss();
			if (iCustomDateTimeListener != null)
				iCustomDateTimeListener.onCancel();
			resetData();
			break;
		}
	}

	/**
	 * @param date
	 *            date in String
	 * @param fromFormat
	 *            format of your <b>date</b> eg: if your date is 2011-07-07
	 *            09:09:09 then your format will be <b>yyyy-MM-dd hh:mm:ss</b>
	 * @param toFormat
	 *            format to which you want to convert your <b>date</b> eg: if
	 *            required format is 31 July 2011 then the toFormat should be
	 *            <b>d MMMM yyyy</b>
	 * @return formatted date
	 */
	public static String convertDate(String date, String fromFormat,
			String toFormat) {
		String formattedDate = "";
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromFormat);
			Date d = simpleDateFormat.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);

			simpleDateFormat = new SimpleDateFormat(toFormat);
			simpleDateFormat.setCalendar(calendar);
			formattedDate = simpleDateFormat.format(calendar.getTime());

		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}

		return formattedDate;
	}

	/**
	 * @param monthNumber
	 *            Month Number starts with 0. For <b>January</b> it is <b>0</b>
	 *            and for <b>December</b> it is <b>11</b>.
	 * @return
	 */
	private String getMonthFullName(int monthNumber) {
		String monthName = "";

		if (monthNumber >= 0 && monthNumber < 12)
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, monthNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
				simpleDateFormat.setCalendar(calendar);
				monthName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}

		return monthName;
	}

	/**
	 * @param monthNumber
	 *            Month Number starts with 0. For <b>January</b> it is <b>0</b>
	 *            and for <b>December</b> it is <b>11</b>.
	 * @return
	 */
	private String getMonthShortName(int monthNumber) {
		String monthName = "";

		if (monthNumber >= 0 && monthNumber < 12)
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, monthNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
				simpleDateFormat.setCalendar(calendar);
				monthName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		return monthName;
	}

	/**
	 * @param weekDayNumber
	 *            Week Number starts with 1. For <b>Sunday</b> it is <b>1</b>
	 *            and for <b>Saturday</b> it is <b>7</b>.
	 * @return
	 */
	private String getWeekDayFullName(int weekDayNumber) {
		String weekName = "";

		if (weekDayNumber > 0 && weekDayNumber < 8) {
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_WEEK, weekDayNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
				simpleDateFormat.setCalendar(calendar);
				weekName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		}
		return weekName;
	}

	/**
	 * @param weekDayNumber
	 *            Week Number starts with 1. For <b>Sunday</b> it is <b>1</b>
	 *            and for <b>Saturday</b> it is <b>7</b>.
	 * @return
	 */
	private String getWeekDayShortName(int weekDayNumber) {
		String weekName = "";
		if (weekDayNumber > 0 && weekDayNumber < 8) {
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_WEEK, weekDayNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
				simpleDateFormat.setCalendar(calendar);
				weekName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		}
		return weekName;
	}

	private int getHourIn12Format(int hour24) {
		int hourIn12Format = 0;

		if (hour24 == 0)
			hourIn12Format = 12;
		else if (hour24 <= 12)
			hourIn12Format = hour24;
		else
			hourIn12Format = hour24 - 12;

		return hourIn12Format;
	}

	private String getAMPM(Calendar calendar) {
		String ampm = (calendar.get(Calendar.AM_PM) == (Calendar.AM)) ? "AM"
				: "PM";
		return ampm;
	}

	private void resetData() {
		calendar_date = null;
		is24HourView = true;
	}

	public static String pad(int i) {
		return i <= 9 ? "0" + i : "" + i;
	}

	public static String getSecondsFromMillis(long milliseconds) {
		return "" + ((int) ((milliseconds / 1000) % 60));
	}

	public static String getMinutesFromMillis(long milliseconds) {
		return "" + (int) ((milliseconds / (1000 * 60)) % 60);
	}

	public static String getHoursFromMillis(long milliseconds) {
		return "" + (int) ((milliseconds / (1000 * 60 * 60)) % 24);
	}

	/**
	 * @param monthNumber
	 *            Month Number starts with 0. For <b>January</b> it is <b>0</b>
	 *            and for <b>December</b> it is <b>11</b>.
	 * @param year
	 * @return
	 */
	public static int getDaysInMonth(int monthNumber, int year) {
		int days = 0;
		if (monthNumber >= 0 && monthNumber < 12) {
			try {
				Calendar calendar = Calendar.getInstance();
				int date = 1;
				calendar.set(year, monthNumber, date);
				days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		}
		return days;
	}

	/**
	 * @param monthNumber
	 *            Month Number starts with 0. For <b>January</b> it is <b>0</b>
	 *            and for <b>December</b> it is <b>11</b>.
	 * @return
	 */
	public static int getDaysInMonthInPresentYear(int monthNumber) {
		int days = 0;
		if (monthNumber >= 0 && monthNumber < 12) {
			try {
				Calendar calendar = Calendar.getInstance();
				int date = 1;
				int year = calendar.get(Calendar.YEAR);
				calendar.set(year, monthNumber, date);
				days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		}
		return days;
	}

	public static int getDaysDifference(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null)
			return 0;

		return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static int getDaysDifference(Calendar calendar1, Calendar calendar2) {
		if (calendar1 == null || calendar2 == null)
			return 0;

		return (int) ((calendar2.getTimeInMillis() - calendar1
				.getTimeInMillis()) / (1000 * 60 * 60 * 24));
	}

}