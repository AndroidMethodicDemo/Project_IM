package com.zihao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zihao.ui.DragLayout;
import com.zihao.ui.DragLayout.DragListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.Activity;

public class MainActivity extends Activity {

	/** 左边侧滑菜单 */
	private DragLayout mDragLayout;
	private ListView menuListView;// 菜单列表
	private ImageButton menuSettingBtn;// 菜单呼出按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/**
		 * 如果需要在别的Activity界面中也实现侧滑菜单效果，需要在布局中引入DragLayout（同本Activity方式），
		 * 然后在onCreate中声明使用; Activity界面部分，需要包裹在MyRelativeLayout中.
		 */
		mDragLayout = (DragLayout) findViewById(R.id.dl);
		mDragLayout.setDragListener(new DragListener() {// 动作监听
					@Override
					public void onOpen() {
					}

					@Override
					public void onClose() {
					}

					@Override
					public void onDrag(float percent) {

					}
				});

		// 生成测试菜单选项
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 5; i++) {
			Map<String, Object> item;
			item = new HashMap<String, Object>();
			item.put("item", "选项" + (i + 1));
			data.add(item);
		}
		menuListView = (ListView) findViewById(R.id.menu_listview);
		menuListView.setAdapter(new SimpleAdapter(this, data,
				R.layout.menulist_item_text, new String[] { "item" },
				new int[] { R.id.menu_text }));

		// 添加监听，可点击呼出菜单
		menuSettingBtn = (ImageButton) findViewById(R.id.menu_imgbtn);
		menuSettingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDragLayout.open();
			}
		});
	}
}