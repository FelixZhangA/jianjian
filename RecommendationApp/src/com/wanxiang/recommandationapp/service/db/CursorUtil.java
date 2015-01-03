package com.wanxiang.recommandationapp.service.db;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class CursorUtil
{
	public static Cursor createCopy(Cursor c)
	{
		MatrixCursor mc = new MatrixCursor(c.getColumnNames());
		int cols = c.getColumnCount();
		
		if (c.moveToFirst())
		{
			do
			{
				Object[] row = new Object[cols];
				for (int col = 0; col < cols; col++)
				{
					row[col] = c.getString(col);
				}
				mc.addRow(row);
			}
			while (c.moveToNext());
		}
		return mc;
	}

	public static void safeClose(Cursor cursor)
	{
		if (cursor != null && !cursor.isClosed())
		{
			try
			{
				cursor.close();
			}
			catch (Exception e)
			{
				Log.e("WANXIANG", "Error closing " + cursor.getClass().getSimpleName(), e);
			}
		}
	}
}
