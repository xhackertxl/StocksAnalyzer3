package com.alex.develop.ui;

import com.alex.develop.stockanalyzer.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * 程序加载数据的Dialog，背景透明，只包含一个居于
 * 屏幕中央的环形进度条，不可以被取消
 * @author Created by alex
 */
public class LoadingDialog extends Dialog {

	/**
	 * 构造指定主题的LoadingDialog
	 * @param context 上下文
	 * @param theme 在style.xml中定义的主题索引
	 */
	public LoadingDialog(Context context, int theme) {
		super(context, getTheme(theme));
	}

	/**
	 * 构造默认主题的LoadingDialog(背景透明)
	 * @param context 上下文
	 */
	public LoadingDialog(Context context) {
		super(context, getTheme(-1));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_dialog);
		setCancelable(false);
	}
	
	/**
	 * 用于设置默认主题
	 * @param theme 主题索引
	 * @return 主题索引
	 */
	private static int getTheme(int theme) {
		int result = R.style.LoadingDialogDefalut;
		if(theme > 0) {
			result = theme;
		}
		return result;
	}
}
