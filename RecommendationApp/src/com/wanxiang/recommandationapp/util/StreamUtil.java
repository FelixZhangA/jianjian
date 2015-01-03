package com.wanxiang.recommandationapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * 
 * @author brandon.salzberg
 */
public class StreamUtil
{
	public static int copyInputStreamToOutputStream(InputStream is, OutputStream os, int bufferSize) throws IOException
	{
		return tryCopyInputStreamToOutputStream(is, os, bufferSize, Integer.MAX_VALUE);
	}

	public static int copyInputStreamToOutputStream(InputStream is, OutputStream os, byte[] transferBuffer) throws IOException
	{
		return tryCopyInputStreamToOutputStream(is, os, transferBuffer, Integer.MAX_VALUE);
	}

	/**
	 * Copies bytesToCopy bytes from <code>is</code> to <code>os</code>. If <code>bytesToCopy</code> bytes aren't available on <code>is</code>, this
	 * method will throw an exception.
	 * 
	 * @param is
	 * @param os
	 * @param bufferSize
	 * @param bytesToCopy
	 * @return
	 * @throws IOException
	 */
	public static int copyInputStreamToOutputStream(InputStream is, OutputStream os, int bufferSize, int bytesToCopy) throws IOException
	{
		int numBytesTransferred = tryCopyInputStreamToOutputStream(is, os, bufferSize, bytesToCopy);

		if (numBytesTransferred < bytesToCopy)
		{
			throw new IOException("Stream ended after " + numBytesTransferred + " bytes of data, we wanted " + bytesToCopy + " total");
		}

		return numBytesTransferred;
	}

	public static int copyInputStreamToOutputStream(InputStream is, OutputStream os, byte[] transferBuffer, int bytesToCopy) throws IOException
	{
		int numBytesTransferred = tryCopyInputStreamToOutputStream(is, os, transferBuffer, bytesToCopy);

		if (numBytesTransferred < bytesToCopy)
		{
			throw new IOException("Stream ended after " + numBytesTransferred + " bytes of data, we wanted " + bytesToCopy + " total");
		}

		return numBytesTransferred;
	}

	/**
	 * Try to get up to maxBytesToCopy from the input stream into the output
	 * stream. Will stop if the stream ends.
	 * 
	 * @param is
	 * @param os
	 * @param bufferSize
	 * @param maxBytesToCopy
	 * @return the number of bytes actually copied
	 * @throws IOException
	 */
	public static int tryCopyInputStreamToOutputStream(InputStream is, OutputStream os, int bufferSize, int maxBytesToCopy) throws IOException
	{
		byte[] buf = new byte[bufferSize];

		return tryCopyInputStreamToOutputStream(is, os, buf, maxBytesToCopy);
	}

	public static int tryCopyInputStreamToOutputStream(InputStream is, OutputStream os, byte[] transferBuffer, int maxBytesToCopy) throws IOException
	{
		int numBytesTransferred = 0;

		int bufferSize = transferBuffer.length;

		int numRead = 0;
		int bytesLeft = maxBytesToCopy;

		do
		{
			int nToRead = Math.min(bytesLeft, bufferSize);

			numRead = is.read(transferBuffer, 0, nToRead);

			if (numRead > 0)
			{
				os.write(transferBuffer, 0, numRead);
				numBytesTransferred += numRead;
				bytesLeft -= numRead;
			}
		}
		while (numRead > 0 && bytesLeft > 0);

		return numBytesTransferred;
	}
}