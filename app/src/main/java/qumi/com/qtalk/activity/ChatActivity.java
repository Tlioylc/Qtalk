package qumi.com.qtalk.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import qumi.com.qtalk.R;
import qumi.com.qtalk.adapter.ChatAdapter;
import qumi.com.qtalk.adapter.FaceVPAdapter;
import qumi.com.qumitalk.service.CallBack.QMSendMessageCallBack;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.ExpressionUtil;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qtalk.view.DropdownListView;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qumitalk.service.Util.BaseUtil;


/**
 * 聊天界面
 * @author 白玉梁
 * @blog http://blog.csdn.net/baiyuliang2013
 * @weibo http://weibo.com/274433520
 * 
 * */
@SuppressLint("SimpleDateFormat")
public class ChatActivity extends Activity implements OnClickListener,DropdownListView.OnRefreshListenerHeader {

    private static final int REQUEST_CODE_PICK_IMAGE = 1023;

	private ViewPager mViewPager;
	private LinearLayout mDotsLayout;
	private EditText input;
	private TextView send;
	private DropdownListView mListView;
	private ChatAdapter mLvAdapter;

	private LinearLayout chat_face_container,chat_add_container;
	private ImageView image_face;//表情图标
	private ImageView image_add;//更多图标
	
	private TextView tv_title,tv_pic,//图片
	tv_camera,//拍照
	tv_loc;//位置
	
	//表情图标每页6列4行
	private int columns = 6;
	private int rows = 4;
	//每页显示的表情view
	private List<View> views = new ArrayList<View>();
	//表情列表
	private List<String> staticFacesList;
	//消息
	private List<QMMessageBean> listQMMessageBean;
	private SimpleDateFormat sd;
	private NewMsgReciver newMsgReciver;
	private MsgOperReciver msgOperReciver;
	private LayoutInflater inflater;
	private int offset;
	private String I,YOU;//为了好区分，I就是自己，YOU就是对方
	private int chatType;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mLvAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_chat);
		I= PreferencesUtils.getSharePreStr(this, "username");
		YOU=getIntent().getStringExtra("from");
		chatType = getIntent().getIntExtra("type",0);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_title.setText(YOU);
		sd=new SimpleDateFormat("MM-dd HH:mm");
		msgOperReciver=new MsgOperReciver();
		newMsgReciver=new NewMsgReciver();
		IntentFilter intentFilter=new IntentFilter(Const.ACTION_MSG_OPER);
		registerReceiver(msgOperReciver, intentFilter);
		intentFilter=new IntentFilter(Const.ACTION_NEW_MSG);
		registerReceiver(newMsgReciver, intentFilter);
		staticFacesList= ExpressionUtil.initStaticFaces(this);
		//初始化控件
		initViews();
		//初始化表情
		initViewPager();
		//初始化更多选项（即表情图标右侧"+"号内容）
		initAdd();
		//初始化数据
		initData();
		//更新与该用户的聊天记录全部为已读
		updateMsgToReaded();
	}
	
	
	private void updateMsgToReaded() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				QtalkClient.getInstance().getQmChatMessageManager().markAllMessageAsRead(YOU,I);
			}
		}).start();
	}


	/**
	 * 初始化控件
	 */
	private void initViews() {
		mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
		//表情图标
		image_face=(ImageView) findViewById(R.id.image_face);
		//更多图标
		image_add=(ImageView) findViewById(R.id.image_add);
		//表情布局
		chat_face_container=(LinearLayout) findViewById(R.id.chat_face_container);
		//更多
		chat_add_container=(LinearLayout) findViewById(R.id.chat_add_container);
		
		mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		mViewPager.setOnPageChangeListener(new PageChange());
		//表情下小圆点
		mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
		input = (EditText) findViewById(R.id.input_sms);
		send = (TextView) findViewById(R.id.send_sms);
		input.setOnClickListener(this);
		
		//表情按钮
		image_face.setOnClickListener(this);
		//更多按钮
		image_add.setOnClickListener(this);
		// 发送
		send.setOnClickListener(this);
		
		mListView.setOnRefreshListenerHead(this);
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction()==MotionEvent.ACTION_DOWN){
					if(chat_face_container.getVisibility()==View.VISIBLE){
						chat_face_container.setVisibility(View.GONE);
					}
					if(chat_add_container.getVisibility()==View.VISIBLE){
						chat_add_container.setVisibility(View.GONE);
					}
					hideSoftInputView();
				}
				return false;
			}
		});
	}
	
	public void initAdd(){
		tv_pic=(TextView) findViewById(R.id.tv_pic);
		tv_camera=(TextView) findViewById(R.id.tv_camera);
		tv_loc=(TextView) findViewById(R.id.tv_loc);
		
		tv_pic.setOnClickListener(this);
		tv_camera.setOnClickListener(this);
		tv_loc.setOnClickListener(this);
		
	}
	
	public void initData(){
		offset=0;
		listQMMessageBean =QtalkClient.getInstance().getQmChatMessageManager().getMessageList(YOU,I,offset);
		offset= listQMMessageBean.size();
		mLvAdapter = new ChatAdapter(this, listQMMessageBean);
		mListView.setAdapter(mLvAdapter);
		mListView.setSelection(listQMMessageBean.size());
	}
	
	/**
	 * 初始化表情 
	 */
	private void initViewPager() {
		int pagesize= ExpressionUtil.getPagerCount(staticFacesList.size(),columns,rows);
		// 获取页数
		for (int i = 0; i <pagesize; i++) {
			views.add(ExpressionUtil.viewPagerItem(this, i, staticFacesList,columns, rows, input));
			LayoutParams params = new LayoutParams(16, 16);
			mDotsLayout.addView(dotsItem(i), params);
		}
		FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
		mViewPager.setAdapter(mVpAdapter);
		mDotsLayout.getChildAt(0).setSelected(true);
	}

	/**
	 * 表情页切换时，底部小圆点
	 * @param position
	 * @return
	 */
	private ImageView dotsItem(int position) {
		View layout = inflater.inflate(R.layout.dot_image, null);
		ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
		iv.setId(position);
		return iv;
	}
	
	/**
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.send_sms:
			String content=input.getText().toString();
			if(TextUtils.isEmpty(content)){
				return;
			}
			sendMsgText(content);
			break;
		case R.id.input_sms:
			if(chat_face_container.getVisibility()==View.VISIBLE){
				chat_face_container.setVisibility(View.GONE);
			}
			if(chat_add_container.getVisibility()==View.VISIBLE){
				chat_add_container.setVisibility(View.GONE);
			}
			break;
		case R.id.image_face:
			hideSoftInputView();//隐藏软键盘
			if(chat_add_container.getVisibility()==View.VISIBLE){
				chat_add_container.setVisibility(View.GONE);
			}
			if(chat_face_container.getVisibility()==View.GONE){
				chat_face_container.setVisibility(View.VISIBLE);
			}else{
				chat_face_container.setVisibility(View.GONE);
			}
			break;
		case R.id.image_add:
			hideSoftInputView();//隐藏软键盘
			if(chat_face_container.getVisibility()==View.VISIBLE){
				chat_face_container.setVisibility(View.GONE);
			}
			if(chat_add_container.getVisibility()==View.GONE){
				chat_add_container.setVisibility(View.VISIBLE);
			}else{
				chat_add_container.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_pic://模拟一张图片路径
//			sendMsgImg("http://my.csdn.net/uploads/avatar/3/B/9/1_baiyuliang2013.jpg");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");// 相片类型
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
			break;
		case R.id.tv_camera://拍照，换个美女图片吧
			sendMsgImg("http://b.hiphotos.baidu.com/image/pic/item/55e736d12f2eb93872b0d889d6628535e4dd6fe8.jpg");
			break;
		case R.id.tv_loc://位置，正常情况下是需要定位的，可以用百度或者高德地图，现设置为北京坐标
			sendMsgLocation("116.404,39.915");
			break;
		}
	}
	
	/**
	 * 执行发送消息 图片类型
	 * @param
	 */
	void sendMsgImg(String imgpath){

		final QMMessageBean qMMessageBean =getChatInfoTo(imgpath,Const.MSG_TYPE_IMG);
		qMMessageBean.setMsgId(QtalkClient.getInstance().getQmChatMessageManager().addMessage(qMMessageBean));

		listQMMessageBean.add(qMMessageBean);
		offset= listQMMessageBean.size();
		mLvAdapter.notifyDataSetChanged();
		final QMMessageBean qmMessageBean2 = QMMessageBean.createImgMessage(imgpath,YOU,I);
		QtalkClient.getInstance().getChatClient().sendImageMessage(qmMessageBean2, new QMSendMessageCallBack() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailed() {

			}
		});
//		listQMMessageBean.add(QMMessageBean);
//		offset= listQMMessageBean.size();
//		mLvAdapter.notifyDataSetChanged();
//		final String message=YOU+Const.SPLIT+I+Const.SPLIT+Const.MSG_TYPE_IMG+Const.SPLIT+imgpath+Const.SPLIT+sd.format(new Date());
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					XmppUtil.sendMessage(QQApplication.xmppConnection, message, YOU);
//				} catch (Exception e) {
//					e.printStackTrace();
//					Looper.prepare();
//					ToastUtil.showShortToast(ChatActivity.this, "发送失败");
//					Looper.loop();
//				}
//			}
//		}).start();
//		updateSession(Const.MSG_TYPE_TEXT,"[图片]");
	}
	
	/**
	 * 执行发送消息 文本类型
	 * @param content
	 */
	void sendMsgText(String content){
		QMMessageBean QMMessageBean =getChatInfoTo(content,Const.MSG_TYPE_TEXT);
		QMMessageBean.setMsgId(QtalkClient.getInstance().getQmChatMessageManager().addMessage(QMMessageBean));
		listQMMessageBean.add(QMMessageBean);
		offset= listQMMessageBean.size();
		mLvAdapter.notifyDataSetChanged();
		input.setText("");

	    final QMMessageBean qmMessageBean = QMMessageBean.createTextMessage(content,YOU,I);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(chatType == 0)
						QtalkClient.getInstance().getChatClient().sendMessage(qmMessageBean);
					else
						QtalkClient.getInstance().getChatClient().sendGroupMessage(qmMessageBean);
				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					ToastUtil.showShortToast(ChatActivity.this, "发送失败");
					Looper.loop();
				}
			}
		}).start();
	}
	
	/**
	 * 执行发送消息 定位消息
	 * @param content
	 */
	void sendMsgLocation(String content){
		QMMessageBean QMMessageBean =getChatInfoTo(content,Const.MSG_TYPE_LOCATION);
		QMMessageBean.setMsgId(QtalkClient.getInstance().getQmChatMessageManager().addMessage(QMMessageBean));
		listQMMessageBean.add(QMMessageBean);
		offset= listQMMessageBean.size();
		mLvAdapter.notifyDataSetChanged();

		final QMMessageBean qmMessageBean = QMMessageBean.createTextMessage(content,YOU,I);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(chatType == 0)
						QtalkClient.getInstance().getChatClient().sendMessage(qmMessageBean);
					else
						QtalkClient.getInstance().getChatClient().sendGroupMessage(qmMessageBean);
				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					ToastUtil.showShortToast(ChatActivity.this, "发送失败");
					Looper.loop();
				}
			}
		}).start();
	}
	
	/**
	 * 发送的信息
	 *  from为收到的消息，to为自己发送的消息
	 * @param message => 接收者卍发送者卍消息类型卍消息内容卍发送时间
	 * @return
	 */
	private QMMessageBean getChatInfoTo(String message, int msgtype) {
		String time=sd.format(new Date());
		QMMessageBean qMMessageBean = QMMessageBean.createEmptyMessage();
		qMMessageBean.setFromUser(YOU);
		qMMessageBean.setToUser(I);
		qMMessageBean.setType(msgtype);
		qMMessageBean.setIsComing(1);
		qMMessageBean.setChatType(chatType);
		qMMessageBean.setContent(message);
		qMMessageBean.setDate(time);
		return qMMessageBean;
	}
	

	
	/**
	 * 表情页改变时，dots效果也要跟着改变
	 * */
	class PageChange implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
				mDotsLayout.getChildAt(i).setSelected(false);
			}
			mDotsLayout.getChildAt(arg0).setSelected(true);
		}
	}

	/**
	 * 下拉加载更多
	 */
	@Override
	public void onRefresh() {
		List<QMMessageBean> list=QtalkClient.getInstance().getQmChatMessageManager().getMessageList(YOU,I,offset);
		if(list.size()<=0){
			mListView.setSelection(0);
			mListView.onRefreshCompleteHeader();
			return;
		}
		listQMMessageBean.addAll(0,list);
		offset= listQMMessageBean.size();
		mListView.onRefreshCompleteHeader();
		mLvAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size());
	}
	
	/** 
	 * 弹出输入法窗口 
	 */  
	private void showSoftInputView(final View v) {  
	    new Handler().postDelayed(new Runnable() {  
	        @Override  
	        public void run() {  
	        	((InputMethodManager) v.getContext().getSystemService(Service.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	        }  
	    }, 0);  
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 接收消息记录操作广播：删除复制
	 * @author baiyuliang
	 */
	private class MsgOperReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type=intent.getIntExtra("type", 0);
			final int position=intent.getIntExtra("position", 0);
			if(listQMMessageBean.size()<=0){
				return;
			}
			final QMMessageBean QMMessageBean = listQMMessageBean.get(position);
			switch (type) {
			case 1://聊天记录操作
				Builder bd = new Builder(ChatActivity.this);
				String[] items=null;
				if(QMMessageBean.getType() == Const.MSG_TYPE_TEXT){
					items =  new String[]{"删除记录","删除全部记录","复制文字"};
				}else{
					items =  new String[]{"删除记录","删除全部记录"};
				}
				bd.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0://删除
							listQMMessageBean.remove(position);
							offset= listQMMessageBean.size();
							mLvAdapter.notifyDataSetChanged();
							QtalkClient.getInstance().getQmChatMessageManager().deleteMessage(QMMessageBean.getMsgId());
							break;
						case 1://删除全部
							listQMMessageBean.removeAll(listQMMessageBean);
							offset= listQMMessageBean.size();
							mLvAdapter.notifyDataSetChanged();
							QtalkClient.getInstance().getQmChatMessageManager().deleteConversationAllMessage(YOU, I);
							break;
						case 2://复制
							ClipboardManager cmb = (ClipboardManager) ChatActivity.this.getSystemService(ChatActivity.CLIPBOARD_SERVICE);
							cmb.setText(QMMessageBean.getContent());
							Toast.makeText(getApplicationContext(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
				bd.show();
				break;
			}
				
			}
	}
	
	/**
	 * 接收消息记录操作广播：删除复制
	 * @author baiyuliang
	 */
	private class NewMsgReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b=intent.getBundleExtra("QMMessageBean");
			QMMessageBean QMMessageBean =(QMMessageBean) b.getSerializable("QMMessageBean");
			if(!QMMessageBean.getFromUser().equals(YOU)){
				return;
			}
			listQMMessageBean.add(QMMessageBean);
			offset= listQMMessageBean.size();
			mLvAdapter.notifyDataSetChanged();
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(msgOperReciver);
		unregisterReceiver(newMsgReciver);
	}
	
    @Override
    protected void onResume() {
 		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//让输入框获取焦点
				input.requestFocus();
			}
		}, 100);
    	super.onResume();
    };

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public String getRealFilePath(final Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri uri = data.getData();
            //获取图片绝对路径
            String link = getRealFilePath(uri);
            //发送就可以
            if (BaseUtil.obtain().fileIsExists(link)) {
                sendMsgImg(link);
            }
        }
    }
	
	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			 hideSoftInputView();
				if(chat_face_container.getVisibility()==View.VISIBLE){
					chat_face_container.setVisibility(View.GONE);
				}else if(chat_add_container.getVisibility()==View.VISIBLE){
					chat_add_container.setVisibility(View.GONE);
				}else{
					finish();
				}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	

}
