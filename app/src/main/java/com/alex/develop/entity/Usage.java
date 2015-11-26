package com.alex.develop.entity;

/**
 * 采用单例模式实现，用来记录和统计App的信息
 * @author Created by alex 2014/11/12
 */
public class Usage extends BaseObject {
	
	public static Usage getInstance() {
		if(instance == null) {
			instance = new Usage();
		}
		
		return instance;
	}
	
	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}
	
	public long getInstallStamp() {
		return installStamp;
	}

	public void setInstallStamp(long installStamp) {
		this.installStamp = installStamp;
	}

	private Usage() {
		useCount = 0;
		installStamp = -1;
	}

	private int useCount;// 使用次数
	private long installStamp;// 安装时间
	
	private static Usage instance;// 唯一的本类对象
}
