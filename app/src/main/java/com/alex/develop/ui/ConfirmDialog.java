package com.alex.develop.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alex.develop.stockanalyzer.R;

/**
 * 信息确认Dialog，包含标题Title（非普通Dialog的Title）、确认信息
 * Content(使用TextView显示)以及一个确认（好）和一个取消（不好）按钮
 * @author Created by alex 
 */
public class ConfirmDialog {
	
	/**
	 * 监听用户是否点击Dialog中的两个按钮
	 */
	public interface OnConfirmListener {
		
		/**
		 * 用户点击确认（好）时执行的操作
		 * @param dialog 包含按钮的Dialog
		 * @param which 被点击的按钮
		 */
		public void positive(DialogInterface dialog, int which);
		

		/**
		 * 用户点击取消（不好）时执行的操作
		 * @param dialog 包含按钮的Dialog
		 * @param which 被点击的按钮
		 */
		public void negative(DialogInterface dialog, int which);
	}
	
	/**
	 * 使用AlertDialog构造一个ConfirmDialog
	 * @param context 上下文
	 */
	@SuppressLint("InflateParams")
	public ConfirmDialog(Context context) {
		
		if(view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmListener.positive(dialog, which);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmListener.negative(dialog, which);
			}
		});
		builder.setCancelable(false);
		builder.setView(view);
		
		dialog = builder.create();
	}	

	/**
	 * 设置ConfirmDialog显示的标题
	 * @param title 标题内容
	 */
	public void setTitle(String title) {
		((TextView) view.findViewById(R.id.confirmTitle)).setText(title);
	}
	
	/**
	 * 设置ConfirmDialog显示的标题
	 * @param resid 标题的索引
	 */
	public void setTitle(int resid) {
		((TextView) view.findViewById(R.id.confirmTitle)).setText(resid);
	}
	
	/**
	 * 设置ConfirmDialog显示的确认信息内容
	 * @param content 确认信息
	 */
	public void setContent(String content) {
		((TextView) view.findViewById(R.id.confirmContent)).setText(content);
	}
	
	/**
	 * 设置ConfirmDialog显示的确认信息内容
	 * @param resid 确认信息索引
	 */
	public void setContent(int resid) {
		((TextView) view.findViewById(R.id.confirmContent)).setText(resid);
	}
	
	/**
	 * 设置ConfirmDialog确认按钮的文字
	 * @param text 要显示的文字信息
	 */
	public void setPositiveBtnText(String text) {
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(text);
	}
	
	/**
	 * 设置ConfirmDialog确认(好)按钮的文字
	 * @param resid 要显示的文字信息索引
	 */
	public void setPositiveBtnText(int resid) {
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(resid);
	}
	
	/**
	 * 设置ConfirmDialog取消(不好)按钮的文字
	 * @param text 要显示的文字信息
	 */
	public void setNegativeBtnText(String text) {
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(text);
	}
	
	/**
	 * 设置ConfirmDialog取消(不好)按钮的文字
	 * @param resid 要显示的文字信息索引
	 */
	public void setNegativeBtnText(int resid) {
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(resid);
	}
	
	/**
	 * 设置确认监听器
	 * @param confirmListener 监听器
	 */
	public void setOnConfirmListener(OnConfirmListener confirmListener) {
		this.confirmListener = confirmListener;
	}
	
	/**
	 * ConfirmDialog是否可以取消（点击非Dialog区域，Dialog会自动消失）
	 * @param cancelable true,可以；false,不可以
	 */
	public void setCancelable(boolean cancelable) {
		dialog.setCancelable(cancelable);
	}
	
	/**
	 * 显示ConfirmDialog
	 */
	public void show() {
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}
	
	/**
	 * 销毁（不再显示）ConfirmDialog
	 */
	public void dismiss() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	/**
	 * ConfirmDialog的布局
	 */
	private View view;
	
	/**
	 * ConfrimDialog实际上是一个AlertDialog
	 */
	private AlertDialog dialog;
	
	/**
	 * 确认监听器
	 */
	private OnConfirmListener confirmListener;
}
