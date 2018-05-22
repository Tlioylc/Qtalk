package qumi.com.qtalk.activity;


import qumi.com.qtalk.R;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qtalk.util.XmppConnectionManager;
import qumi.com.qtalk.util.XmppUtil;
import qumi.com.qtalk.view.LoadingDialog;
import qumi.com.qtalk.view.TitleBarView;
import qumi.com.qumitalk.service.QtalkClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	private Context mContext;
	private Button btn_complete;
	private TitleBarView mTitleBarView;
	private EditText et_name,et_password;
	
	private String account,password;
	private LoadingDialog loadDialog;
	

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(loadDialog.isShowing()){
				loadDialog.dismiss();
			}
			switch (msg.what) {
			case 0:
				ToastUtil.showLongToast(mContext, "注册失败");
				break;
			case 1:
				ToastUtil.showLongToast(mContext, "注册成功，请牢记您的账号和密码");
				PreferencesUtils.putSharePre(mContext, "username", account);
				PreferencesUtils.putSharePre(mContext, "pwd", password);
				finish();
				break;
			case 2:
				ToastUtil.showLongToast(mContext, "该昵称已被注册");
				break;
			case 3:
				ToastUtil.showLongToast(mContext, "注册失败,请检查您的网络");
				break;
//			case 4:
//				ToastUtil.showLongToast(mContext, "注册失败,请检查您的网络");
//				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_userinfo);
		mContext=this;
		loadDialog=new LoadingDialog(this);
		findView();
		initTitleView();
		init();
	}
	
	private void findView(){
		mTitleBarView=(TitleBarView) findViewById(R.id.title_bar);
		btn_complete=(Button) findViewById(R.id.register_complete);
		
		et_name=(EditText) findViewById(R.id.name);//账号
		et_password=(EditText) findViewById(R.id.password);//密码
		
	}
	
	private void init(){
		btn_complete.setOnClickListener(completeOnClickListener);
	}
	
	private void initTitleView(){
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE,View.GONE, View.GONE);
		mTitleBarView.setTitleText(R.string.title_register_info);
		mTitleBarView.setBtnLeft(R.drawable.fft, R.string.back);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * 点击注册
	 */
	private OnClickListener completeOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			doReg();
		}
	};
	
	void doReg(){
		account=et_name.getText().toString();
		password=et_password.getText().toString();
		if(TextUtils.isEmpty(account)){
			ToastUtil.showLongToast(mContext, "请填写昵称");
			return;
		}
		if(TextUtils.isEmpty(password)){
			ToastUtil.showLongToast(mContext, "请填写密码");
			return;
		}
		loadDialog.setTitle("正在注册...");
		loadDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				QtalkClient client= QtalkClient.getInstance();
				int result=client.creatAccount(account, password);
				mHandler.sendEmptyMessage(result);

			}
		}).start();
	}

}
