package com.simon.guardservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


/**
 * Created by Simon on 2017/9/14.
 */
public class LocalGuardService extends Service {

    MyBinder mBinder;
    MyServiceConnection mServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        mServiceConnection = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, RemoteGuardService.class), mServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("Simon", "远程服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // RemoteGuardService被杀掉，重启RemoteGuardlService
            LocalGuardService.this.startService(new Intent(LocalGuardService.this, RemoteGuardService.class));
            LocalGuardService.this.bindService(new Intent(LocalGuardService.this, RemoteGuardService.class),
                    mServiceConnection, Context.BIND_IMPORTANT);
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

}