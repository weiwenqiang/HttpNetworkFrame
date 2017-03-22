package com.wwq.httpnetworkframe.view.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 魏文强 on 2017/2/18.
 */

public class BaseActivity extends AppCompatActivity {
    public static final List<BaseActivity> mActivityList = new LinkedList<BaseActivity>();

    private KillAllReceiver receiver;


    private class KillAllReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化xUtils3
        x.view().inject(this);

        ((BaseApplication) getApplication()).addActivity(this);

        receiver = new KillAllReceiver();
        IntentFilter filter = new IntentFilter("com.wwq.activity.killall");
        registerReceiver(receiver, filter);

        synchronized (mActivityList) {//为了线程安全加同步锁
            mActivityList.add(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((BaseApplication) getApplication()).removeActivity(this);

        synchronized (mActivityList) {//为了线程安全加同步锁
            mActivityList.remove(this);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    protected void initView() {

    }

    protected void initData() {

    }

    //管理运行的所有activity
    public void killAll() {
        List<BaseActivity> copy;
        synchronized (mActivityList) {//为了线程安全加同步锁
            copy = new LinkedList<BaseActivity>(mActivityList);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
        //杀死当前进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
