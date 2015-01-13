package com.wanxiang.recommandationapp.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;

import com.wanxiang.recommandationapp.ui.JianjianApplication;
import com.wanxiang.recommandationapp.util.NetworkUtils;

public class CacheManager {

	public static final int CONFIG_CACHE_MOBILE_TIMEOUT = 3600000; // 1 hour
	public static final int CONFIG_CACHE_WIFI_TIMEOUT = 300000; // 5 minute

	public static Object loadCache(Context context, String key) {
		Object ret = null;
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		File cacheDir = context.getCacheDir();
		File cacheFile = new File(cacheDir.getPath() + File.separator + key);
		if (cacheFile.exists()) {
			long expiredTime = System.currentTimeMillis()
					- cacheFile.lastModified();
			Log.d(JianjianApplication.TAG, cacheFile.getAbsolutePath()
					+ " expiredTime:" + expiredTime / 60000 + "min");
			// 1. in case the system time is incorrect (the time is turn back
			// long ago)
			// 2. when the network is invalid, you can only read the cache
			if (JianjianApplication.mNetWorkState != NetworkUtils.NETWORN_NONE
					&& expiredTime < 0) {
				return null;
			}
			if (JianjianApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI
					&& expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
				return null;
			} else if (JianjianApplication.mNetWorkState == NetworkUtils.NETWORN_MOBILE
					&& expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
				return null;
			}
			try {
				fileInputStream = new FileInputStream(cacheFile.toString());
				objectInputStream = new ObjectInputStream(fileInputStream);
				return objectInputStream.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {

				try {
					if (objectInputStream != null) {
						objectInputStream.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return ret;
	}

	public static void saveCache(Context context, String key, Object dest) {
		File cacheDir = context.getCacheDir();
		File cacheFile = new File(cacheDir.getPath() + File.separator + key);
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}

			fileOutputStream = new FileOutputStream(cacheFile.toString(), false);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(dest);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public static void clearCache(Context context, String key) {
		File cacheDir = context.getCacheDir();
		File cacheFile = new File(cacheDir.getPath() + File.separator + key);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}
}
