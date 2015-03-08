package com.imooc.slidingmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.imooc.slidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView
{
	private static final String TAG = SlidingMenu.class.getSimpleName();
	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private int mScreenWidth;

	private int mMenuWidth;
	// dp
	private int mMenuRightPadding = 50;

	private boolean once;

	private boolean isOpen;
	//SlidingMenu�ķ�񣬵�Ϊ1ʱΪԭʼ����Ϊ2ʱΪ��������ʽ����Ϊ3ʱΪQQ5.0ʽ��
	private int style=13;

	/**
	 * δʹ���Զ�������ʱ������
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	/**
	 * ��ʹ�����Զ�������ʱ������ô˹��췽��
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		// ��ȡ���Ƕ��������
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.SlidingMenu_rightPadding:
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));
				break;
			}
		}
		a.recycle();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

	}

	public SlidingMenu(Context context)
	{
		this(context, null);
	}

	/**
	 * ������View�Ŀ�͸� �����Լ��Ŀ�͸�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (!once)
		{
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(1);
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ͨ������ƫ��������menu����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed)
		{
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_UP:
			// ��������ߵĿ��
			int scrollX = getScrollX();
			if (scrollX >= mMenuWidth / 2)
			{
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			} else
			{
				this.smoothScrollTo(0, 0);
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * �򿪲˵�
	 */
	public void openMenu()
	{
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}

	public void closeMenu()
	{
		if (!isOpen)
			return;
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
	}

	/**
	 * �л��˵�
	 */
	public void toggle()
	{
		if (isOpen)
		{
			closeMenu();
		} else
		{
			openMenu();
		}
	}
	boolean once2;
	long currentTime;
	long oldTime=0;
	/**
	 * ��������ʱ
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		// l����ԭʼ��Ŀ�ȣ�����scroll֮ǰ�ľ��룬��Ϊ��onLayout��scroll��mMenuWidth�����룬����l��ֵ��mMenuWIdth��0֮��
		float scale = l * 1.0f / mMenuWidth; // 1 ~ 0
		Log.i(TAG, "l:"+l+";scale:"+scale);
		currentTime=System.currentTimeMillis();
		Log.i(TAG, "ȡֵʱ������"+(currentTime-oldTime));
		oldTime=currentTime;
		switch (style) {
		case 11:
			// menu�˵�����Content��������Content���ص�����ʱ��menuҲ��ȫ������
			ViewHelper.setTranslationX(mMenu, mMenuWidth-l);
			break;
		case 12:
			ViewHelper.setTranslationX(mMenu, mMenuWidth*(1-scale));
			break;
		case 13:
			if(!once2){
				once2=true;
				ViewHelper.setTranslationX(mMenu, mMenuWidth);
			}
			break;
		case 1:
			//��mMenuһ��������������ָ�ڲ������һ��������ڲ������󻬣�������Ǿ�ֹ������̫˧�ˣ�
			ViewHelper.setTranslationX(mMenu, mMenuWidth*scale);
			break;
		case 2:
			
			break;
		case 3:
			/**
			 * ����1����������1.0~0.7 ���ŵ�Ч�� scale : 1.0~0.0; 
			 * 																			  0.7 + 0.3 * scale
			 * ����2���˵���ƫ������Ҫ�޸�
			 * ����3���˵�����ʾʱ�������Լ�͸���ȱ仯 ���ţ�0.7 ~1.0 						͸���� 0.6 ~ 1.0 
			 * 																							1.0 - scale * 0.3 					 0.6+ 0.4 * (1- scale) ;
			 * 
			 */
			float cotentScale = 0.7f + 0.3f * scale;
			float menuScale = 1.0f - scale * 0.3f;
			float menuAlpha = 0.6f + 0.4f * (1 - scale);

			// �������Զ���������TranslationX
			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.8f);
			
			ViewHelper.setAlpha(mMenu, menuAlpha);
			ViewHelper.setScaleX(mMenu, menuScale);
			ViewHelper.setScaleY(mMenu, menuScale);
			// ����content�����ŵ����ĵ�
			ViewHelper.setPivotX(mContent, 0);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			// �������ŵĳ߶�
			ViewHelper.setScaleX(mContent, cotentScale);
			ViewHelper.setScaleY(mContent, cotentScale);
			break;
		}// End of switch
		

	}


}
