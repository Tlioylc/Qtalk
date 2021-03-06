package qumi.com.qtalk.fragment;

import java.util.ArrayList;
import java.util.List;

import qumi.com.qtalk.R;
import qumi.com.qtalk.activity.ChatActivity;
import qumi.com.qtalk.adapter.SessionAdapter;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qtalk.view.CustomListView;
import qumi.com.qtalk.view.TitleBarView;
import qumi.com.qtalk.view.CustomListView.OnRefreshListener;
import qumi.com.qumitalk.service.QtalkClient;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NewsFragment extends Fragment implements OnRefreshListener{
	private Context mContext;
	private View mBaseView;
	private CustomListView mCustomListView;
	private SessionAdapter adapter;
	private List<Session> sessionList = new ArrayList<Session>();
	private TitleBarView mTitleBarView;
	private String userid;

	private AddFriendReceiver addFriendReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_news_father, null);
		userid=PreferencesUtils.getSharePreStr(mContext, "username");
		addFriendReceiver=new AddFriendReceiver();
		IntentFilter intentFilter=new IntentFilter(Const.ACTION_ADDFRIEND);
		mContext.registerReceiver(addFriendReceiver, intentFilter);
		findView();
		initData();
		return mBaseView;
	}

	private void findView() {
		mTitleBarView=(TitleBarView) mBaseView.findViewById(R.id.title_bar);
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.GONE);
		mTitleBarView.setTitleText(R.string.msgs);
		
		mCustomListView = (CustomListView) mBaseView.findViewById(R.id.lv_news);//listview
		mCustomListView.setOnRefreshListener(this);//设置listview下拉刷新监听
		mCustomListView.setCanLoadMore(false);//设置禁止加载更多
		mCustomListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				final Session session=sessionList.get(arg2-1);
				if(session.getType() == Const.MSG_TYPE_ADD_FRIEND ){
					if(!TextUtils.isEmpty(session.getIsdispose())){
						if(!session.getIsdispose().equals("1")){
							Builder bd=new Builder(mContext);
							bd.setItems(new String[]{"同意"}, new OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if(QtalkClient.getInstance().getQMMContactsManager().agreeApply( session.getFromUser())){
										//告知对方，同意添加其为好友
										new Thread(new Runnable() {
											@Override
											public void run() {
												try {
													QMMessageBean qmMessageBean = QMMessageBean.createReceivedFriendMessage(session.getFromUser(),userid);
													QtalkClient.getInstance().getChatClient().sendMessage(qmMessageBean);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}).start();
										QtalkClient.getInstance().getQMConversationManager().updateFriendRequestHandle(session.getId());//将本条数据在数据库中改为已处理
//										ToastUtil.showShortToast(mContext, "你们已经是好友了，快去聊天吧！");
										sessionList.remove(session);
										session.setIsdispose("1");
										sessionList.add(0,session);
										adapter.notifyDataSetChanged();
										//发送广播更新好友列表
										 Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
							        	 mContext.sendBroadcast(intent);
									}else{
										ToastUtil.showLongToast(mContext, "添加好友失败");
									}
								}
							});
							bd.create().show();
						}else{
							ToastUtil.showShortToast(mContext, "已同意");
						}
					}
				}else{
					Intent intent=new Intent(mContext, ChatActivity.class);
					intent.putExtra("from", session.getFromUser());
					intent.putExtra("type", session.getChatType());
					startActivity(intent);
				}
			}
		});
		mCustomListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				final Session session=sessionList.get(arg2-1);
				Builder bd=new Builder(mContext);
				bd.setItems(new String[] {"删除会话"}, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						QtalkClient.getInstance().getQMConversationManager().deleteConversation(session);
						QtalkClient.getInstance().getQmChatMessageManager().deleteConversationAllMessage(session.getFromUser(), session.getToUser());
						mContext.sendBroadcast(new Intent(Const.ACTION_NEW_MSG));
						initData();
					}
				}).create().show();
				return true;
			}
		});
	}

	private void initData() {
		//注意，当数据量较多时，此处要开线程了，否则阻塞主线程
		sessionList=QtalkClient.getInstance().getQMConversationManager().getAllConversation(userid);
		adapter = new SessionAdapter(mContext, sessionList);
		mCustomListView.setAdapter(adapter);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		initData();
	}

	@Override
	public void onRefresh() {
		 initData();
		mCustomListView.onRefreshComplete();
	}
	
	class AddFriendReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			initData();
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(addFriendReceiver);
	}

}
