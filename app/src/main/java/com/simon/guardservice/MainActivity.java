package com.simon.guardservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.simon.guardservice.doubleprocess.LocalGuardService;
import com.simon.guardservice.doubleprocess.RemoteGuardService;
import com.simon.guardservice.jobschedule.GuardJobProcess;

import java.text.SimpleDateFormat;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity {

    private MyHandler mHandler = new MyHandler();
    private TextView mTv_show;
    private NotificationManager manager;
    private Notification.Builder builder;
    private Notification notification;
    private RemoteViews remoteView;
    private SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy年MM月dd日 HH时:mm分:ss秒");
    private EditText mEt_process_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动本地服务和远程服务
        startService(new Intent(this, LocalGuardService.class));
        startService(new Intent(this, RemoteGuardService.class));
        startJobScheduler();
        //http://blog.csdn.net/u013263323/article/details/56285475
        ActivityCollector.addActivity(this, getClass());

        mTv_show = (TextView) findViewById(R.id.tv_show);
        mEt_process_id = (EditText) findViewById(R.id.et_process_id);


        mHandler.sendEmptyMessageDelayed(0, 4000);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    public void clickButton(View view) {
        String processId = mEt_process_id.getText().toString();
        if (!TextUtils.isEmpty(processId)) {
           ProcessUtils.killCurrentProcess(Integer.parseInt(processId));
        }
    }

    public void startJobScheduler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), GuardJobProcess.class.getName()));
            builder.setPeriodic(2000);
            builder.setPersisted(true);//设备重启之后你的任务是否还要继续执行
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);//任何网络状态
            builder.setRequiresCharging(false); // 未充电状态
            builder.setRequiresDeviceIdle(false);
            jobScheduler.schedule(builder.build());
        }
    }

    public class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTv_show.setText(sdf.format(System.currentTimeMillis()));
                    remoteView.setTextViewText(R.id.tv_notification_num, sdf.format(System.currentTimeMillis()));
                    manager.notify(1990, notification);
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    break;
                case 1:
                    createNotification();
                    break;
            }
        }
    }

    // 创建通知
    private void createNotification() {
        // 创建通知管理者
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建builder实例
        builder = new Notification.Builder(MainActivity.this);

        // 设置小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("");
//        builder.setContentText("");
        // 点击后不会自动消失
        builder.setAutoCancel(false);
        // 不会滑动删除通知
        builder.setOngoing(false);
        // 设置优先级
        builder.setPriority(Notification.PRIORITY_MAX);
        // 通知提示语
        builder.setTicker("notification is build!");
        // 提示音 震动 闪光 都为系统默认
        // builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring));
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        // pendingIntent跳转
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        notification = builder.build();
        // 发起正在运行事件（活动中）
        // notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 三色灯提醒
        // notification.flags = Notification.FLAG_SHOW_LIGHTS;
        // 让声音、振动无限循环，直到用户响应 （取消或者打开）
        // notification.flags = Notification.FLAG_INSISTENT;
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        // 只有全部清除时，Notification才会清除
        // notification.flags = Notification.FLAG_NO_CLEAR;

        // 自定义通知样式
        remoteView = new RemoteViews(getPackageName(), R.layout.layout_guard_notification);
        remoteView.setTextViewText(R.id.tv_notification_num, sdf.format(System.currentTimeMillis()));
        notification.contentView = remoteView;

        manager.notify(1990, notification);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Simon", "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Simon", "MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Simon", "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Simon", "MainActivity onDestroy");
        mHandler.removeCallbacks(null);
        ActivityCollector.removeAllActivity();
    }
}
