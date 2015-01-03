package com.wanxiang.recommandationapp.persistent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

public class SharedPreferencesProvider implements SharedPreferences
{
	private static final String SHARED_PREFERENCES_NAME = "my_contacts_backup";
	private static final String SECURE_SHARED_PREFERENCES_NAME = "secureprefs";

	private SharedPreferences sharedPreferences;

	private Context context = null;
	private String mSharedPrefsFileName = SECURE_SHARED_PREFERENCES_NAME;

	public SharedPreferencesProvider()
	{
		/*
		 * Call Hierarchy will report that this constructor is not used.
		 * But if you remove it, this will fail:
		 * ConfigurationInstanceManager.getInstance().get(AppPrefs.class);
		 * 
		 * Since it uses the Class's default PUBLIC NO ARGUMENT constructor
		 */
	}

	protected SharedPreferencesProvider(Context context)
	{
		setContext(context);
	}

	public void setContext(Context context)
	{
		SetSharedPrefsFileName(GetSharedPrefsFileName());
		if (this.context == null && context != null)
		{
			this.context = context;

			SharedPreferences sharedPreferences = this.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			this.sharedPreferences = sharedPreferences;
		}
	}
	
	protected void SetSharedPrefsFileName(String fileName)
	{
		if(null != fileName)mSharedPrefsFileName = fileName;
	}
	
	//Override this method if you want to use your own sharedprefs file
	protected String GetSharedPrefsFileName()
	{
		return SECURE_SHARED_PREFERENCES_NAME;
	}


	public String get(String key, String defaultValue)
	{
		return getString(key, defaultValue);
	}

	public long get(String key, long defaultValue)
	{
		return getLong(key, defaultValue);
	}

	public int get(String key, int defaultValue)
	{
		return getInt(key, defaultValue);
	}

	protected float get(String key, float defaultValue)
	{
		return getFloat(key, defaultValue);
	}

	public boolean get(String key, boolean defaultValue)
	{
		return getBoolean(key, defaultValue);
	}

    public boolean set(String key, String value)
	{
		if (value == null)
		{
			return edit().remove(key).commit();
		}
		return edit().putString(key, value).commit();
	}

	public boolean set(String key, long value)
	{
		return edit().putLong(key, value).commit();
	}

	public boolean set(String key, int value)
	{
		return edit().putInt(key, value).commit();
	}

	public boolean set(String key, boolean value)
	{
		return edit().putBoolean(key, value).commit();
	}

	public boolean set(String key, float value)
	{
		return edit().putFloat(key, value).commit();
	}

    public boolean set(String key, Date value){
        return edit().putLong(key, value.getTime()).commit();
    }

	@Override
	public boolean contains(String key)
	{
		return sharedPreferences.contains(key);
	}

	@Override
	public Editor edit()
	{
		return sharedPreferences.edit();
	}

	@Override
	public Map<String, ?> getAll()
	{
		return sharedPreferences.getAll();
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue)
	{
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * Get a String value and parse to a Boolean
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanString(String key, boolean defaultValue)
	{
		String value = getString(key, Boolean.toString(defaultValue));
		return Boolean.parseBoolean(value);
	}

	@Override
	public float getFloat(String key, float defaultValue)
	{
		return sharedPreferences.getFloat(key, defaultValue);
	}

	/**
	 * Get a String value and parse to an Double
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public double getDoubleString(String key, double defaultValue)
	{
		String value = getString(key, Double.toString(defaultValue));
		return Double.parseDouble(value);
	}

	@Override
	public int getInt(String key, int defaultValue)
	{
		return sharedPreferences.getInt(key, defaultValue);
	}

	/**
	 * Get a String value and parse to an Integer
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIntString(String key, int defaultValue)
	{
		String value = getString(key, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	@Override
	public long getLong(String key, long defaultValue)
	{
		return sharedPreferences.getLong(key, defaultValue);
	}

	/**
	 * Get a String value and parse to a Long
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLongString(String key, long defaultValue)
	{
		String value = getString(key, Long.toString(defaultValue));
		return Long.parseLong(value);
	}

    public Date getDate(String key, Date defaultValue) {
        Long value = getLong(key, defaultValue.getTime());
        return new Date(value);
    }

	@Override
	public String getString(String key, String defaultValue)
	{
		return sharedPreferences.getString(key, defaultValue);
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defaultValues)
	{
		return null;
	}
	
	public Object readObject(Context context,String key) {
		Object obStored = null;
		SharedPreferences preferences = context.getSharedPreferences("base64",
				context.MODE_PRIVATE);
		String productBase64 = preferences.getString(key, "");
		if (productBase64 == "") {
			obStored = null;
			return null;
		}
		
		//读取字节
		byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);
		
		//封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			//再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				//读取对象
				obStored = (Object) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obStored;
	}

	public void saveObject(Context context,Object oject,String key) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				context.MODE_PRIVATE);
		
		//创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			//创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			//将对象写入字节流
			oos.writeObject(oject);

			//将字节流编码成base64的字符窜
			String productBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));

			Editor editor = preferences.edit();
			editor.putString(key, productBase64);

			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("ok", "存储成功");
	}


	@Override
	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener)
	{
		sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener)
	{
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	protected final Context getContext()
	{
		return context;
	}
}