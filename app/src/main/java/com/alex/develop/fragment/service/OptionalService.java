package com.alex.develop.fragment.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.alex.develop.easymoney.helper.TheMainCost;
import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.util.NetworkHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OptionalService extends Service {
    /**
     * 用于Handler里的消息类型
     */
    public static final int MSG_SAY_HELLO = 1;
    /**
     * 这个Messenger可以关联到Service里的Handler，Activity用这个对象发送Message给Service，Service通过Handler进行处理。
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private MediaPlayer mMediaPlayer = null;
    private Vibrator vibrator;

    // 状态栏提示要用的
    private NotificationManager m_Manager;
    private PendingIntent m_PendingIntent;
    private Notification m_Notification;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            // 要释放资源，不然会打开很多个MediaPlayer
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        super.onDestroy();
    }

    /**
     * 一启动就响铃，震动提醒
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        final List<Stock> stockList = Analyzer.getStockList();
        ArrayList<Stock[]> arrayList =Analyzer.getArraystockList();

        if (null != stockList) {
            new Thread(new TheMainCostRunnable(stockList, 0, stockList.size())).start();
            new Thread(new LoadDataRunnable(arrayList)).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class TheMainCostRunnable implements Runnable {
        List<Stock> stockList;
        int start;
        int end;
        public TheMainCostRunnable(List<Stock> stockList, int start, int end) {
            this.stockList = stockList;
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            try {
                int i = start;
                for (i = start; i < end; i++) {
                    Stock stock = stockList.get(i);
                    int x = stock.getToday().getChangeString().indexOf("停");
                    System.out.println(stock.getToday().getChangeString());
                    if (stock.getMain_cost_one() <= 0) {
                        TheMainCost.fetchDataFromWeb(stock.getCode(), stock);
                        try {
                            //发送Action为com.example.communication.RECEIVER的广播
                            Intent intent = new Intent(Analyzer.STOCK_UPDATE).putExtra(Analyzer.STOCK_UPDATE, 1);
                            sendBroadcast(intent);
                            Thread.sleep(1000);
                            System.out.println("start ------- A " + "Server");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (i == end - 1) {
                        i = 0;
                        Context mContext = getApplicationContext();
                        {
                            // 等待3秒，震动3秒，从第0个索引开始，一直循环
                            //0一直循环  -1 不循环
                            vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
                            vibrator.vibrate(new long[]{100, 100, 100, 1000}, -1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LoadDataRunnable implements Runnable {
        ArrayList<Stock[]> stockList;

        public LoadDataRunnable(ArrayList<Stock[]> stockList) {
            this.stockList = stockList;
        }

        @Override
        public void run() {
            int i = 0;
            while (true) {
                try {
                    //通过SimpleDateFormat获取24小时制时间
                    //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
                    //通过SimpleDateFormat获取24小时制时间
                    SimpleDateFormat sdf = new SimpleDateFormat("HH.mm", Locale.getDefault());
                    String time = sdf.format(new Date()).toString();
                    Intent intent = new Intent(Analyzer.STOCK_UPDATE).putExtra(Analyzer.STOCK_UPDATE, 1);
                    sendBroadcast(intent);
//                    if (Double.parseDouble(time) > 15.30)
//                    {
//                        return;
//                    }
                    for(Stock[] stocks : stockList)
                    {
                        NetworkHelper.LoadData(stocks);
                        Thread.sleep(100);
                        System.out.println("start ------- " + stocks.length);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 在Service处理Activity传过来消息的Handler
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    public void PlaySound(final Context context) {

//            {
//                vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);
//                // 等待3秒，震动3秒，从第0个索引开始，一直循环
//                //0一直循环  -1 不循环
//                vibrator.vibrate(new long[]{3000, 3000}, -1);
//            }

//            {
//                AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                PlaySound(mContext);
//            }

        Log.e("ee", "正在响铃");
        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // 如果为空，才构造，不为空，说明之前有构造过
        if (mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, uri);
            mMediaPlayer.setLooping(true); //循环播放
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public OptionalService getService() {
            return OptionalService.this;
        }
    }

    /**
     * 当Activity绑定Service的时候，通过这个方法返回一个IBinder，Activity用这个IBinder创建出的Messenger，就可以与Service的Handler进行通信了
     */
    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        // mMessenger.getBinder();
        return new LocalBinder();
    }


    public class LocalBinder extends Binder {
        OptionalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return OptionalService.this;
        }
    }
}
