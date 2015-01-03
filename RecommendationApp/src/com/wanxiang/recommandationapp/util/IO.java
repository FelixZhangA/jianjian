package com.wanxiang.recommandationapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

import android.database.Cursor;

public class IO
{
//	private static Logger logger = LoggerFactory.getLogger(IO.class);

	public static final void safeClose(Socket socket)
	{
		if (socket != null && !socket.isClosed())
		{
			try
			{
				socket.close();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + socket.getClass().getSimpleName(), e);
			}
		}
	}

	public static final void safeClose(ServerSocket socket)
	{
		if (socket != null && !socket.isClosed())
		{
			try
			{
				socket.close();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + socket.getClass().getSimpleName(), e);
			}
		}
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
//				logger.debug("Error closing " + cursor.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeClose(Reader reader)
	{
		if (reader != null)
		{
			try
			{
				reader.close();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + reader.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeClose(HttpEntity httpEntity)
	{
		if (httpEntity != null)
		{
			try
			{
				httpEntity.consumeContent();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + httpEntity.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeClose(HttpClient httpClient)
	{
		if (httpClient != null)
		{
			try
			{
				httpClient.getConnectionManager().shutdown();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + httpClient.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeClose(InputStream inputStream)
	{
		if (inputStream != null)
		{
			try
			{
				inputStream.close();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + inputStream.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeClose(OutputStream outputStream)
	{
		if (outputStream != null)
		{
			try
			{
				outputStream.close();
			}
			catch (Exception e)
			{
//				logger.debug("Error closing " + outputStream.getClass().getSimpleName(), e);
			}
		}
	}

	public static void safeFlush(OutputStream outputStream)
	{
		if (outputStream != null)
		{
			try
			{
				outputStream.flush();
			}
			catch (Exception e)
			{
//				logger.debug("Error flushing " + outputStream.getClass().getSimpleName(), e);
			}
		}
	}

	public static byte[] getStreamAsBytes(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		pipeStreams(inputStream, outputStream);
		return outputStream.toByteArray();
	}

	public static void pipeStreams(InputStream inputStream, OutputStream outputStream, long length) throws IOException
	{
		byte[] buffer = new byte[1024];
		int total = 0;

		int read;
		while (total < length && (read = inputStream.read(buffer, 0, Math.min(buffer.length, (int) (length - total)))) != -1)
		{
			if (read > 0)
			{
				total += read;
				outputStream.write(buffer, 0, read);
			}
		}
	}

	public static void pipeStreams(InputStream inputStream, OutputStream outputStream) throws IOException
	{
		byte[] buffer = new byte[1024];

		int read;
		while ((read = inputStream.read(buffer)) != -1)
		{
			if (read > 0)
			{
				outputStream.write(buffer, 0, read);
			}
		}
	}
}