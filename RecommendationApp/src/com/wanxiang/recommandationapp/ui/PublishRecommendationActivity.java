package com.wanxiang.recommandationapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.wanxiang.recommandationapp.R;
import com.wanxiang.recommandationapp.model.AbstractRecommendation;
import com.wanxiang.recommandationapp.model.Category;
import com.wanxiang.recommandationapp.model.Entity;
import com.wanxiang.recommandationapp.ui.fragment.ChooseCategoryFragment;
import com.wanxiang.recommandationapp.ui.fragment.EditPublishFragment;
import com.wanxiang.recommandationapp.util.AppConstants;


public class PublishRecommendationActivity extends FragmentActivity implements OnFragmentChangeListener
{
	private static final int	MAX_LENGTH	= 500;
	private FragmentManager		mFragmentManager;
	
	private Category mCategory;
	private Entity mEntity;
	private long mNeedId;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_publish);
		
		Intent intent = getIntent();
		if (intent != null) 
		{
			mCategory = (Category)intent.getSerializableExtra("category");
			mEntity = (Entity)intent.getSerializableExtra("entity");
			mNeedId = intent.getLongExtra(AppConstants.HEADER_NEEDID, 0);
		}
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.content, new EditPublishFragment(this, mEntity, mNeedId));
		transaction.commit();
	}

	@Override
	public void swichFragment( int index, AbstractRecommendation aRec )
	{
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		if (index == 0)
		{
			finish();
			return;
		}
		if (index == 1)
		{
			transaction.replace(R.id.content, new EditPublishFragment(this, mEntity, mNeedId));
		}
		else if (index == 2)
		{
			transaction.replace(R.id.content, new ChooseCategoryFragment(this, aRec, mCategory));

		}
		transaction.commit();
	}

	// private void initDropDownList()
	// {
	// mCategoryAdapter = ArrayAdapter.createFromResource(this,
	// R.array.default_categories, android.R.layout.simple_spinner_item);
	// mAutoCompleteCategory.setAdapter(mCategoryAdapter);
	// mAutoCompleteCategory.setThreshold(1);
	// // mAutoCompleteCategory.setCompletionHint("show 5 items");
	// mAutoCompleteCategory.setOnFocusChangeListener(new
	// View.OnFocusChangeListener()
	// {
	// @Override
	// public void onFocusChange( View v, boolean hasFocus )
	// {
	// AutoCompleteTextView view = (AutoCompleteTextView)v;
	// if (hasFocus)
	// {
	// view.showDropDown();
	// }
	// }
	// });
	// }
	//
	// // @Override
	// // public boolean onCreateOptionsMenu( Menu menu )
	// // {
	// // MenuInflater inflater = getMenuInflater();
	// // inflater.inflate(R.menu.menu_publish_actions, menu);
	// // return super.onCreateOptionsMenu(menu);
	// // }
	//
	// public boolean onOptionsItemSelected( MenuItem item )
	// {
	// if (item.getItemId() == android.R.id.home)
	// {
	// onBackPressed();
	// }
	// else if (item.getItemId() == R.id.action_attach_img)
	// {
	// onBackPressed();
	// }
	// else if (item.getItemId() == R.id.action_publish)
	// {
	// if (TextUtils.isEmpty(mCategory))
	// {
	// MyDialog dialog = new MyDialog(this);
	// dialog.show();
	// }
	// else
	// {
	// publish();
	// }
	//
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }
	//
	// private void publish()
	// {
	// String entity = mEditEntity.getText().toString();
	// String content = mEditContent.getText().toString();
	// String user = "MOCKER";
	//
	// if (TextUtils.isEmpty(entity) || TextUtils.isEmpty(mCategory))
	// {
	// Toast.makeText(this, R.string.error_publish_missing,
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	// else if (!TextUtils.isEmpty(content) && content.length() >= MAX_LENGTH)
	// {
	// Toast.makeText(this, R.string.error_publish_exceed,
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	// PublishRecommandationAsyncTask task = new
	// PublishRecommandationAsyncTask(entity, mCategory, content, user);
	// task.execute();
	//
	// }
	//
	// private class PublishRecommandationAsyncTask extends AsyncTask<Object,
	// Object, Object>
	// {
	// private String mEntity;
	// private String mCategory;
	// private String mContent;
	// private String mUser;
	// private ProgressDialog pd;
	//
	// public PublishRecommandationAsyncTask( String entity, String category,
	// String content, String user )
	// {
	// mEntity = entity;
	// mCategory = category;
	// mContent = content;
	// mUser = user;
	// }
	//
	// @Override
	// protected void onPreExecute()
	// {
	// super.onPreExecute();
	// pd = new ProgressDialog(PublishRecommendationActivity.this);
	// pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// pd.setMessage(getString(R.string.string_publishing));
	// pd.setIndeterminate(true);
	// pd.show();
	//
	// }
	//
	// @Override
	// protected void onPostExecute( Object result )
	// {
	// super.onPostExecute(result);
	// if (pd != null && pd.isShowing())
	// {
	// pd.dismiss();
	// }
	// finish();
	// }
	//
	// @Override
	// protected Object doInBackground( Object... params )
	// {
	// // Recommendation r = new Recommendation(mEntity, mCategory, mUser,
	// // System.currentTimeMillis(), mContent, 0, 0);
	// // mDao.insertRecommandation(r);
	// HttpClient client = null;
	// try
	// {
	// // Send recommendation to server;
	// Map<String, String> headers = new HashMap<String, String>();
	// headers.put(AppConstants.HEADER_IMEI,
	// AppPrefs.getInstance(PublishRecommendationActivity.this).getIMEI());
	// HttpPost request =
	// HttpHelper.createHttpPost(AppConstants.ACTION_PUBLISH_REC, null,
	// headers);
	// NameValuePair nameValuePair1 = new
	// BasicNameValuePair(AppConstants.HEADER_USER_ID, "1");
	// NameValuePair nameValuePair2 = new
	// BasicNameValuePair(AppConstants.HEADER_CATEGORY, mCategory);
	// NameValuePair nameValuePair3 = new
	// BasicNameValuePair(AppConstants.HEADER_ENTITY, mEntity);
	// NameValuePair nameValuePair4 = new
	// BasicNameValuePair(AppConstants.HEADER_DESCRIPTION, mContent);
	// List list = new ArrayList();
	// list.add(nameValuePair1);
	// list.add(nameValuePair2);
	// list.add(nameValuePair3);
	// list.add(nameValuePair4);
	// request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
	// client = HttpHelper.createHttpClient(false);
	//
	// HttpResponse response = client.execute(request);
	// checkStatusCode(response);
	//
	// }
	// catch (UnsupportedEncodingException e)
	// {
	// e.printStackTrace();
	// }
	// catch (ClientProtocolException e)
	// {
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// finally
	// {
	// if (client != null)
	// {
	// client.getConnectionManager().shutdown();
	// }
	// }
	//
	// return null;
	// }
	//
	// }
	//
	// private void checkStatusCode( HttpResponse response )
	// {
	// if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
	// {
	// Log.d("wanxiang", "publish failed. Error is " +
	// response.getStatusLine().getStatusCode());
	// }
	// else
	// {
	// try
	// {
	// InputStream is = response.getEntity().getContent();
	// InputStreamReader isr = new InputStreamReader(is, "UTF-8");
	// BufferedReader bis = new BufferedReader(isr);
	// StringBuilder sb = new StringBuilder();
	// String s;
	//
	// while (true)
	// {
	// s = bis.readLine();
	// if (TextUtils.isEmpty(s))
	// {
	// break;
	// }
	// sb.append(s);
	// }
	// Log.d("wanxiang", "Response is " + sb.toString());
	// }
	// catch (IllegalStateException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// private class MyDialog extends Dialog implements OnClickListener
	// {
	// private static final int SUCCESS = 1;
	// Context context;
	// private Button mBtnPubish = null;
	// private EditText mEditText = null;
	// private Cursor mCursor = null;
	// private LinearLayout mainLayout;
	// private LinearLayout mainLayout2;
	// private Handler mHandler = new Handler()
	// {
	// @Override
	// public void handleMessage( Message msg )
	// {
	// if (msg.what == SUCCESS)
	// {
	// LinearLayout.LayoutParams lp = new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
	// int i = 0;
	// while (mCursor.moveToNext() && i < 6)
	// {
	// Button button = new Button(context);
	// button.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY)));
	// button.setBackgroundColor(mCursor.getInt(mCursor.getColumnIndex(DatabaseConstants.COLUMN_CATEGORY_COLOR)));
	// button.setOnClickListener(MyDialog.this);
	// button.setLayoutParams(lp);
	// if (i < 3)
	// {
	// mainLayout.addView(button);
	// }
	// else
	// {
	// mainLayout2.addView(button);
	// }
	// i++;
	// }
	// }
	// }
	// };
	//
	// public MyDialog( Context context )
	// {
	// super(context);
	// // TODO Auto-generated constructor stub
	// this.context = context;
	// }
	//
	// public MyDialog( Context context, int theme )
	// {
	// super(context, theme);
	// this.context = context;
	// }
	//
	// @Override
	// protected void onCreate( Bundle savedInstanceState )
	// {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// this.setContentView(R.layout.dialog_customization);
	// mainLayout = (LinearLayout)findViewById(R.id.default_category);
	// mainLayout2 = (LinearLayout)findViewById(R.id.default_category_2);
	// this.setTitle(context.getString(R.string.string_category));
	// new Thread()
	// {
	// public void run()
	// {
	// CategoryDao dao = CategoryDao.getAdapterObject(context);
	// mCursor = dao.getAllCategoryCursor();
	// mHandler.sendEmptyMessage(SUCCESS);
	// }
	//
	// }.start();
	//
	// mBtnPubish = (Button)findViewById(R.id.btn_publish);
	// mBtnPubish.setOnClickListener(this);
	// mEditText = (EditText)findViewById(R.id.edit_category);
	// }
	//
	// @Override
	// public void onClick( View v )
	// {
	// // TODO Auto-generated method stub
	// if (v.getId() == R.id.btn_publish)
	// {
	// mCategory = mEditText.getText().toString();
	// }
	// else
	// {
	// Button btn = (Button)v;
	// mCategory = btn.getText().toString();
	// }
	// publish();
	// }
	// }

}
