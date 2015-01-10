package com.wanxiang.recommandationapp.ui.login;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.friends.UploadContactService;
import com.wanxiang.recommandationapp.service.login.GetPinCodeMessage;
import com.wanxiang.recommandationapp.service.login.GetPinCodeMessage.GetPinCodeResult;
import com.wanxiang.recommandationapp.service.login.LoginMessage;
import com.wanxiang.recommandationapp.service.login.LoginMessage.LoginResult;
import com.wanxiang.recommandationapp.service.login.RegisterMessage;
import com.wanxiang.recommandationapp.service.login.RegisterMessage.RegisterResult;
import com.wanxiang.recommandationapp.ui.JianjianApplication;
import com.wanxiang.recommandationapp.ui.MainFragmentsActivity;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.xiaomi.mipush.sdk.MiPushClient;

public class LoginActivity extends Activity {

	public static final String INTENT_REG_LOGIN = "isLogin";
	private static final String PATTERN_PHONE = "1[3|5|7|8|][0-9]{9}";
	private static final int MIN_LENGTH = 2;
	private EditText mEditPhone;
	private EditText mEditPassword;
	private EditText mEditConfirmPassword;
	private EditText mEditUserName;
	private EditText mEditCode;
	private Button mBtnLogin;
	private Button mBtnGetPincode;
	private boolean mIsLogin;
	private Pattern mPhonePattern = Pattern.compile(PATTERN_PHONE);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_login);
		Intent intent = getIntent();
		mIsLogin = intent.getBooleanExtra(INTENT_REG_LOGIN, true);
		initUI();
	}

	private void initUI() {
		mEditPhone = (EditText) findViewById(R.id.et_mobile_number);
		mEditPassword = (EditText) findViewById(R.id.et_password);
		mEditConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
		mEditUserName = (EditText) findViewById(R.id.et_user_name);
		mEditCode = (EditText) findViewById(R.id.et_code);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInputValid()) {

					if (mIsLogin) {
						sendLoginMessage();
					} else {
						sendRegisterMessage();
					}
				}
			}
		});

		mBtnGetPincode = (Button) findViewById(R.id.btn_get_pincode);
		mBtnGetPincode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mEditPhone.getText().toString())) {
					Toast.makeText(LoginActivity.this, "手机号码不能为空",
							Toast.LENGTH_LONG).show();
				} else {
					sendPincodeMessage();
				}
			}

		});
		if (mIsLogin) {
			mEditConfirmPassword.setVisibility(View.GONE);
			mBtnLogin.setText(getString(R.string.login));
			mEditUserName.setVisibility(View.GONE);
			mEditCode.setVisibility(View.GONE);
			mBtnGetPincode.setVisibility(View.GONE);

		} else {
			mEditConfirmPassword.setVisibility(View.VISIBLE);
			mBtnLogin.setText(getString(R.string.register));
			mEditUserName.setVisibility(View.VISIBLE);
			mEditCode.setVisibility(View.VISIBLE);
			mBtnGetPincode.setVisibility(View.VISIBLE);
		}

	}

	private void sendPincodeMessage() {
		GetPinCodeMessage message = new GetPinCodeMessage(
				HTTP_TYPE.HTTP_TYPE_GET);
		message.setParam(AppConstants.HEADER_PHONE, mEditPhone.getText()
				.toString());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				GetPinCodeResult result = (GetPinCodeResult) msg
						.getResponseData();
				if (result != null) {
					if (result.success) {
						mEditCode.setText(result.code);
					} else {
						Toast.makeText(LoginActivity.this, result.errMsg,
								Toast.LENGTH_LONG).show();

					}
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}
		});
		FusionBus.getInstance(LoginActivity.this).sendMessage(message);

	}

	protected void sendRegisterMessage() {
		RegisterMessage message = new RegisterMessage(HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_PHONE, mEditPhone.getText()
				.toString());
		message.setParam(AppConstants.HEADER_PASSWORD, mEditPassword.getText()
				.toString());
		message.setParam(AppConstants.HEADER_USER_NAME, mEditUserName.getText()
				.toString());
		message.setParam(AppConstants.HEADER_VERIFY_CODE, mEditCode.getText()
				.toString());

		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				RegisterResult result = (RegisterResult) msg.getResponseData();
				if (result != null) {
					if (result.errCode == 0) {
						registerMiPush();
						AppPrefs.getInstance(LoginActivity.this).setSessionId(
								result.token);
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								MainFragmentsActivity.class);
						startActivity(intent);
						Intent uploadContact = new Intent();
						uploadContact.setClass(LoginActivity.this,
								UploadContactService.class);
						startService(uploadContact);
						finish();
					} else {
						Toast.makeText(LoginActivity.this, result.errMsg,
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(LoginActivity.this, result.errMsg,
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}
		});

		FusionBus.getInstance(LoginActivity.this).sendMessage(message);

	}

	private boolean checkInputValid() {
		String phone = mEditPhone.getText().toString();
		if (TextUtils.isEmpty(phone) || !mPhonePattern.matcher(phone).matches()) {
			Toast.makeText(LoginActivity.this, "手机号不合法", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		String password = mEditPassword.getText().toString();
		if (TextUtils.isEmpty(password) || password.length() < MIN_LENGTH) {
			Toast.makeText(LoginActivity.this, "密码长度不够", Toast.LENGTH_LONG)
					.show();
			return false;

		}
		String confirmPassword = mEditConfirmPassword.getText().toString();
		if (!mIsLogin && (!TextUtils.equals(confirmPassword, password))) {
			Toast.makeText(LoginActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return true;
	}

	protected void sendLoginMessage() {
		LoginMessage message = new LoginMessage(HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_PHONE, mEditPhone.getText()
				.toString());
		message.setParam(AppConstants.HEADER_PASSWORD, mEditPassword.getText()
				.toString());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
				LoginResult result = (LoginResult) msg.getResponseData();
				if (result != null) {
					if (result.errCode == 0) {
						registerMiPush();
						AppPrefs.getInstance(LoginActivity.this).setSessionId(
								result.token);
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								MainFragmentsActivity.class);
						startActivity(intent);
						Intent uploadContact = new Intent();
						uploadContact.setClass(LoginActivity.this,
								UploadContactService.class);
						startService(uploadContact);
						finish();
					} else {
						Toast.makeText(LoginActivity.this, result.errMsg,
								Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}
		});

		FusionBus.getInstance(LoginActivity.this).sendMessage(message);

	}

	private void registerMiPush() {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
		if (shouldInit()) {
			MiPushClient.registerPush(this, JianjianApplication.APP_ID, JianjianApplication.APP_KEY);
		}

	}

	private boolean shouldInit() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}
}
