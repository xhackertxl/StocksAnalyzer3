package com.alex.develop.util;

import java.util.ArrayList;

import android.app.Activity;

/**
 * 收集程序所创建的Activity，用于彻底退出App
 * @author Created by Alex 2015/03/27
 */
public class ApplicationHelper {
	
	public static void add(Activity activity) {
		if(null == activitys) {
			activitys = new ArrayList<Activity>();
		}
		
		activitys.add(activity);
	}
	
	public static void remove(Activity activity) {
		
		if(null == activitys && 0 == activitys.size()) {
			return ;
		}
		
		activitys.remove(activity);
	}
	
	public static void exitApplication() {
		
		if(null == activitys && 0 == activitys.size()) {
			return ;
		}
		
		for(Activity a : activitys) {
			a.finish();
		}
	}
	
	private static ArrayList<Activity> activitys;

	public static ArrayList<Activity> getActivitys() {
		return activitys;
	}

	public static void setActivitys(ArrayList<Activity> activitys) {
		ApplicationHelper.activitys = activitys;
	}
}
