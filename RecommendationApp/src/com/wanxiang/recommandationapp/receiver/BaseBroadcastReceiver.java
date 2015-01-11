package com.wanxiang.recommandationapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.wanxiang.recommandationapp.ui.JianjianApplication;
import com.wanxiang.recommandationapp.util.NetworkUtils;

public class BaseBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            JianjianApplication.mNetWorkState = NetworkUtils.getNetworkState(context);
        }

    }

}
