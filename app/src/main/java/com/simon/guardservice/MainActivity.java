package com.simon.guardservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static MyHandler myHandler = new MyHandler();
    private static int count = 0;
    private static TextView mTv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 启动本地服务和远程服务
        startService(new Intent(this, LocalGuardService.class));
        startService(new Intent(this, RemoteGuardService.class));
        /*GuardJobProcess1和2是两种不同写法 需要android6.0版本机子测试*/


        mTv_show = (TextView) findViewById(R.id.tv_show);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("run");
    }

    public static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // String message = (String) msg.obj;
                    Log.i("Simon", "run " + count++);
                    mTv_show.setText("Process is running : " + count);
                    myHandler.sendEmptyMessageDelayed(1, 10000);
                    break;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return String.valueOf(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Message message = new Message();
            message.what = 1;
            message.obj = s;
            myHandler.sendMessageDelayed(message, 10000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(null);
    }
}
