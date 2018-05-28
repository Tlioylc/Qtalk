package qumi.com.qtalk.activity;

import java.util.ArrayList;
import java.util.List;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import qumi.com.qtalk.R;
import qumi.com.qtalk.adapter.AddFriendAdapter;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qtalk.util.XmppUtil;
import qumi.com.qtalk.view.LoadingDialog;
import qumi.com.qumitalk.service.QtalkClient;


/**
 * 
 * @author 白玉梁
 */
public class AddFriendActivity extends Activity implements OnClickListener{
	
	private ImageView go_back;
	private EditText search_key;
	private Button btn_search;
	private ListView search_list;
	
	private String search_content;
	List<Session> listUser;
	AddFriendAdapter addFriendAdapter;
	private LoadingDialog loadingDialog;

	private int currentSearchType = 0;//0 好友 1群；
	private String I;
	
	private String sendInviteUser="";//被邀请人
	
	PopupWindow popWindow;
	EditText edit;
	Button btn_ok,btn_cancel;
	Button joinGroup,addFriend;
	private LayoutInflater layoutInflater;
	
	
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			switch (msg.what) {
			case 1:
				addFriendAdapter=new AddFriendAdapter(AddFriendActivity.this, listUser);
				search_list.setAdapter(addFriendAdapter);
				break;
			case -1:
				ToastUtil.showLongToast( AddFriendActivity.this, "未查询到信息");
				break;
			case 2:
				if(currentSearchType == 0)
					ToastUtil.showLongToast( AddFriendActivity.this, "邀请已发送");
				else
					ToastUtil.showLongToast( AddFriendActivity.this, "已加入群组");
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loadingDialog=new LoadingDialog(this);
		loadingDialog.setTitle("正在查询...");
		I= PreferencesUtils.getSharePreStr(this, "username");
		initView();
	}



	/**
	 * 初始化控件
	 */
	private void initView() {
		go_back = (ImageView) findViewById(R.id.img_back);//返回
		search_key=(EditText) findViewById(R.id.search_key);
		btn_search=(Button) findViewById(R.id.btn_search);

		joinGroup = findViewById(R.id.constact_join_group);
		addFriend = findViewById(R.id.constact_add_friend);
		//listview
		search_list=(ListView) findViewById(R.id.search_list);
		search_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		search_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
				final String YOU=listUser.get(arg2).getFromUser();

				if(YOU.equals(I)){
					ToastUtil.showLongToast(getApplicationContext(), "不能添加自己为好友");
					return;
				}
				//此处是判断是否重复邀请了某人，当然这个判断很简单，也是不科学的，至于怎么判断此人是否被你邀请过，大家自己可以想办法
				//还有一点是，已经成为好友了，再去点邀请，这个也是需要判断的
				if(sendInviteUser.equals(YOU)){
					ToastUtil.showShortToast(getApplicationContext(), "你已经邀请过"+YOU+"了");
					return;
				}
				showPopupWindow(search_list,YOU);
				popupInputMethodWindow();
			}
		});
		
		go_back.setOnClickListener(this);
		btn_search.setOnClickListener(this);

		joinGroup.setOnClickListener(this);
		addFriend.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {//返回
		case R.id.img_back:
			this.finish();
			break;
		case R.id.btn_search:
			search_content=search_key.getText().toString();
			if(TextUtils.isEmpty(search_content)){
				return;
			}
			searchUser();
			break;
		case R.id.constact_join_group:
			search_key.setText("");
			search_key.setHint("群名");
			currentSearchType = 1;
			listUser  = new ArrayList<>();
			addFriendAdapter=new AddFriendAdapter(AddFriendActivity.this, listUser);
			search_list.setAdapter(addFriendAdapter);
			break;
		case R.id.constact_add_friend:
			search_key.setText("");
			search_key.setHint("昵称");
			currentSearchType = 0;
			listUser  = new ArrayList<>();
			addFriendAdapter=new AddFriendAdapter(AddFriendActivity.this, listUser);
			search_list.setAdapter(addFriendAdapter);
			break;
		}
	}



	private void searchUser() {
		loadingDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(currentSearchType == 0){
					listUser= XmppUtil.searchUsers(search_content);
				}else {
					listUser = QtalkClient.getInstance().getQMGoupChatManager().findMulitGroup(search_content);
				}
				if(listUser.size()>0){
					mHandler.sendEmptyMessage(1);
				}else{
					mHandler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}
	
	@SuppressWarnings("deprecation")
	private void showPopupWindow(View parent,final String toUser){
		if (popWindow == null) {		
			View view = layoutInflater.inflate(R.layout.pop_edit,null);
			popWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
			initPop(view,toUser);
		}
		if(currentSearchType == 0){
			edit.setHint("请填写好友验证信息");
		}else {
			edit.setHint("群聊申请信息");
		}
		popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());	
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}
	
	public void initPop(View view,final String toUser){
		edit=(EditText) view.findViewById(R.id.edit);
		btn_ok = (Button) view.findViewById(R.id.btn_ok);//确定
		btn_cancel= (Button) view.findViewById(R.id.btn_cancel);//取消
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(edit.getText().toString())){
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
//							XmppUtil.addGroup(roster, "我的好友");//先默认创建一个分组
							if(currentSearchType == 0){
								XmppUtil.addUsers(toUser, toUser,"我的好友");
								QMMessageBean qmMessageBean = QMMessageBean.createFriendMessage(edit.getText().toString(),toUser,I);
								QtalkClient.getInstance().getChatClient().sendMessage(qmMessageBean);
								sendInviteUser=toUser;
							}else {
								QtalkClient.getInstance().getQMGoupChatManager().joinMultiUserChat(null,toUser);
							}

							mHandler.sendEmptyMessage(2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				popWindow.dismiss();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popWindow.dismiss();
			}
		});
	}
	
	/** 
	 * 弹出输入法窗口 
	 */  
	private void popupInputMethodWindow() {  
	    new Handler().postDelayed(new Runnable() {  
	        @Override  
	        public void run() {  
	            ((InputMethodManager) edit.getContext().getSystemService(Service.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	        }  
	    }, 0);  
	}  
	
}
