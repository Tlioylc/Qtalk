package qumi.com.qtalk.fragment;

import java.util.ArrayList;
import java.util.List;

import qumi.com.qtalk.QQApplication;
import qumi.com.qtalk.R;
import qumi.com.qtalk.activity.AddFriendActivity;
import qumi.com.qtalk.activity.ChatActivity;
import qumi.com.qtalk.activity.CreatGroupActivity;
import qumi.com.qtalk.adapter.ConstactAdapter;
import qumi.com.qtalk.bean.Child;
import qumi.com.qtalk.bean.Group;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qtalk.view.IphoneTreeView;
import qumi.com.qtalk.view.TitleBarView;
import qumi.com.qumitalk.service.DataBean.UserBean;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qumitalk.service.Util.LogUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import static qumi.com.qumitalk.service.QMContactsManager.FRIEND;

public class ConstactFragment extends Fragment {
	private Context mContext;
	private View mBaseView;
	private View addView;
	private View shadowView;
	private TitleBarView mTitleBarView;
	private IphoneTreeView mIphoneTreeView;
	private ConstactAdapter mExpAdapter;
	private List<Group> listGroup;
	private View creatGroupChat;
	private View addChat;
	
	private FriendsOnlineStatusReceiver friendsOnlineStatusReceiver;

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
 				initData();
				if(mExpAdapter != null){
					for(int i = 0; i < mExpAdapter.getGroupCount(); i++){
						mIphoneTreeView.expandGroup(i);
					}
				}
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_constact_father, null);
		friendsOnlineStatusReceiver=new FriendsOnlineStatusReceiver();
		IntentFilter intentFilter=new IntentFilter(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
		mContext.registerReceiver(friendsOnlineStatusReceiver, intentFilter);
		findView();
		return mBaseView;
	}

	private void findView() {
		mTitleBarView=(TitleBarView) mBaseView.findViewById(R.id.title_bar);
		addView = mBaseView.findViewById(R.id.add_view);
		shadowView = mBaseView.findViewById(R.id.shadow_view);
		addChat = mBaseView.findViewById(R.id.add_chat);
		creatGroupChat = mBaseView.findViewById(R.id.create_group_chat);

		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE);
		mTitleBarView.setTitleText(R.string.constacts);//标题
		mTitleBarView.setTitleLeft("刷新");//左按钮-刷新好友
		mTitleBarView.setTitleRight("添加");//右按钮-添加好友
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				initData();
			}
		});
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QtalkClient.getInstance().getQMGoupChatManager().joinMultiUserChat(null,"2222_ghv");
			}
		});

		addChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(mContext, AddFriendActivity.class);
				startActivity(intent);
			}
		});

		creatGroupChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(mContext, CreatGroupActivity.class);
				startActivity(intent);
//				try {
//					QtalkClient.getInstance().getQMGoupChatManager().createChatGroup("群聊","");
//				}catch (Exception e){
//					Toast.makeText(getContext(),"已创建或加入该群聊",Toast.LENGTH_SHORT).show();
//				}
			}
		});

		shadowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				shadowView.setVisibility(View.GONE);
				addView.setVisibility(View.GONE);
			}
		});
		mTitleBarView.setBtn_titleRightIcon(new OnClickListener() {
			@Override
			public void onClick(View view) {
				shadowView.setVisibility(View.VISIBLE);
				addView.setVisibility(View.VISIBLE);
				shadowView.bringToFront();
				addView.bringToFront();
			}
		});
		mIphoneTreeView = (IphoneTreeView) mBaseView.findViewById(R.id.iphone_tree_view);
		mIphoneTreeView.setHeaderView(LayoutInflater.from(mContext).inflate(R.layout.fragment_constact_head_view, mIphoneTreeView, false));
		mIphoneTreeView.setGroupIndicator(null);
		mIphoneTreeView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,int arg3, long arg4) {
				Intent intent =new Intent(mContext, ChatActivity.class);
				intent.putExtra("from", listGroup.get(arg2).getChildList().get(arg3).getUsername());
				intent.putExtra("type", listGroup.get(arg2).getChildList().get(arg3).getChatType());
				startActivity(intent);
				return true;
			}
		});

		initData();
	}
	
	/**
	 * 加载数据
	 */
	void initData(){
		findFriends();
		if(listGroup.size()<=0){
			mIphoneTreeView.setVisibility(View.GONE);
			ToastUtil.showShortToast(mContext, "暂无好友");
			return;
		}
		mExpAdapter = new ConstactAdapter(mContext, listGroup, mIphoneTreeView);
		mIphoneTreeView.setAdapter(mExpAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	public void findFriends() {
		try {
			listGroup=new ArrayList<Group>();
			List<UserBean> userBeans = QtalkClient.getInstance().getQMMContactsManager().getAllContactsFromServer();
			Group mygroup=new Group();
			mygroup.setGroupName("我的好友");
			List<Child> childListNotOnline=new ArrayList<Child>();//不在线
			List<Child> childListOnline=new ArrayList<Child>();//在线
			for (UserBean userBean : userBeans) {
					if(userBean.getFriendRelationship() == FRIEND) {
//						Presence presence = roster.getPresence(entry.getUser());
						LogUtil.e(userBean.getNickName()+"-----------------");
						Child child = new Child();
						child.setUsername(userBean.getNickName());
						child.setMood(userBean.getMood());
						if (userBean.isAvailable()) {//在线
							child.setOnline_status("1");
							childListOnline.add(child);
						} else {//下线
							child.setOnline_status("0");
							childListNotOnline.add(child);
						}
						child.setChatType(0);
					}
//				}
			}
			childListOnline.addAll(childListNotOnline);//在线的靠前排列
			mygroup.setChildList(childListOnline);

			Group mygroup2=new Group();
			mygroup2.setGroupName("群聊");
			List<Child> childListGroup=new ArrayList<Child>();//在线
			List<String> mulitGroup = QtalkClient.getInstance().getQMGoupChatManager().findMulitGroup();
			for(String name : mulitGroup){
				Child child = new Child();
				child.setUsername(name);
				child.setMood("群聊");
				child.setOnline_status("1");
				childListGroup.add(child);
				child.setChatType(1);
			}

			mygroup2.setChildList(childListGroup);
			listGroup.add(mygroup);
			listGroup.add(mygroup2);
			Log.e("jj", "好友数量="+listGroup.get(0).getChildList().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class FriendsOnlineStatusReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String from=intent.getStringExtra("from");
			int status=intent.getIntExtra("status",0);
			if(!TextUtils.isEmpty(from)){
				if(status== 1){
					ToastUtil.showShortToast(mContext, from+"上线了");
				}else if(status==0){
					ToastUtil.showShortToast(mContext, from+"下线了");
				}
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(1);
		
				}
			}, 1000);
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(friendsOnlineStatusReceiver);
	}

}
