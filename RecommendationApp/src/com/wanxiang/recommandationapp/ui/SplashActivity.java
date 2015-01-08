package com.wanxiang.recommandationapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.ui.login.LoginActivity;

public class SplashActivity extends Activity implements OnClickListener
{

    private Button mBtnRegister;
    private Button mBtnLogin;
    private Handler mHandler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_splash);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        AppPrefs.getInstance(this).setIMEI(tm.getDeviceId());
        mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				String token = AppPrefs.getInstance(SplashActivity.this).getSessionId();
				if (!TextUtils.isEmpty(token)) {
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, MainFragmentsActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, 100);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
        case R.id.btn_register:
            register();
            break;
        case R.id.btn_login:
            login();
            break;
        default:
            break;
        }
    }

    private void register()
    {
    	Intent intent = new Intent();
        intent.setClass(SplashActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.INTENT_REG_LOGIN, false);
        startActivity(intent);
        finish();        
    }

    private void login()
    {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
