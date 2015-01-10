package com.wanxiang.recommandationapp.ui;

import android.app.Application;
import android.util.Log;

import com.wanxiang.recommandationapp.push.MiPushReceiver.MiPushHandler;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;

public class JianjianApplication extends Application {
	  // user your appid the key.
    public static final String APP_ID = "2882303761517294091";
    // user your appid the key.
    public static final String APP_KEY = "5581729442091";

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "com.jianjianapp";
	private static MiPushHandler handler;
    @Override
    public void onCreate() {
        super.onCreate();

        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
//        if (shouldInit()) {
//            MiPushClient.registerPush(this, APP_ID, APP_KEY);
//        }

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        if (handler == null)
            handler = new MiPushHandler(getApplicationContext());
    }
    
   
    
    public static MiPushHandler getHandler() {
        return handler;
    }

}
