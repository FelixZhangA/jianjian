package com.wanxiang.recommandationapp.ui.base;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.jianjianapp.R;
import com.wanxiang.recommandationapp.ui.base.TripBaseFragment.Anim;

/**
 * 页面跳转控制参数
 * 
 * @author 弦叶
 * @date 2014-5-23
 */
public class PageSwitchBean implements Parcelable {
	/**
	 * 页面名
	 */
	private String pageName = "";

	/**
	 * 相关数据
	 */
	private Bundle bundle;
	/**
	 * 动画类型
	 */
	private int[] anim = null;

	/**
	 * 是否添加到栈中
	 */
	private boolean addToBackStack = true;

	/**
	 * 是否起新页面
	 */
	private boolean newActivity = false;

	/**
	 * 是否进行连续点击检查，避免快速点击多次打开一个页面
	 */
	private boolean checkDoubleClick = true;
	/**
	 * fragment跳转 暂不支持
	 */
	private int requestCode = -1;
	
	
	public PageSwitchBean(String pageName){
		this.pageName = pageName;
	}
	
	public PageSwitchBean(String pageName,Bundle bundle){
		this.pageName = pageName;
		this.bundle = bundle;
	}
	
	public PageSwitchBean(String pageName,Bundle bundle,Anim anim){
		this.pageName = pageName;
		this.bundle = bundle;
		this.setAnim(anim);
	}
	public PageSwitchBean(String pageName,Bundle bundle,Anim anim,boolean newActivity){
		this.pageName = pageName;
		this.bundle = bundle;
		this.setAnim(anim);
		this.newActivity = newActivity;
	}
	public PageSwitchBean(String pageName,Bundle bundle,Anim anim,boolean newActivity,boolean addToBackStack){
		this.pageName = pageName;
		this.bundle = bundle;
		this.setAnim(anim);
		this.newActivity = newActivity;
		this.addToBackStack = addToBackStack;
	}
	public PageSwitchBean(String pageName,Bundle bundle,int[] anim,boolean newActivity,boolean addToBackStack){
		this.pageName = pageName;
		this.bundle = bundle;
		this.anim = anim;
		this.newActivity = newActivity;
		this.addToBackStack = addToBackStack;
	}
	
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public int[] getAnims(){
		return this.anim;
	}
	public void setAnim(Anim anim) {
		this.anim = convertAnimations(anim);
	}
	public void setAnim(int[] anim) {
		this.anim = anim;
	}
	public boolean isAddToBackStack() {
		return addToBackStack;
	}

	public void setAddToBackStack(boolean addToBackStack) {
		this.addToBackStack = addToBackStack;
	}

	public boolean isNewActivity() {
		return newActivity;
	}

	public void setNewActivity(boolean newActivity) {
		this.newActivity = newActivity;
	}

	public boolean checkDoubleClick() {
		return checkDoubleClick;
	}

	public void setCheckDoubleClick(boolean checkDoubleClick) {
		this.checkDoubleClick = checkDoubleClick;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	@Override
	public String toString() {
		return "JumpBean [pageName=" + pageName + ", bundle=" + (bundle != null ? bundle.toString() : "") + ", anim=" + anim + ", addToBackStack=" + addToBackStack + ", newActivity=" + newActivity
				+ ", checkDoubleClick=" + checkDoubleClick + ", requestCode=" + requestCode + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		if (pageName == null) {
			pageName = "";
		}
		if (bundle == null) {
			bundle = new Bundle();
		}
		if (anim == null) {
			int[] a ={-1,-1,-1,-1}; 
			anim = a;
		}
		out.writeString(pageName);
		bundle.writeToParcel(out, flags);
		if(anim!=null && anim.length==4){
			out.writeInt(anim[0]);
			out.writeInt(anim[1]);
			out.writeInt(anim[2]);
			out.writeInt(anim[3]);
		}else{
			out.writeInt(-1);
			out.writeInt(-1);
			out.writeInt(-1);
			out.writeInt(-1);
		}
		out.writeInt(addToBackStack?1:0);
		out.writeInt(newActivity ? 1 : 0);
		out.writeInt(checkDoubleClick ? 1 : 0);
		out.writeInt(requestCode);
	}

	public static final Parcelable.Creator<PageSwitchBean> CREATOR = new Parcelable.Creator<PageSwitchBean>() {
		public PageSwitchBean createFromParcel(Parcel in) {
			return new PageSwitchBean(in);
		}

		public PageSwitchBean[] newArray(int size) {
			return new PageSwitchBean[size];
		}
	};

	private PageSwitchBean(Parcel in) {
		pageName = in.readString();
		bundle = in.readBundle();
		int[] a = {in.readInt(), in.readInt(), in.readInt(), in.readInt()};
		anim = a;
		addToBackStack = in.readInt()==1?true:false;
		newActivity = in.readInt()==1?true:false;
		checkDoubleClick = in.readInt()==1?true:false;
		requestCode = in.readInt();
	}
	
	public static int[] convertAnimations(Anim anim) {
		if (anim == Anim.city_guide) {
			int[] animations = { R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right };
			return animations;
		} else if (anim == Anim.present) {
			int[] animations = { R.anim.push_in_down, R.anim.push_no_ani, R.anim.push_no_ani, R.anim.push_out_down };
			return animations;
		} else if (anim == Anim.fade) {
			int[] animations = { R.anim.alpha_in, R.anim.alpha_out, R.anim.alpha_in, R.anim.alpha_out };
			return animations;
		} else if (anim == Anim.slide) {
			int[] animations = { R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right };
			return animations;
		} else if (anim == Anim.slide_inverse) {
			int[] animations = { R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right };
			return animations;
		}
		return null;
	}

}
