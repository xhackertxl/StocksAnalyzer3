package com.alex.develop.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * 提供窗口的一些常用功能
 * @author alex
 * @version 0.0.0
 * @fixed|bug
 */
public class WindowHelper {
	
	public static void initialize(Activity act) {
		WindowHelper.act = act;
		view = act.getWindow().getDecorView();
	}
	
	/**
	 * 设置状态栏和导航条的状态
	 * @param uiOptions View 类的一些静态常量
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setStatusAndNavigationBar(int uiOptions) {
		view.setSystemUiVisibility(uiOptions);
	}
	
	/**
	 * 使状态栏和导航条模糊（并非隐藏）
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void dimStatusAndNavigationBar() {
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		view.setSystemUiVisibility(uiOptions);
	}
	
	/**
	 * 全屏模式（包含导航条）
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void fullScreenWithNavigationBar() {
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		view.setSystemUiVisibility(uiOptions);
	}
	
	/**
	 * 全屏模式（不包括导航条）
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void fullScreenWithoutNavigationBar() {
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		view.setSystemUiVisibility(uiOptions);
	}
	
	/**
	 * 显示状态栏和导航条
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void revealStatusAndNabigationBar() {
		view.setSystemUiVisibility(0);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void nonStickyImmersion() {
		int uiOption = 
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE;
		
		view.setSystemUiVisibility(uiOption);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void stickyImmersion() {
		int uiOption = 
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
		view.setSystemUiVisibility(uiOption);
	}
		
	public static int WIDTH;
	public static int HEIGHT;
	private static Activity act;
	private static View view;
}