package com.wanxiang.recommandationapp.http;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.ContentProducer;

import com.wanxiang.recommandationapp.model.Recommendation;

public class RecommendationContentProducer implements ContentProducer
{

	private Recommendation mRec;
	public RecommendationContentProducer(Recommendation r)
	{
		mRec = r;
	}
	@Override
	public void writeTo( OutputStream outstream ) throws IOException
	{
		try
		{
			final String statusAckContent = writeRecommendation();
			outstream.write(statusAckContent.getBytes());
			outstream.flush();
		}
		finally
		{
			outstream.close();
		}		
	}

	private String writeRecommendation()
	{
// 'user_id=1&category=ͼ��&entity=���޸�&description=�ܸ��˵�һ����' 
//user_id=1&category=��ʳ&entity=����&description=����
//		final StringBuilder sb = new StringBuilder();
//		sb.append(AppConstants.HEADER_USER_ID);
//		sb.append("=1");
//		//sb.append(mRec.getUser().getName());
//		sb.append("&");
//		sb.append(AppConstants.HEADER_CATEGORY);
//		sb.append("=");
//		sb.append(mRec.getCategory().getCategoryName());
//		sb.append("&");
//		sb.append(AppConstants.HEADER_ENTITY);
//		sb.append("=");
//		sb.append(mRec.getEntity().getEntityName());
//		sb.append("&");
//		sb.append(AppConstants.HEADER_DESCRIPTION);
//		sb.append("=");
//		sb.append(mRec.getContent());
//		String ret = sb.toString();
//		Log.d("wanxiang", ret);
//		return ret;
		return null;
	}

}
