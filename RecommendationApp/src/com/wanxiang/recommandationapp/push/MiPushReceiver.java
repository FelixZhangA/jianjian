package com.wanxiang.recommandationapp.push;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wanxiang.recommandationapp.controller.FusionBus;
import com.wanxiang.recommandationapp.controller.FusionCallBack;
import com.wanxiang.recommandationapp.controller.FusionMessage;
import com.wanxiang.recommandationapp.http.impl.NetTaskMessage.HTTP_TYPE;
import com.wanxiang.recommandationapp.persistent.AppPrefs;
import com.wanxiang.recommandationapp.service.push.SendRegisterMessage;
import com.wanxiang.recommandationapp.ui.JianjianApplication;
import com.wanxiang.recommandationapp.ui.MainFragmentsActivity;
import com.wanxiang.recommandationapp.util.AppConstants;
import com.wanxiang.recommandationapp.util.Utils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class MiPushReceiver extends PushMessageReceiver {

	private Context mContext;

	@Override
	public void onCommandResult(Context context, MiPushCommandMessage message) {
		Log.d(JianjianApplication.TAG,
				"onCommandResult is called. " + message.toString());
		mContext = context;
		String command = message.getCommand();
		List<String> arguments = message.getCommandArguments();
		String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments
				.get(0) : null);
		String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments
				.get(1) : null);
		String log = "";
		if (MiPushClient.COMMAND_REGISTER.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				Log.d(JianjianApplication.TAG,
						"register success " + message.getResultCode());
				AppPrefs.getInstance(mContext).setRegistrationId(cmdArg1);
				sendNotifyRegisterMessage(cmdArg1);
			} else {
				Log.d(JianjianApplication.TAG,
						"register fail " + message.getResultCode());
			}
		}
	}

	@Override
	public void onReceiveMessage(Context context, MiPushMessage message) {
		Log.d(JianjianApplication.TAG,
				"onReceiveMessage is called. " + message.toString());

//		Message msg = Message.obtain();
//		if (message.isNotified()) {
//			msg.obj = message;
//		}
		Message msg = new Message();
		msg.obj = message;

		JianjianApplication.getHandler().sendMessage(msg);
	}

	private void sendNotifyRegisterMessage(String regId) {
		SendRegisterMessage message = new SendRegisterMessage(
				HTTP_TYPE.HTTP_TYPE_POST);
		message.setParam(AppConstants.HEADER_RID, regId);
		message.setParam(AppConstants.HEADER_PLATFORM, String.valueOf(1));
		message.setParam(AppConstants.HEADER_TOKEN,
				AppPrefs.getInstance(mContext).getSessionId());
		message.setParam(AppConstants.HEADER_IMEI,
				AppPrefs.getInstance(mContext).getIMEI());
		message.setFusionCallBack(new FusionCallBack() {

			@Override
			public void onFinish(FusionMessage msg) {
				super.onFinish(msg);
			}

			@Override
			public void onFailed(FusionMessage msg) {
				super.onFailed(msg);
			}
		});

		FusionBus.getInstance(mContext).sendMessage(message);

	}

	public static class MiPushHandler extends Handler {

		private Context context;

		public MiPushHandler(Context context) {
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			MiPushMessage message = (MiPushMessage) msg.obj;
			if (message != null) {
				String title = message.getTitle();
				String content = message.getDescription();
				Intent intent = new Intent(context, MainFragmentsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				Utils.showNotification(context, title, content, pendingIntent);
			}

		}
	}

}
