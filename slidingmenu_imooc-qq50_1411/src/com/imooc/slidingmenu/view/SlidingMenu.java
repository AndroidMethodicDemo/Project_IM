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
	//SlidingMenu的风格，当为1时为原始，当为2时为网易新闻式，当为3时为QQ5.0式。
	private int style=13;

	/**
	 * 未使用自定义属性时，调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	/**
	 * 当使用了自定义属性时，会调用此构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		// 获取我们定义的属性
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
	 * 设置子View的宽和高 设置自己的宽和高
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
	 * 通过设置偏移量，将menu隐藏
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
			// 隐藏在左边的宽度
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
	 * 打开菜单
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
	 * 切换菜单
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
	 * 滚动发生时
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		// l，离原始点的宽度，距离scroll之前的距离，因为在onLayout中scroll了mMenuWidth个距离，所以l的值在mMenuWIdth与0之间
		float scale = l * 1.0f / mMenuWidth; // 1 ~ 0
		Log.i(TAG, "l:"+l+";scale:"+scale);
		currentTime=System.currentTimeMillis();
		Log.i(TAG, "取值时间间隔："+(currentTime-oldTime));
		oldTime=currentTime;
		switch (style) {
		case 11:
			// menu菜单跟随Content滑动，到Content隐藏到极限时，menu也完全隐藏了
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
			//给mMenu一个反作用力，手指在不断向右滑，动画在不断向左滑，结果就是静止不动，太帅了！
			ViewHelper.setTranslationX(mMenu, mMenuWidth*scale);
			break;
		case 2:
			
			break;
		case 3:
			/**
			 * 区别1：内容区域1.0~0.7 缩放的效果 scale : 1.0~0.0; 
			 * 																			  0.7 + 0.3 * scale
			 * 区别2：菜单的偏移量需要修改
			 * 区别3：菜单的显示时有缩放以及透明度变化 缩放：0.7 ~1.0 						透明度 0.6 ~ 1.0 
			 * 																							1.0 - scale * 0.3 					 0.6+ 0.4 * (1- scale) ;
			 * 
			 */
			float cotentScale = 0.7f + 0.3f * scale;
			float menuScale = 1.0f - scale * 0.3f;
			float menuAlpha = 0.6f + 0.4f * (1 - scale);

			// 调用属性动画，设置TranslationX
			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.8f);
			
			ViewHelper.setAlpha(mMenu, menuAlpha);
			ViewHelper.setScaleX(mMenu, menuScale);
			ViewHelper.setScaleY(mMenu, menuScale);
			// 设置content的缩放的中心点
			ViewHelper.setPivotX(mContent, 0);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			// 设置缩放的尺度
			ViewHelper.setScaleX(mContent, cotentScale);
			ViewHelper.setScaleY(mContent, cotentScale);
			break;
		}// End of switch
		

	}


}
