package com.alex.develop.fragment.service;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Created by Administrator on 2015-11-19.
 */
public class OptionalServiceConnection<T> implements ServiceConnection  {

    /**
     * 向Service发送Message的Messenger对象
     */
    Messenger mService = null;
    IBinder ibinder = null;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("info", "Service Connection Success");
        //成功连接服务，该方法被执行。在该方法中可以通过IBinder对象取得onBind方法的返回值，一般通过向下转型
        // 通过参数service来创建Messenger对象，这个对象可以向Service发送Message，与Service进行通信
        //ibinder = service;


        //mService =  new Messenger(service);
    }



    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("info", "Service Connection Filed");
        //连接失败执行
    }
    public IBinder getIbinder() {
        return ibinder;
    }
    public void setIbinder(IBinder ibinder) {
        this.ibinder = ibinder;
    }
    public Messenger getmService() {
        return mService;
    }
    public void setmService(Messenger mService) {
        this.mService = mService;
    }
}
