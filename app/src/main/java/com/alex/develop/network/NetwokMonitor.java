package com.alex.develop.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by alex on 15-5-30.
 * 监听网络状态的变化
 */
public class NetwokMonitor extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
