package com.mapabc.android.activity.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 下个路口显示控件
 */
public class NextRoadView  extends TextView {
	public NextRoadView(Context con) {
		  super(con);
		}

		public NextRoadView(Context context, AttributeSet attrs) {
		  super(context, attrs);
		}
		public NextRoadView(Context context, AttributeSet attrs, int defStyle) {
		  super(context, attrs, defStyle);
		}
		@Override
		public boolean isFocused() {
		return true;
		}
		@Override
		protected void onFocusChanged(boolean focused, int direction,
		   Rect previouslyFocusedRect) {  
		}
		}

