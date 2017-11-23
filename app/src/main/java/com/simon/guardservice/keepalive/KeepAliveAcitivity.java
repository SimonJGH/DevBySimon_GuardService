package com.simon.guardservice.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.simon.guardservice.R;

public class KeepAliveAcitivity extends AppCompatActivity {
    private KillReceiver mKillReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_alive_acitivity);

        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 100;
        params.width = 100;
        window.setAttributes(params);

        mKillReceiver = new KillReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FINISH_ALIVE_ACTIVITY");
        registerReceiver(mKillReceiver, intentFilter);
        Log.i("Simon", "KeepAliveAcitivity onCreate");
    }

    class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeepAliveAcitivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Simon", "KeepAliveAcitivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Simon", "KeepAliveAcitivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Simon", "KeepAliveAcitivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mKillReceiver);
        Log.i("Simon", "KeepAliveAcitivity onDestroy");
    }
}
