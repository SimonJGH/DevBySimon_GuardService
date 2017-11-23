package com.simon.guardservice.doubleprocess;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.simon.guardservice.SimonConnection;
import com.simon.guardservice.keepalive.KeepAliveAcitivity;

/**
 * Created by Simon on 2017/9/14.
 */
public class RemoteGuardService extends Service {
    MyBinder mBinder;
    MyServiceConnection mServiceConnection;
    private KeepAliveReceiver mKeepAliveReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        mServiceConnection = new MyServiceConnection();

        mKeepAliveReceiver = new KeepAliveReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(mKeepAliveReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, LocalGuardService.class), mServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("Simon", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // LocalGuardService被杀死，重启LocalGuardService
            RemoteGuardService.this.startService(new Intent(RemoteGuardService.this, LocalGuardService.class));
            RemoteGuardService.this.bindService(new Intent(RemoteGuardService.this, LocalGuardService.class), mServiceConnection, Context.BIND_IMPORTANT);
        }

    }

    /**
     * 监听屏幕状态的广播
     */
    public class KeepAliveReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // 屏幕关闭启动1像素Activity
                Intent aliveIntent = new Intent(context, KeepAliveAcitivity.class);
                aliveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(aliveIntent);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // 屏幕打开 结束1像素
                Log.i("Simon", "MainActivity ACTION_SCREEN_ON");
                Intent killIntent = new Intent();
                killIntent.setAction("FINISH_ALIVE_ACTIVITY");
                sendBroadcast(killIntent);
                /*Intent main = new Intent(Intent.ACTION_MAIN);
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                main.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(main);*/
            }
        }
    }

    class MyBinder extends SimonConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "restart guard service";
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mKeepAliveReceiver);
    }
}