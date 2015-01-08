package com.wanxiang.recommandationapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.jianjianapp.R;


public class NotificationHelper
{
	private NotificationManager notificationManager;

	public void setSingleTopNotification(Context context, Intent intent, String title, String message, int notificationId )
	{
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder build = new NotificationCompat.Builder(context);
		build.setContentTitle(title);
		build.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		build.setSmallIcon(R.drawable.ic_launcher);

		build.setContentIntent(PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
		build.setDefaults(Notification.DEFAULT_SOUND);
		build.setAutoCancel(false);
		notificationManager.notify("MT", 1, build.build());
	}
}
