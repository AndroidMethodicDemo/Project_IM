package com.itheima29.spinnerdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {

    private List<String> numberList;
	private ListView lv;
	private EditText etNumber;
	private PopupWindow pw;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etNumber = (EditText) findViewById(R.id.et_number);
        
        initListView();
    }

    private void initListView() {
    	lv = new ListView(this);
    	lv.setBackgroundResource(R.drawable.listview_background);
    	// 取出ListView的分割线
    	lv.setDivider(null);
    	lv.setDividerHeight(0);
    	lv.setOnItemClickListener(this);
    	lv.setSelector(android.R.color.transparent);
    	
    	numberList = new ArrayList<String>();
    	for (int i = 0; i < 30; i++) {
			numberList.add("1889900" + i);
		}
    	
    	lv.setAdapter(new MyAdapter());
	}
    
    class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return numberList.size();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			NumberViewHolder mHolder = null;
			
			if(convertView == null) {
				convertView = View.inflate(MainActivity.this, R.layout.number_item, null);
				
				mHolder = new NumberViewHolder();
				mHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
				mHolder.ibDelete = (ImageButton) convertView.findViewById(R.id.ib_delete);
				
				convertView.setTag(mHolder);
			} else {
				mHolder = (NumberViewHolder) convertView.getTag();
			}
			
			mHolder.tvNumber.setText(numberList.get(position));
			mHolder.ibDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("删除: " + position);
					
					numberList.remove(position);
					notifyDataSetChanged();
					
					if(numberList.size() == 0) {
						pw.dismiss();
					}
					
				}
			});
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
    }
    
    class NumberViewHolder {
    	public TextView tvNumber;
    	public ImageButton ibDelete;
    }

	/**
     * 弹出号码对话框
     * @param v
     */
    public void showDialog(View v) {
    	pw = new PopupWindow(lv, etNumber.getWidth() - 5, 300);
    	// 设置点击窗体外部可以关闭对话框
    	pw.setOutsideTouchable(true);
    	pw.setBackgroundDrawable(new BitmapDrawable());
    	
    	pw.setFocusable(true);		// 设置窗体可以获取焦点
    	pw.showAsDropDown(etNumber, 3, -4);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("onItemClick: " + position);
		
		String number = numberList.get(position);
		etNumber.setText(number);
		pw.dismiss();
	}
    
}
