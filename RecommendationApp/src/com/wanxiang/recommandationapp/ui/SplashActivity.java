package com.wanxiang.recommandationapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.persistent.AppPrefs;

public class SplashActivity extends Activity implements OnClickListener
{

    private Button mBtnRegister;
    private Button mBtnLogin;
    
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
        // TODO Auto-generated method stub
        
    }

    private void login()
    {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainFragmentsActivity.class);
        startActivity(intent);
        finish();
    }

}
